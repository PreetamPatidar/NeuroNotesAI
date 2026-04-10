package com.skillmate.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.skillmate.ai.R;

import java.util.ArrayList;
import java.util.List;

public class RoadmapActivity extends AppCompatActivity {

    private RecyclerView rvRoadmap;
    private MaterialButton btnContinueTasks;
    private String userId, skill;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadmap);

        userId = getIntent().getStringExtra("user_id");
        skill = getIntent().getStringExtra("skill");

        ArrayList<String> beginner = getIntent().getStringArrayListExtra("beginner");
        ArrayList<String> intermediate = getIntent().getStringArrayListExtra("intermediate");
        ArrayList<String> advanced = getIntent().getStringArrayListExtra("advanced");

        rvRoadmap = findViewById(R.id.rvRoadmap);
        btnContinueTasks = findViewById(R.id.btnContinueTasks);

        rvRoadmap.setLayoutManager(new LinearLayoutManager(this));
        
        List<RoadmapItem> items = new ArrayList<>();
        if (beginner != null) {
            for (String s : beginner) items.add(new RoadmapItem("Beginner", s));
        }
        if (intermediate != null) {
            for (String s : intermediate) items.add(new RoadmapItem("Intermediate", s));
        }
        if (advanced != null) {
            for (String s : advanced) items.add(new RoadmapItem("Advanced", s));
        }

        rvRoadmap.setAdapter(new RoadmapAdapter(items));

        btnContinueTasks.setOnClickListener(v -> {
            Intent intent = new Intent(RoadmapActivity.this, DailyTasksActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("skill", skill);
            startActivity(intent);
        });
    }

    private static class RoadmapItem {
        String level, topic;
        RoadmapItem(String l, String t) { level = l; topic = t; }
    }

    private static class RoadmapAdapter extends RecyclerView.Adapter<RoadmapAdapter.ViewHolder> {
        private final List<RoadmapItem> items;

        RoadmapAdapter(List<RoadmapItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_roadmap, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvLevel.setText(items.get(position).level);
            holder.tvTopic.setText(items.get(position).topic);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvLevel, tvTopic;
            ViewHolder(View v) {
                super(v);
                tvLevel = v.findViewById(R.id.tvLevel);
                tvTopic = v.findViewById(R.id.tvTopic);
            }
        }
    }
}
