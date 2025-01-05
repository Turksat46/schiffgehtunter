package com.turksat46.schiffgehtunter;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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


    private double initialX;
    private double initialY;


    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // Create a draggable rectangle
        Rectangle draggableRect = new Rectangle(64, 64, Color.BLUE);
        makeDraggable(draggableRect);

        // Create two target rectangles
        Rectangle targetRect1 = createTargetRectangle();
        Rectangle targetRect2 = createTargetRectangle();

        root.setCenter(targetRect1);
        root.setRight(targetRect2);

        root.setLeft(draggableRect);

        Scene scene = new Scene(root, 400, 150);
        stage.setScene(scene);
        stage.show();

        // Continuously check for intersections
        draggableRect.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
            checkIntersection(draggableRect, targetRect1);
            checkIntersection(draggableRect, targetRect2);
        });
    }

    private void makeDraggable(Rectangle rect) {
        rect.setOnMousePressed(event -> {
            initialX = event.getSceneX() - rect.getTranslateX();
            initialY = event.getSceneY() - rect.getTranslateY();
        });

        rect.setOnMouseDragged(event -> {
            rect.setTranslateX(event.getSceneX() - initialX);
            rect.setTranslateY(event.getSceneY() - initialY);
        });
    }

    private Rectangle createTargetRectangle() {
        return new Rectangle(100, 100, Color.LIGHTGRAY);
    }

    private void checkIntersection(Rectangle draggable, Rectangle target) {
        if (draggable.getBoundsInParent().intersects(target.getBoundsInParent())) {
            target.setFill(Color.GREEN); // Change to green on intersection
        } else {
            target.setFill(Color.LIGHTGRAY); // Reset to gray when not intersecting
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}