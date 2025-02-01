package com.turksat46.schiffgehtunter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Test extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Stage gridStage; // Zweite Stage für die GridPanes
    private GridPane grid1;
    private GridPane grid2;

    @Override
    public void start(Stage primaryStage) {
        // Slider zur Auswahl der Anzahl an Feldern (min 2, max 10)
        Slider gridSizeSlider = new Slider(2, 10, 2);
        gridSizeSlider.setMajorTickUnit(1);
        gridSizeSlider.setMinorTickCount(0);
        gridSizeSlider.setSnapToTicks(true);
        gridSizeSlider.setShowTickMarks(true);
        gridSizeSlider.setShowTickLabels(true);

        Label sliderLabel = new Label("Anzahl der Felder pro Grid: 2");

        // Event-Listener für den Slider
        gridSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int gridSize = newVal.intValue();
            sliderLabel.setText("Anzahl der Felder pro Grid: " + gridSize);
            updateGrids(gridSize);
        });

        // Layout für die erste Stage
        VBox controlLayout = new VBox(10, sliderLabel, gridSizeSlider);
        controlLayout.setPrefSize(300, 150);

        primaryStage.setTitle("Steuerung");
        primaryStage.setScene(new Scene(controlLayout));
        primaryStage.show();

        // Zweite Stage mit den GridPanes starten
        createGridStage(2); // Start mit 2x2 Feldern
    }

    // Erstellt die zweite Stage mit den GridPanes
    private void createGridStage(int gridSize) {
        grid1 = createGridPane(gridSize);
        grid2 = createGridPane(gridSize);

        HBox root = new HBox(20);
        root.getChildren().addAll(grid1, grid2);
        HBox.setHgrow(grid1, Priority.ALWAYS);
        HBox.setHgrow(grid2, Priority.ALWAYS);

        gridStage = new Stage();
        gridStage.setTitle("GridPanes");
        gridStage.setScene(new Scene(root, 800, 400));
        gridStage.show();
    }

    // Aktualisiert die Grids basierend auf dem Slider-Wert
    private void updateGrids(int gridSize) {
        grid1.getChildren().clear();
        grid1.getColumnConstraints().clear();
        grid1.getRowConstraints().clear();
        grid2.getChildren().clear();
        grid2.getColumnConstraints().clear();
        grid2.getRowConstraints().clear();

        fillGridPane(grid1, gridSize);
        fillGridPane(grid2, gridSize);
    }

    // Erstellt ein skalierbares GridPane
    private GridPane createGridPane(int gridSize) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        fillGridPane(gridPane, gridSize);
        return gridPane;
    }

    // Füllt das GridPane mit Buttons und setzt die Constraints
    private void fillGridPane(GridPane gridPane, int gridSize) {
        for (int i = 0; i < gridSize; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / gridSize);
            gridPane.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / gridSize);
            gridPane.getRowConstraints().add(row);
        }

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Button btn = new Button((row + 1) + "," + (col + 1));
                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(btn, Priority.ALWAYS);
                GridPane.setVgrow(btn, Priority.ALWAYS);
                gridPane.add(btn, col, row);
            }
        }
    }

}
