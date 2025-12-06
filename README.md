# BotWithIATest

Lightweight Spring Boot playground for testing trading strategy components, risk controls, and integrations.

## Running locally with H2

You can run the service without any external dependencies using the embedded H2 database. Two options are available:

1. **Shell script (requires JDK + Maven locally):**
   ```bash
   ./scripts/run-local.sh
   ```
   Environment variables (with defaults) you can override:
   - `SPRING_DATASOURCE_URL` (default `jdbc:h2:mem:tradingdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL`)
   - `SPRING_DATASOURCE_USERNAME` (default `sa`)
   - `SPRING_DATASOURCE_PASSWORD` (default empty)
   - `TRADING_RISK_PERCENT` (default `0.01` → 1% of equity per trade)
   - `TRADING_RISK_MAX_PERCENT` (default `0.02` → hard stop at 2% of equity)
   - `TRADING_RISK_MIN_STOP_LOSS_PIPS` (default `1`)

2. **Docker Compose (no local Java toolchain required):**
   ```bash
   docker compose up --build
   ```
   Compose mounts the repo into a Maven 21 image and runs `./mvnw spring-boot:run`. Use the same environment variables as above to tune DB and risk parameters.

The application listens on port `8080` by default and exposes `/health` for a simple readiness check.

## Tests

Run the full unit test suite with:

```bash
./mvnw test
```

## Risk and connectivity safeguards

Incoming trade requests are validated for missing symbols/trends and non-positive risk numbers. Trades are rejected when:
- MT5 bridge reports it is disconnected.
- Requested position would breach configured risk caps.
