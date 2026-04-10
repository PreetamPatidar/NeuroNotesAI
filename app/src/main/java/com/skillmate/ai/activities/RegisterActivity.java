package com.skillmate.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.skillmate.ai.R;
import com.skillmate.ai.models.User;
import com.skillmate.ai.network.ApiClient;
import com.skillmate.ai.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister, btnGoToLogin;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = ApiClient.getClient().create(ApiService.class);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> registerUser());
        btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        User user = new User();
        user.name = "Learner"; // Default to bypass backend requirements
        user.selectedSkill = ""; // Bypasses FastAPI 422 Strict Field Validation
        user.level = ""; // Bypasses FastAPI 422 Strict Field Validation
        user.userId = ""; // Bypasses 422
        user.createdAt = ""; // Bypasses 422
        user.email = email;
        user.password = password;

        apiService.register(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    navigateToNextScreen(response.body().userId);
                } else {
                    try {
                        String errMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown Error";
                        Toast.makeText(RegisterActivity.this, "Error " + response.code() + ": " + errMsg, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Registration Failed Code: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToNextScreen(String userId) {
        Intent intent = new Intent(RegisterActivity.this, SkillSelectionActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
        finish(); // Remove from backstack
    }
}
