package com.app.ancea;

import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * Central license management system for HookeXpert.
 *
 * IMPROVEMENTS:
 * 1. Multi-storage license persistence (like trial manager)
 * 2. Callback mechanism for admin mode changes without restart
 * 3. Better error handling and logging
 *
 * WORKFLOW:
 * 1. Client runs app → sees trial countdown or expiration dialog
 * 2. Client copies Machine ID and sends to developer
 * 3. Developer uses LicenseGenerator to create license key
 * 4. Client enters license key → App validates and activates
 */
public class LicenseManager {

    private static final Logger logger = Logger.getLogger(LicenseManager.class.getName());

    // IMPORTANT: Keep this secret and ONLY in your code, not shared with clients
    private static final String SECRET_SALT = "HookeXpert_License_Salt_2025!@#$%^&*";
    private static final String APP_NAME = "HookeXpert";

    private final MultiStorageTrialManager trialManager;
    private final String machineId;
    private final Preferences prefs;

    // Multi-storage for license persistence
    private final List<LicenseStorageProvider> storageProviders;

    // Store the current role
    private String currentRole = "USER";

    // NEW: Callback for when license/role changes (allows UI update without restart)
    private Consumer<Boolean> onAdminModeChanged;

    public LicenseManager() {
        this.trialManager = new MultiStorageTrialManager();
        this.machineId = HardwareFingerprint.generateMachineId();
        this.prefs = Preferences.userRoot().node("/hookexpert/license");

        // Initialize multi-storage providers
        this.storageProviders = new ArrayList<>();
        storageProviders.add(new PreferencesLicenseStorage());
        storageProviders.add(new FileLicenseStorage());
        storageProviders.add(new AppDataLicenseStorage());

        logger.info("License Manager initialized. Machine ID: " + machineId);
    }

    /**
     * NEW: Set callback to be notified when admin mode changes.
     * This allows the UI to update without requiring app restart.
     *
     * @param callback Consumer that receives true for admin, false for user
     */
    public void setOnAdminModeChanged(Consumer<Boolean> callback) {
        this.onAdminModeChanged = callback;
    }

    /**
     * Get this machine's unique identifier.
     */
    public String getMachineId() {
        return machineId;
    }

    /**
     * Check if current license grants Admin privileges.
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(currentRole);
    }

    /**
     * Get the current role string.
     */
    public String getCurrentRole() {
        return currentRole;
    }

    /**
     * VALIDATE LICENSE (with Role support)
     */
    public boolean validateLicenseKey(String licenseKey) {
        if (licenseKey == null || licenseKey.trim().isEmpty()) return false;

        try {
            String decoded = new String(Base64.getDecoder().decode(licenseKey.trim()));
            String[] parts = decoded.split("\\|");

            String licensedMachineId;
            LocalDate expirationDate;
            String signature;
            String role = "USER";

            if (parts.length == 4) {
                // New Format: ID | Date | Role | Signature
                licensedMachineId = parts[0];
                expirationDate = LocalDate.parse(parts[1]);
                role = parts[2];
                signature = parts[3];
            } else if (parts.length == 3) {
                // Old Format (Backward compatibility): ID | Date | Signature
                licensedMachineId = parts[0];
                expirationDate = LocalDate.parse(parts[1]);
                signature = parts[2];
            } else {
                logger.warning("Invalid license format: " + parts.length + " parts");
                return false;
            }

            // Check machine ID matches
            if (!licensedMachineId.equals(this.machineId)) {
                logger.warning("License machine ID mismatch. Expected: " + this.machineId + ", Got: " + licensedMachineId);
                return false;
            }

            // Check not expired
            if (LocalDate.now().isAfter(expirationDate)) {
                logger.warning("License has expired on: " + expirationDate);
                return false;
            }

            // Verify Signature
            String expectedSignature = generateSignature(licensedMachineId, expirationDate, role);
            if (!signature.equals(expectedSignature)) {
                logger.warning("License signature mismatch");
                return false;
            }

            // Update role and notify listeners
            String oldRole = this.currentRole;
            this.currentRole = role;

            // NEW: Notify callback if role changed
            if (!oldRole.equals(role) && onAdminModeChanged != null) {
                onAdminModeChanged.accept(isAdmin());
            }

            logger.info("License validated successfully. Role: " + role + ", Expires: " + expirationDate);
            return true;

        } catch (Exception e) {
            logger.log(Level.WARNING, "License validation error", e);
            return false;
        }
    }

