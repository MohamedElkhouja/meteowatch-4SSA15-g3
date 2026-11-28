import os
from collections.abc import AsyncIterator
from contextlib import asynccontextmanager

import uvicorn
from fastapi import FastAPI
from py_eureka_client.eureka_client import EurekaClient

SERVICE_NAME = "NOTIFICATION"
PORT = int(os.getenv("PORT", "8084"))
EUREKA_SERVER = os.getenv("EUREKA_SERVER", "http://localhost:8761/eureka")


@asynccontextmanager
async def lifespan(app: FastAPI) -> AsyncIterator[None]:
    client = EurekaClient(
        eureka_server=EUREKA_SERVER,
        app_name=SERVICE_NAME,
        instance_port=PORT,
    )
    await client.start()
    try:
        yield
    finally:
        await client.stop()


app = FastAPI(lifespan=lifespan)


@app.get("/api/notifications/hello")
async def hello() -> dict[str, str]:
    return {"message": "Hello from Notification Service"}


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=PORT)
