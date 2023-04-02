package ca.lambton.habittracker.community.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.community.model.Post;
import ca.lambton.habittracker.community.model.PostImage;
import ca.lambton.habittracker.community.viewmodel.PostViewModel;
import ca.lambton.habittracker.community.viewmodel.PostViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentComposeBinding;
import ca.lambton.habittracker.habit.model.User;

public class ComposeFragment extends Fragment {
    private static final String TAG = ComposeFragment.class.getName();
    private FragmentComposeBinding binding;
    private PostViewModel postViewModel;
    private FirebaseUser mUser;
    private EditText editText;
    private FirebaseUser currentUser;
    private StorageReference storageRef;
    private ImageView imageView;
    private LinearProgressIndicator postProgress;
    int progressStatus = 0;
    private Handler handler = new Handler();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentComposeBinding.inflate(LayoutInflater.from(requireContext()));

        postProgress = binding.postProgress;

        binding.postButton.setOnClickListener(this::composeNewPost);
        editText = binding.postEditText;
        imageView = binding.imageThumbnailImageView;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        postViewModel = new ViewModelProvider(this, new PostViewModelFactory(requireActivity().getApplication())).get(PostViewModel.class);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        // Create a Cloud Storage reference from the app
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Get the system clipboard
        editText.setCustomInsertionActionModeCallback(new ActionMode.Callback2() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Disable the default "paste" menu item
                menu.removeItem(android.R.id.paste);
                // Add a custom "paste image" menu item
                menu.add("Paste Image").setIcon(android.R.drawable.ic_menu_gallery).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getTitle().equals("Paste Image")) {
                    // Get the clipboard manager
                    ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    // Check if the clipboard contains an image
                    if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription().hasMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpeg"))) {
                        // Get the image from the clipboard
                        ClipData.Item clipItem = clipboard.getPrimaryClip().getItemAt(0);
                        Uri imageUri = clipItem.getUri();
                        // Insert the image into the EditText
                        insertImageIntoEditText(editText, imageUri);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });


        return binding.getRoot();
    }

    private void executePost(OnTaskFinishedListener onTaskFinishedListener) {

        Post post = new Post();
        post.setVisible(1);
        LocalDateTime today = LocalDateTime.now();
        String message = binding.postEditText.getText().toString();
        if (!message.equals("")) post.setMessage(message);

        if (mUser != null) {
            User user = new User();
            user.setName(mUser.getDisplayName());
            user.setPhotoUrl(mUser.getPhotoUrl() != null ? mUser.getPhotoUrl().toString() : "");
            user.setAccountId(mUser.getUid());
            user.setEmail(mUser.getEmail());
            post.setUser(user);
        }

        // Upload image to Fire storage
        postProgress.setProgress(progressStatus, true);
        uploadImage(imageView, path -> {
            final PostImage postImage = new PostImage();

            postImage.setPath(path);
            System.out.println(path);

            post.setPostImage(postImage);
            post.setCreationDate(today.toString());

            postViewModel.createPost(post);
            onTaskFinishedListener.onCompleted(true);
        });
    }

    // Compose a new post
    private void composeNewPost(View view) {
        postProgress.setIndeterminate(true);
        postProgress.setVisibility(View.VISIBLE);
        executePost(isCompleted -> {

            if (isCompleted) {
                postProgress.setVisibility(View.INVISIBLE);
                // Stop the progress bar
                NavHostFragment.findNavController(requireParentFragment()).popBackStack(R.id.nav_community, false);
            }

        });
    }

    private void insertImageIntoEditText(EditText editText, Uri imageUri) {
        // Get the SpannableStringBuilder from the EditText
        SpannableStringBuilder builder = new SpannableStringBuilder(editText.getText());
        SpannableString spannableString = new SpannableString(" ");

        // Get the cursor position
        int cursorPosition = editText.getSelectionStart();
        // Insert the image at the cursor position
        try {
            ImageSpan imageSpanFromUri = createImageSpanFromUri(imageUri);
            //SpannableString spannableString = new SpannableString(" ");
            //spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //imageView.setImageDrawable(imageSpanFromUri.getDrawable());
            spannableString.setSpan(imageSpanFromUri, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.insert(cursorPosition, spannableString);
            Drawable image = imageSpanFromUri.getDrawable();
            imageView.setImageDrawable(image);


            //DownloadImageTask downloadImageTask = new DownloadImageTask();
            //downloadImageTask.execute(imageUri.toString());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        // Set the modified text back into the EditText
        editText.setText(builder);
    }

    private void uploadImage(ImageView image, OnUploadingImageAction onUploadingImageAction) {
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache(true);

        if (image.getDrawable() != null) {

            new Thread(() -> {
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] data = outputStream.toByteArray();

            StorageReference photosRef = storageRef.child("pictures/" + currentUser.getUid() + "_" + UUID.randomUUID());
            UploadTask uploadTask = photosRef.putBytes(data);

            uploadTask.addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
                Toast.makeText(requireContext(), "Photo error uploading occurred", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                //Toast.makeText(requireContext(), "Photo uploaded", Toast.LENGTH_SHORT).show();
            });

            // Get the URI of the photo
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                // Continue with the task to get the download URL
                return photosRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri pictureDownloadUri = task.getResult();
                    System.out.println(TAG + ": " + pictureDownloadUri);
                    onUploadingImageAction.onFinished(pictureDownloadUri.toString());
                } else {
                    // Handle failures
                    Log.e(TAG, "Error fetching the photo URL");
                }
            });
        } else {
            onUploadingImageAction.onFinished("");
        }

    }

    private ImageSpan createImageSpanFromUri(Uri imageUri) throws FileNotFoundException {
        // Load the image from the URI
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(requireContext().getContentResolver().openFileDescriptor(imageUri, "r").getFileDescriptor());
        // Scale the image to fit within the width of the EditText
        int width = editText.getWidth() - editText.getPaddingLeft() - editText.getPaddingRight();
        int height = (int) (((float) width / bitmap.getWidth()) * bitmap.getHeight());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        // Create an ImageSpan from the scaled bitmap
        return new ImageSpan(requireContext(), scaledBitmap, ImageSpan.ALIGN_BASELINE);
    }

    private class UploadImageTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String path = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Read the input stream into a byte array
                InputStream input = connection.getInputStream();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                byte[] imageBytes = output.toByteArray();

                // Save the image to external storage
                String fileName = "my_image.jpg";
                File file = new File(requireContext().getFilesDir(), fileName);

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(imageBytes);
                fos.close();
                path = file.getAbsolutePath();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return path;
        }
    }

    public interface OnUploadingImageAction {
        void onFinished(String path);
    }

    public interface OnTaskFinishedListener {
        void onCompleted(boolean isCompleted);
    }

}
