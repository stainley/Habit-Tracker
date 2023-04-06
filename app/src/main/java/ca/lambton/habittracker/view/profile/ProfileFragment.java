package ca.lambton.habittracker.view.profile;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import ca.lambton.habittracker.databinding.FragmentProfileBinding;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getName();
    private FragmentProfileBinding binding;
    private FirebaseUser currentUser;
    private StorageReference storageRef;
    private CircleImageView profileImage;
    private Uri tempImageUri = null;

    private final ActivityResultLauncher<Uri> selectCameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        if (result) {

            try {
                String picturePath = "content://media/" + tempImageUri.getPath();

                Picasso.get().load(picturePath).resize(300, 300).centerInside().into(profileImage);
                profileImage.setDrawingCacheEnabled(true);
                profileImage.buildDrawingCache(true);

                new Thread(() -> {

                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (profileImage.getDrawable() != null) {
                        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        byte[] data = outputStream.toByteArray();

                        StorageReference photosRef = storageRef.child("photos/" + currentUser.getUid());
                        UploadTask uploadTask = photosRef.putBytes(data);

                        uploadTask.addOnFailureListener(exception -> {
                            // Handle unsuccessful uploads
                            Toast.makeText(requireContext(), "Photo error uploading occurred", Toast.LENGTH_SHORT).show();
                        }).addOnSuccessListener(taskSnapshot -> {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            Toast.makeText(requireContext(), "Photo uploaded", Toast.LENGTH_SHORT).show();
                        });


                        uploadTask.continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            // Continue with the task to get the download URL
                            return photosRef.getDownloadUrl();
                        }).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Uri photoDownloadUri = task.getResult();
                                UserProfileChangeRequest profileChangeNameRequest = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(photoDownloadUri)
                                        .build();

                                currentUser.updateProfile(profileChangeNameRequest).addOnCompleteListener(taskProfile -> {
                                    if (task.isSuccessful()) {
                                        binding.nameProfile.setText(currentUser.getDisplayName());
                                        Toast.makeText(requireContext(), "Display name profile updated.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // Handle failures
                                Log.e(TAG, "Error fetching the photo URL");
                            }
                        });
                    }
                }).start();


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    });

    private final ActivityResultLauncher<PickVisualMediaRequest> selectPictureLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<>() {

        @Override
        public void onActivityResult(Uri result) {
            try {
                if (result != null) {
                    tempImageUri = result;
                    String picturePath = "content://media/" + result.getPath();
                    System.out.println(picturePath);

                    Picasso.get().load(picturePath).resize(300, 300).centerInside().into(profileImage);

                    profileImage.setDrawingCacheEnabled(true);
                    profileImage.buildDrawingCache(true);

                    new Thread(() -> {

                        try {
                            Thread.sleep(1000);

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        if (profileImage.getDrawable() != null) {
                            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            byte[] data = outputStream.toByteArray();

                            StorageReference photosRef = storageRef.child("photos/" + currentUser.getUid());
                            UploadTask uploadTask = photosRef.putBytes(data);

                            uploadTask.addOnFailureListener(exception -> {
                                // Handle unsuccessful uploads
                                Toast.makeText(requireContext(), "Photo error uploading occurred", Toast.LENGTH_SHORT).show();
                            }).addOnSuccessListener(taskSnapshot -> {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            });


                            uploadTask.continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                // Continue with the task to get the download URL
                                return photosRef.getDownloadUrl();
                            }).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Uri photoDownloadUri = task.getResult();
                                    UserProfileChangeRequest profileChangeNameRequest = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(photoDownloadUri)
                                            .build();

                                    currentUser.updateProfile(profileChangeNameRequest).addOnCompleteListener(taskProfile -> {
                                        if (task.isSuccessful()) {
                                            binding.nameProfile.setText(currentUser.getDisplayName());
                                            Toast.makeText(requireContext(), "Display name profile updated.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    // Handle failures
                                    Log.e(TAG, "Error fetching the photo URL");
                                }
                            });
                        }
                    }).start();


                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    });


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentProfileBinding.inflate(LayoutInflater.from(requireContext()));
        profileImage = binding.profileImage;

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.editNameButton.setOnClickListener(this::editProfileName);

        binding.changeImageButton.setOnClickListener(this::changeProfilePicture);
        binding.cameraImageButton.setOnClickListener(this::takePhotoProfile);
        // Create a Cloud Storage reference from the app
        storageRef = FirebaseStorage.getInstance().getReference();

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if (currentUser != null) {
            binding.nameProfile.setText(currentUser.getDisplayName());
            binding.emailProfile.setText(currentUser.getEmail());

            if (currentUser.getPhotoUrl() != null) {
                Picasso.get().load(currentUser.getPhotoUrl()).fit().into(binding.profileImage);
            }
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void editProfileName(View view) {


        TextInputEditText newEditText = new TextInputEditText(requireContext());
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        newEditText.setSingleLine();
        newEditText.setPadding(50, 0, 50, 32);
        String name = currentUser.getDisplayName();
        newEditText.setText(name != null ? name : "");
        newEditText.setHint("Name");
        newEditText.setPadding(75, newEditText.getPaddingTop(), newEditText.getPaddingRight(), newEditText.getPaddingBottom());


        new MaterialAlertDialogBuilder(requireContext()).setView(newEditText).setMessage("Enter your name").setNeutralButton("Cancel", (dialog, which) -> {
        }).setNegativeButton("Save", (dialog, which) -> {

            String inputText = Objects.requireNonNull(newEditText.getText()).toString();
            if (inputText.equals("")) {
                Toast.makeText(requireContext(), "Couldn't be empty", Toast.LENGTH_SHORT).show();
            } else {
                UserProfileChangeRequest profileChangeNameRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newEditText.getText().toString())
                        .build();

                currentUser.updateProfile(profileChangeNameRequest).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.nameProfile.setText(currentUser.getDisplayName());
                        Toast.makeText(requireContext(), "Display name profile updated.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).setCancelable(false).show();

    }

    private void changeProfilePicture(View view) {
        addPhotoFromLibrary();
    }

    private void takePhotoProfile(View view) {
        takePhoto();
    }

    public void addPhotoFromLibrary() {
        System.out.println("ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) " + ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA));
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            selectPictureLauncher.launch(new PickVisualMediaRequest());
        }
    }

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
}
