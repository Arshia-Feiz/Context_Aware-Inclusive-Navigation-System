package com.group2.navigation.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.navigation.model.*;
import com.group2.navigation.repository.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads context data from CSV files into the H2 database at startup.
 * Only loads data if the tables are empty (i.e., first run or after DB reset).
 * 
 * Data is persisted to disk, so this only runs once unless you delete navigation_db files.
 */
@Component
public class ContextDataLoader implements CommandLineRunner {

    @Autowired
    private CrimeIncidentRepository crimeRepo;

    @Autowired
    private StreetLightRepository lightRepo;

    @Autowired
    private ConstructionProjectRepository constructionRepo;

    @Autowired
    private HealthServiceRepository healthRepo;

    @Autowired
    private CyclingSegmentRepository cyclingRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Batch size for saving records (keeps memory usage reasonable)
    private static final int BATCH_SIZE = 1000;

    @Override
    public void run(String... args) {
        loadAllData();
    }

    /**
     * Load all datasets if they haven't been loaded yet.
     * Checks each table independently so partial loads can resume.
     */
    public void loadAllData() {
        System.out.println("=== Starting data load check ===");

        if (crimeRepo.count() == 0) {
            loadCrimeData("data/raw/Major_Crime_Indicators_Open_Data_-3805566126367379926.csv");
        } else {
            System.out.println("Crime data already loaded (" + crimeRepo.count() + " records)");
        }

        if (lightRepo.count() == 0) {
            loadStreetLightData("data/raw/Poles - 4326.csv");
        } else {
            System.out.println("Street light data already loaded (" + lightRepo.count() + " records)");
        }

        if (constructionRepo.count() == 0) {
            loadConstructionData("data/raw/Road Reconstruction Program - 4326.csv");
        } else {
            System.out.println("Construction data already loaded (" + constructionRepo.count() + " records)");
        }

        if (healthRepo.count() == 0) {
            loadHealthData("data/raw/Health Services - 4326.csv");
        } else {
            System.out.println("Health service data already loaded (" + healthRepo.count() + " records)");
        }

        if (cyclingRepo.count() == 0) {
            loadCyclingData("data/raw/cycling-network - 4326.csv");
        } else {
            System.out.println("Cycling data already loaded (" + cyclingRepo.count() + " records)");
        }

        System.out.println("=== Data load complete ===");
    }

