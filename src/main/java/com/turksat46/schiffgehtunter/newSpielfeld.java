package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.netzwerk.Server;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import com.turksat46.schiffgehtunter.other.Cell;
import javafx.util.Duration;

import java.util.*;
import java.util.List;

public class newSpielfeld {

    //
    //  FÜRS SPEICHERN WICHTIGE VARIABLEN
    //


    private static GridPane gridPane;
    private static int GRID_SIZE;
    private static BorderPane root;
    private static int CELL_SIZE = 50;
    public Map<Group, Set<Cell>> shipCellMap = new HashMap<>();
    public List<Map<String, Object>> shipCellListFromData = new ArrayList<>();
    private final List<Group> draggables = new ArrayList<>();
    private static int shipCount;
    private static int remainingCells;
    public static boolean isEditable = true;

    private final Set<Cell> hitCells = new HashSet<>();

    /***
     *
     * @param size Feldgröße
     * @param isEnemyField
     * @param root
     */
    public newSpielfeld(int size, boolean isEnemyField, BorderPane root, HBox draggableContainer) {
        newSpielfeld.GRID_SIZE = size;
        newSpielfeld.root = root;

        CELL_SIZE = size <= 5 ? 75 : size <= 10 ? 50 : size <= 20 ? 30 : 20;

        gridPane = createGridPane();
        root.setCenter(gridPane);

        List<Integer> shipSizesList = new ArrayList<>();

        if (!isEnemyField) {
            draggableContainer.setPadding(new Insets(10));

            int[] shipSizes = {5, 4, 3, 2};
            Random random = new Random();
            shipCount = (int) ((size * size) * 0.3);
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

                    shipSizesList.add(shipSize);
                }
            }
            //scrollPane.setContent(draggableContainer);
            //root.setBottom(draggableContainer);
        }
        Server.setShips(shipSizesList);
    }

    //Fürs Laden vom Speicherstand
    public newSpielfeld(int size, boolean isEnemyField, BorderPane root, Map<String, Object> data) {
        newSpielfeld.GRID_SIZE = size;
        newSpielfeld.root = root;

        CELL_SIZE = size <= 5 ? 75 : size <= 10 ? 50 : size <= 20 ? 30 : 20;

        gridPane = createGridPane();
        root.setCenter(gridPane);
        shipCellListFromData = (List<Map<String, Object>>) data.get("ships");

        if (!isEnemyField) {
            HBox draggableContainer = new HBox(10);
            draggableContainer.setPadding(new Insets(10));

            root.setBottom(draggableContainer);
        }
        //Server.setShips(shipSizesList);

        //Daten laden
        drawShipsFromData(data);
    }

    // Funktion zum Zeichnen von Schiffen aufm Feld
    private void drawShipsFromData(Map<String, Object> data) {
        Map<Group, Set<Cell>> newShipCellMap = new HashMap<>();

        shipCellMap.forEach((key, cellSet) -> {
            if (cellSet.isEmpty()) return;

            int shipLength = cellSet.size();
            boolean isHorizontal = false;
            boolean isVertical = false;

            Cell[] cellsArray = cellSet.toArray(new Cell[0]);
            if (cellsArray.length >= 2) {
                if (cellsArray[0].getRow() == cellsArray[1].getRow()) {
                    isHorizontal = true;
                } else if (cellsArray[0].getCol() == cellsArray[1].getCol()) {
                    isVertical = true;
                }
            } else if (cellsArray.length == 1) {
                isHorizontal = true;
            }

            Group drawnShipGroup = createDraggableShip(shipLength);

            if (isHorizontal) {
                // Rotation is already set to 90 in createDraggableShip, if that's your horizontal default.
            } else if (isVertical) {
                drawnShipGroup.setRotate(0);
            }

            Cell firstCell = cellsArray[0];
            double startX = firstCell.getCol() * CELL_SIZE;
            double startY = firstCell.getRow() * CELL_SIZE;
            drawnShipGroup.setTranslateX(startX);
            drawnShipGroup.setTranslateY(startY);

            root.getChildren().add(drawnShipGroup);
            draggables.add(drawnShipGroup);

            newShipCellMap.put(drawnShipGroup, cellSet);

            makeDraggable(drawnShipGroup);
        });
        shipCellMap = newShipCellMap;

        // --- Modify these lines to explicitly cast to Integer ---
        if (data != null && data.containsKey("ships")) {
            List<Map<String, Object>> loadedShipsDataList = (List<Map<String, Object>>) data.get("ships");
            shipCellMap = new HashMap<>();

            if (loadedShipsDataList != null) {
                for (Map<String, Object> shipData : loadedShipsDataList) {
                    String shipId = (String) shipData.get("shipId");
                    List<Map<String, Double>> cellDataList = (List<Map<String, Double>>) shipData.get("cells");

                    Set<Cell> cellSet = new HashSet<>();
                    if (cellDataList != null) {
                        for (Map<String, Double> cellData : cellDataList) {
                            // Explicitly cast to Double then to int:
                            int col = ((Double) cellData.get("col")).intValue();
                            int row = ((Double) cellData.get("row")).intValue();
                            cellSet.add(new Cell(col, row));
                        }
                    }
                    // ... (rest of the ship loading code remains the same) ...
                    int shipLength = cellSet.size();
                    Group drawnShipGroup = createDraggableShip(shipLength);
                    // ... (orientation and positioning code) ...
                    root.getChildren().add(drawnShipGroup);
                    draggables.add(drawnShipGroup);
                    shipCellMap.put(drawnShipGroup, cellSet);
                    makeDraggable(drawnShipGroup);
                    placeShipsOnPositions(drawnShipGroup, cellSet);

                }
            }
            updateShipCellMap();
        } else {
            System.out.println("Keine Schiffsdaten im Speicherstand gefunden oder Daten sind null.");
        }
    }

    private void placeShipsOnPositions(Group ship, Set<Cell> cellSet) {
        if (ship.getChildren().isEmpty() || cellSet.isEmpty()) return;

        // Nimm die erste Cell aus dem Set als Zielposition
        Cell targetCell = cellSet.iterator().next();

        // Suche die passende Rectangle-Zelle im GridPane
        Node targetNode = null;
        for (Node node : gridPane.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            // Falls die Node in der gesuchten Spalte & Zeile ist, speichern
            if (colIndex != null && rowIndex != null && colIndex == targetCell.getCol() && rowIndex == targetCell.getRow()) {
                targetNode = node;
                break;
            }
        }

        if (targetNode == null) {
            System.out.println("Keine passende Zelle gefunden für: " + targetCell);
            return;
        } else {
            Bounds cellBounds = targetNode.localToScene(targetNode.getBoundsInLocal());
            System.out.println("Gefundene Zelle an (" + targetCell.getCol() + ", " + targetCell.getRow() + ") mit Bounds: " + cellBounds);
        }

        if (targetNode != null) {
            Bounds cellBounds = targetNode.localToScene(targetNode.getBoundsInLocal());

            // Mittelpunkte berechnen
            double targetX = cellBounds.getMinX() + cellBounds.getWidth() / 2;
            double targetY = cellBounds.getMinY() + cellBounds.getHeight() / 2;

            // Schiffsmittelpunkt berechnen
            ImageView firstPart = (ImageView) ship.getChildren().get(0);
            Bounds shipBounds = firstPart.localToScene(firstPart.getBoundsInLocal());
            double shipCenterX = shipBounds.getMinX() + shipBounds.getWidth() / 2;
            double shipCenterY = shipBounds.getMinY() + shipBounds.getHeight() / 2;

            // Setze das Schiff auf die richtige Position
            ship.setTranslateX(ship.getTranslateX() + targetX - shipCenterX);
            ship.setTranslateY(ship.getTranslateY() + targetY - shipCenterY);
            System.out.println("Setze Schiff auf X: " + ship.getTranslateX() + ", Y: " + ship.getTranslateY());
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
            VBox draggableContainer = new VBox(10);
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
        //grid.setMaxHeight(4000);
        //grid.setMaxWidth(4000);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE, Color.TRANSPARENT);
                cell.setStroke(Color.BLACK);
                //GridPane.setHgrow(cell, Priority.ALWAYS);
                //GridPane.setVgrow(cell, Priority.ALWAYS);
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
        if (isEditable) {
            ship.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    ship.setUserData(new double[]{event.getSceneX(), event.getSceneY(), ship.getTranslateX(), ship.getTranslateY()});
                }
                if (event.getButton() == MouseButton.SECONDARY) {
                    rotateDraggableGroup(ship);
                }
            });

            ship.setOnMouseDragged(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    double[] data = (double[]) ship.getUserData();
                    ship.setTranslateX(data[2] + event.getSceneX() - data[0]);
                    ship.setTranslateY(data[3] + event.getSceneY() - data[1]);
                }
                if (event.getButton() == MouseButton.SECONDARY) {
                    rotateDraggableGroup(ship);
                }
            });

            ship.setOnMouseReleased(event -> {
                snapToGrid(ship);
                updateShipCellMap();
            });
        }
    }

    public void changeEditableState(boolean ye) {
        isEditable = ye;
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

    public Map<Group, Set<Cell>> getShipCellMap() {
        return shipCellMap;
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

    public void selectFeld(int x, int y, Color color) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y) {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(color);
                }
                //Wenn es ein Schiff ist
                if (node instanceof ImageView) {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), node);
                    scaleTransition.setFromX(1.0);
                    scaleTransition.setFromY(1.0);
                    scaleTransition.setToX(0.0);
                    scaleTransition.setToY(0.0);
                    scaleTransition.setCycleCount(1);
                    scaleTransition.setAutoReverse(false);

                    // Start the animation
                    scaleTransition.play();
                }
            }
        }
    }

    public int isShipAtPosition(int x, int y) {
        // Iteriere durch alle Schiffsgruppen und ihre belegten Zellen
        for (Map.Entry<Group, Set<Cell>> entry : shipCellMap.entrySet()) {
            Set<Cell> occupiedCells = entry.getValue();

            // Überprüfe, ob die Zelle (x, y) in der belegten Zellenmenge enthalten ist
            for (Cell cell : occupiedCells) {
                if (cell.getRow() == y && cell.getCol() == x) {
                    // Füge die Trefferzelle hinzu
                    hitCells.add(cell);

                    // Überprüfe, ob alle Zellen dieses Schiffs getroffen sind
                    if (hitCells.containsAll(occupiedCells)) {
                        return 2; // Das gesamte Schiff wurde versenkt
                    }

                    return 1; // Treffen, aber nicht alle Zellen des Schiffs sind getroffen
                }
            }
        }
        return 0; // Kein Treffer
    }

}