package com.skillmate.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.skillmate.ai.R;
import com.skillmate.ai.models.RoadmapResponse;
import com.skillmate.ai.network.ApiClient;
import com.skillmate.ai.network.ApiService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SkillSelectionActivity extends AppCompatActivity {

    private Spinner spinnerSkills;
    private MaterialButton btnGenerateRoadmap;
    private ProgressBar progressBar;
    private ApiService apiService;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_selection);

        userId = getIntent().getStringExtra("user_id");
        apiService = ApiClient.getClient().create(ApiService.class);

        spinnerSkills = findViewById(R.id.spinnerSkills);
        btnGenerateRoadmap = findViewById(R.id.btnGenerateRoadmap);
        progressBar = findViewById(R.id.progressBar);

        String[] skills = new String[]{"Python", "Android", "Data Structures", "English"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, skills);
        spinnerSkills.setAdapter(adapter);

        btnGenerateRoadmap.setOnClickListener(v -> generateRoadmap());
    }

    private void generateRoadmap() {
        String skill = spinnerSkills.getSelectedItem().toString();
        progressBar.setVisibility(View.VISIBLE);

        ApiService.RoadmapRequest request = new ApiService.RoadmapRequest(skill);
        apiService.generateRoadmap(request).enqueue(new Callback<RoadmapResponse>() {
            @Override
            public void onResponse(Call<RoadmapResponse> call, Response<RoadmapResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(SkillSelectionActivity.this, RoadmapActivity.class);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("skill", skill);
                    intent.putStringArrayListExtra("beginner", new ArrayList<>(response.body().beginner));
                    intent.putStringArrayListExtra("intermediate", new ArrayList<>(response.body().intermediate));
                    intent.putStringArrayListExtra("advanced", new ArrayList<>(response.body().advanced));
                    startActivity(intent);
                } else {
                    Toast.makeText(SkillSelectionActivity.this, "Failed to generate roadmap", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RoadmapResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SkillSelectionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
