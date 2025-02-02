package com.turksat46.schiffgehtunter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.turksat46.schiffgehtunter.backgroundgeneration.BackgroundGenerator;
import com.turksat46.schiffgehtunter.filemanagement.SaveData;
import com.turksat46.schiffgehtunter.filemanagement.SaveFileManager;
import com.turksat46.schiffgehtunter.other.Cell;
import com.turksat46.schiffgehtunter.other.Music;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MainGameController implements Initializable {

    private SaveFileManager saveFileManager;


    @FXML
    public BorderPane spielerstackpane, gegnerstackpane;

    @FXML AnchorPane anchorPane;
    @FXML
    Label hinweistext;

    @FXML public HBox container;
    @FXML public Pane images;

    @FXML public HBox label1;

    @FXML public Button startButton;

    @FXML HBox draggableContainer;

    public static int currentState, currentMode, currentDifficulty, groesse;
    static GridPane feld;
    static Scene scene;
    private static AI bot;

    Music soundPlayer;

    static boolean rotated;

    static newSpielfeld spielerspielfeld;
    public static Spielfeld gegnerspielfeld;

    BackgroundGenerator backgroundManager;

    //Drei States: place = Schiffe platzieren, offense = Angriff, defense = Verteidigung bzw. auf Angriff vom Gegner warten
    public String[] state = {"place", "offense", "defense"};
    //Spielmodus P = Spieler, C=Computer
    public String[] mode = {"PvsC", "PvsP", "CvsC"};

    public Map<Group, Set<Cell>> shipCellMap;
    private final Set<Cell> hitCells = new HashSet<>();

    public MainGameController(){
        saveFileManager = new SaveFileManager();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Wenn Klasse initialisiert wird, Hintergrund erstellen
        //backgroundManager = new BackgroundGenerator(anchorPane);
        //backgroundManager.createBackground();

        try {
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/images/background.png")));
            BackgroundImage background = new BackgroundImage(
                    backgroundImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );

            anchorPane.setBackground(new Background(background));
        } catch (NullPointerException e) {
            System.err.println("Fehler beim Laden der Texturen. Stelle sicher, dass die Dateien im Ressourcenordner liegen.");
            throw e;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-game-view.fxml"));
        Parent root = loader.getRoot();


        MainGameController controller = loader.getController();



    }

    public void startGame(){
        currentState = 1;
        spielerspielfeld.changeEditableState(false);
        shipCellMap = spielerspielfeld.getShipCellMap();
        hinweistext.setVisible(false);
        startButton.setVisible(false);
    }

    public void setupSpiel(int groesse, Stage stage, int currentDifficulty, int currentMode, Scene scene) throws FileNotFoundException {
        //spielerspielfeld = new Spielfeld(groesse,  false);
        gegnerspielfeld = new Spielfeld(groesse,  true, false, gegnerstackpane);

        spielerspielfeld = new newSpielfeld(groesse, false, spielerstackpane, draggableContainer);
        //gegnerspielfeld = new newSpielfeld(groesse, true, gegnerstackpane);

        //spielerstackpane.getChildren().add(newSpielfeld.gridPane);
        //agegnerstackpane.getChildren().add(gegnerspielfeld.gridPane);

        if(groesse <= 7){
            stage.setMinWidth(1110);
            stage.setMinHeight(650);
        }else if(groesse <= 15){
            stage.setMinWidth(1510);
            stage.setMinHeight(800);
        }else if(groesse > 15){
            stage.setMaximized(true);
        }

        // StackPane-Margen setzen
        HBox.setMargin(spielerstackpane, new Insets(10, 10, 100, 10)); // Abstand für spielerstackpane
        HBox.setMargin(gegnerstackpane, new Insets(10, 10, 100, 300)); // Abstand für gegnerstackpane

        setupBase(groesse, stage, currentDifficulty, currentMode, scene);

    }

    // Fürs Laden von einem gespeicherten Spiel
    public void setupSpiel(Stage stage, Scene scene, Map<String, Object> data) throws FileNotFoundException {
        //spielerspielfeld = new Spielfeld(groesse,  false);
        Double groessedouble = (double) data.get("groesse");
        int groesse = groessedouble.intValue();

        Double currentDifDouble = (double) data.get("currentDifficulty");
        int currentDifficulty = currentDifDouble.intValue();

        Double currentModeDouble = (double) data.get("currentMode");
        int currentMode = currentModeDouble.intValue();

        gegnerspielfeld = new Spielfeld(groesse,  true, false, gegnerstackpane);

        spielerspielfeld = new newSpielfeld(groesse, false, spielerstackpane, data);

        //gegnerspielfeld = new newSpielfeld(groesse, true, gegnerstackpane);

        //spielerstackpane.getChildren().add(newSpielfeld.gridPane);
        //gegnerstackpane.getChildren().add(gegnerspielfeld.gridPane);

        // StackPane-Margen setzen
        //HBox.setMargin(spielerstackpane, new Insets(10, 10, 100, 10)); // Abstand für spielerstackpane
        //HBox.setMargin(gegnerstackpane, new Insets(10, 10, 100, 150)); // Abstand für gegnerstackpane

        setupBase(groesse, stage, currentDifficulty, currentMode, scene, data);

    }

    public void setupBase (int groesse,Stage stage, int currentDifficulty, int currentMode, Scene scene) throws FileNotFoundException {


        this.currentMode = currentMode;
        this.groesse = groesse;
        MainGameController.scene = scene;
        System.out.println(MainGameController.scene);
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

        //generateSchiffe();

        setPausierenEventHandler(stage);

        saveFileManager = new SaveFileManager();
    }
    // Fürs Laden von einem Spielstand
    public void setupBase (int groesse,Stage stage, int currentDifficulty, int currentMode, Scene scene, Map<String, Object> data) throws FileNotFoundException {


        this.currentMode = currentMode;
        this.groesse = groesse;
        MainGameController.scene = scene;
        System.out.println(MainGameController.scene);
        /*scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            WIDTH = newValue.intValue();
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            HEIGHT = newValue.intValue();
        });
        */
        this.currentDifficulty = currentDifficulty;
        System.out.println("Mode selected and set to: " + mode[currentMode]);
        System.out.println("State selected and set to: " + state[currentState]);
        System.out.println("Difficulty selected and set to: " + currentDifficulty);
        bot = new AI(currentDifficulty, groesse, this, data);

        //generateSchiffe();

        setPausierenEventHandler(stage);

        saveFileManager = new SaveFileManager();
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

    public void handleHit(int x, int y){
        //gegnerspielfeld.selectFeld(x,y,Color.GREEN);
    }

    public void handleWinForPlayer(){
        Stage victoryStage = new Stage();
        Image image;
        Font customFont;
        try {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/images/water_animated.gif")));
            customFont = Font.loadFont(getClass().getResource("/com/turksat46/schiffgehtunter/MinecraftRegular-Bmg3.otf").toExternalForm(), 40);

        } catch (NullPointerException e) {
            System.err.println("Fehler beim Laden der Texturen. Stelle sicher, dass die Dateien im Ressourcenordner liegen.");
            throw e;
        }

        StackPane root = new StackPane();

        // Background image
        Rectangle background = new Rectangle(600, 400);

        background.setFill(new ImagePattern(image));

        // Victory message
        Text victoryText = new Text("VICTORY FOR PLAYER !");
        victoryText.setFill(javafx.scene.paint.Color.WHITE);
        victoryText.setFont(customFont);

        // Adding animations
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), victoryText);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(3);
        fadeIn.setAutoReverse(true);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(1.5), victoryText);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(1.2);
        scale.setToY(1.2);
        scale.setCycleCount(3);
        scale.setAutoReverse(true);

        // Start animations
        fadeIn.play();
        scale.play();
        soundPlayer = Music.getInstance();
        soundPlayer.playWin();
        // Add elements to root
        root.getChildren().addAll(background, victoryText);

        // Show stage
        Scene scene = new Scene(root, 600, 400);
        victoryStage.setTitle("Victory!");
        victoryStage.setScene(scene);
        victoryStage.show();
        victoryStage.toFront();
    }

    public void handleWinForOpponent(){
        Stage victoryStage = new Stage();
        Image image;
        Font customFont;
        try {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/images/water_animated.gif")));
            customFont = Font.loadFont(getClass().getResource("/com/turksat46/schiffgehtunter/MinecraftRegular-Bmg3.otf").toExternalForm(), 40);

        } catch (NullPointerException e) {
            System.err.println("Fehler beim Laden der Texturen. Stelle sicher, dass die Dateien im Ressourcenordner liegen.");
            throw e;
        }

        StackPane root = new StackPane();

        // Background image
        Rectangle background = new Rectangle(600, 400);

        //Setting the image view 1
        //mageView imageView1 = new ImageView(image);


        background.setFill(new ImagePattern(image));
        // Simulated Minecraft "blocks"

        // Victory message
        Text victoryText = new Text("VICTORY FOR OPPONENT !");
        victoryText.setFill(javafx.scene.paint.Color.WHITE);
        victoryText.setFont(customFont);

        // Adding animations
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), victoryText);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(3);
        fadeIn.setAutoReverse(true);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(1.5), victoryText);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(1.2);
        scale.setToY(1.2);
        scale.setCycleCount(3);
        scale.setAutoReverse(true);

        // Start animations
        fadeIn.play();
        scale.play();

        // Add elements to root
        root.getChildren().addAll(background, victoryText);

        // Show stage
        Scene scene = new Scene(root, 600, 400);
        victoryStage.setTitle("Victory!");
        victoryStage.setScene(scene);
        victoryStage.show();
        victoryStage.toFront();
    }

    private void shootShip(Spielfeld spielfeld, int posx, int posy){
        System.out.println("schiffe erschießen");
        if(spielfeld.istGegnerFeld){
            if(spielfeld.felder[posx][posy].wurdeGetroffen == false){
                currentState = 2;
                System.out.println("Feld ist gegnerfeld");
                spielfeld.felder[posx][posy].wurdeGetroffen = true;
                spielfeld.selectFeld(posx,posy);
                bot.receiveMove(posx, posy, spielfeld);
                //animateSnowball(posx, posy);
            }else{
                //Feld wurde bereits getroffen
                System.out.println("Feld wurde bereits getroffen. Klick wird ignoriert!");
            }
        }
    }

    public void receiveShoot(int posx, int posy){
        System.out.println("receiveShot at: " + posx + ", " + posy);
        spielerspielfeld.selectFeld(posx, posy, Color.DARKRED);

        boolean shipHit = false;
        boolean wholeShipDestroyed = false;
        Set<Cell> destroyedShipCells = null;

        Iterator<Map.Entry<Group, Set<Cell>>> iterator = shipCellMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Group, Set<Cell>> entry = iterator.next();
            Group schiffGroup = entry.getKey();
            Set<Cell> occupiedCells = entry.getValue();
            System.out.println(shipCellMap.toString());

            for (Cell cell : occupiedCells) {
                if (cell.getRow() == posy && cell.getCol() == posx) {
                    shipHit = true;
                    hitCells.add(cell);

                    if (hitCells.containsAll(occupiedCells)) {
                        wholeShipDestroyed = true;
                        destroyedShipCells = occupiedCells;
                        iterator.remove(); // Entferne das versenkte Schiff
                    }
                    break; // Schiff getroffen, innere Schleife abbrechen
                }
            }
        }

        if (shipHit) {
            if (wholeShipDestroyed) {
                System.out.println("Schiff komplett zerstört an Position: " + posx + ", " + posy);
                if (shipCellMap.isEmpty()) {
                    bot.allShipsShot();
                }
                bot.receiveHit(posx, posy, true, true);
            } else {
                System.out.println("Schiff getroffen an Position: " + posx + ", " + posy);
                bot.receiveHit(posx, posy, true, false);
            }
        } else {
            System.out.println("Kein Schiff getroffen an Position: " + posx + ", " + posy);
            bot.receiveHit(posx, posy, false, false);
        }

        currentState = 1;
    }

    private void  watchShip(Spielfeld spielfeld, int posx, int posy){
        System.out.println("schiffe beobachten ");
    }

    //
    //  SaveFileProcedure
    //

    public void saveFile(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        SaveData saveData = new SaveData(this, spielerspielfeld, gegnerspielfeld, bot);
        saveFileManager.openSaveFileChooserAndSave(saveData.sampleData());
    }

}