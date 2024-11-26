package com.practical.edumasters.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.practical.edumasters.databinding.ActivityLoginBinding;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding; // ViewBinding for layout
    private FirebaseAuth mAuth; // FirebaseAuth instance




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using DataBinding to set the content view
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth


        binding.tvRegisterHere.setOnClickListener(v ->  {

                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

        });

        // Login Button Click Listener
        binding.btnLogin.setOnClickListener(v-> {

                loginUser();

        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));

        });

    }

    private void loginUser() {
        // Get input from EditTexts
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Validate email and password
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform Firebase authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful, now check email verification
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                // If email is verified, allow login
                                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If email is not verified, inform the user without signing out
                                Toast.makeText(this, "Email not verified. Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                                resendVerificationEmail(user); // Offer to resend the verification email
                            }
                        }
                    } else {
                        // Login failed, display error
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * Resend email verification link if the user hasn't verified their email.
     */
    private void resendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Failed to send email.";
                        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Helper method to validate email format.
     */
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }











}
