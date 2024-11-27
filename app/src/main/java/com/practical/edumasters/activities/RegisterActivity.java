//package com.practical.edumasters.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.util.Patterns;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.practical.edumasters.R;
//import com.practical.edumasters.databinding.ActivityRegisterBinding;
//import com.practical.edumasters.models.User;
//
//import org.mindrot.jbcrypt.BCrypt;
//
//public class RegisterActivity extends AppCompatActivity {
//
//    ActivityRegisterBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        binding.tvLoginHere.setOnClickListener(v -> {
//            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//        });
//
//        binding.btnSignUp.setOnClickListener(v -> {
//            String username = binding.etUsername.getText().toString().trim();
//            String email = binding.etEmail.getText().toString().trim();
//            String password = binding.etPassword.getText().toString().trim();
//
//            signUp(email, password, username);
//        });
//    }
//
//    private void signUp(String email, String password, String username) {
//        // Validate username
//        if (username.isEmpty()) {
//            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Validate email format
//        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Validate password length
//        if (password.isEmpty() || password.length() < 6) {
//            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//
//        // Hash the password using Bcrypt
//        String hashedPassword = hashPassword(password);
//
//        // Register the user with Firebase
//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser user = firebaseAuth.getCurrentUser();
//                        if (user != null) {
//                            String userId = user.getUid();
//
//                            // Generate current date
//                            String registrationDate = java.text.DateFormat.getDateInstance().format(new java.util.Date());
//                            String loginDate = registrationDate;  // Assuming login date is the same as registration
//
//                            // User data object
//                            User newUser = new User(
//                                    username,
//                                    email,
//                                    hashedPassword, // Save hashed password
//                                    registrationDate,
//                                    loginDate,
//                                    0, // Default XP
//                                    null, // Default avatar
//                                    null  // Default bio
//                            );
//
//                            // Save user to Firestore
//                            firestore.collection("users").document(userId)
//                                    .set(newUser)
//                                    .addOnCompleteListener(databaseTask -> {
//                                        if (databaseTask.isSuccessful()) {
//                                            user.sendEmailVerification()
//                                                    .addOnCompleteListener(emailTask -> {
//                                                        if (emailTask.isSuccessful()) {
//                                                            Toast.makeText(this, "Registration successful. Verify your email.", Toast.LENGTH_LONG).show();
//                                                            startActivity(new Intent(this, LoginActivity.class));
//                                                            finish();
//                                                        } else {
//                                                            Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    });
//                                        } else {
//                                            if (databaseTask.getException() != null) {
//                                                Log.e("DatabaseError", databaseTask.getException().getMessage());
//                                                Toast.makeText(this, "Failed to save user data: " + databaseTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
//                        }
//                    } else {
//                        if (task.getException() != null) {
//                            Log.e("SignupError", task.getException().getMessage());
//                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//    }
//
//    // Method to hash the password using Bcrypt
//    private String hashPassword(String plainPassword) {
//        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12)); // 12 is the work factor
//    }
//}
package com.practical.edumasters.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.practical.edumasters.R;
import com.practical.edumasters.databinding.ActivityRegisterBinding;
import com.practical.edumasters.models.User;

import org.mindrot.jbcrypt.BCrypt;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvLoginHere.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });

        binding.btnSignUp.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            signUp(email, password, username);
        });
    }

    private void signUp(String email, String password, String username) {
        // Validate username
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password length
        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Hash the password using Bcrypt
        String hashedPassword = hashPassword(password);

        // Register the user with Firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            // Generate current date
                            String registrationDate = java.text.DateFormat.getDateInstance().format(new java.util.Date());
                            String loginDate = registrationDate;  // Assuming login date is the same as registration

                            // User data object
                            User newUser = new User(
                                    username,
                                    email,
                                    hashedPassword, // Save hashed password
                                    registrationDate,
                                    loginDate,
                                    0, // Default XP
                                    "", // Default avatar
                                    ""  // Default bio
                            );

                            // Save user to Firestore
                            firestore.collection("users").document(userId)
                                    .set(newUser)
                                    .addOnCompleteListener(databaseTask -> {
                                        if (databaseTask.isSuccessful()) {
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(emailTask -> {
                                                        if (emailTask.isSuccessful()) {
                                                            Toast.makeText(this, "Registration successful. Verify your email.", Toast.LENGTH_LONG).show();
                                                            startActivity(new Intent(this, LoginActivity.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            if (databaseTask.getException() != null) {
                                                Log.e("DatabaseError", databaseTask.getException().getMessage());
                                                Toast.makeText(this, "Failed to save user data: " + databaseTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    } else {
                        if (task.getException() != null) {
                            Log.e("SignupError", task.getException().getMessage());
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Method to hash the password using Bcrypt
    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12)); // 12 is the work factor
    }
}
