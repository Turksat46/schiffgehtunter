package com.turksat46.schiffgehtunter;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainGameController {
    @FXML
    public Pane spielerstackpane, gegnerstackpane;


    @FXML public HBox container;
    @FXML public Pane images;

    @FXML public HBox label1;
   public static int currentState, currentMode, currentDifficulty, groesse;
    GridPane feld;
    Scene scene;
    private static AI bot;

    boolean rotated;

    Spielfeld spielerspielfeld;
    Spielfeld gegnerspielfeld;


    //Drei States: place = Schiffe platzieren, offense = Angriff, defense = Verteidigung bzw. auf Angriff vom Gegner warten
    public String[] state = {"place", "offense", "defense"};
    //Spielmodus P = Spieler, C=Computer
    public String[] mode = {"PvsC", "PvsP", "CvsC"};


    public void setupSpiel(int groesse, Stage stage, int currentDifficulty, int currentMode, Scene scene) throws FileNotFoundException {
        spielerspielfeld = new Spielfeld(groesse,  false);
        gegnerspielfeld = new Spielfeld(groesse,  true);

        spielerstackpane.getChildren().add(spielerspielfeld.gridPane);
        gegnerstackpane.getChildren().add(gegnerspielfeld.gridPane);

        // StackPane-Margen setzen
        HBox.setMargin(spielerstackpane, new Insets(10, 10, 100, 10)); // Abstand für spielerstackpane
        HBox.setMargin(gegnerstackpane, new Insets(10, 10, 100, 300)); // Abstand für gegnerstackpane


        this.currentMode = currentMode;
        this.groesse = groesse;
        this.scene = scene;
        currentState = 0;
        this.currentDifficulty = currentDifficulty;
        System.out.println("Mode selected and set to: " + mode[currentMode]);
        System.out.println("State selected and set to: " + state[currentState]);
        System.out.println("Difficulty selected and set to: " + currentDifficulty);
        bot = new AI(currentDifficulty, groesse, this);

        generateSchiffe();

        setPausierenEventHandler(stage);
    }

    private EventHandler<KeyEvent> createrRotateShiffe(Pane pane) {
        return event -> {
            if (event.getCode() == KeyCode.R) {
                //System.out.println("Rotate ship");

                // Rechtecke aus der aktuellen Pane extrahieren
                List<Rectangle> rectangles = new ArrayList<>();
                for (var node : pane.getChildren()) {
                    if (node instanceof Rectangle) {
                        rectangles.add((Rectangle) node);
                    }
                }

                Pane newPane;

                if (pane instanceof HBox) {
                    // Erstellen einer VBox, wenn die aktuelle Pane eine HBox ist
                    VBox vbox = new VBox();
                    vbox.setSpacing(5); // Abstand zwischen den Rechtecken
                    vbox.getChildren().addAll(rectangles);
                    newPane = vbox;
                } else if (pane instanceof VBox) {
                    // Erstellen einer HBox, wenn die aktuelle Pane eine VBox ist
                    HBox hbox = new HBox();
                    hbox.setSpacing(10); // Abstand zwischen den Rechtecken
                    hbox.getChildren().addAll(rectangles);
                    newPane = hbox;
                } else {
                    return; // Keine Aktion, wenn die Pane weder HBox noch VBox ist
                }

                // Position der ursprünglichen Pane übernehmen
                newPane.setTranslateX(pane.getTranslateX());
                newPane.setTranslateY(pane.getTranslateY());

                // Eltern-Container der aktuellen Pane finden und die Pane ersetzen
                if (pane.getParent() instanceof Pane) {
                    Pane parent = (Pane) pane.getParent();
                    parent.getChildren().remove(pane);
                    parent.getChildren().add(newPane);

                    // Drag-and-Drop-Logik zur neuen Pane hinzufügen
                    addDragAndDrop(newPane);
                }
            }
        };
    }


    private void generateSchiffe(){

        int currentOffset = 0;
        int offset = 100;
        HBox hbox = null;
        // hier richtige:
        for (int size : spielerspielfeld.schiffe) {

            // Neue HBox für jedes Schiff erstellen
            hbox = new HBox();
            hbox.setSpacing(10);
            hbox.setTranslateY(currentOffset); // Y-Position für Versatz

            // Rechtecke basierend auf der Größe des Schiffs hinzufügen
            for (int i = 0; i < size; i++) {
                Rectangle rectangle = new Rectangle();
                rectangle.setWidth(spielerspielfeld.zellengroesse-10);
                rectangle.setHeight(spielerspielfeld.zellengroesse-10);
                rectangle.setFill(Color.RED);
                rectangle.setStroke(Color.BLACK);

                hbox.getChildren().add(rectangle); // Rechteck zur HBox hinzufügen
            }
            // Drag-and-Drop-Funktion hinzufügen
            addDragAndDrop(hbox);

            // Die HBox zum StackPane hinzufügen
            spielerstackpane.getChildren().add(hbox);

            currentOffset += offset;
        }




    }



    private void addDragAndDrop(Pane pane) {
        final double[] initialMouseX = {0};
        final double[] initialMouseY = {0};

        EventHandler<KeyEvent> rotateShipFilter = createrRotateShiffe(pane);

        pane.setOnMousePressed(event -> {
            // Startposition der Maus speichern
            initialMouseX[0] = event.getSceneX() - pane.getTranslateX();
            initialMouseY[0] = event.getSceneY() - pane.getTranslateY();

            // Eventhandler hinzufügen
            scene.addEventFilter(KeyEvent.KEY_PRESSED, rotateShipFilter);
        });

        pane.setOnMouseDragged(event -> {
            // Pane an neue Position verschieben
            pane.setTranslateX(event.getSceneX() - initialMouseX[0]);
            pane.setTranslateY(event.getSceneY() - initialMouseY[0]);
        });

        pane.setOnMouseReleased(event -> {
            // Aktion, wenn die Maus losgelassen wird
            //System.out.println("Losgelassen");

            // Eventhandler entfernen
            scene.removeEventFilter(KeyEvent.KEY_PRESSED, rotateShipFilter);
        });
        // Setze ein DragDetected-Ereignis auf das Quellrechteck
        pane.setOnDragDetected(event -> {
            pane.startDragAndDrop(TransferMode.COPY);
            System.out.println("Dragging started" + spielerstackpane.getChildren());
            event.consume();
        });

    }

    public void setPausierenEventHandler(Stage stage){
        stage.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode() == KeyCode.ESCAPE){
                    //Spiel pausieren
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("PauseGame.fxml"));
                        Stage stage = new Stage();
                        stage.setTitle("Pause");
                        stage.setScene(new Scene(loader.load()));
                        stage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
    }


    public void handlePrimaryClick(Spielfeld spielfeld, int posx, int posy){
        System.out.println(currentState);
        switch (currentState){
            //Schiffe setzen
            case 0:
                if(!spielfeld.istGegnerFeld){
                    placeShip(spielfeld,posx, posy);
                }else{
                    System.out.println("Spielfeld ist gegner");
                }
                break;

            //Schiffe erschießen
            case 1:
                System.out.println("Klick mit State 1 entdeckt");
                shootShip(spielfeld, posx, posy);
                break;

            //Schiffe beobachten (Spieler: Klicks ignorieren)
            case 2:
                watchShip(spielfeld, posx, posy);
                break;

            default:
                //Errorstate, weil es nicht mehr als 3 states gibt
        }
    }

    public void handleSecondaryClick(Spielfeld spielfeld, int posx, int posy){
        if (currentState == 0){
            //Schiffe entfernen
            if(spielfeld.felder[posx][posy].gesetzt){
                spielfeld.felder[posx][posy].gesetzt = false;
                spielfeld.deselectRowAndColumn(spielfeld, posx, posy);
                System.out.println("Feld wurde abgewählt");
            }
        }
    }

    public void setFeld(GridPane feld){
        this.feld = feld;
    }

    public int nachbarFeldGewaehlt(Spielfeld spielfeld, int posx, int posy){
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int x = posx + dir[0];
            int y = posy + dir[1];

            // Überprüfen, ob die Koordinaten innerhalb des Spielfelds liegen
            if (x >= 0 && x < spielfeld.groesse && y >= 0 && y < spielfeld.groesse) {
                if (spielfeld.felder[x][y].gesetzt) {
                    // Färbe das aktuelle und das Nachbarfeld grün
                    spielfeld.felder[x][y].setFill(Color.GREEN);
                    spielfeld.felder[posx][posy].setFill(Color.GREEN);

                    break; // Sobald ein gesetztes Nachbarfeld gefunden wurde, abbrechen
                }
            }
        }


        // Prüfe die Nachbarschaft und bestimme die Paarlänge
        int maxLength = 1; // Mindestens das aktuelle Feld
        for (int[] dir : directions) {
            int lengthInDir = getPairLength(spielfeld, posx, posy, dir[0], dir[1]) +
                    getPairLength(spielfeld, posx, posy, -dir[0], -dir[1]);
            maxLength = Math.max(maxLength, lengthInDir + 1);
        }

        return maxLength;
    }

    private int getPairLength(Spielfeld spielfeld, int x, int y, int dx, int dy) {

        int length = 0;
        int nx = x + dx;
        int ny = y + dy;

        while (nx >= 0 && nx < spielfeld.groesse && ny >= 0 && ny < spielfeld.groesse && spielfeld.felder[nx][ny].gesetzt) {
            length++;
            nx += dx;
            ny += dy;
        }
        return length;
    }

    //Gibt sortierte Positionen benachbarter gesetzter Felder zurück.
    private List<int[]> getPairPositionen(Spielfeld spielfeld, int posx, int posy) {
        List<int[]> positionen = new ArrayList<>();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int x = posx + dir[0];
            int y = posy + dir[1];
            while (x >= 0 && x < spielfeld.groesse && y >= 0 && y < spielfeld.groesse) {
                if (spielfeld.felder[x][y].gesetzt) {
                    positionen.add(new int[]{x, y});
                } else {
                    break;
                }
                x += dir[0];
                y += dir[1];
            }
        }
        positionen.add(new int[]{posx, posy});

        positionen.sort((p1, p2) -> {
            if (p1[0] != p2[0]) {
                return Integer.compare(p1[0], p2[0]);
            } else {
                return Integer.compare(p1[1], p2[1]);
            }
        });

        return positionen;
    }

