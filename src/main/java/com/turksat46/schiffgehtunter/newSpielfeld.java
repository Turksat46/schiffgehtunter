package com.turksat46.schiffgehtunter;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import com.turksat46.schiffgehtunter.other.Cell;

import java.util.*;

public class newSpielfeld {

    private static GridPane gridPane;
    private static int GRID_SIZE;
    private static BorderPane root;
    private static int CELL_SIZE = 50;
    final Map<Group, Set<Cell>> shipCellMap = new HashMap<>();
    private final List<Group> draggables = new ArrayList<>();
    private static int shipCount;
    private static int remainingCells;

    public newSpielfeld(int size, boolean isEnemyField, BorderPane root) {
        newSpielfeld.GRID_SIZE = size;
        newSpielfeld.root = root;

        CELL_SIZE = size <= 5 ? 75 : size <= 10 ? 50 : size <= 20 ? 30 : 20;

        gridPane = createGridPane();
        root.setCenter(gridPane);

        if (!isEnemyField) {
            HBox draggableContainer = new HBox(10);
            draggableContainer.setPadding(new Insets(10));

            int[] shipSizes = {5, 4, 3, 2};
            Random random = new Random();
            shipCount = (int)((size*size)*0.3);
            remainingCells = shipCount;
            while (remainingCells > 1) {
                // Zufällig eine Schiffsgröße auswählen
                int shipSize = shipSizes[random.nextInt(shipSizes.length)];
                // Prüfen, ob das Schiff noch platziert werden kann
                if (shipSize <= remainingCells) {
                    Group shipGroup = createDraggableShip(shipSize);
                    makeDraggable(shipGroup);
                    draggables.add(shipGroup);
                    draggableContainer.getChildren().add(shipGroup);
                    remainingCells -= shipSize;

                }
            }
            root.setBottom(draggableContainer);
        }
    }

    //Konstruktor ohne schiff Berechnung für Multiplayer
    public newSpielfeld(int size, boolean isEnemyField, BorderPane root, List<Integer> ships) {
        newSpielfeld.GRID_SIZE = size;
        newSpielfeld.root = root;

        CELL_SIZE = size <= 5 ? 75 : size <= 10 ? 50 : size <= 20 ? 30 : 20;

        gridPane = createGridPane();
        root.setCenter(gridPane);

        if (!isEnemyField) {
            HBox draggableContainer = new HBox(10);
            draggableContainer.setPadding(new Insets(10));

            for (int shipSize : ships) {
                Group shipGroup = createDraggableShip(shipSize);
                makeDraggable(shipGroup);
                draggables.add(shipGroup);
                draggableContainer.getChildren().add(shipGroup);
            }

            root.setBottom(draggableContainer);
        }
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE, Color.TRANSPARENT);
                cell.setStroke(Color.BLACK);
                grid.add(cell, col, row);
            }
        }
        return grid;
    }

    private Group createDraggableShip(int length) {
        Group ship = new Group();
        for (int i = 0; i < length; i++) {
            ImageView part = new ImageView(new Image(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/images/ship.png")));
            part.setFitHeight(CELL_SIZE);
            part.setFitWidth(CELL_SIZE);
            part.setRotate(90);
            part.setTranslateX(i * CELL_SIZE);
            ship.getChildren().add(part);
        }
        return ship;
    }

    private void makeDraggable(Group ship) {
        ship.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                ship.setUserData(new double[]{event.getSceneX(), event.getSceneY(), ship.getTranslateX(), ship.getTranslateY()});
            }
            if(event.getButton() == MouseButton.SECONDARY) {
                rotateDraggableGroup(ship);
            }
        });

        ship.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double[] data = (double[]) ship.getUserData();
                ship.setTranslateX(data[2] + event.getSceneX() - data[0]);
                ship.setTranslateY(data[3] + event.getSceneY() - data[1]);
            }
            if(event.getButton() == MouseButton.SECONDARY) {
                rotateDraggableGroup(ship);
            }
        });

        ship.setOnMouseReleased(event -> {
            snapToGrid(ship);
            updateShipCellMap();
        });
    }

    private void rotateDraggableGroup(Group group) {
        Bounds bounds = group.getLayoutBounds();
        double pivotX = bounds.getWidth() / 2;
        double pivotY = bounds.getHeight() / 2;
        group.setRotate(group.getRotate() + 90);
    }


    private void snapToGrid(Group ship) {
        if (ship.getChildren().isEmpty()) return;

        ImageView firstPart = (ImageView) ship.getChildren().get(0);
        Bounds bounds = firstPart.localToScene(firstPart.getBoundsInLocal());
        double centerX = bounds.getMinX() + bounds.getWidth() / 2;
        double centerY = bounds.getMinY() + bounds.getHeight() / 2;

        Node closestCell = null;
        double minDistance = Double.MAX_VALUE;

        for (Node node : gridPane.getChildren()) {
            if (node instanceof Rectangle cell) {
                Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
                double cellCenterX = cellBounds.getMinX() + cellBounds.getWidth() / 2;
                double cellCenterY = cellBounds.getMinY() + cellBounds.getHeight() / 2;

                double distance = Math.hypot(centerX - cellCenterX, centerY - cellCenterY);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestCell = cell;
                }
            }
        }

        if (closestCell != null) {
            Bounds cellBounds = closestCell.localToScene(closestCell.getBoundsInLocal());
            double targetX = cellBounds.getMinX() + closestCell.getBoundsInLocal().getWidth() / 2 - bounds.getWidth() / 2;
            double targetY = cellBounds.getMinY() + closestCell.getBoundsInLocal().getHeight() / 2 - bounds.getHeight() / 2;

            ship.setTranslateX(ship.getTranslateX() + targetX - bounds.getMinX());
            ship.setTranslateY(ship.getTranslateY() + targetY - bounds.getMinY());
        }

    }

    private void updateShipCellMap() {
        shipCellMap.clear();

        for (Group ship : draggables) {
            Set<Cell> occupiedCells = new HashSet<>();

            for (Node part : ship.getChildren()) {
                if (part instanceof ImageView imageView) {
                    Bounds partBounds = imageView.localToScene(imageView.getBoundsInLocal());

                    for (Node node : gridPane.getChildren()) {
                        if (node instanceof Rectangle cell) {
                            Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
                            if (partBounds.intersects(cellBounds)) {
                                int col = GridPane.getColumnIndex(cell);
                                int row = GridPane.getRowIndex(cell);
                                occupiedCells.add(new Cell(col, row));
                            }
                        }
                    }
                }
            }

            shipCellMap.put(ship, occupiedCells);
        }

        shipCellMap.forEach((group, cells) -> {
            System.out.println("Ship at cells: " + cells);
        });
    }

    public void selectFeld(int x, int y, Color color){
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y) {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(color);
                }
            }
        }
    }
}