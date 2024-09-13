from fastapi import APIRouter
from models import ProblemsResponse, Problem, ProblemOption, ProblemLevel, ProblemType
from repositories import save_problem


router = APIRouter(prefix="/api/recommend", tags=["recommend"])

# 예시 데이터
example_problem = Problem(
    problemId=1,
    title="Sample Problem 1",
    content="This is a sample problem content.",
    problemOptions=[
        ProblemOption(problemOptionId=1, option="Option A"),
        ProblemOption(problemOptionId=2, option="Option B"),
        ProblemOption(problemOptionId=3, option="Option C"),
        ProblemOption(problemOptionId=4, option="Option D")
    ],
    solution="Sample Solution",
    problemLevel=ProblemLevel(problemLevelCode="L1", name="Level 1"),
    problemType=ProblemType(
        problemTypeCode="T1",
        problemTypeDetailCode="T1-D1",
        name="Type 1"
    )
)

dummy_data = {
        "problems": [
            {
                "problemId": 1,
                "title": "Sample Problem 1",
                "content": "This is a sample problem content.",
                "problemOptions": [
                    {"problemOptionId": 1, "option": "Option A"},
                    {"problemOptionId": 2, "option": "Option B"},
                    {"problemOptionId": 3, "option": "Option C"},
                    {"problemOptionId": 4, "option": "Option D"}
                ],
                "solution": "Sample Solution",
                "problemLevel": {"problemLevelCode": "L1", "name": "Level 1"},
                "problemType": {
                    "problemTypeCode": "T1",
                    "problemTypeDetailCode": "T1-D1",
                    "name": "Type 1"
                }
            },
            {
                "problemId": 2,
                "title": "Sample Problem 2",
                "content": "This is another sample problem content.",
                "problemOptions": [
                    {"problemOptionId": 5, "option": "Option A"},
                    {"problemOptionId": 6, "option": "Option B"},
                    {"problemOptionId": 7, "option": "Option C"},
                    {"problemOptionId": 8, "option": "Option D"}
                ],
                "solution": "Another Sample Solution",
                "problemLevel": {"problemLevelCode": "L2", "name": "Level 2"},
                "problemType": {
                    "problemTypeCode": "T2",
                    "problemTypeDetailCode": "T2-D1",
                    "name": "Type 2"
                }
            }
        ]
    }

@router.get("/test/level", response_model=ProblemsResponse)
async def get_level_test():
    # 더미 데이터 생성
    return dummy_data

@router.get("/test/general", response_model=ProblemsResponse)
async def get_level_test():
    # 더미 데이터 생성
    return dummy_data

@router.get("/type", response_model=ProblemsResponse)
async def get_level_test():
    # 더미 데이터 생성
    return dummy_data

@router.get("/similar", response_model=ProblemsResponse)
async def get_level_test():
    # 더미 데이터 생성
    return dummy_data

@router.post("/make")
async def make_problem():
    save_problem(example_problem)