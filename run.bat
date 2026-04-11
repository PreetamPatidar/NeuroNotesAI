@echo off
echo Starting NeuroNotes Backend...
cd /d %~dp0

if not exist venv (
  echo Creating virtual environment...
  python -m venv venv
)

echo Activating venv...
call venv\Scripts\activate.bat

echo Installing dependencies...
pip install -r requirements.txt

echo Starting FastAPI server...
uvicorn main:app --reload --host 0.0.0.0 --port 8000

pause

