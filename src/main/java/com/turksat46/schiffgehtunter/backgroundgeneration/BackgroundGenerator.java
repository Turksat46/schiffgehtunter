package com.turksat46.schiffgehtunter.backgroundgeneration;

import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Random;

public class BackgroundGenerator {

    //Gesamtgröße des Hintergrund
    private static int WIDTH = 2500;
    private static int HEIGHT = 600;
    //Größe eines Blocks
    private static final int TILE_SIZE = 30;
    //Menge der Blöcke in die jeweiligen Richtungen
    private static final int WORLD_WIDTH_TILES = 37;
    private static final int WORLD_HEIGHT_TILES = 25;
    //Experimental: Schatten
    private static final double SHADOW_OFFSET_X = 2;
    private static final double SHADOW_OFFSET_Y = 2;

    //Hintergrundmap
    private Tile[][] world;
    //Player ignorieren, das wird für die Kamera gebraucht
    //TODO: Playerwerte allgemein entfernen
    private double playerX;
    private double playerY;
    //Jeweiligen Texturen
    private Image sandTexture;
    private Image waterTexture;
    // Pfad zum Schneeball-Bild
    private Image snowballImage;

    //Hintergrundcanvas, worauf gezeichnet wird
    private Canvas backgroundCanvas;

    static javafx.scene.layout.Pane rootElement;

    //Typen von Blöcken
    public enum Tile {
        WATER, SAND
    }

    public BackgroundGenerator(javafx.scene.layout.Pane rootElement) {
        BackgroundGenerator.rootElement = rootElement;
        try{
            snowballImage = new Image(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/images/snowball.jpeg"));
        }catch (NullPointerException e){
            System.err.println("Schneeball-Ressource konnte nicht geladen werden!");
            throw e;
        }
    }

    public void createBackground() {
        backgroundCanvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = backgroundCanvas.getGraphicsContext2D();

        // Lade die Texturen
        try {
            sandTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/images/sand_texture.png")));
            waterTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/images/water_animated.gif")));
        } catch (NullPointerException e) {
            System.err.println("Fehler beim Laden der Texturen. Stelle sicher, dass die Dateien im Ressourcenordner liegen.");
            throw e;
        }

        generateWorld();
        playerX = WIDTH / 2.0;
        playerY = HEIGHT / 2.0;


        new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw(gc);
            }
        }.start();
    }

    private void generateWorld() {
        world = new Tile[WORLD_WIDTH_TILES][WORLD_HEIGHT_TILES];
        // Standardmäßig alles mit Wasser füllen
        for (int x = 0; x < WORLD_WIDTH_TILES; x++) {
            for (int y = 0; y < WORLD_HEIGHT_TILES; y++) {
                world[x][y] = Tile.WATER;
            }
        }

        int islandStartY = WORLD_HEIGHT_TILES - 5;
        Random random = new Random();

        // Insel in der Mitte erstellen
        int middleXStart = WORLD_WIDTH_TILES / 2 - 2; // Starte etwas links von der Mitte
        int middleXEnd = WORLD_WIDTH_TILES / 2 + 2;   // Ende etwas rechts von der Mitte

        for (int x = middleXStart; x <= middleXEnd; x++) {
            for (int y = 2; y < WORLD_HEIGHT_TILES - 2; y++) { // Insel geht von unten nach oben, etwas Platz lassen
                // Füge etwas Zufall hinzu, um die Inselform interessanter zu gestalten
                // Innere Teile der Insel immer Sand
                if (x > middleXStart && x < middleXEnd) {
                    world[x][y] = Tile.SAND;
                } else {
                    // Zufällige Entscheidung für die Seiten
                    if (random.nextDouble() > 0.4) { // Hier kannst du die Wahrscheinlichkeit anpassen
                        world[x][y] = Tile.SAND;
                    }
                }
            }
        }
        //Kanten randomisieren
        for (int x = 0; x < WORLD_WIDTH_TILES; x++) {
            for (int y = islandStartY; y < WORLD_HEIGHT_TILES; y++) {
                if (y == islandStartY && random.nextDouble() < 0.3) { // Wahrscheinlichkeit für Wasser am oberen Rand der Insel
                    continue;
                }
                world[x][y] = Tile.SAND;
            }
        }
    }

    private javafx.scene.paint.Color getShadowColor(javafx.scene.paint.Color baseColor) {
        return baseColor.darker();
    }


    private void draw(GraphicsContext gc) {
        //gc.clearRect(0, 0, WIDTH, HEIGHT);

        double cameraOffsetX = playerX - WIDTH / 2.0;
        double cameraOffsetY = playerY - HEIGHT / 2.0 +150;

        // Zeichne die Schatten der Sandblöcke
        for (int x = 0; x < WORLD_WIDTH_TILES; x++) {
            for (int y = 0; y < WORLD_HEIGHT_TILES; y++) {
                double tileX = x * TILE_SIZE - cameraOffsetX;
                double tileY = y * TILE_SIZE - cameraOffsetY;

                if (tileX + TILE_SIZE > 0 && tileX < WIDTH && tileY + TILE_SIZE > 0 && tileY < HEIGHT) {
                    if (world[x][y] ==  Tile.SAND) {
                        gc.setFill(getShadowColor(javafx.scene.paint.Color.YELLOW));
                        gc.fillRect(tileX + SHADOW_OFFSET_X, tileY + SHADOW_OFFSET_Y, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        // Zeichne die Welt mit Texturen
        for (int x = 0; x < WORLD_WIDTH_TILES; x++) {
            for (int y = 0; y < WORLD_HEIGHT_TILES; y++) {
                double tileX = x * TILE_SIZE - cameraOffsetX;
                double tileY = y * TILE_SIZE - cameraOffsetY;

                if (tileX + TILE_SIZE > 0 && tileX < WIDTH && tileY + TILE_SIZE > 0 && tileY < HEIGHT) {
                    if (world[x][y] ==  Tile.WATER) {
                        gc.drawImage(waterTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                    } else if (world[x][y] ==  Tile.SAND) {
                        gc.drawImage(sandTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        //Snapshot von der erstellten Welt erstellen und diese in Hintergrund speichern und anzeigen
        SnapshotParameters params = new SnapshotParameters();
        WritableImage image = backgroundCanvas.snapshot(params, null);

        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        Background background = new Background(backgroundImage);
        rootElement.setBackground(background);
    }



}
