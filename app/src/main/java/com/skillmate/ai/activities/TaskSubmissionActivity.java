package com.skillmate.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.skillmate.ai.R;
import com.skillmate.ai.models.EvaluationResponse;
import com.skillmate.ai.network.ApiClient;
import com.skillmate.ai.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskSubmissionActivity extends AppCompatActivity {

    private TextView tvTaskDescription;
    private TextInputEditText etAnswer;
    private MaterialButton btnSubmit;
    private ProgressBar progressBar;
    private ApiService apiService;
    private String userId, taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_submission);

        userId = getIntent().getStringExtra("user_id");
        taskId = getIntent().getStringExtra("task_id");
        String taskDesc = getIntent().getStringExtra("task_desc");

        apiService = ApiClient.getClient().create(ApiService.class);

        tvTaskDescription = findViewById(R.id.tvTaskDescription);
        etAnswer = findViewById(R.id.etAnswer);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        tvTaskDescription.setText(taskDesc);

        btnSubmit.setOnClickListener(v -> submitTask());
    }

    private void submitTask() {
        String answer = etAnswer.getText().toString();
        if (answer.isEmpty()) {
            Toast.makeText(this, "Empty answer!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiService.EvaluationRequest request = new ApiService.EvaluationRequest(taskId, answer);

        apiService.evaluateAnswer(request).enqueue(new Callback<EvaluationResponse>() {
            @Override
            public void onResponse(Call<EvaluationResponse> call, Response<EvaluationResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(TaskSubmissionActivity.this, FeedbackActivity.class);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("score", response.body().score);
                    intent.putExtra("feedback", response.body().feedback);
                    startActivity(intent);
                    finish(); // Don't return here
                } else {
                    Toast.makeText(TaskSubmissionActivity.this, "Evaluation Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EvaluationResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TaskSubmissionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
