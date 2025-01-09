package com.turksat46.schiffgehtunter;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class newSpielfeld {

    public static GridPane gridPane;

    private double initialX;
    private double initialY;
    private double dragOffsetX;
    private double dragOffsetY;
    private static boolean gegnerFeld;
    private static int GRID_SIZE;
    private static BorderPane root;
    private static int CELL_SIZE = 50;
    private List<Group> draggables = new ArrayList<>();
    private Group currentlyDraggedGroup = null;

    List<Integer> shipLengths = new ArrayList<>();

    public newSpielfeld(int groesse, boolean istGegnerFeld, BorderPane root) {
        newSpielfeld.GRID_SIZE = groesse;
        newSpielfeld.gegnerFeld = istGegnerFeld;
        newSpielfeld.root = root;

        if (groesse <= 5) {
            CELL_SIZE = 75;
        } else if (groesse <= 10) {
            CELL_SIZE = 50;
        } else if (groesse <= 20) {
            CELL_SIZE = 30;
        } else {
            CELL_SIZE = 20;
        }

        int[] schiffsGroessen = {5, 4, 3, 2}; // Größen der Schiffe
        int totalCells = groesse * groesse; // Gesamtanzahl der Zellen im Spielfeld
        int shipCount = (int) (totalCells * 0.3); // 30 % der Zellen für Schiffe


        // Greedy-Algorithmus zum Auffüllen der Zellen
        //des hier bei feldgroesse von 10+
        for (int size : schiffsGroessen) {
            while (shipCount >= size) {
                shipLengths.add(size); // Schiff hinzufügen
                shipCount -= size; // Zellen zählen
            }
        }

        // Ausgabe der Ergebnisse
        System.out.println("Schiffe in Zellen: " + shipLengths);
        System.out.println("Verbleibende Zellen: " + shipCount);

        gridPane = createGridPane();
        gridPane.setPadding(new Insets(0, 0, 70, 0));
        root.setCenter(gridPane);

        if (!istGegnerFeld) {
            HBox draggableContainer = new HBox(10);
            draggableContainer.setPadding(new Insets(10));

            for (int length : shipLengths) {
                Group draggableGroup = createDraggableGroup(length);
                makeDraggable(draggableGroup);
                makeDuplicatable(draggableGroup);
                draggables.add(draggableGroup);
                draggableContainer.getChildren().add(draggableGroup);
            }
            root.setBottom(draggableContainer);

            rotateShip();

            // Request focus so the scene can receive key events
            root.requestFocus();

            // Continuously check for intersections and update visual feedback
            for (Group draggableGroup : draggables) {
                draggableGroup.getChildren().get(0).boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> { // Listen to the first rectangle's bounds
                    updateGridVisual(draggableGroup, gridPane);
                });

                // Snap to the grid cell on release
                draggableGroup.setOnMouseReleased(event -> {
                    snapToGrid(draggableGroup, gridPane);
                    currentlyDraggedGroup = null; // Reset dragged item
                });
            }
        }
    }

    private EventHandler<KeyEvent> rotateShip(){
        return event -> {
            if(event.getCode() == KeyCode.R){
                System.out.println("Dragging " + currentlyDraggedGroup);
                rotateDraggableGroup(currentlyDraggedGroup);
                event.consume(); // Prevent other nodes from handling the event
            }
        };
    }

    private Group createDraggableGroup(int length) {
        Group group = new Group();
        for (int i = 0; i < length; i++) {
            Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE, Color.BLUE);
            rect.setTranslateX(i * CELL_SIZE);
            group.getChildren().add(rect);
        }
        return group;
    }

    private void makeDraggable(Group group) {
        group.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                initialX = event.getSceneX();
                initialY = event.getSceneY();
                dragOffsetX = group.getTranslateX();
                dragOffsetY = group.getTranslateY();
                currentlyDraggedGroup = group;
            }
        });

        group.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                group.setTranslateX(dragOffsetX + event.getSceneX() - initialX);
                group.setTranslateY(dragOffsetY + event.getSceneY() - initialY);
            }
        });
    }

    private void makeDuplicatable(Group group) {
        group.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                //duplicateDraggableGroup(group);
                rotateDraggableGroup(group);
            }
        });
    }

    private void duplicateDraggableGroup(Group original) {
        Group duplicate = new Group();
        for (var node : original.getChildren()) {
            if (node instanceof Rectangle originalRect) {
                Rectangle newRect = new Rectangle(originalRect.getWidth(), originalRect.getHeight(), originalRect.getFill());
                newRect.setTranslateX(originalRect.getTranslateX());
                newRect.setTranslateY(originalRect.getTranslateY());
                duplicate.getChildren().add(newRect);
            }
        }
        makeDraggable(duplicate);
        makeDuplicatable(duplicate);
        draggables.add(duplicate);
        HBox draggableContainer = (HBox) root.getBottom();
        draggableContainer.getChildren().add(duplicate);

        // Add listeners for the new draggable group
        duplicate.getChildren().get(0).boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
            updateGridVisual(duplicate, gridPane);
        });

        duplicate.setOnMouseReleased(event -> {
            snapToGrid(duplicate, gridPane);
            currentlyDraggedGroup = null;
        });
    }

    private void rotateDraggableGroup(Group group) {
        Bounds bounds = group.getLayoutBounds();
        double pivotX = bounds.getWidth() / 2;
        double pivotY = bounds.getHeight() / 2;
        group.setRotate(group.getRotate() + 90);
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

    private void updateGridVisual(Group draggableGroup, GridPane gridPane) {
        // Reset all cells to transparent
        for (var node : gridPane.getChildren()) {
            if (node instanceof Rectangle cell) {
                cell.setFill(Color.TRANSPARENT);
            }
        }

        List<Rectangle> intersectingCells = getIntersectingGridCells(draggableGroup, gridPane);
        intersectingCells.forEach(cell -> cell.setFill(Color.GREEN));
    }

    private List<Rectangle> getIntersectingGridCells(Group draggableGroup, GridPane gridPane) {
        List<Rectangle> intersectingCells = new ArrayList<>();

        for (var draggableNode : draggableGroup.getChildren()) {
            if (draggableNode instanceof Rectangle draggableRect) {
                Bounds draggableBounds = draggableRect.localToScene(draggableRect.getBoundsInLocal());
                for (var gridNode : gridPane.getChildren()) {
                    if (gridNode instanceof Rectangle cell) {
                        Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
                        if (draggableBounds.intersects(cellBounds)) {
                            intersectingCells.add(cell);
                        }
                    }
                }
            }
        }
        return intersectingCells;
    }

    private void snapToGrid(Group draggableGroup, GridPane gridPane) {
        if (draggableGroup.getChildren().isEmpty()) {
            return;
        }

        Rectangle firstRect = (Rectangle) draggableGroup.getChildren().get(0);
        Bounds firstRectBounds = firstRect.localToScene(firstRect.getBoundsInLocal());
        double firstRectCenterX = firstRectBounds.getMinX() + firstRect.getWidth() / 2;
        double firstRectCenterY = firstRectBounds.getMinY() + firstRect.getHeight() / 2;

        Rectangle closestCell = null;
        double minDistance = Double.MAX_VALUE;

        for (var node : gridPane.getChildren()) {
            if (node instanceof Rectangle cell) {
                Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
                double cellCenterX = cellBounds.getMinX() + cell.getWidth() / 2;
                double cellCenterY = cellBounds.getMinY() + cell.getHeight() / 2;

                double distance = Math.hypot(firstRectCenterX - cellCenterX, firstRectCenterY - cellCenterY);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestCell = cell;
                }
            }
        }

        if (closestCell != null) {
            Bounds cellBounds = closestCell.localToScene(closestCell.getBoundsInLocal());
            double cellCenterX = cellBounds.getMinX() + closestCell.getWidth() / 2;
            double cellCenterY = cellBounds.getMinY() + closestCell.getHeight() / 2;

            double targetX = cellCenterX - firstRect.getWidth() / 2;
            double targetY = cellCenterY - firstRect.getHeight() / 2;

            Bounds groupBounds = draggableGroup.getBoundsInParent();
            double currentCenterX = groupBounds.getMinX() + groupBounds.getWidth() / 2;
            double currentCenterY = groupBounds.getMinY() + groupBounds.getHeight() / 2;

            double translateX = targetX - (firstRectBounds.getMinX());
            double translateY = targetY - (firstRectBounds.getMinY());

            draggableGroup.setTranslateX(draggableGroup.getTranslateX() + translateX);
            draggableGroup.setTranslateY(draggableGroup.getTranslateY() + translateY);
        }
    }
}