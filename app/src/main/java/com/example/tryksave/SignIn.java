package com.example.tryksave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    private TextView textViewBack;
    private Intent intentGetStarted, intentHome;

    private Button btnSignIn;
    private EditText editEmail, editPassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        textViewBack = findViewById(R.id.TextViewBackSignUp);
        intentGetStarted = new Intent(this, Get_Started.class);
        intentHome = new Intent(this, Home.class);
        editEmail = findViewById(R.id.EditTextEmailSignIn);
        editPassword = findViewById(R.id.EditTextEndLocation);
        btnSignIn = findViewById(R.id.ButtonSignIn);

        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentGetStarted);
            }
        });



        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email, password;
                email = String.valueOf(editEmail.getText());
                password = String.valueOf(editPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    // Email is empty
                    Toast.makeText(SignIn.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    // Password is empty
                    Toast.makeText(SignIn.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    // Password is too short
                    Toast.makeText(SignIn.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(SignIn.this, "Signed In",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(intentHome);
                                        finish(); // Close the current activity
                                    } else {
                                        // User is null
                                        Toast.makeText(SignIn.this, "User is null after signing in", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignIn.this, "Authentication failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
