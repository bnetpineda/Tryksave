package com.example.tryksave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    private TextView txtViewShowPassword;
    private Intent intentGetStarted, intentSignIn;

    private EditText editName, editEmail, editPassword;

    private boolean isPasswordVisible = false;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button btnSignUp = findViewById(R.id.ButtonSignIn);
        TextView txtViewBack = findViewById(R.id.TextViewBackSignUp);
        editName = findViewById(R.id.EditTextEmailSignIn);
        editPassword = findViewById(R.id.EditTextEndLocation);
        editEmail = findViewById(R.id.EditTextEmail);
        txtViewShowPassword = findViewById(R.id.TextViewShowSignUp);
        TextView txtViewAlreadyAccount = findViewById(R.id.TextViewAlreadyAccount);
        mAuth = FirebaseAuth.getInstance();
        intentGetStarted = new Intent(this, Get_Started.class);
        intentSignIn = new Intent(this, SignIn.class);

        txtViewAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentSignIn);
            }
        });
        txtViewShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    editPassword.setTransformationMethod(new PasswordTransformationMethod());
                    isPasswordVisible = false;
                    txtViewShowPassword.setText("Show");
                } else {

                    editPassword.setTransformationMethod(null);
                    isPasswordVisible = true;
                    txtViewShowPassword.setText("Hide");
                }
                editPassword.setSelection(editPassword.getText().length());
            }
        });
        txtViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentGetStarted);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, email, password;
                name = String.valueOf(editName.getText());
                email = String.valueOf(editEmail.getText());
                password = String.valueOf(editPassword.getText());

                if(TextUtils.isEmpty(email)){
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(SignUp.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(intentSignIn);
                                    
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUp.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });


    }
}