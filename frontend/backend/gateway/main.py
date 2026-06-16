from fastapi import FastAPI, APIRouter, Header, Query
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
import httpx
import os
from schema import SigninSchema, SignupSchema

app = FastAPI(title="Appointment Booking API Gateway")

# Allow requests from Vite Dev Server (port 5173) and any other client
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

router = APIRouter()

# Read backend URLs from environment variables (defaults for local execution)
SPRING_BOOT_URL = os.getenv("SPRING_BOOT_URL", "http://localhost:8001")
NODE_SERVICE_URL = os.getenv("NODE_SERVICE_URL", "http://localhost:8002")

# Timeout for proxy requests (connect: 5s, read: 30s)
PROXY_TIMEOUT = httpx.Timeout(30.0, connect=5.0)

async def safe_request(method, url, **kwargs):
    """Wrapper that handles connection errors and non-JSON responses gracefully."""
    try:
        async with httpx.AsyncClient(timeout=PROXY_TIMEOUT) as client:
            response = await client.request(method, url, **kwargs)
        try:
            return response.json()
        except Exception:
            return JSONResponse(
                status_code=response.status_code,
                content={"code": response.status_code, "message": response.text}
            )
    except httpx.ConnectError:
        return JSONResponse(status_code=503, content={"code": 503, "message": f"Service unavailable: {url}"})
    except httpx.TimeoutException:
        return JSONResponse(status_code=504, content={"code": 504, "message": f"Service timeout: {url}"})
    except Exception as e:
        return JSONResponse(status_code=500, content={"code": 500, "message": str(e)})

@app.get("/")
def welcome():
    return {"message": "Welcome to the API Gateway!", "status": "running"}

@app.get("/test")
def test():
    return {"message": "Gateway is online."}

# ----------------- Auth Service Routing (Spring Boot) -----------------

@router.post("/authservice/signin")
async def signin(user: SigninSchema):
    return await safe_request("POST", f"{SPRING_BOOT_URL}/authservice/signin", json=user.model_dump())

@router.post("/authservice/signup")
async def signup(user: SignupSchema):
    return await safe_request("POST", f"{SPRING_BOOT_URL}/authservice/signup", json=user.model_dump())

@router.get("/authservice/uinfo")
async def uinfo(Token: str = Header(...)):
    return await safe_request("GET", f"{SPRING_BOOT_URL}/authservice/uinfo", headers={"Token": Token})

@router.get("/authservice/rbac")
async def rbac(Token: str = Header(...)):
    # Map frontend rbac call to uinfo
    return await safe_request("GET", f"{SPRING_BOOT_URL}/authservice/uinfo", headers={"Token": Token})

@router.get("/authservice/profile")
async def profile(Token: str = Header(...)):
    return await safe_request("GET", f"{SPRING_BOOT_URL}/authservice/profile", headers={"Token": Token})

@router.get("/authservice/getallusers/{page}/{limit}")
async def get_all_users(page: int, limit: int, Token: str = Header(...)):
    return await safe_request("GET", f"{SPRING_BOOT_URL}/authservice/getallusers/{page}/{limit}", headers={"Token": Token})

@router.get("/authservice/getuser/{id}")
async def get_user(id: str, Token: str = Header(...)):
    return await safe_request("GET", f"{SPRING_BOOT_URL}/authservice/getuser/{id}", headers={"Token": Token})

@router.post("/authservice/saveuser")
async def save_user(userData: dict, Token: str = Header(...)):
    return await safe_request("POST", f"{SPRING_BOOT_URL}/authservice/saveuser", json=userData, headers={"Token": Token})

@router.put("/authservice/updateuser/{id}")
async def update_user(id: str, userData: dict, Token: str = Header(...)):
    return await safe_request("PUT", f"{SPRING_BOOT_URL}/authservice/updateuser/{id}", json=userData, headers={"Token": Token})

@router.delete("/authservice/deleteuser/{id}")
async def delete_user(id: str, Token: str = Header(...)):
    return await safe_request("DELETE", f"{SPRING_BOOT_URL}/authservice/deleteuser/{id}", headers={"Token": Token})

