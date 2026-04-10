package com.skillmate.ai.network;

import com.skillmate.ai.models.EvaluationResponse;
import com.skillmate.ai.models.ProgressResponse;
import com.skillmate.ai.models.RoadmapResponse;
import com.skillmate.ai.models.TaskItem;
import com.skillmate.ai.models.User;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("register")
    Call<User> register(@Body User user);

    @POST("login")
    Call<User> login(@Body User user);

    @POST("generate-roadmap")
    Call<RoadmapResponse> generateRoadmap(@Body RoadmapRequest request);

    @POST("generate-tasks")
    Call<TaskItem> generateTask(@Body TaskRequest request);

    @POST("evaluate-answer")
    Call<EvaluationResponse> evaluateAnswer(@Body EvaluationRequest request);

    @GET("progress/{userId}")
    Call<ProgressResponse> getProgress(@Path("userId") String userId);

    // Request Models
    class RoadmapRequest {
        public String skill;
        public RoadmapRequest(String skill) { this.skill = skill; }
    }

    class TaskRequest {
        public String skill;
        public String topic;
        public TaskRequest(String skill, String topic) {
            this.skill = skill;
            this.topic = topic;
        }
    }

    class EvaluationRequest {
        public String taskId;
        public String answer;
        public EvaluationRequest(String taskId, String answer) {
            this.taskId = taskId;
            this.answer = answer;
        }
    }
}
