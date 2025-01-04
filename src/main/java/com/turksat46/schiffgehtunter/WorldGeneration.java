package com.turksat46.schiffgehtunter;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class WorldGeneration extends Application {

    @Override
    public void start(Stage stage) {
        // GridPane erstellen
        GridPane gridPane = new GridPane();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button("Button " + (i * 3 + j + 1));
                gridPane.add(button, j, i);
            }
        }

        // VBox erstellen, die über dem GridPane "hovered"
        HBox hoverBox = new HBox();
        hoverBox.setStyle("-fx-background-color: rgba(255, 255, 0, 1);");
        hoverBox.setMinSize(200, 100); // Größe der Box
        hoverBox.setVisible(true); // Start als unsichtbar

        final double[] initialMouseX = {0};
        final double[] initialMouseY = {0};

        // Setze die Position der hoverBox bei MousePressed
        hoverBox.setOnMousePressed(event -> {
            initialMouseX[0] = event.getSceneX() - hoverBox.getTranslateX();
            initialMouseY[0] = event.getSceneY() - hoverBox.getTranslateY();
        });

        // Bewege die hoverBox bei MouseDragged
        hoverBox.setOnMouseDragged(event -> {
            hoverBox.setTranslateX(event.getSceneX() - initialMouseX[0]);
            hoverBox.setTranslateY(event.getSceneY() - initialMouseY[0]);
        });

        // Drag-Event für das GridPane
        gridPane.setOnDragDetected(event -> {
            Dragboard db = gridPane.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("Some data");
            db.setContent(content);
            System.out.println("hallo");
            event.consume();
        });

        gridPane.setOnDragEntered(event -> {
            hoverBox.setLayoutX(event.getSceneX() - 100);
            hoverBox.setLayoutY(event.getSceneY() - 50);
            hoverBox.setVisible(true);

            System.out.println("hallo");
            event.consume();
        });

        gridPane.setOnDragExited(event -> {
            hoverBox.setVisible(false);
            event.consume();
        });

        // Root-Layout erstellen
        Pane root = new Pane(); // Verwende Pane, um die Positionierung der Box zu ermöglichen
        root.getChildren().addAll(gridPane, hoverBox);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}