    /**
     * GENERATE LICENSE (with Role support)
     */
    public static String generateLicenseKey(String clientMachineId, LocalDate expirationDate, String role) {
        String safeRole = (role != null && role.equalsIgnoreCase("ADMIN")) ? "ADMIN" : "USER";
        String signature = generateSignature(clientMachineId, expirationDate, safeRole);
        String raw = clientMachineId + "|" + expirationDate.toString() + "|" + safeRole + "|" + signature;
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }

    public static String generateLicenseKey(String clientMachineId, LocalDate expirationDate) {
        return generateLicenseKey(clientMachineId, expirationDate, "USER");
    }

    /**
     * SIGNATURE GENERATION (includes Role)
     */
    private static String generateSignature(String machineId, LocalDate expiration, String role) {
        String data = machineId + expiration.toString() + role + SECRET_SALT;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes());
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                hex.append(String.format("%02X", hash[i]));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Signature error", e);
        }
    }

    /**
     * Get stored license key from ALL storage locations.
     * Returns the first valid one found (repairs missing locations).
     */
    public String getStoredLicenseKey() {
        String foundLicense = null;

        // Try to read from all locations
        for (LicenseStorageProvider provider : storageProviders) {
            try {
                String license = provider.readLicense();
                if (license != null && !license.isEmpty()) {
                    foundLicense = license;
                    logger.fine("Found license in " + provider.getClass().getSimpleName());
                    break;
                }
            } catch (Exception e) {
                logger.log(Level.FINE, "Could not read from " + provider.getClass().getSimpleName(), e);
            }
        }

        // If found, ensure it's written to all locations (repair)
        if (foundLicense != null) {
            storeLicenseKey(foundLicense);
        }

        return foundLicense;
    }

    /**
     * Store a license key in ALL storage locations for redundancy.
     */
    public void storeLicenseKey(String licenseKey) {
        int successCount = 0;
        for (LicenseStorageProvider provider : storageProviders) {
            try {
                provider.writeLicense(licenseKey);
                successCount++;
            } catch (Exception e) {
                logger.log(Level.FINE, "Could not write to " + provider.getClass().getSimpleName(), e);
            }
        }
        logger.fine("License stored in " + successCount + "/" + storageProviders.size() + " locations");
    }

    /**
     * Clear stored license key from all locations.
     */
    public void clearStoredLicense() {
        for (LicenseStorageProvider provider : storageProviders) {
            try {
                provider.clearLicense();
            } catch (Exception e) {
                logger.log(Level.FINE, "Could not clear " + provider.getClass().getSimpleName(), e);
            }
        }
        currentRole = "USER";
    }

    /**
     * Check overall license status: licensed, trial, or expired.
     */
    public LicenseStatus checkLicense() {
        // First check if there's a valid stored license key
        String storedLicense = getStoredLicenseKey();
        if (storedLicense != null && !storedLicense.isEmpty()) {
            if (validateLicenseKey(storedLicense)) {
                try {
                    String decoded = new String(Base64.getDecoder().decode(storedLicense));
                    String[] parts = decoded.split("\\|");
                    LocalDate expDate = LocalDate.parse(parts[1]);
                    long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expDate);
                    return new LicenseStatus(LicenseType.LICENSED, daysUntilExpiry,
                            "Version sous licence", expDate);
                } catch (Exception e) {
                    return new LicenseStatus(LicenseType.LICENSED, -1, "Version sous licence", null);
                }
            } else {
                // License invalid or expired - clear it
                clearStoredLicense();
            }
        }

        // Fall back to trial
        currentRole = "USER"; // Trials are always USER
        MultiStorageTrialManager.TrialStatus trialStatus = trialManager.checkTrial();

        if (trialStatus.isValid()) {
            return new LicenseStatus(LicenseType.TRIAL, trialStatus.getDaysRemaining(),
                    "Essai: " + trialStatus.getDaysRemaining() + " jour(s) restant(s)",
                    LocalDate.now().plusDays(trialStatus.getDaysRemaining()));
        }

        return new LicenseStatus(LicenseType.EXPIRED, 0, "Période d'essai expirée", null);
    }

    /**
     * Activate a license key and notify listeners.
     *
     * @param licenseKey The license key to activate
     * @return true if activation successful, false otherwise
     */
    public boolean activateLicense(String licenseKey) {
        if (validateLicenseKey(licenseKey)) {
            storeLicenseKey(licenseKey);
            logger.info("License activated successfully. Role: " + currentRole);

            // Callback is already triggered in validateLicenseKey
            return true;
        }
        return false;
    }

    // ==================== LICENSE STORAGE PROVIDERS ====================

    interface LicenseStorageProvider {
        String readLicense() throws Exception;
        void writeLicense(String license) throws Exception;
        void clearLicense() throws Exception;
    }

    /**
     * Storage using Java Preferences API.
     */
    class PreferencesLicenseStorage implements LicenseStorageProvider {
        private static final String LICENSE_KEY = "license_key";

        @Override
        public String readLicense() {
            return prefs.get(LICENSE_KEY, null);
        }

        @Override
        public void writeLicense(String license) throws Exception {
            prefs.put(LICENSE_KEY, license);
            prefs.flush();
        }

        @Override
        public void clearLicense() throws Exception {
            prefs.remove(LICENSE_KEY);
            prefs.flush();
        }
    }

    /**
     * Storage using hidden file in user home.
     */
    class FileLicenseStorage implements LicenseStorageProvider {
        private final Path filePath;

        FileLicenseStorage() {
            String home = System.getProperty("user.home");
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                filePath = Paths.get(home, "." + APP_NAME.toLowerCase() + ".lic");
            } else {
                filePath = Paths.get(home, "." + APP_NAME.toLowerCase(), ".license");
            }
        }

        @Override
        public String readLicense() throws Exception {
            if (!Files.exists(filePath)) return null;
            return Files.readString(filePath).trim();
        }

        @Override
        public void writeLicense(String license) throws Exception {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, license);
            // Make hidden on Windows
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                try {
                    Files.setAttribute(filePath, "dos:hidden", true);
                } catch (Exception ignored) {}
            }
        }

        @Override
        public void clearLicense() throws Exception {
            Files.deleteIfExists(filePath);
        }
    }

    /**
     * Storage using OS-specific app data folder.
     */
    class AppDataLicenseStorage implements LicenseStorageProvider {
        private final Path filePath;

        AppDataLicenseStorage() {
            String os = System.getProperty("os.name").toLowerCase();
            String home = System.getProperty("user.home");

            if (os.contains("win")) {
                String appData = System.getenv("APPDATA");
                if (appData == null) {
                    appData = Paths.get(home, "AppData", "Roaming").toString();
                }
                filePath = Paths.get(appData, APP_NAME, "license.key");
            } else if (os.contains("mac")) {
                filePath = Paths.get(home, "Library", "Application Support", APP_NAME, ".license.key");
            } else {
                filePath = Paths.get(home, ".config", APP_NAME.toLowerCase(), "license.key");
            }
        }

        @Override
        public String readLicense() throws Exception {
            if (!Files.exists(filePath)) return null;
            return Files.readString(filePath).trim();
        }

        @Override
        public void writeLicense(String license) throws Exception {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, license);
        }

        @Override
        public void clearLicense() throws Exception {
            Files.deleteIfExists(filePath);
        }
    }

    // ==================== STATUS CLASSES ====================

    public enum LicenseType {
        LICENSED,
        TRIAL,
        EXPIRED
    }

    public static class LicenseStatus {
        private final LicenseType type;
        private final long daysRemaining;
        private final String message;
        private final LocalDate expirationDate;

        public LicenseStatus(LicenseType type, long daysRemaining, String message, LocalDate expirationDate) {
            this.type = type;
            this.daysRemaining = daysRemaining;
            this.message = message;
            this.expirationDate = expirationDate;
        }

        public LicenseType getType() { return type; }
        public long getDaysRemaining() { return daysRemaining; }
        public String getMessage() { return message; }
        public LocalDate getExpirationDate() { return expirationDate; }

        public boolean isUsable() {
            return type == LicenseType.LICENSED || type == LicenseType.TRIAL;
        }
    }
}