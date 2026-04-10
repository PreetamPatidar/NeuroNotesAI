package com.skillmate.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.skillmate.ai.R;

public class FeedbackActivity extends AppCompatActivity {

    private TextView tvScore, tvFeedback;
    private MaterialButton btnDashboard;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        userId = getIntent().getStringExtra("user_id");
        int score = getIntent().getIntExtra("score", 0);
        String feedback = getIntent().getStringExtra("feedback");

        tvScore = findViewById(R.id.tvScore);
        tvFeedback = findViewById(R.id.tvFeedback);
        btnDashboard = findViewById(R.id.btnDashboard);

        tvScore.setText(score + "/10");
        tvFeedback.setText(feedback != null ? feedback : "No feedback provided.");

        btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(FeedbackActivity.this, ProgressDashboardActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            finish();
        });
    }
}
