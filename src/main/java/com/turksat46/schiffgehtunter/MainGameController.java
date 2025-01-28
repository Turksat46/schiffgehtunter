package com.turksat46.schiffgehtunter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.turksat46.schiffgehtunter.backgroundgeneration.BackgroundGenerator;
import com.turksat46.schiffgehtunter.filemanagement.SaveData;
import com.turksat46.schiffgehtunter.filemanagement.SaveFileManager;
import com.turksat46.schiffgehtunter.other.Music;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    @FXML public HBox container;
    @FXML public Pane images;

    @FXML public HBox label1;

    @FXML public Button startButton;

   public static int currentState, currentMode, currentDifficulty, groesse;
    static GridPane feld;
    static Scene scene;
    private static AI bot;

    Music soundPlayer;

    static boolean rotated;

    static newSpielfeld spielerspielfeld;
    static Spielfeld gegnerspielfeld;

    BackgroundGenerator backgroundManager;

    //Drei States: place = Schiffe platzieren, offense = Angriff, defense = Verteidigung bzw. auf Angriff vom Gegner warten
    public String[] state = {"place", "offense", "defense"};
    //Spielmodus P = Spieler, C=Computer
    public String[] mode = {"PvsC", "PvsP", "CvsC"};

    public MainGameController(){
        saveFileManager = new SaveFileManager();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Wenn Klasse initialisiert wird, Hintergrund erstellen
        backgroundManager = new BackgroundGenerator(anchorPane);
        backgroundManager.createBackground();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-game-view.fxml"));
        Parent root = loader.getRoot();


        MainGameController controller = loader.getController();



    }

    public void startGame(){
        currentState = 1;
        spielerspielfeld.changeEditableState(false);
    }

    public void setupSpiel(int groesse, Stage stage, int currentDifficulty, int currentMode, Scene scene) throws FileNotFoundException {
        //spielerspielfeld = new Spielfeld(groesse,  false);
        gegnerspielfeld = new Spielfeld(groesse,  true);

        spielerspielfeld = new newSpielfeld(groesse, false, spielerstackpane);
        //gegnerspielfeld = new newSpielfeld(groesse, true, gegnerstackpane);

        //spielerstackpane.getChildren().add(newSpielfeld.gridPane);
        gegnerstackpane.getChildren().add(gegnerspielfeld.gridPane);

        // StackPane-Margen setzen
        //HBox.setMargin(spielerstackpane, new Insets(10, 10, 100, 10)); // Abstand für spielerstackpane
        //HBox.setMargin(gegnerstackpane, new Insets(10, 10, 100, 150)); // Abstand für gegnerstackpane


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

    // schau hier mal ob des nur ein vorkommen löscht aus array list ist noc unsiche
    // und gucken ob des effizient ist, wird ja immer geschlieift wenn man clicked
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
        spielerspielfeld.selectFeld(posx, posy, Color.DARKRED);
        //bot.receiveHit(posx, posy, spielerspielfeld.felder[posx][posy].istSchiff);
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
        String data = gson.toJson(saveData.sampleData());
        saveFileManager.openSaveFileChooserAndSave(data);
    }

}