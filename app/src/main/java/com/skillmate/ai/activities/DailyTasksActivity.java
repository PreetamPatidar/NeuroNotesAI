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
import com.skillmate.ai.models.TaskItem;
import com.skillmate.ai.network.ApiClient;
import com.skillmate.ai.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyTasksActivity extends AppCompatActivity {

    private TextInputEditText etTopic;
    private MaterialButton btnGenerateTask;
    private ProgressBar progressBar;
    private ApiService apiService;
    private String userId, skill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_tasks);

        userId = getIntent().getStringExtra("user_id");
        skill = getIntent().getStringExtra("skill");
        apiService = ApiClient.getClient().create(ApiService.class);

        etTopic = findViewById(R.id.etTopic);
        btnGenerateTask = findViewById(R.id.btnGenerateTask);
        progressBar = findViewById(R.id.progressBar);

        btnGenerateTask.setOnClickListener(v -> generateTask());
    }

    private void generateTask() {
        String topic = etTopic.getText().toString();
        if (topic.isEmpty()) {
            Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiService.TaskRequest request = new ApiService.TaskRequest(skill, topic);

        apiService.generateTask(request).enqueue(new Callback<TaskItem>() {
            @Override
            public void onResponse(Call<TaskItem> call, Response<TaskItem> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    TaskItem taskItem = response.body();
                    Intent intent = new Intent(DailyTasksActivity.this, TaskSubmissionActivity.class);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("task_id", taskItem.taskId);
                    intent.putExtra("task_desc", taskItem.task);
                    startActivity(intent);
                } else {
                    Toast.makeText(DailyTasksActivity.this, "Failed to generate task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskItem> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DailyTasksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
