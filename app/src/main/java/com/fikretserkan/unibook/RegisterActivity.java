package com.fikretserkan.unibook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fikretserkan.unibook.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.email_reg)
    EditText email;
    @BindView(R.id.username_reg)
    EditText username;
    @BindView(R.id.password_reg)
    EditText password;
    @BindView(R.id.register_reg)
    Button register;
    @BindView(R.id.tologin)
    Button tologin;
    @BindView(R.id.emailError_reg)
    TextInputLayout emailError;
    @BindView(R.id.usernameError_reg)
    TextInputLayout usernameError;
    @BindView(R.id.passError_reg)
    TextInputLayout passError;

    Dialog progressDialog;

    boolean isEmailValid, isUsernameValid, isPasswordValid;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("users");

        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.setCancelable(false);

        if(progressDialog.getWindow() != null)
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValidation();
            }
        });

        tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to RegisterActivity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void SetValidation() {
        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid username.
        if (username.getText().toString().isEmpty()) {
            usernameError.setError(getResources().getString(R.string.username_error));
            isUsernameValid = false;
        } else if (username.getText().toString().length() < 4 || username.getText().toString().length() > 15) {
            usernameError.setError(getResources().getString(R.string.error_invalid_username));
            isUsernameValid = false;
        } else {
            isUsernameValid = true;
            usernameError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            passError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (isEmailValid && isPasswordValid) {
            progressDialog.show();
            registerUser();
        }
    }

    private void registerUser() {
        final String userEmail = email.getText().toString().trim();
        final String userUsername = username.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "New user registration: " + task.getException());

                        User user = new User(userUsername, userEmail);
                        String key = mAuth.getUid();

                        myRef.child(key).setValue(user);

                        if (!task.isSuccessful()) {
                            RegisterActivity.this.showToast("Authentication failed. " + task.getException());
                        } else {
                            progressDialog.dismiss();
                            RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            RegisterActivity.this.finish();
                        }
                    }
                });
    }

    public void showToast(String toastText) {
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }
}

