package com.app.ancea;

import java.io.*;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.*;

/**
 * Generates a unique hardware fingerprint based on multiple system identifiers.
 * Used for machine-bound licensing.
 *
 * VERSION 2.0 - STABLE + WINDOWS 11 COMPATIBLE
 *
 * Changes:
 * - Removed user.name (was causing license loss on user switch)
 * - Added PowerShell fallbacks for Windows 11 (WMIC is deprecated)
 * - Stable MAC address selection (prefers Ethernet over WiFi)
 * - Better fallback chain for each component
 */
public class HardwareFingerprint {

    /**
     * Generates a unique machine ID based on STABLE hardware characteristics.
     *
     * @return A 32-character uppercase hex string unique to this machine
     */
    public static String generateMachineId() {
        StringBuilder sb = new StringBuilder();

        // Collect hardware identifiers with fallbacks
        sb.append(getMotherboardSerial());
        sb.append("|");
        sb.append(getCpuId());
        sb.append("|");
        sb.append(getStableMacAddress());
        sb.append("|");
        sb.append(getDiskSerial());
        sb.append("|");
        sb.append(getStableOsInfo()); // Without user.name for stability

        // Hash it to create a fixed-length fingerprint
        return hashSHA256(sb.toString()).substring(0, 32).toUpperCase();
    }

