# Notification Service

This FastAPI service follows the Eureka assignment requirements. It registers
with Eureka as `NOTIFICATION` and exposes `GET /api/notifications/hello`.

## Configuration

| Environment variable | Default | Description |
|---|---|---|
| `PORT` | `8084` | HTTP port and registered Eureka instance port |
| `EUREKA_SERVER` | `http://localhost:8761/eureka` | Eureka server URL |

## Run on Windows

From this directory, create and activate a virtual environment, then install
the dependencies:

```powershell
py -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

Start the service:

```powershell
python main.py
```

The service listens on `http://localhost:8084` by default. Once it starts,
`NOTIFICATION` should appear on the Eureka dashboard at `http://localhost:8761`.

To run another instance on a different port, open another terminal, activate
the same virtual environment, and run:

```powershell
$env:PORT = "8085"
python main.py
```

Override the Eureka URL with `EUREKA_SERVER` if the server is not running at
the default address.

## Test

```powershell
python -m unittest discover -s tests
```
