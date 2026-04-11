import os
import json
import google.generativeai as genai
from openai import OpenAI
from dotenv import load_dotenv

load_dotenv("env/.env", override=True)

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")

# Initialization
gemini_model = None
openai_client = None

if GEMINI_API_KEY and GEMINI_API_KEY != "AIzaSyCRZhU7b_rSAjpFsHMpYgSS05vNsG7KVbg":
    try:
        genai.configure(api_key=GEMINI_API_KEY)
        gemini_model = genai.GenerativeModel('gemini-flash-lite-latest')
        print("Gemini AI initialized successfully.")
    except Exception as e:
        print(f"Gemini initialization error: {e}")

if OPENAI_API_KEY:
    try:
        openai_client = OpenAI(api_key=OPENAI_API_KEY)
        print("OpenAI client initialized.")
    except Exception as e:
        print(f"OpenAI initialization error: {e}")

def generate_roadmap(skill: str) -> dict:
    query = skill.replace(' ', '+')
    prompt = (
        f"Generate a beginner to advanced learning roadmap for {skill}. "
        "Return ONLY a JSON object with keys: 'beginner', 'intermediate', 'advanced', 'links'. "
        "Each roadmap key must contain a list of strings. 'links' should contain 3 useful study resources or learning links for this topic."
    )
    
    # Try Gemini First
    if gemini_model:
        try:
            response = gemini_model.generate_content(prompt)
            text = response.text.strip()
            if "```json" in text:
                text = text.split("```json")[1].split("```")[0].strip()
            elif "```" in text:
                text = text.split("```")[1].split("```")[0].strip()
            return json.loads(text)
        except Exception as e:
            print(f"Gemini Error (Roadmap): {e}")

    # Try OpenAI Fallback
    if openai_client:
        try:
            response = openai_client.chat.completions.create(
                model="gpt-4o-mini",
                messages=[
                    {"role": "system", "content": "You are an expert AI Learning Coach. Output valid JSON only."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.7
            )
            return json.loads(response.choices[0].message.content)
        except Exception as e:
            print(f"OpenAI Error (Roadmap): {e}")

    # Final Fallback
    return {
        "beginner": ["Basics of " + skill, "Setup your environment", "Learn the core syntax"],
        "intermediate": ["Work through intermediate projects", "Understand common patterns", "Practice problem solving"],
        "advanced": ["Build a capstone project", "Optimize performance", "Learn best practices"],
        "links": [
            f"https://www.google.com/search?q={query}+tutorial",
            f"https://www.youtube.com/results?search_query={query}+course",
            f"https://www.freecodecamp.org/search?q={query}"
        ]
    }

def generate_task(skill: str, topic: str) -> str:
    prompt = f"Create a small programming practice task for the topic '{topic}' in the '{skill}' language. Be concise."
    
    if gemini_model:
        try:
            response = gemini_model.generate_content(prompt)
            return response.text.strip()
        except Exception as e:
            print(f"Gemini Error (Task): {e}")

    if openai_client:
        try:
            response = openai_client.chat.completions.create(
                model="gpt-4o-mini",
                messages=[{"role": "user", "content": prompt}],
                temperature=0.7
            )
            return response.choices[0].message.content.strip()
        except Exception as e:
            print(f"OpenAI Error (Task): {e}")

    return f"Practice task for {topic} in {skill}: Implement a basic solution illustrating the concept."

def evaluate_answer(answer: str) -> dict:
    prompt = f"Evaluate this student's programming answer and provide score out of 10 with feedback. \nAnswer: {answer}\nReturn MUST be ONLY a JSON object with exactly these keys: {{'score': <integer>, 'feedback': '<string>'}}"
    
    if gemini_model:
        try:
            response = gemini_model.generate_content(prompt)
            text = response.text.strip()
            if "```json" in text: text = text.split("```json")[1].split("```")[0].strip()
            return json.loads(text)
        except Exception as e:
            print(f"Gemini Error (Eval): {e}")

    if openai_client:
        try:
            response = openai_client.chat.completions.create(
                model="gpt-4o-mini",
                messages=[{"role": "user", "content": prompt}],
                temperature=0.5
            )
            return json.loads(response.choices[0].message.content)
        except Exception as e:
            print(f"OpenAI Error (Eval): {e}")

    return {"score": 5, "feedback": "Evaluation service currently limited. Keep practicing!"}

def solve_doubt(question: str) -> str:
    prompt = f"Answer the following doubt simply and concisely: {question}"
    
    if gemini_model:
        try:
            response = gemini_model.generate_content(prompt)
            return response.text.strip()
        except Exception as e:
            print(f"Gemini Error (Doubt): {e}")

    if openai_client:
        try:
            response = openai_client.chat.completions.create(
                model="gpt-4o-mini",
                messages=[{"role": "user", "content": prompt}],
                temperature=0.7
            )
            return response.choices[0].message.content.strip()
        except Exception as e:
            print(f"OpenAI Error (Doubt): {e}")

    return "I am currently processing many requests. Please try again in a moment."


import re


def neuro_notes_engine(input_text: str) -> dict:
    input_text = input_text.strip()
    if not input_text or re.match(r'^[^\w\s]+$', input_text):
        return {"error": "Invalid input provided"}

    words = input_text.split()
    word_count = len(words)

    system_prompt = "You are the NeuroNotes AI Engine. Respond ONLY with valid JSON matching the requested structure. Do not mention being an AI."

    if word_count <= 3:
        title = words[0] if words else "Unknown"
        prompt = (
            f"Provide dictionary info for '{input_text}': "
            f"Return ONLY JSON: {{'title': '{title}', 'content': 'clear definition', 'details': ['phonetic pronunciation (IPA text)', 'usage example sentence']}}"
        )
        type_ = "dictionary"
    else:
        title = input_text[:50] + "..." if len(input_text) > 50 else input_text
        prompt = (
            f"Summarize notes '{input_text}' concisely. "
            f"Return ONLY JSON: {{'title': '{title}', 'content': '1-sentence overview', 'details': ['bullet key concept 1', 'bullet key concept 2', 'bullet key concept 3']}}"
        )
        type_ = "summary"

    # Try Gemini first
    if gemini_model:
        try:
            response = gemini_model.generate_content([
                system_prompt,
                prompt
            ])
            text = response.text.strip()
            if "```json" in text:
                text = text.split("```json")[1].split("```")[0].strip()
            parsed = json.loads(text)
            return {"type": type_, "result": parsed}
        except Exception as e:
            print(f"Gemini NeuroNotes Error: {e}")

    # OpenAI fallback
    if openai_client:
        try:
            response = openai_client.chat.completions.create(
                model="gpt-4o-mini",
                messages=[
                    {"role": "system", "content": system_prompt},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.1
            )
            text = response.choices[0].message.content.strip()
            parsed = json.loads(text)
            return {"type": type_, "result": parsed}
        except Exception as e:
            print(f"OpenAI NeuroNotes Error: {e}")

    # Fallback
    if word_count <= 3:
        return {
            "type": "dictionary",
            "result": {
                "title": title,
                "content": "Definition not available.",
                "details": ["/fɔːlˈbæk/", "Example: This is a fallback."]
            }
        }
    else:
        return {
            "type": "summary",
            "result": {
                "title": title,
                "content": "Summary unavailable.",
                "details": ["Key concept 1", "Key concept 2"]
            }
        }

