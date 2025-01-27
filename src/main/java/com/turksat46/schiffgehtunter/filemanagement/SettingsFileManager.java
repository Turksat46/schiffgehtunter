package com.turksat46.schiffgehtunter.filemanagement;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsFileManager {

    private static final String SETTINGS_FILE = "settings.properties";
    private static Properties properties;

    public SettingsFileManager() {
        properties = new Properties();
    }

    // Lädt die gespeicherten Einstellungen aus der Datei
    public static void loadSettings() {
        try (FileInputStream fileInputStream = new FileInputStream(SETTINGS_FILE)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Einstellungen: " + e.getMessage());
        }
    }

    // Speichert die aktuellen Einstellungen in der Datei
    public static void saveSettings(boolean musicEnabled, double volume) {
        properties.setProperty("musicEnabled", String.valueOf(musicEnabled));
        properties.setProperty("volume", String.valueOf(volume));

        try (FileOutputStream fileOutputStream = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(fileOutputStream, "Einstellungen");
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern der Einstellungen: " + e.getMessage());
        }
    }

    // Gibt zurück, ob Musik aktiviert oder deaktiviert ist
    public static boolean isMusicEnabled() {
        // Wenn die Einstellungen noch nicht geladen wurden, laden wir sie
        if (properties.isEmpty()) {
            loadSettings();
        }
        return Boolean.parseBoolean(properties.getProperty("musicEnabled", "false"));
    }

    // Gibt den gespeicherten Lautstärkewert zurück
    public static double getVolume() {
        // Wenn die Einstellungen noch nicht geladen wurden, laden wir sie
        if (properties.isEmpty()) {
            loadSettings();
        }
        return Double.parseDouble(properties.getProperty("volume", "0.5"));
    }
}