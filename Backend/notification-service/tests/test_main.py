import unittest
from unittest.mock import AsyncMock, patch

from main import EUREKA_SERVER, PORT, SERVICE_NAME, app, lifespan


class NotificationServiceTests(unittest.IsolatedAsyncioTestCase):
    async def test_eureka_client_starts_and_stops_with_app_lifespan(self) -> None:
        with patch("main.EurekaClient") as client_factory:
            client = client_factory.return_value
            client.start = AsyncMock()
            client.stop = AsyncMock()

            async with lifespan(app):
                client_factory.assert_called_once_with(
                    eureka_server=EUREKA_SERVER,
                    app_name=SERVICE_NAME,
                    instance_port=PORT,
                )
                client.start.assert_awaited_once()

            client.stop.assert_awaited_once()

    async def test_hello_route_is_available(self) -> None:
        route = next(
            route
            for route in app.routes
            if getattr(route, "path", None) == "/api/notifications/hello"
        )

        self.assertIn("GET", route.methods)
        self.assertEqual(
            await route.endpoint(),
            {"message": "Hello from Notification Service"},
        )


if __name__ == "__main__":
    unittest.main()
