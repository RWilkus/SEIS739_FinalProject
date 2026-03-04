# Database Setup

## Initial Setup

1. Create database:
   ```bash
   createdb skycontrol_pro
   ```

2. Run schema:
   ```bash
   psql skycontrol_pro < schema/02_create_tables.sql
   ```

3. Load sample data (optional):
   ```bash
   psql skycontrol_pro < sample-data/insert_drones.sql
   ```
