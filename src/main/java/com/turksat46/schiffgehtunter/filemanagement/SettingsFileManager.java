package com.turksat46.schiffgehtunter.filemanagement;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Diese Klasse verwaltet die Einstellungen, die in einer Properties-Datei gespeichert sind.
 * Sie ermöglicht das Laden, Speichern und Abrufen von Einstellungen wie Musikstatus und Lautstärke.
 */
public class SettingsFileManager {

    // Der Name der Datei, in der die Einstellungen gespeichert werden
    private static final String SETTINGS_FILE = "settings.properties";

    // Das Properties-Objekt, das die Einstellungen speichert
    private static Properties properties;

    /**
     * Konstruktor, der das Properties-Objekt initialisiert.
     */
    public SettingsFileManager() {
        properties = new Properties();
    }

    /**
     * Lädt die gespeicherten Einstellungen aus der Datei.
     * Falls beim Laden ein Fehler auftritt, wird eine neue Einstellungsdatei erstellt.
     */
    public static void loadSettings() {
        try (FileInputStream fileInputStream = new FileInputStream(SETTINGS_FILE)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Einstellungen: " + e.getMessage());
            createSettingsFile();
        }
    }

    /**
     * Erstellt eine neue Einstellungsdatei mit Standardwerten, falls die Datei nicht existiert.
     */
    private static void createSettingsFile() {
        try {
            properties.setProperty("musicEnabled", "false");
            properties.setProperty("volume", "1.0");
            properties.store(new FileOutputStream(SETTINGS_FILE), "Einstellungen");
        } catch (IOException e) {
            // Anzeige eines Fehlers, wenn die Datei nicht erstellt werden kann
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Erstellen von Einstellungen: " + e.getMessage(), ButtonType.CLOSE);
        }
    }

    /**
     * Speichert die aktuellen Einstellungen in der Datei.
     *
     * @param musicEnabled Gibt an, ob Musik aktiviert ist.
     * @param volume       Der Lautstärkewert (zwischen 0.0 und 1.0).
     */
    public static void saveSettings(boolean musicEnabled, double volume) {
        properties.setProperty("musicEnabled", String.valueOf(musicEnabled));
        properties.setProperty("volume", String.valueOf(volume));

        try (FileOutputStream fileOutputStream = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(fileOutputStream, "Einstellungen");
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern der Einstellungen: " + e.getMessage());
        }
    }

    /**
     * Gibt zurück, ob Musik aktiviert oder deaktiviert ist.
     * Falls die Einstellungen noch nicht geladen wurden, werden sie vorher geladen.
     *
     * @return true, wenn Musik aktiviert ist, andernfalls false.
     */
    public static boolean isMusicEnabled() {
        // Wenn die Einstellungen noch nicht geladen wurden, laden wir sie
        if (properties.isEmpty()) {
            loadSettings();
        }
        return Boolean.parseBoolean(properties.getProperty("musicEnabled", "false"));
    }

    /**
     * Gibt den gespeicherten Lautstärkewert zurück.
     * Falls die Einstellungen noch nicht geladen wurden, werden sie vorher geladen.
     *
     * @return Der Lautstärkewert als double (zwischen 0.0 und 1.0).
     */
    public static double getVolume() {
        // Wenn die Einstellungen noch nicht geladen wurden, laden wir sie
        if (properties.isEmpty()) {
            loadSettings();
        }
        return Double.parseDouble(properties.getProperty("volume", "0.5"));
    }
}
