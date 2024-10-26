    package com.turksat46.schiffgehtunter;

    import com.turksat46.schiffgehtunter.other.Feld;
    import javafx.scene.layout.GridPane;
    import javafx.scene.layout.StackPane;
    import javafx.scene.paint.Color;
    import javafx.scene.shape.Rectangle;
    import javafx.scene.text.Text;
    import javafx.stage.Stage;
    import org.jetbrains.annotations.NotNull;

    public class Spielfeld {

        public GridPane gridPane;
        double zelleWidth;
        double zelleHeight;
        int[][] feld;

        //TODO: Für die Felder eigene Klasse
        Feld felder;

        public Spielfeld (int groesse, @NotNull Stage stage){
            gridPane = new GridPane();

            this.feld= new int [groesse][groesse];

            //TODO: richtig initialisieren und nutzen
            felder = new Feld(feld);


            //Listener damit man beim rezizen die stage width hat und mit groesse (also spielfeldgroesse) dann einzelne cellwidth and height ausrechnen
            stage.widthProperty().addListener((obs, oldVal, newVal) -> {

              System.out.println("Spielfeld width: " + stage.getWidth());
                zelleWidth = (int)(stage.getWidth() /groesse);

                System.out.println("zelleWidth: " + stage.getWidth());
            });

            stage.heightProperty().addListener((obs, oldVal, newVal) -> {
                System.out.println("Spielfeld height: " + newVal);

                zelleHeight = (int) (stage.getHeight() /groesse);
                System.out.println("zelleWidth: " + zelleWidth);
            });

            // Schleife zur Erstellung der Zellen (als Rectangle mit Text)
            for (int i = 0; i < groesse; i++) {
                for (int j = 0; j < groesse; j++) {
                    int row = i;
                    int col = j;
                    this.feld[row][col] = 0;

                    // Rechteck und Text erstellen
                    Rectangle cell = new Rectangle(zelleWidth, zelleHeight);
                    cell.setFill(Color.LIGHTBLUE);
                    cell.setStroke(Color.BLACK);

                    Text cellText = new Text(String.valueOf(feld[row][col]));

                    // StackPane für Rechteck und Text in einer Zelle
                    StackPane cellPane = new StackPane();
                    cellPane.getChildren().addAll(cell, cellText);

                    // Klick-Event für jede Zelle und aktualisiert einen neuen wert später also das löschen
                    cellPane.setOnMouseClicked(event -> {
                        feld[row][col] = 1;
                        cellText.setText("1");
                        System.out.println(cell.getHeight());
                    });

                    // Zelle dem GridPane hinzufügen
                    gridPane.add(cellPane, j, i);
                }
            }
        }
    }