# NeuroNotes AI Backend

FastAPI server for NeuroNotes Android/Web apps.

## Quick Start

```bash
cd NeuroNotesBackend
python -m venv venv
venv\\Scripts\\activate  # Windows
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

Open http://localhost:8000/docs for API docs.

## Endpoints
- `GET /api/users/dashboard` - Returns mock dashboard stats (dayStreak, totalScore, etc.)

## Clients
- **Web**: localhost:8000
- **Android Emulator**: 10.0.2.2:8000 (auto-configured in RetrofitClient.kt)

## Customization
Edit `main.py` mock data. Add DB/auth as needed.

