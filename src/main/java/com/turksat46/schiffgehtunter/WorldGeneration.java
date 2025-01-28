package com.turksat46.schiffgehtunter;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
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
    private static final int GRID_SIZE = 5; // 5x5 grid
    private static final int CELL_SIZE = 50; // Size of each grid cell

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // Create a draggable rectangle
        Rectangle draggableRect = new Rectangle(40, 40, Color.BLUE);
        makeDraggable(draggableRect);

        // Create a 5x5 grid of rectangles
        GridPane gridPane = createGridPane();

        root.setCenter(gridPane);
        root.setLeft(draggableRect);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();

        // Continuously check for intersections and update visual feedback
        draggableRect.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
            updateGridVisual(draggableRect, gridPane);
        });

        // Snap to the grid cell on release
        draggableRect.setOnMouseReleased(event -> {
            snapToGrid(draggableRect, gridPane);
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

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE, Color.LIGHTGRAY);
                cell.setStroke(Color.BLACK);
                gridPane.add(cell, col, row);
            }
        }
        return gridPane;
    }

    private void updateGridVisual(Rectangle draggable, GridPane gridPane) {
        Rectangle closestCell = null;
        double minDistance = Double.MAX_VALUE;

        for (var node : gridPane.getChildren()) {
            if (node instanceof Rectangle cell) {
                if (draggable.getBoundsInParent().intersects(cell.getBoundsInParent())) {
                    // Calculate the distance from the draggable rectangle to the cell
                    Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
                    double centerX = cellBounds.getMinX() + cell.getWidth() / 2;
                    double centerY = cellBounds.getMinY() + cell.getHeight() / 2;

                    double distance = Math.hypot(centerX - draggable.getBoundsInParent().getCenterX(),
                            centerY - draggable.getBoundsInParent().getCenterY());

                    if (distance < minDistance) {
                        minDistance = distance;
                        closestCell = cell;
                    }
                }
            }
        }

        // Reset all cells to gray
        for (var node : gridPane.getChildren()) {
            if (node instanceof Rectangle cell) {
                cell.setFill(Color.LIGHTGRAY);
            }
        }

        // Highlight the closest intersecting cell, if any
        if (closestCell != null) {
            closestCell.setFill(Color.GREEN);
        }
    }

    private void snapToGrid(Rectangle draggable, GridPane gridPane) {
        for (var node : gridPane.getChildren()) {
            if (node instanceof Rectangle cell) {
                if (draggable.getBoundsInParent().intersects(cell.getBoundsInParent())) {
                    // Snap the draggable rectangle to the center of the closest intersecting cell
                    Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());

                    double cellCenterX = cellBounds.getMinX() + cell.getWidth() / 2;
                    double cellCenterY = cellBounds.getMinY() + cell.getHeight() / 2;

                    int x = GridPane.getColumnIndex(cell);
                    int y = GridPane.getRowIndex(cell);
                    System.out.println("x: " + x + ", y: " + y);

                    draggable.setTranslateX(cellCenterX - draggable.getWidth() / 2);
                    draggable.setTranslateY(cellCenterY - draggable.getHeight() / 2);
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}