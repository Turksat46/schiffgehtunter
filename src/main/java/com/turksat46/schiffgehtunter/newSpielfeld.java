package com.turksat46.schiffgehtunter;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class newSpielfeld {

    public static GridPane gridPane;

    private double initialX;
    private double initialY;
    private static boolean gegnerFeld;
    private static int GRID_SIZE;
    private static BorderPane root;
    private static int CELL_SIZE = 50;

    public newSpielfeld(int groesse, boolean istGegnerFeld, BorderPane root) {


        newSpielfeld.GRID_SIZE = groesse;
        newSpielfeld.gegnerFeld = istGegnerFeld;
        newSpielfeld.root = root;

        if(groesse <=5 ){
            CELL_SIZE=75;
        }else if(groesse > 5 && groesse <= 10){
            CELL_SIZE=50;
        }else if(groesse > 10 && groesse <= 20){
            CELL_SIZE=30;
        }else {
            CELL_SIZE=20;
        }


        gridPane = createGridPane();
        gridPane.setPadding(new Insets(0, 0, 70, 0)); // 20 is the bottom padding

        root.setCenter(gridPane);
        if(!istGegnerFeld){
            // Create a draggable rectangle
            Rectangle draggableRect = new Rectangle(40, 40, Color.BLUE);
            makeDraggable(draggableRect);
            root.setBottom(draggableRect);


            // Continuously check for intersections and update visual feedback
            draggableRect.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
                updateGridVisual(draggableRect, gridPane);
            });

            // Snap to the grid cell on release
            draggableRect.setOnMouseReleased(event -> {
                snapToGrid(draggableRect, gridPane);
            });
        }
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
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE, Color.TRANSPARENT);
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
        // Reset all cells to transparent
        for (var node : gridPane.getChildren()) {
            if (node instanceof Rectangle cell) {
                cell.setFill(Color.TRANSPARENT);
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
                    // Zentrum der Zelle in Szenen-Koordinaten
                    Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
                    double cellCenterX = cellBounds.getMinX() + cell.getWidth() / 2;
                    double cellCenterY = cellBounds.getMinY() + cell.getHeight() / 2;

                    // Aktuelle Position des Rechtecks in Szenen-Koordinaten
                    Bounds draggableBounds = draggable.localToScene(draggable.getBoundsInLocal());
                    double draggableCenterX = draggableBounds.getMinX() + draggable.getWidth() / 2;
                    double draggableCenterY = draggableBounds.getMinY() + draggable.getHeight() / 2;

                    // Berechne die notwendige Translation
                    double translateX = cellCenterX - draggableCenterX;
                    double translateY = cellCenterY - draggableCenterY;

                    draggable.setTranslateX(draggable.getTranslateX() + translateX);
                    draggable.setTranslateY(draggable.getTranslateY() + translateY);
                    return;
                }
            }
        }
    }

}
