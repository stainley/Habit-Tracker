package ca.lambton.habittracker.community.view;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import ca.lambton.habittracker.util.Utils;

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
    private int progressStatus = 0;
    private Uri tempImageUri = null;

    private float startX;
    private float startY;
    private CardView imageThumbnailCard;


    private static final int INVALID_POINTER_ID = -1;

    // Keep track of the two fingers used for the pinch gesture
    private int mActivePointerId1 = INVALID_POINTER_ID;
    private int mActivePointerId2 = INVALID_POINTER_ID;
    // Keep track of the initial distance between the two fingers
    private float mInitialDistance;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentComposeBinding.inflate(LayoutInflater.from(requireContext()));

        postProgress = binding.postProgress;

        binding.postButton.setOnClickListener(this::composeNewPost);
        binding.addImageButton.setOnClickListener(this::showBottomOptions);

        binding.deleteImage.setOnClickListener(this::removeImage);

        editText = binding.postEditText;
        imageView = binding.imageThumbnailImageView;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        postViewModel = new ViewModelProvider(this, new PostViewModelFactory(requireActivity().getApplication())).get(PostViewModel.class);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        // Create a Cloud Storage reference from the app
        storageRef = FirebaseStorage.getInstance().getReference();

        imageThumbnailCard = binding.imageThumbnailCard;
    }

    private void removeImage(View view) {
        imageView.setImageDrawable(null);
        imageThumbnailCard.setVisibility(View.GONE);
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
                if (Objects.equals(item.getTitle(), "Paste Image")) {
                    // Get the clipboard manager
                    ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    // Check if the clipboard contains an image
                    if (clipboard.hasPrimaryClip() && Objects.requireNonNull(clipboard.getPrimaryClipDescription()).hasMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpeg"))) {
                        // Get the image from the clipboard
                        ClipData.Item clipItem = Objects.requireNonNull(clipboard.getPrimaryClip()).getItemAt(0);
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
            post.setCount(0);
            post.setPostImage(postImage);
            post.setCreationDate(today.toString());

            postViewModel.createPost(post);
            onTaskFinishedListener.onCompleted(true);
        });
    }

    // Compose a new post
    private void composeNewPost(View view) {

        if (binding.postEditText.getText().toString().equals("")) {
            binding.postEditText.setError("A post must contain a message");
            return;
        }

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

    public void insertImageIntoEditText(@NonNull EditText editText, Uri imageUri) {
        // Get the SpannableStringBuilder from the EditText

        binding.imageThumbnailImageView.setVisibility(View.INVISIBLE);
        binding.imageThumbnailCard.setVisibility(View.INVISIBLE);

        SpannableStringBuilder builder = new SpannableStringBuilder(editText.getText());
        SpannableString spannableString = new SpannableString(" ");

        // Get the cursor position
        int cursorPosition = editText.getSelectionStart();
        // Insert the image at the cursor position
        ImageSpan imageSpanFromUri = createImageSpanFromUri(imageUri);

        spannableString.setSpan(imageSpanFromUri, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.insert(cursorPosition, spannableString);

        if (imageSpanFromUri != null) {
            Drawable image = imageSpanFromUri.getDrawable();
            imageView.setImageDrawable(image);
        }

        // Set the modified text back into the EditText
        editText.setText(builder);
    }

    private void uploadImage(@NonNull ImageView image, OnUploadingImageAction onUploadingImageAction) {
        Drawable drawable = image.getDrawable();
        if (drawable == null) {
            onUploadingImageAction.onFinished("");
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        StorageReference photosRef = storageRef.child("pictures/" + currentUser.getUid() + "_" + UUID.randomUUID());
        UploadTask uploadTask = photosRef.putBytes(data);

        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            //Toast.makeText(requireContext(), "Photo error uploading occurred", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
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
                Log.d(TAG, "Photo URL: " + pictureDownloadUri);
                onUploadingImageAction.onFinished(pictureDownloadUri.toString());
            } else {
                // Handle failures
                Log.e(TAG, "Error fetching the photo URL", task.getException());
            }
        });
    }


    @Nullable
    private ImageSpan createImageSpanFromUri(Uri imageUri) {
        try {
            // Load the image from the URI
            Bitmap bitmap = loadBitmapFromUri(imageUri);

            // Scale the image to fit within the width of the EditText
            int width = editText.getWidth() - editText.getPaddingLeft() - editText.getPaddingRight();
            int height = (int) (((float) width / bitmap.getWidth()) * bitmap.getHeight());
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

            // Create an ImageSpan from the scaled bitmap
            return new ImageSpan(requireContext(), scaledBitmap, ImageSpan.ALIGN_BASELINE);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Image file not found: " + imageUri, e);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load image from URI: " + imageUri, e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to create image span from URI: " + imageUri, e);
        }
        return null;
    }

    @NonNull
    private Bitmap loadBitmapFromUri(Uri imageUri) throws IOException {
        try (ParcelFileDescriptor parcelFileDescriptor = requireContext().getContentResolver().openFileDescriptor(imageUri, "r")) {
            if (parcelFileDescriptor == null) {
                throw new FileNotFoundException("Could not open file descriptor for URI: " + imageUri);
            }

            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor());
            if (bitmap == null) {
                throw new IOException("Failed to decode bitmap from URI: " + imageUri);
            }
            return bitmap;

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Image file not found: " + imageUri);
        } catch (IOException e) {
            throw new IOException("Failed to load image from URI: " + imageUri, e);
        }
    }


    /*private void addPicture(View view) {
        addPhotoFromLibrary();
    }*/

    public void addPhotoFromLibrary() {
        System.out.println("ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) " + ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA));
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            selectPictureLauncher.launch(new PickVisualMediaRequest());
        }
    }


    private final ActivityResultLauncher<PickVisualMediaRequest> selectPictureLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<>() {

        @Override
        public void onActivityResult(Uri result) {
            try {
                if (result != null) {
                    CardView imageThumbnailCard = binding.imageThumbnailCard;
                    String picturePath = "content://media/" + result.getPath();

                    ImageView thumbnailOverview = binding.imageThumbnailImageView;
                    Picasso.get()
                            .load(picturePath)
                            .into(thumbnailOverview);
                    thumbnailOverview.setVisibility(View.VISIBLE);

                    imageThumbnailCard.setOnTouchListener(onTouchListener);
                    imageThumbnailCard.setVisibility(View.VISIBLE);

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    });


    /**
     * Touch event
     */
    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(@NonNull View view, @NonNull MotionEvent event) {

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    // Save the initial X and Y coordinates
                    startX = view.getX() - event.getRawX();
                    startY = view.getY() - event.getRawY();
                    // One finger is down, save its ID
                    mActivePointerId1 = event.getPointerId(0);
                    break;

                case MotionEvent.ACTION_MOVE:

                    if (mActivePointerId1 != INVALID_POINTER_ID && mActivePointerId2 != INVALID_POINTER_ID) {
                        // Two fingers are down, scale the ImageView based on the distance between them
                        float distance = getDistance(event, mActivePointerId1, mActivePointerId2);
                        float scale = distance / mInitialDistance;
                        //imageView.setScaleX(scale);
                        //imageView.setScaleY(scale);
                        imageThumbnailCard.setScaleX(scale);
                        imageThumbnailCard.setScaleY(scale);
                    }

                    // Calculate the new X and Y coordinates
                    float newX = event.getRawX() + startX;
                    float newY = event.getRawY() + startY;

                    // Set the new position of the CardView
                    view.setX(newX);
                    view.setY(newY);


                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    // A second finger is down, save its ID and the initial distance between the fingers
                    mActivePointerId2 = event.getPointerId(event.getActionIndex());
                    mInitialDistance = getDistance(event, mActivePointerId1, mActivePointerId2);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    // One of the fingers is lifted, reset its ID and the initial distance
                    int pointerIndex = event.getActionIndex();
                    int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId1 || pointerId == mActivePointerId2) {
                        if (pointerId == mActivePointerId1) {
                            mActivePointerId1 = mActivePointerId2;
                        }
                        mActivePointerId2 = INVALID_POINTER_ID;
                        mInitialDistance = 0;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // All fingers are lifted, reset everything
                    mActivePointerId1 = INVALID_POINTER_ID;
                    mActivePointerId2 = INVALID_POINTER_ID;
                    mInitialDistance = 0;
                    break;
                default:
                    return false;
            }
            return true;
        }

        // Helper method to calculate the distance between two fingers
        private float getDistance(@NonNull MotionEvent event, int pointerId1, int pointerId2) {
            float dx = event.getX(pointerId1) - event.getX(pointerId2);
            float dy = event.getY(pointerId1) - event.getY(pointerId2);
            return (float) Math.sqrt(dx * dx + dy * dy);
        }
    };

    public void showBottomOptions(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.ModalBottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_option, requireActivity().findViewById(R.id.bottomSheetContainer));
        bottomSheetView.findViewById(R.id.take_photo_btn).setOnClickListener(view1 -> {
            takePhoto();
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.upload_image_btn).setOnClickListener(view12 -> {
            addPhotoFromLibrary();
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private final ActivityResultLauncher<Uri> selectCameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        if (result) {

            try {
                CardView imageThumbnailCard = binding.imageThumbnailCard;
                int deviceOrientation = requireActivity().getWindowManager().getDefaultDisplay().getRotation();
                int cameraOrientation = Utils.getCameraOrientation(requireActivity());

                String picturePath = "content://media/" + tempImageUri.getPath();
                SpannableString spannableString = new SpannableString(" ");

                int rotation = (cameraOrientation - deviceOrientation + 360) % 360;

                ImageView thumbnailOverview = binding.imageThumbnailImageView;
                Bitmap bitmap = loadBitmapFromUri(tempImageUri);
                Bitmap rotateBitmap = Utils.rotateBitmap(bitmap, rotation);

                ImageSpan imageSpan = new ImageSpan(requireContext(), rotateBitmap, ImageSpan.ALIGN_BASELINE);//createImageSpanFromUri(tempImageUri);

                spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (imageSpan != null) {
                    Drawable image = imageSpan.getDrawable();
                    imageView.setImageDrawable(image);
                }

                thumbnailOverview.setVisibility(View.VISIBLE);

                imageThumbnailCard.setOnTouchListener(onTouchListener);
                imageThumbnailCard.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    });

    public void takePhoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            ContentResolver cr = requireContext().getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            tempImageUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            selectCameraLauncher.launch(tempImageUri);
        }
    }


    public interface OnUploadingImageAction {
        void onFinished(String path);
    }

    public interface OnTaskFinishedListener {
        void onCompleted(boolean isCompleted);
    }

}
