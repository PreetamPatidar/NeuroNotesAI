import os
import google.generativeai as genai
from dotenv import load_dotenv

load_dotenv("env/.env")
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")

if not GEMINI_API_KEY or "your_gemini_api_key_here" in GEMINI_API_KEY:
    print("[ERROR]: Please add your Gemini API Key to env/.env first!")
else:
    try:
        genai.configure(api_key=GEMINI_API_KEY)
        model = genai.GenerativeModel('gemini-flash-lite-latest')
        response = model.generate_content("Say 'Gemini is working!'")
        print(f"[SUCCESS]: {response.text.strip()}")
    except Exception as e:
        print(f"[ERROR]: {e}")