@router.get("/authservice/searchuser/{val}")
async def search_user(val: str, Token: str = Header(...)):
    return await safe_request("GET", f"{SPRING_BOOT_URL}/authservice/searchuser/{val}", headers={"Token": Token})

@router.post("/authservice/preferences")
async def save_preference(prefData: dict, Token: str = Header(...)):
    return await safe_request("POST", f"{SPRING_BOOT_URL}/authservice/preferences", json=prefData, headers={"Token": Token})

@router.get("/authservice/preferences")
async def get_preference(Token: str = Header(...)):
    return await safe_request("GET", f"{SPRING_BOOT_URL}/authservice/preferences", headers={"Token": Token})

# ----------------- Time Slots Routing (Spring Boot) -----------------

@router.post("/slotsservice/createslot")
async def create_slot(slotData: dict, Token: str = Header(...)):
    return await safe_request("POST", f"{SPRING_BOOT_URL}/slotsservice/createslot", json=slotData, headers={"Token": Token})

@router.get("/slotsservice/getproviderslots")
async def get_provider_slots(Token: str = Header(...)):
    return await safe_request("GET", f"{SPRING_BOOT_URL}/slotsservice/getproviderslots", headers={"Token": Token})

@router.get("/slotsservice/getavailableslots")
async def get_available_slots(Token: str = Header(...)):
    return await safe_request("GET", f"{SPRING_BOOT_URL}/slotsservice/getavailableslots", headers={"Token": Token})

@router.delete("/slotsservice/deleteslot/{id}")
async def delete_slot(id: str, Token: str = Header(...)):
    return await safe_request("DELETE", f"{SPRING_BOOT_URL}/slotsservice/deleteslot/{id}", headers={"Token": Token})

# ----------------- Booking Service Routing (Spring Boot) -----------------

@router.post("/bookingservice/createappointment")
async def create_appointment(appData: dict, Token: str = Header(...)):
    return await safe_request("POST", f"{SPRING_BOOT_URL}/bookingservice/createappointment", json=appData, headers={"Token": Token})

@router.get("/bookingservice/getclienthistory")
async def get_client_history(Token: str = Header(...)):
    return await safe_request("GET", f"{SPRING_BOOT_URL}/bookingservice/getclienthistory", headers={"Token": Token})

@router.get("/bookingservice/getproviderschedule")
async def get_provider_schedule(Token: str = Header(...)):
    return await safe_request("GET", f"{SPRING_BOOT_URL}/bookingservice/getproviderschedule", headers={"Token": Token})

@router.delete("/bookingservice/cancelappointment/{id}")
async def cancel_appointment(id: str, Token: str = Header(...)):
    return await safe_request("DELETE", f"{SPRING_BOOT_URL}/bookingservice/cancelappointment/{id}", headers={"Token": Token})

@router.get("/bookingservice/suggestions")
async def get_suggestions(Token: str = Header(...)):
    return await safe_request("GET", f"{SPRING_BOOT_URL}/bookingservice/suggestions", headers={"Token": Token})

# ----------------- Node.js MongoDB Layer Routing (Node.js) -----------------

@router.post("/node/logs")
async def create_log(logData: dict):
    return await safe_request("POST", f"{NODE_SERVICE_URL}/node/logs", json=logData)

@router.get("/node/logs")
async def get_logs():
    return await safe_request("GET", f"{NODE_SERVICE_URL}/node/logs")

@router.get("/node/preferences/{userId}")
async def get_preferences(userId: int):
    return await safe_request("GET", f"{NODE_SERVICE_URL}/node/preferences/{userId}")

@router.put("/node/preferences/{userId}")
async def update_preferences(userId: int, prefData: dict):
    return await safe_request("PUT", f"{NODE_SERVICE_URL}/node/preferences/{userId}", json=prefData)

@router.post("/node/providers")
async def create_provider_embedding(provData: dict):
    return await safe_request("POST", f"{NODE_SERVICE_URL}/node/providers", json=provData)

@router.get("/node/providers/search")
async def search_providers(q: str = Query("")):
    return await safe_request("GET", f"{NODE_SERVICE_URL}/node/providers/search", params={"q": q})

app.include_router(router)