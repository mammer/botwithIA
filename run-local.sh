#!/usr/bin/env bash
set -euo pipefail

export DB_URL=${DB_URL:-jdbc:h2:mem:tradingbot;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE}
export DB_USERNAME=${DB_USERNAME:-sa}
export DB_PASSWORD=${DB_PASSWORD:-}

./mvnw spring-boot:run