    /**
     * Gets a STABLE MAC address - prefers Ethernet over WiFi, ignores virtual interfaces.
     */
    private static String getStableMacAddress() {
        try {
            List<String> macAddresses = new ArrayList<>();
            String preferredMac = null;

            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();
                String name = network.getName().toLowerCase();
                String displayName = network.getDisplayName() != null ?
                        network.getDisplayName().toLowerCase() : "";

                if (mac != null && mac.length > 0 && !network.isLoopback() && !network.isVirtual()) {
                    // Skip virtual/VPN interfaces
                    if (name.startsWith("docker") || name.startsWith("veth") ||
                            name.startsWith("br-") || name.startsWith("virbr") ||
                            name.startsWith("tun") || name.startsWith("tap") ||
                            name.contains("virtual") || name.contains("vpn") ||
                            displayName.contains("virtual") || displayName.contains("vpn") ||
                            displayName.contains("hyper-v") || displayName.contains("vmware") ||
                            displayName.contains("virtualbox")) {
                        continue;
                    }

                    StringBuilder sb = new StringBuilder();
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                    String macStr = sb.toString();
                    macAddresses.add(macStr);

                    // Prefer Ethernet over WiFi
                    if (preferredMac == null) {
                        preferredMac = macStr;
                    }
                    // Windows Ethernet adapters
                    if (name.startsWith("eth") || name.startsWith("en") || name.startsWith("enp") ||
                            displayName.contains("ethernet") || displayName.contains("realtek") ||
                            displayName.contains("intel") && !displayName.contains("wireless")) {
                        preferredMac = macStr;
                    }
                }
            }

            if (preferredMac != null) {
                return preferredMac;
            }

            // Fallback: sort and take first for consistency
            if (!macAddresses.isEmpty()) {
                Collections.sort(macAddresses);
                return macAddresses.get(0);
            }

        } catch (Exception e) {
            // Fallback silently
        }
        return "NO_MAC";
    }

    /**
     * Gets the CPU identifier (platform-specific).
     * Windows 11: Uses PowerShell as primary, WMIC as fallback
     */
    private static String getCpuId() {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                // Try PowerShell first (Windows 11 compatible)
                String result = executePowerShell(
                        "(Get-CimInstance -ClassName Win32_Processor).ProcessorId"
                );
                if (result.isEmpty()) {
                    // Fallback to WMIC (older Windows)
                    result = executeCommand("wmic cpu get ProcessorId");
                }
                if (result.isEmpty()) {
                    // Last resort: CPU name as identifier
                    result = executePowerShell(
                            "(Get-CimInstance -ClassName Win32_Processor).Name"
                    );
                }
                return result.isEmpty() ? "NO_CPU_ID" : result;

            } else if (os.contains("linux")) {
                String result = executeCommand("cat /proc/cpuinfo | grep -m1 'model name' | cut -d: -f2");
                if (result.isEmpty()) {
                    result = executeCommand("lscpu | grep 'Model name' | cut -d: -f2");
                }
                return result.isEmpty() ? getLinuxMachineId() : result;

            } else if (os.contains("mac")) {
                return executeCommand("sysctl -n machdep.cpu.brand_string");
            }
        } catch (Exception e) {
            // Fallback silently
        }
        return "NO_CPU_ID";
    }

    /**
     * Gets the motherboard serial number (platform-specific).
     * Windows 11: Uses PowerShell as primary, WMIC as fallback
     */
    private static String getMotherboardSerial() {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                // Try PowerShell first (Windows 11 compatible)
                String result = executePowerShell(
                        "(Get-CimInstance -ClassName Win32_BaseBoard).SerialNumber"
                );
                if (result.isEmpty() || result.equalsIgnoreCase("To be filled by O.E.M.") ||
                        result.equalsIgnoreCase("Default string")) {
                    // Try BIOS serial as alternative
                    result = executePowerShell(
                            "(Get-CimInstance -ClassName Win32_BIOS).SerialNumber"
                    );
                }
                if (result.isEmpty()) {
                    // Fallback to WMIC
                    result = executeCommand("wmic baseboard get SerialNumber");
                }
                if (result.isEmpty() || result.equalsIgnoreCase("To be filled by O.E.M.")) {
                    // Use UUID as last resort
                    result = executePowerShell(
                            "(Get-CimInstance -ClassName Win32_ComputerSystemProduct).UUID"
                    );
                }
                return result.isEmpty() ? "NO_MOBO_SERIAL" : result;

            } else if (os.contains("linux")) {
                String result = executeCommand("cat /sys/class/dmi/id/board_serial 2>/dev/null");
                if (result.isEmpty()) {
                    result = getLinuxMachineId();
                }
                return result;

            } else if (os.contains("mac")) {
                return executeCommand("ioreg -rd1 -c IOPlatformExpertDevice | grep IOPlatformSerialNumber | cut -d'\"' -f4");
            }
        } catch (Exception e) {
            // Fallback silently
        }
        return "NO_MOBO_SERIAL";
    }

    /**
     * Gets the disk serial number (platform-specific).
     * Windows 11: Uses PowerShell as primary, WMIC as fallback
     */
    private static String getDiskSerial() {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                // Try PowerShell first (Windows 11 compatible)
                String result = executePowerShell(
                        "(Get-CimInstance -ClassName Win32_DiskDrive | Select-Object -First 1).SerialNumber"
                );
                if (result.isEmpty()) {
                    // Try physical media
                    result = executePowerShell(
                            "(Get-PhysicalDisk | Select-Object -First 1).SerialNumber"
                    );
                }
                if (result.isEmpty()) {
                    // Fallback to WMIC
                    result = executeCommand("wmic diskdrive get SerialNumber");
                }
                if (result.isEmpty()) {
                    // Use volume serial as last resort
                    result = executeCommand("vol C:");
                    if (result.contains("Serial Number is")) {
                        result = result.substring(result.indexOf("Serial Number is") + 17).trim();
                    }
                }
                return result.isEmpty() ? "NO_DISK_SERIAL" : result.trim();

            } else if (os.contains("linux")) {
                String result = executeCommand("lsblk -o SERIAL 2>/dev/null | head -2 | tail -1");
                if (result.isEmpty()) {
                    result = getLinuxMachineId();
                }
                return result;

            } else if (os.contains("mac")) {
                return executeCommand("diskutil info disk0 | grep 'Volume UUID' | cut -d: -f2");
            }
        } catch (Exception e) {
            // Fallback silently
        }
        return "NO_DISK_SERIAL";
    }

    /**
     * Gets the Linux machine-id which is stable across reboots.
     */
    private static String getLinuxMachineId() {
        try {
            File machineIdFile = new File("/etc/machine-id");
            if (machineIdFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(machineIdFile))) {
                    String line = reader.readLine();
                    if (line != null && !line.isEmpty()) {
                        return line.trim();
                    }
                }
            }

            File dbusIdFile = new File("/var/lib/dbus/machine-id");
            if (dbusIdFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(dbusIdFile))) {
                    String line = reader.readLine();
                    if (line != null && !line.isEmpty()) {
                        return line.trim();
                    }
                }
            }
        } catch (Exception e) {
            // Fallback silently
        }
        return "NO_LINUX_MACHINE_ID";
    }

    /**
     * Gets STABLE OS information (WITHOUT username which can change).
     */
    private static String getStableOsInfo() {
        return System.getProperty("os.name") +
                System.getProperty("os.arch");
        // REMOVED: user.name - was causing license invalidation
    }

    /**
     * Executes a PowerShell command and returns the output.
     * Used for Windows 11 compatibility where WMIC is deprecated.
     */
    private static String executePowerShell(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-NonInteractive",
                    "-Command",
                    command
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    result.append(line);
                }
            }

            process.waitFor();
            reader.close();

            String output = result.toString().trim();
            // Filter out common placeholder values
            if (output.equalsIgnoreCase("To be filled by O.E.M.") ||
                    output.equalsIgnoreCase("Default string") ||
                    output.equalsIgnoreCase("None") ||
                    output.equalsIgnoreCase("Not Available")) {
                return "";
            }
            return output;

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Executes a system command and returns the output.
     */
    private static String executeCommand(String command) {
        try {
            Process process;
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", command});
            } else {
                process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Filter out header lines from wmic commands
                if (!line.isEmpty() &&
                        !line.equalsIgnoreCase("serialnumber") &&
                        !line.equalsIgnoreCase("processorid")) {
                    result.append(line);
                }
            }

            process.waitFor();
            reader.close();
            return result.toString().trim();

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Computes SHA-256 hash of the input string.
     */
    private static String hashSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return input;
        }
    }

    /**
     * Test method to display generated machine ID and debug info.
     */
    public static void main(String[] args) {
        System.out.println("=== Hardware Fingerprint Generator v2.0 ===");
        System.out.println("=== Windows 11 Compatible + Stable ===\n");

        System.out.println("Machine ID: " + generateMachineId());
        System.out.println("\nComponents (for debugging):");
        System.out.println("  Motherboard/BIOS: " + getMotherboardSerial());
        System.out.println("  CPU ID: " + getCpuId());
        System.out.println("  MAC Address: " + getStableMacAddress());
        System.out.println("  Disk Serial: " + getDiskSerial());
        System.out.println("  OS Info: " + getStableOsInfo());
        System.out.println("  Linux Machine ID: " + getLinuxMachineId());

        System.out.println("\n----------------------------------------");
        System.out.println("Notes:");
        System.out.println("- Uses PowerShell (Win11) with WMIC fallback (older Windows)");
        System.out.println("- user.name removed for stability");
        System.out.println("- Prefers Ethernet MAC over WiFi");
    }
}