// schau hier mal ob des nur ein vorkommen löscht aus array list ist noc unsiche
    // und gucken ob des effizient ist wird ja immer geschlieift wenn man clicked
    private void placeShip(Spielfeld spielfeld, int posx, int posy){
            if (!spielfeld.felder[posx][posy].gesetzt) {
                spielfeld.felder[posx][posy].gesetzt = true;
                spielfeld.selectFeld(posx, posy);
            // Entferne das passende Schiff aus der Liste
            int nachbarWert = nachbarFeldGewaehlt(spielfeld, posx, posy); // Berechnung nur einmal

               //Suche das Schiff in der Liste und entferne ein Vorkommen
                for (int i = 0; i < spielfeld.schiffe.size(); i++) {
                    int schiff = spielfeld.schiffe.get(i);
                    if (schiff == nachbarWert) {
                        spielfeld.schiffe.remove(i);
                        System.out.println("Schiff mit Wert " + nachbarWert + " aus der Liste entfernt.");
                        // Da die Liste durch das Entfernen eines Elements kleiner wird,
                        // muss der Index um eins reduziert werden, um das nächste Element korrekt zu überprüfen.
                        i--;
                       break;
                    }
                }
                System.out.println("Hier ist schiffe von spieler die zur Asuwahl stehen : " +spielfeld.schiffe);

                if (spielfeld.schiffe.isEmpty()) {
                        System.out.println("State wird auf 1 gesetzt");
                        currentState++;
                    }
                }

    }

    public void handleHit(int x, int y){
        gegnerspielfeld.selectFeld(x,y,Color.GREEN);
    }

    public void handleWinForPlayer(){
        //TODO: @Elion mach was draus
        System.out.println("Spieler hat gewonnen!");
    }

    public void handleWinForOpponent(){
        //TODO: @Elion mach was draus
        System.out.println("Gegner hat gewonnen!");
    }

    private void  shootShip(Spielfeld spielfeld, int posx, int posy){
        System.out.println("schiffe erschießen");
        if(spielfeld.istGegnerFeld){
            if(spielfeld.felder[posx][posy].wurdeGetroffen == false){
                currentState = 2;
                System.out.println("Feld ist gegnerfeld");
                spielfeld.felder[posx][posy].wurdeGetroffen = true;
                spielfeld.selectFeld(posx,posy);
                bot.receiveMove(posx, posy);
            }else{
                //Feld wurde bereits getroffen
                System.out.println("Feld wurde bereits getroffen. Klick wird ignoriert!");
            }
        }
    }

    public void receiveShoot(int posx, int posy){
        spielerspielfeld.selectFeld(posx, posy, Color.DARKRED);
        bot.receiveHit(posx, posy, spielerspielfeld.felder[posx][posy].istSchiff);
        currentState = 1;
    }

    private void  watchShip(Spielfeld spielfeld, int posx, int posy){
        System.out.println("schiffe beobachten ");
    }

}