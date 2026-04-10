package com.skillmate.ai.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.skillmate.ai.R;
import com.skillmate.ai.models.ProgressResponse;
import com.skillmate.ai.network.ApiClient;
import com.skillmate.ai.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressDashboardActivity extends AppCompatActivity {

    private TextView tvTasksCompleted, tvAccuracy, tvStreak;
    private ProgressBar progressBar;
    private ApiService apiService;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_dashboard);

        userId = getIntent().getStringExtra("user_id");
        apiService = ApiClient.getClient().create(ApiService.class);

        tvTasksCompleted = findViewById(R.id.tvTasksCompleted);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        tvStreak = findViewById(R.id.tvStreak);
        progressBar = findViewById(R.id.progressBar);

        fetchProgressData();
    }

    private void fetchProgressData() {
        if (userId == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        apiService.getProgress(userId).enqueue(new Callback<ProgressResponse>() {
            @Override
            public void onResponse(Call<ProgressResponse> call, Response<ProgressResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ProgressResponse progress = response.body();
                    tvTasksCompleted.setText(String.valueOf(progress.tasksCompleted));
                    tvAccuracy.setText(progress.accuracy + "%");
                    tvStreak.setText(progress.streak + "🔥");
                } else {
                    Toast.makeText(ProgressDashboardActivity.this, "Failed to load progress", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProgressResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProgressDashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
