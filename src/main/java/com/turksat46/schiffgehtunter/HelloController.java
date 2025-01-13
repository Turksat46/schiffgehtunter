package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.other.Music;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.PhongMaterial;
import javafx.stage.Stage;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    Music soundsPlayer = new Music();

    //
    // Kommentare zur Hintergrundzeichnung bei @MainGameController.java anschauen
    //
    @FXML
    private VBox rootBox;
    @FXML
    private Label welcomeText;

    private Canvas backgroundCanvas;

    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int TILE_SIZE = 30;
    private static final int WORLD_WIDTH_TILES = 30;
    private static final int WORLD_HEIGHT_TILES = 20;
    private static final double SHADOW_OFFSET_X = 2;
    private static final double SHADOW_OFFSET_Y = 2;

    private Tile[][] world;
    private double playerX;
    private double playerY;
    private Image sandTexture;
    private Image waterTexture;

    public enum Tile {
        WATER, SAND
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createBackground();
    }

    private void createBackground() {
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
                // 1. Zeichne alles auf den Canvas
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

        // Definiere den Bereich für die Insel am unteren Rand
        int islandStartY = WORLD_HEIGHT_TILES - 5; // Beginnt in den untersten 5 Reihen
        Random random = new Random();

        for (int x = 0; x < WORLD_WIDTH_TILES; x++) {
            for (int y = islandStartY; y < WORLD_HEIGHT_TILES; y++) {
                // Mit etwas Zufall eine unregelmäßige Küstenlinie erzeugen
                if (y == islandStartY && random.nextDouble() < 0.5) { // Wahrscheinlichkeit für Wasser am oberen Rand der Insel
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

        // 2. Erstelle einen Snapshot des Canvas, nachdem alles gezeichnet wurde
        SnapshotParameters params = new SnapshotParameters();
        WritableImage image = backgroundCanvas.snapshot(params, null);

        // 3. Erstelle ein BackgroundImage vom Snapshot
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );

        // 4. Erstelle ein Background Objekt mit dem BackgroundImage
        Background background = new Background(backgroundImage);

        // 5. Setze den Hintergrund des VBox
        rootBox.setBackground(background);


    }



    @FXML
    protected void onHelloButtonClick() throws IOException {
        soundsPlayer.playSound();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createGame.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Neues Spiel erstellen");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
        hideCurrentStage();
    }

    @FXML
    protected void onMultiplayerClick() throws IOException {
        soundsPlayer.playSound();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("connect.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Spiel finden");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
        hideCurrentStage();
    }

    public void hideCurrentStage() {
        Stage stage = (Stage) welcomeText.getScene().getWindow();
        stage.hide();
    }

    @FXML
    protected void onSettingsButtonClick() throws IOException {
        soundsPlayer.playSound();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settings.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Einstellungen");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    @FXML
    protected void onExitButtonClick() {
        soundsPlayer.playSound();
        System.exit(0);
    }

    @FXML
    protected void onEasterEggClick(){
        soundsPlayer.playEasterEgg();
    }
}