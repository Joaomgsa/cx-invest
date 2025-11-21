#!/bin/sh
set -e
DB_DIR=/data
DB_FILE=$DB_DIR/quarkus.db
IMPORT_SQL=/data/imports.sql

mkdir -p "$DB_DIR"
# ensure DB file exists
if [ ! -f "$DB_FILE" ]; then
  touch "$DB_FILE"
fi

# Function to check if DB has any user tables
has_tables() {
  # Query sqlite_master for user tables (excluding sqlite_sequence)
  TABLES=$(sqlite3 "$DB_FILE" "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' LIMIT 1;")
  if [ -n "$TABLES" ]; then
    return 0
  fi
  return 1
}

if [ -f "$IMPORT_SQL" ] && [ -s "$IMPORT_SQL" ]; then
  if has_tables; then
    echo "Database already contains tables; skipping import."
  else
    echo "Running import SQL: $IMPORT_SQL -> $DB_FILE"
    sqlite3 "$DB_FILE" < "$IMPORT_SQL" || true
  fi
else
  echo "No import SQL found at $IMPORT_SQL; skipping import."
fi

# start the app
exec java $JAVA_OPTS -jar /app/app.jar

