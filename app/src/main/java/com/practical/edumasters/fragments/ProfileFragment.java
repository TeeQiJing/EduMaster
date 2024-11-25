package com.practical.edumasters.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.practical.edumasters.activities.LoginActivity;
import com.practical.edumasters.models.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.practical.edumasters.R;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for picking an image
    private ImageView avatarImageView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView tvUsername, tvPoints, tvCourses;
    private LinearLayout btnLogout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        avatarImageView = rootView.findViewById(R.id.profileImage);
        tvUsername = rootView.findViewById(R.id.tvUsername);
        tvPoints = rootView.findViewById(R.id.tvPoints);
        tvCourses = rootView.findViewById(R.id.tvCourses);
        btnLogout = rootView.findViewById(R.id.btnLogout);

        // Load current avatar image if it exists
        loadUserProfile();

        // Set up avatar click listener to open the image picker
        avatarImageView.setOnClickListener(v -> openImagePicker());

        // Set up logout button click listener
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        return rootView;
    }

    /**
     * Load the current user's profile details from Firebase.
     */
    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    // Display user details
                    tvUsername.setText(user.getUsername());
                    tvPoints.setText(user.getXp() + "\nTotal Points");
                    tvCourses.setText(user.getCourses() != null ? user.getCourses().size() + "\nCourses Enrolled" : "0 \nCourses Enrolled");

                    // Load avatar if it exists
                    if (user.getAvatar() != null) {
                        String base64Image = user.getAvatar();
                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        avatarImageView.setImageBitmap(decodedByte);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Open the image picker to let the user select an image.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handle the result of the image picker and upload the image to Firebase.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Convert the image to base64 and upload it
                String base64Image = null;
                try {
                    base64Image = convertImageToBase64(imageUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (base64Image != null) {
                    uploadAvatar(base64Image);  // Upload the avatar as base64
                }
            }
        }
    }

    /**
     * Convert the selected image to a Base64 string.
     */
    private String convertImageToBase64(Uri imageUri) throws IOException {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
            byte[] byteArray = getBytes(inputStream);
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert InputStream to byte array.
     */
    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Upload the avatar image (base64 encoded) to Firebase Realtime Database.
     */
    private void uploadAvatar(String base64Image) {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child(userId).child("avatar").setValue(base64Image)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ProfileFragment", "Avatar URL updated successfully");
                        // Optionally update the UI with the new avatar
                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        avatarImageView.setImageBitmap(decodedByte);
                    } else {
                        Log.e("ProfileFragment", "Failed to update avatar URL");
                        Toast.makeText(getContext(), "Failed to update avatar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Logout the user.
     */

    private void showLogoutConfirmationDialog() {
        // Inflate the custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_logout_confirmation, null);

        // Build the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Set up Cancel button
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> dialog.dismiss()); // Dismiss dialog on cancel

        // Set up Logout button
        Button logoutButton = dialogView.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            dialog.dismiss(); // Dismiss dialog
            logout(); // Call the logout method
        });

        // Show the dialog
        dialog.show();
    }


    private void logout() {
        mAuth.signOut();
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        // Optionally redirect to login screen or perform other actions
         startActivity(new Intent(getActivity(), LoginActivity.class));
    }
}
