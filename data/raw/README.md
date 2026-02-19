# Required Data Files

The backend application needs these CSV files to populate the H2 database with context data (crime, lighting, construction, health services, cycling network).

## Download from Google Drive

All required data files are available in the **Group Google Drive**. Download them and place them in this `data/raw/` directory.

### Required Files

1. **Major_Crime_Indicators_Open_Data_-3805566126367379926.csv**
   - Crime incident data from Toronto Police Open Data
   - ~464,000 records
   - Used for: crime score calculation in route planning

2. **Poles - 4326.csv**
   - Street light pole locations from Toronto Open Data
   - ~305,000 records (filtered to street lights only)
   - Used for: determining if streets are lit

3. **Road Reconstruction Program - 4326.csv**
   - Active road construction projects from Toronto Open Data
   - ~330 records
   - Used for: avoiding construction zones in routes

4. **Health Services - 4326.csv**
   - Hospital and clinic locations from Toronto Open Data
   - ~266 records
   - Used for: finding nearest hospital for route preferences

5. **cycling-network - 4326.csv**
   - Cycling infrastructure segments from Toronto Open Data
   - ~1,500 records
   - Used for: identifying bike-friendly routes

## Setup Instructions

1. **Download all 5 CSV files** from the Group Google Drive
2. **Place them in this directory** (`data/raw/`)
3. **Run the Spring Boot application**:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

4. **On first run**, the `ContextDataLoader` will automatically:
   - Create the H2 database schema
   - Parse all CSV files
   - Load data into the database (takes a few minutes)
   - Save the database to `./data/navigation_db.mv.db`

5. **Subsequent runs** will skip data loading (database already exists)

## Notes

- The CSV files are **not committed to git** (too large, see `.gitignore`)
- Each developer needs to download the files from Google Drive
- The database is created locally on each machine
- If you delete `backend/data/navigation_db.mv.db`, the loader will rebuild it on next run

## Troubleshooting

**"File not found" error?**
- Make sure all 5 CSV files are in `data/raw/` (check spelling/capitalization)
- The app looks for files relative to the project root when running

**Data not loading?**
- Check the console output for error messages
- Verify CSV files aren't corrupted (try opening one in Excel/text editor)
- Delete `backend/data/navigation_db.mv.db` to force a fresh load
