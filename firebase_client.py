import os
import firebase_admin 
from firebase_admin import credentials, firestore
from datetime import datetime
import uuid

# Path to the service account key
SERVICE_ACCOUNT_FILE = "firebase-adminsdk.json"

db = None
mock_db = {
    "Users": {},
    "Roadmaps": [],
    "Tasks": {}
}

def init_firebase():
    global db
    if not os.path.exists(SERVICE_ACCOUNT_FILE):
        print(f"WARNING: {SERVICE_ACCOUNT_FILE} not found. Running in MOCK MODE.")
        return

    try:
        if not firebase_admin._apps:
            cred = credentials.Certificate(SERVICE_ACCOUNT_FILE)
            firebase_admin.initialize_app(cred)
        db = firestore.client()
        # Test connection
        db.collection("Test").document("test").get()
        print("Firebase initialized successfully.")
    except Exception as e:
        db = None
        print(f"Firebase initialization error: {e}. Falling back to MOCK MODE.")

def save_user(user_data: dict):
    user_data["createdAt"] = datetime.utcnow().isoformat()
    
    if db is None:
        print("Running in MOCK MODE - Saving user locally...")
        # Use email as key for easy lookup in mock_db
        mock_db["Users"][user_data["email"]] = user_data
        return user_data

    try:
        print(f"Saving user {user_data.get('email')} to Firestore...")
        db.collection("Users").document(user_data["userId"]).set(user_data)
        print("User saved successfully in Firestore ✅")
        return user_data
    except Exception as e:
        print("❌ Firestore Error (save_user):", e)
        print("Falling back to MOCK MODE...")
        mock_db["Users"][user_data["email"]] = user_data
        return user_data

def get_user_by_email(email: str):
    if not db:
        return mock_db["Users"].get(email)
    
    try:
        users = db.collection("Users").where("email", "==", email).stream()
        for user in users:
            return user.to_dict()
    except Exception as e:
        print("Firestore Error (get_user_by_email):", e)
        return mock_db["Users"].get(email)
    return None

def save_roadmap(user_id: str, skill: str, roadmap: dict):
    data = {
        "userId": user_id,
        "skill": skill,
        "roadmap": roadmap,
        "createdAt": datetime.utcnow().isoformat()
    }
    if not db:
        mock_db["Roadmaps"].append(data)
        return
    
    try:
        db.collection("Roadmaps").add(data)
    except Exception as e:
        print("Firestore Error (save_roadmap):", e)
        mock_db["Roadmaps"].append(data)

def save_task(task_data: dict):
    task_data["createdAt"] = datetime.utcnow().isoformat()
    if not db:
        mock_db["Tasks"][task_data["taskId"]] = task_data
        return
    
    try:
        doc_ref = db.collection("Tasks").document(task_data["taskId"])
        doc_ref.set(task_data)
    except Exception as e:
        print("Firestore Error (save_task):", e)
        mock_db["Tasks"][task_data["taskId"]] = task_data

def update_task_evaluation(task_id: str, answer: str, score: int, feedback: str):
    update_data = {
        "answer": answer,
        "score": score,
        "feedback": feedback,
        "status": "completed"
    }
    
    if not db:
        if task_id in mock_db["Tasks"]:
            mock_db["Tasks"][task_id].update(update_data)
        return
    
    try:
        doc_ref = db.collection("Tasks").document(task_id)
        doc_ref.update(update_data)
    except Exception as e:
        print("Firestore Error (update_task):", e)
        if task_id in mock_db["Tasks"]:
            mock_db["Tasks"][task_id].update(update_data)

def get_user_progress(user_id: str):
    if not db:
        completed_tasks = [t for t in mock_db["Tasks"].values() if t.get("userId") == user_id and t.get("status") == "completed"]
        tasks_count = len(completed_tasks)
        total_score = sum(t.get("score", 0) for t in completed_tasks)
        accuracy = (total_score / (tasks_count * 10)) * 100 if tasks_count > 0 else 0
        return {
            "tasksCompleted": tasks_count,
            "accuracy": round(accuracy, 2),
            "streak": 1
        }
    
    try:
        tasks = db.collection("Tasks").where("userId", "==", user_id).where("status", "==", "completed").stream()
        total_score = 0
        tasks_count = 0
        for t in tasks:
            task_data = t.to_dict()
            total_score += task_data.get("score", 0)
            tasks_count += 1
        
        accuracy = (total_score / (tasks_count * 10)) * 100 if tasks_count > 0 else 0
        return {
            "tasksCompleted": tasks_count,
            "accuracy": round(accuracy, 2),
            "streak": 1
        }
    except Exception as e:
        print("Firestore Error (get_progress):", e)
        return {"tasksCompleted": 0, "accuracy": 0, "streak": 0}
