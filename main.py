from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from typing import Any
import uuid

import models
import firebase_client
import openai_service

# Initialize Firebase with error handling
try:
    firebase_client.init_firebase()
except Exception as e:
    print(f"Critical error during Firebase init: {e}")

app = FastAPI(title="NeuroNotes AI Backend", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.post("/register", response_model=models.UserRegisterReq)
def register(user: models.UserRegisterReq):
    try:
        user_dict = user.model_dump()
        user_dict["userId"] = str(uuid.uuid4())  
        saved = firebase_client.save_user(user_dict)
        return saved if saved else user_dict
    except Exception as e:
        print(f"Register Error: {e}")
        return user.model_dump()

@app.post("/login", response_model=models.UserRegisterReq)
def login(creds: models.UserLoginReq):
    try:
        user = firebase_client.get_user_by_email(creds.email)
        if not user or user.get("password") != creds.password:
            raise HTTPException(status_code=401, detail="Invalid credentials")
        return user
    except HTTPException:
        raise
    except Exception as e:
        print(f"Login Error: {e}")
        raise HTTPException(status_code=500, detail="Internal server error")

@app.post("/generate-roadmap", response_model=models.RoadmapResponse)
def generate_roadmap(req: models.RoadmapReq):
    try:
        roadmap_data = openai_service.generate_roadmap(req.skill)
        firebase_client.save_roadmap("user_mock", req.skill, roadmap_data)
        return roadmap_data
    except Exception as e:
        print(f"Roadmap Error: {e}")
        return {"beginner": ["Intro"], "intermediate": ["Adv Topics"], "advanced": ["Expert"]}

@app.post("/generate-tasks", response_model=models.TaskResponse)
def generate_tasks(req: models.TaskReq):
    try:
        task_desc = openai_service.generate_task(req.skill, req.topic)
        task_data = {
            "taskId": str(uuid.uuid4()),
            "userId": "user_mock",
            "skill": req.skill,
            "topic": req.topic,
            "taskDescription": task_desc,
            "status": "pending"
        }
        firebase_client.save_task(task_data)
        return {"task": task_desc, "taskId": task_data["taskId"]}
    except Exception as e:
        print(f"Task Gen Error: {e}")
        return {"task": "Practice basic coding.", "taskId": str(uuid.uuid4())}

@app.post("/ask-doubt", response_model=models.DoubtResponse)
def ask_doubt(req: models.DoubtReq):
    answer = openai_service.solve_doubt(req.question)
    return {"answer": answer}

@app.post("/evaluate-answer", response_model=models.EvaluationResponse)
def evaluate_answer(req: models.EvaluationReq):
    try:
        eval_result = openai_service.evaluate_answer(req.answer)
        firebase_client.update_task_evaluation(
            req.taskId, 
            req.answer, 
            eval_result.get("score", 0), 
            eval_result.get("feedback", "")
        )
        return eval_result
    except Exception as e:
        print(f"Evaluation Error: {e}")
        return {"score": 5, "feedback": "Evaluation service unavailable."}

@app.get("/progress/{userId}", response_model=models.ProgressResponse)
def get_progress(userId: str):
    progress = firebase_client.get_user_progress(userId)
    return progress

@app.post("/define", response_model=models.DefineResponse)
def define_word(req: models.DefineReq):
    """Dictionary lookup using OpenAI (Gemini for other endpoints)"""
    from openai_service import openai_client
    
    if not openai_client:
        raise HTTPException(status_code=503, detail="OpenAI service not available. Add OPENAI_API_KEY to env/.env")
    
    try:
        response = openai_client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "system", "content": "You are a precise dictionary AI. Provide concise, accurate definitions."},
                {"role": "user", "content": f"Define '{req.word}' in 1-2 sentences."}
            ],
            temperature=0.1
        )
        definition = response.choices[0].message.content.strip()
        return models.DefineResponse(definition=definition)
    except Exception as e:
        print(f"OpenAI Define Error: {e}")
        raise HTTPException(status_code=500, detail="Definition service error")


@app.post("/neuro-notes", response_model=models.NeuroNotesResponse)
def neuro_notes(req: models.NeuroNotesReq):
    """NeuroNotes AI Engine: Dictionary or Note Summarizer based on input length."""
    try:
        result = openai_service.neuro_notes_engine(req.input)
        if "error" in result:
            from fastapi import HTTPException
            raise HTTPException(status_code=400, detail=result["error"])
        return models.NeuroNotesResponse(**result)
    except Exception as e:
        print(f"NeuroNotes Error: {e}")
        raise HTTPException(status_code=500, detail="NeuroNotes service error")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
