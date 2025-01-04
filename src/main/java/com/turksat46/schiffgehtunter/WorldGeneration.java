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
        // Erstellen von zwei VBox-Boxen
        VBox box1 = new VBox(10);
        box1.setStyle("-fx-border-color: black; -fx-padding: 20; -fx-pref-width: 200; -fx-pref-height: 200;");

        VBox box2 = new VBox(10);
        box2.setStyle("-fx-border-color: black; -fx-padding: 20; -fx-pref-width: 200; -fx-pref-height: 200;");

        // Erstellen eines Rechtecks zum Ziehen
        Rectangle draggableRectangle = new Rectangle(50, 50, Color.BLUE);

        // Drag and Drop Ereignisse
        draggableRectangle.setOnDragDetected(event -> {
            Dragboard db = draggableRectangle.startDragAndDrop(TransferMode.MOVE);
            event.consume();
        });

        // Ereignis für box1
        box1.setOnDragEntered((DragEvent event) -> {
            if (event.getGestureSource() != box1 && event.getDragboard().hasContent(DataFormat.PLAIN_TEXT)) {
                box1.setStyle("-fx-border-color: green;");
            }
            event.consume();
        });

        // Ereignis für box2
        box2.setOnDragEntered((DragEvent event) -> {
            if (event.getGestureSource() != box2 && event.getDragboard().hasContent(DataFormat.PLAIN_TEXT)) {
                box2.setStyle("-fx-border-color: green;");
            }
            event.consume();
        });

        // Reset der Boxen, wenn der Drag-Vorgang die Box verlässt
        box1.setOnDragExited(event -> {
            box1.setStyle("-fx-border-color: black;");
            event.consume();
        });

        box2.setOnDragExited(event -> {
            box2.setStyle("-fx-border-color: black;");
            event.consume();
        });

        // Füge die Boxen und das Rechteck zur Szene hinzu
        box1.getChildren().add(new Label("Box 1"));
        box2.getChildren().add(new Label("Box 2"));
        VBox root = new VBox(20, draggableRectangle, box1, box2);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}