# BotWithIATest

Spring Boot based trading bot playground with strategy, ML and MT5 integration components.

## Running locally

### Using the Maven wrapper

```bash
./run-local.sh
```

The script reads environment variables (with defaults) so you can tweak storage configuration without editing code:

- `DB_URL` (default `jdbc:h2:mem:tradingbot;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`)
- `DB_USERNAME` (default `sa`)
- `DB_PASSWORD` (default empty)

The application exposes the H2 console at `/h2-console` when running locally.

### Using Docker Compose

1. Build and start the containerized app with local sources mounted:

```bash
docker-compose up --build
```

2. Override configuration via environment variables if desired:

```bash
DB_URL=jdbc:h2:mem:bot;MODE=MySQL DB_USERNAME=sa DB_PASSWORD=secret docker-compose up
```

The compose file maps port `8080` for the API and `9090` for actuator endpoints.

## Validation and safety

- External inputs such as symbols, prices and stop-loss values are validated before trades are attempted.
- Trades are rejected if the MT5 bridge is disconnected or if calculated risk exceeds configured limits.

## Testing

Run the unit test suite:

```bash
./mvnw test
```
