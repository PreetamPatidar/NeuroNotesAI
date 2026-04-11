from pydantic import BaseModel, Field
from typing import List, Dict, Optional, Literal
from datetime import datetime

class UserRegisterReq(BaseModel):
    userId: str
    name: str
    email: str
    password: str
    selectedSkill: Optional[str] = None
    level: str = "beginner"

class UserLoginReq(BaseModel):
    email: str
    password: str

class RoadmapReq(BaseModel):
    skill: str

class RoadmapResponse(BaseModel):
    beginner: List[str]
    intermediate: List[str]
    advanced: List[str]
    links: Optional[List[str]] = None

class TaskReq(BaseModel):
    skill: str
    topic: str

class TaskResponse(BaseModel):
    task: str
    taskId: str

class DoubtReq(BaseModel):
    question: str

class DoubtResponse(BaseModel):
    answer: str

class EvaluationReq(BaseModel):
    taskId: str
    answer: str

class EvaluationResponse(BaseModel):
    score: int
    feedback: str

class ProgressResponse(BaseModel):
    tasksCompleted: int
    accuracy: float
    streak: int

class DefineReq(BaseModel):
    word: str

class DefineResponse(BaseModel):
    definition: str


from typing import Literal


class NeuroNotesReq(BaseModel):
    input: str


class NeuroNotesResult(BaseModel):
    title: str
    content: str
    details: List[str]


class NeuroNotesResponse(BaseModel):
    type: Literal["dictionary", "summary"]
    result: NeuroNotesResult