    /**
     * Load crime data from Toronto Police Major Crime Indicators CSV.
     * 
     * Key columns: EVENT_UNIQUE_ID, OCC_YEAR, OFFENCE, CSI_CATEGORY,
     *              LAT_WGS84, LONG_WGS84, HOOD_158, NEIGHBOURHOOD_158
     */
    public void loadCrimeData(String csvPath) {
        System.out.println("Loading crime data from " + csvPath + "...");
        int loaded = 0;
        int skipped = 0;

        try (Reader reader = new FileReader(csvPath);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            List<CrimeIncident> batch = new ArrayList<>();

            for (CSVRecord record : parser) {
                try {
                    String latStr = record.get("LAT_WGS84");
                    String lngStr = record.get("LONG_WGS84");

                    // Skip records with missing coordinates
                    if (latStr == null || latStr.isEmpty() || lngStr == null || lngStr.isEmpty()) {
                        skipped++;
                        continue;
                    }

                    double lat = Double.parseDouble(latStr);
                    double lng = Double.parseDouble(lngStr);

                    // Skip invalid coordinates (0,0 means no location data)
                    if (lat == 0.0 || lng == 0.0) {
                        skipped++;
                        continue;
                    }

                    int occYear = 0;
                    try {
                        occYear = Integer.parseInt(record.get("OCC_YEAR"));
                    } catch (NumberFormatException e) {
                        // Some rows might have bad year data, skip them
                        skipped++;
                        continue;
                    }

                    CrimeIncident crime = new CrimeIncident(
                            record.get("EVENT_UNIQUE_ID"),
                            occYear,
                            record.get("OFFENCE"),
                            record.get("CSI_CATEGORY"),
                            lat,
                            lng,
                            record.get("HOOD_158"),
                            record.get("NEIGHBOURHOOD_158")
                    );

                    batch.add(crime);

                    if (batch.size() >= BATCH_SIZE) {
                        crimeRepo.saveAll(batch);
                        loaded += batch.size();
                        batch.clear();
                        if (loaded % 10000 == 0) {
                            System.out.println("  Crime data: " + loaded + " records loaded...");
                        }
                    }
                } catch (Exception e) {
                    skipped++;
                }
            }

            // Save any remaining records
            if (!batch.isEmpty()) {
                crimeRepo.saveAll(batch);
                loaded += batch.size();
            }

        } catch (Exception e) {
            System.err.println("Error loading crime data: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Crime data loaded: " + loaded + " records, " + skipped + " skipped");
    }

    /**
     * Load street light data from Toronto Poles CSV.
     * Filters for SUBTYPE_CODE = 6006 (Street Light Pole) only.
     * 
     * Key columns: SUBTYPE_CODE, geometry (MultiPoint JSON with [lng, lat])
     */
    public void loadStreetLightData(String csvPath) {
        System.out.println("Loading street light data from " + csvPath + "...");
        int loaded = 0;
        int skipped = 0;

        try (Reader reader = new FileReader(csvPath);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            List<StreetLight> batch = new ArrayList<>();

            for (CSVRecord record : parser) {
                try {
                    // Only keep street light poles (code 6006)
                    String subtypeCode = record.get("SUBTYPE_CODE");
                    if (!"6006".equals(subtypeCode)) {
                        skipped++;
                        continue;
                    }

                    String geometry = record.get("geometry");
                    double[] coords = parseMultiPointGeometry(geometry);
                    if (coords == null) {
                        skipped++;
                        continue;
                    }

                    StreetLight light = new StreetLight(coords[0], coords[1]); // lat, lng
                    batch.add(light);

                    if (batch.size() >= BATCH_SIZE) {
                        lightRepo.saveAll(batch);
                        loaded += batch.size();
                        batch.clear();
                        if (loaded % 10000 == 0) {
                            System.out.println("  Street light data: " + loaded + " records loaded...");
                        }
                    }
                } catch (Exception e) {
                    skipped++;
                }
            }

            if (!batch.isEmpty()) {
                lightRepo.saveAll(batch);
                loaded += batch.size();
            }

        } catch (Exception e) {
            System.err.println("Error loading street light data: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Street light data loaded: " + loaded + " records, " + skipped + " skipped");
    }

    /**
     * Load construction/road reconstruction data from Toronto Open Data CSV.
     * Stores the centroid of each construction project's geometry.
     * 
     * Key columns: PROJECT, LOCATION, STATUS, DURATION/ Construction Timeline, geometry
     */
    public void loadConstructionData(String csvPath) {
        System.out.println("Loading construction data from " + csvPath + "...");
        int loaded = 0;
        int skipped = 0;

        try (Reader reader = new FileReader(csvPath);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            List<ConstructionProject> batch = new ArrayList<>();

            for (CSVRecord record : parser) {
                try {
                    String geometry = record.get("geometry");
                    double[] centroid = parseMultiLineStringCentroid(geometry);
                    if (centroid == null) {
                        skipped++;
                        continue;
                    }

                    ConstructionProject project = new ConstructionProject(
                            record.get("PROJECT"),
                            record.get("LOCATION"),
                            record.get("STATUS"),
                            record.get("DURATION/ Construction Timeline"),
                            centroid[0], // lat
                            centroid[1]  // lng
                    );

                    batch.add(project);

                    if (batch.size() >= BATCH_SIZE) {
                        constructionRepo.saveAll(batch);
                        loaded += batch.size();
                        batch.clear();
                    }
                } catch (Exception e) {
                    skipped++;
                }
            }

            if (!batch.isEmpty()) {
                constructionRepo.saveAll(batch);
                loaded += batch.size();
            }

        } catch (Exception e) {
            System.err.println("Error loading construction data: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Construction data loaded: " + loaded + " records, " + skipped + " skipped");
    }

    /**
     * Load health service data from Toronto Open Data CSV.
     * Uses direct LATITUDE/LONGITUDE columns.
     * 
     * Key columns: AGENCY_NAME, ORGANIZATION_ADDRESS, LATITUDE, LONGITUDE,
     *              ACCESSIBILITY, OFFICE_PHONE
     */
    public void loadHealthData(String csvPath) {
        System.out.println("Loading health service data from " + csvPath + "...");
        int loaded = 0;
        int skipped = 0;

        try (Reader reader = new FileReader(csvPath);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            List<HealthService> batch = new ArrayList<>();

            for (CSVRecord record : parser) {
                try {
                    String latStr = record.get("LATITUDE");
                    String lngStr = record.get("LONGITUDE");

                    if (latStr == null || latStr.isEmpty() || lngStr == null || lngStr.isEmpty()) {
                        skipped++;
                        continue;
                    }

                    double lat = Double.parseDouble(latStr);
                    double lng = Double.parseDouble(lngStr);

                    if (lat == 0.0 || lng == 0.0) {
                        skipped++;
                        continue;
                    }

                    HealthService health = new HealthService(
                            record.get("AGENCY_NAME"),
                            record.get("ORGANIZATION_ADDRESS"),
                            lat,
                            lng,
                            record.get("ACCESSIBILITY"),
                            record.get("OFFICE_PHONE")
                    );

                    batch.add(health);

                    if (batch.size() >= BATCH_SIZE) {
                        healthRepo.saveAll(batch);
                        loaded += batch.size();
                        batch.clear();
                    }
                } catch (Exception e) {
                    skipped++;
                }
            }

            if (!batch.isEmpty()) {
                healthRepo.saveAll(batch);
                loaded += batch.size();
            }

        } catch (Exception e) {
            System.err.println("Error loading health service data: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Health service data loaded: " + loaded + " records, " + skipped + " skipped");
    }

    /**
     * Load cycling network data from Toronto Open Data CSV.
     * Stores the centroid of each cycling segment's geometry.
     * 
     * Key columns: STREET_NAME, INFRA_LOWORDER, SURFACE, geometry
     */
    public void loadCyclingData(String csvPath) {
        System.out.println("Loading cycling network data from " + csvPath + "...");
        int loaded = 0;
        int skipped = 0;

        try (Reader reader = new FileReader(csvPath);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            List<CyclingSegment> batch = new ArrayList<>();

            for (CSVRecord record : parser) {
                try {
                    String geometry = record.get("geometry");
                    double[] centroid = parseMultiLineStringCentroid(geometry);
                    if (centroid == null) {
                        skipped++;
                        continue;
                    }

                    CyclingSegment segment = new CyclingSegment(
                            record.get("STREET_NAME"),
                            record.get("INFRA_LOWORDER"),
                            record.get("SURFACE"),
                            centroid[0], // lat
                            centroid[1]  // lng
                    );

                    batch.add(segment);

                    if (batch.size() >= BATCH_SIZE) {
                        cyclingRepo.saveAll(batch);
                        loaded += batch.size();
                        batch.clear();
                    }
                } catch (Exception e) {
                    skipped++;
                }
            }

            if (!batch.isEmpty()) {
                cyclingRepo.saveAll(batch);
                loaded += batch.size();
            }

        } catch (Exception e) {
            System.err.println("Error loading cycling data: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Cycling network data loaded: " + loaded + " records, " + skipped + " skipped");
    }

    // =========================================================================
    // Geometry parsing helpers
    // =========================================================================

    /**
     * Parse a MultiPoint geometry JSON to extract lat/lng.
     * Format: {"coordinates": [[-79.548, 43.586]], "type": "MultiPoint"}
     * GeoJSON convention: coordinates are [longitude, latitude]
     * 
     * @return double[] {latitude, longitude} or null if parsing fails
     */
    private double[] parseMultiPointGeometry(String geometryJson) {
        try {
            JsonNode root = objectMapper.readTree(geometryJson);
            JsonNode coordinates = root.get("coordinates");

            if (coordinates != null && coordinates.isArray() && coordinates.size() > 0) {
                JsonNode firstPoint = coordinates.get(0);
                if (firstPoint.isArray() && firstPoint.size() >= 2) {
                    double lng = firstPoint.get(0).asDouble();
                    double lat = firstPoint.get(1).asDouble();
                    return new double[]{lat, lng};
                }
            }
        } catch (Exception e) {
            // Parsing failed, return null
        }
        return null;
    }

    /**
     * Parse a MultiLineString geometry JSON and compute the centroid.
     * Format: {"coordinates": [[[-79.406, 43.726], [-79.409, 43.725]]], "type": "MultiLineString"}
     * GeoJSON convention: coordinates are [longitude, latitude]
     * 
     * @return double[] {latitude, longitude} of centroid, or null if parsing fails
     */
    private double[] parseMultiLineStringCentroid(String geometryJson) {
        try {
            JsonNode root = objectMapper.readTree(geometryJson);
            JsonNode coordinates = root.get("coordinates");

            if (coordinates != null && coordinates.isArray() && coordinates.size() > 0) {
                double sumLat = 0;
                double sumLng = 0;
                int count = 0;

                // Iterate over all line strings in the MultiLineString
                for (JsonNode lineString : coordinates) {
                    if (lineString.isArray()) {
                        for (JsonNode point : lineString) {
                            if (point.isArray() && point.size() >= 2) {
                                sumLng += point.get(0).asDouble();
                                sumLat += point.get(1).asDouble();
                                count++;
                            }
                        }
                    }
                }

                if (count > 0) {
                    return new double[]{sumLat / count, sumLng / count};
                }
            }
        } catch (Exception e) {
            // Parsing failed, return null
        }
        return null;
    }
}
