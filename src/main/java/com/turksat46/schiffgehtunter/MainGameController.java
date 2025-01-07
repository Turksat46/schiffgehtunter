package com.turksat46.schiffgehtunter;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MainGameController implements Initializable {

    private static int WIDTH = 2500;
    private static int HEIGHT = 600;
    private static final int TILE_SIZE = 30;
    private static final int WORLD_WIDTH_TILES = 37;
    private static final int WORLD_HEIGHT_TILES = 25;
    private static final double SHADOW_OFFSET_X = 2;
    private static final double SHADOW_OFFSET_Y = 2;

    private Tile[][] world;
    private double playerX;
    private double playerY;
    private Image sandTexture;
    private Image waterTexture;

    private Canvas backgroundCanvas;


    public enum Tile {
        WATER, SAND
    }

    @FXML
    public Pane spielerstackpane, gegnerstackpane;

    @FXML public AnchorPane anchorPane;

    @FXML public HBox container;
    @FXML public Pane images;

    @FXML public HBox label1;
   public static int currentState, currentMode, currentDifficulty, groesse;
    static GridPane feld;
    static Scene scene;
    private static AI bot;

    static boolean rotated;

    static Spielfeld spielerspielfeld;
    static Spielfeld gegnerspielfeld;


    //Drei States: place = Schiffe platzieren, offense = Angriff, defense = Verteidigung bzw. auf Angriff vom Gegner warten
    public String[] state = {"place", "offense", "defense"};
    //Spielmodus P = Spieler, C=Computer
    public String[] mode = {"PvsC", "PvsP", "CvsC"};


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createBackground();
    }

    private void createBackground() {
        backgroundCanvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = backgroundCanvas.getGraphicsContext2D();

        // Lade die Texturen
        try {
            sandTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/images/sand_texture.png")));
            waterTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/images/water_animated.gif")));
        } catch (NullPointerException e) {
            System.err.println("Fehler beim Laden der Texturen. Stelle sicher, dass die Dateien im Ressourcenordner liegen.");
            throw e;
        }

        generateWorld();
        playerX = WIDTH / 2.0;
        playerY = HEIGHT / 2.0;


        new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw(gc);
            }
        }.start();
    }

    private void generateWorld() {
        world = new Tile[WORLD_WIDTH_TILES][WORLD_HEIGHT_TILES];
        // Standardmäßig alles mit Wasser füllen
        for (int x = 0; x < WORLD_WIDTH_TILES; x++) {
            for (int y = 0; y < WORLD_HEIGHT_TILES; y++) {
                world[x][y] = Tile.WATER;
            }
        }

        int islandStartY = WORLD_HEIGHT_TILES - 5;
        Random random = new Random();

        // Insel in der Mitte erstellen
        int middleXStart = WORLD_WIDTH_TILES / 2 - 2; // Starte etwas links von der Mitte
        int middleXEnd = WORLD_WIDTH_TILES / 2 + 2;   // Ende etwas rechts von der Mitte

        for (int x = middleXStart; x <= middleXEnd; x++) {
            for (int y = 2; y < WORLD_HEIGHT_TILES - 2; y++) { // Insel geht von unten nach oben, etwas Platz lassen
                // Füge etwas Zufall hinzu, um die Inselform interessanter zu gestalten
                // Innere Teile der Insel immer Sand
                if (x > middleXStart && x < middleXEnd) {
                    world[x][y] = Tile.SAND;
                } else {
                    // Zufällige Entscheidung für die Seiten
                    if (random.nextDouble() > 0.4) { // Hier kannst du die Wahrscheinlichkeit anpassen
                        world[x][y] = Tile.SAND;
                    }
                }
            }
        }


        for (int x = 0; x < WORLD_WIDTH_TILES; x++) {
            for (int y = islandStartY; y < WORLD_HEIGHT_TILES; y++) {
                if (y == islandStartY && random.nextDouble() < 0.3) { // Wahrscheinlichkeit für Wasser am oberen Rand der Insel
                    continue;
                }
                world[x][y] = Tile.SAND;
            }
        }
    }

    private javafx.scene.paint.Color getShadowColor(javafx.scene.paint.Color baseColor) {
        return baseColor.darker();
    }

    private void draw(GraphicsContext gc) {
        //gc.clearRect(0, 0, WIDTH, HEIGHT);

        double cameraOffsetX = playerX - WIDTH / 2.0;
        double cameraOffsetY = playerY - HEIGHT / 2.0 +150;

        // Zeichne die Schatten der Sandblöcke
        for (int x = 0; x < WORLD_WIDTH_TILES; x++) {
            for (int y = 0; y < WORLD_HEIGHT_TILES; y++) {
                double tileX = x * TILE_SIZE - cameraOffsetX;
                double tileY = y * TILE_SIZE - cameraOffsetY;

                if (tileX + TILE_SIZE > 0 && tileX < WIDTH && tileY + TILE_SIZE > 0 && tileY < HEIGHT) {
                    if (world[x][y] ==  Tile.SAND) {
                        gc.setFill(getShadowColor(javafx.scene.paint.Color.YELLOW));
                        gc.fillRect(tileX + SHADOW_OFFSET_X, tileY + SHADOW_OFFSET_Y, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        // Zeichne die Welt mit Texturen
        for (int x = 0; x < WORLD_WIDTH_TILES; x++) {
            for (int y = 0; y < WORLD_HEIGHT_TILES; y++) {
                double tileX = x * TILE_SIZE - cameraOffsetX;
                double tileY = y * TILE_SIZE - cameraOffsetY;

                if (tileX + TILE_SIZE > 0 && tileX < WIDTH && tileY + TILE_SIZE > 0 && tileY < HEIGHT) {
                    if (world[x][y] ==  Tile.WATER) {
                        gc.drawImage(waterTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                    } else if (world[x][y] ==  Tile.SAND) {
                        gc.drawImage(sandTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        SnapshotParameters params = new SnapshotParameters();
        WritableImage image = backgroundCanvas.snapshot(params, null);

        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        Background background = new Background(backgroundImage);
        anchorPane.setBackground(background);
    }

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
        /*scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            WIDTH = newValue.intValue();
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            HEIGHT = newValue.intValue();
        });
        */
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
                    vbox.setSpacing(5);
                    vbox.getChildren().addAll(rectangles);
                    newPane = vbox;
                } else if (pane instanceof VBox) {
                    // Erstellen einer HBox, wenn die aktuelle Pane eine VBox ist
                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.getChildren().addAll(rectangles);
                    newPane = hbox;
                } else {
                    return;
                }

                // Position der ursprünglichen Pane übernehmen
                newPane.setTranslateX(pane.getTranslateX());
                newPane.setTranslateY(pane.getTranslateY());

                // Eltern-Container der aktuellen Pane finden und die Pane ersetzen
                if (pane.getParent() instanceof Pane) {
                    Pane parent = (Pane) pane.getParent();
                    parent.getChildren().remove(pane);
                    parent.getChildren().add(newPane);

                    // Drag and drop Logik zur neuen Pane hinzufügen
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

            for (int i = 0; i < size; i++) {
                Rectangle rectangle = new Rectangle();
                rectangle.setWidth(spielerspielfeld.zellengroesse-10);
                rectangle.setHeight(spielerspielfeld.zellengroesse-10);
                rectangle.setFill(Color.RED);
                rectangle.setStroke(Color.BLACK);

                hbox.getChildren().add(rectangle);
            }
            // Drag-and-Drop hinzufügen
            addDragAndDrop(hbox);


            spielerstackpane.getChildren().add(hbox);

            currentOffset += offset;
        }
    }

    private void addDragAndDrop(Pane pane) {
        final double[] initialMouseX = {0};
        final double[] initialMouseY = {0};

        EventHandler<KeyEvent> rotateShipFilter = createrRotateShiffe(pane);

        pane.setOnMousePressed(event -> {

            initialMouseX[0] = event.getSceneX() - pane.getTranslateX();
            initialMouseY[0] = event.getSceneY() - pane.getTranslateY();


            scene.addEventFilter(KeyEvent.KEY_PRESSED, rotateShipFilter);
        });

        pane.setOnMouseDragged(event -> {
            // Pane an neue Position verschieben
            pane.setTranslateX(event.getSceneX() - initialMouseX[0]);
            pane.setTranslateY(event.getSceneY() - initialMouseY[0]);
        });

        pane.setOnMouseReleased(event -> {
            // Aktion, wenn die Maus losgelassen wird
            scene.removeEventFilter(KeyEvent.KEY_PRESSED, rotateShipFilter);
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