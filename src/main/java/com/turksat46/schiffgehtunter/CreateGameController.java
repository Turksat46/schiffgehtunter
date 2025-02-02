package com.turksat46.schiffgehtunter;
import com.turksat46.schiffgehtunter.filemanagement.SaveFileManager;
import com.turksat46.schiffgehtunter.netzwerk.Server;
import com.turksat46.schiffgehtunter.netzwerk.establishConnection;
import com.turksat46.schiffgehtunter.other.Music;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Map;


/**
 * Das ist der Controller also die Logik für das CreateGameView
 */
public class CreateGameController {

    /**
     * cb ist schwierigkeit der KI
     * cb2 ist Spielstrategie
     * groesseslider ist die ausgwählte grid größe
     * grossetextfield auch die grid größe
     *
     */
    Music soundPlayer = Music.getInstance();
    @FXML
    ChoiceBox cb = new ChoiceBox();
    @FXML
    ChoiceBox cb2 = new ChoiceBox();
    @FXML
    Slider groesseslider = new Slider();
    @FXML
    TextField groessetextfield = new TextField();
    @FXML
    Label ladenhinweislabel = new Label();
    @FXML
    HBox kiDifficultyUI = new HBox();
    MainGameController mainGameController;
    MultipayerMainGameController multipayerMainGameController;
    SaveFileManager saveFileManager;
    ObservableList<String> skillLevels = FXCollections.observableArrayList("Noob", "Average", "Hardcore");
    ObservableList<String> gameModes = FXCollections.observableArrayList("Spieler vs. Computer", "Spieler vs. Spieler", "Computer vs. Computer");


    /**
     * Es werden die choiceboxen mit den Daten für das skillLevel und spielstrategie befüllt.
     * Und ebenso die Daten gespeichert der grid größe
     */
    public void initialize() {
        cb.setItems(skillLevels);
        cb2.setItems(gameModes);


        cb.setValue(skillLevels.get(0));
        cb2.setValue(gameModes.get(0));

        cb2.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                onStrategieChanged();
            }
        });

        groessetextfield.setText(Double.toString(groesseslider.getValue()));
        groesseslider.valueProperty().addListener((observable, oldValue, newValue) -> {
            groessetextfield.setText(Double.toString(newValue.intValue()));
        });
        groessetextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            groesseslider.setValue(Double.valueOf(newValue));
        });

        mainGameController = new MainGameController();
        saveFileManager = new SaveFileManager();
    }

    /**
     * Eventhandler für das wechseln der Spielstrategie.
     */
    public void onStrategieChanged() {
        if (cb2.getSelectionModel().getSelectedIndex() == 0) {
            kiDifficultyUI.setVisible(true);
        } else {
            kiDifficultyUI.setVisible(false);
        }
    }

    /**
     * Hier wird getestet wie welche Strategie gewählt wurde und je nachdem wird hier StartSinglePlayer oder OpenconnectionSetup aufgerufen
     * @throws IOException
     */
    public void startGame() throws IOException {
        if (cb2.getSelectionModel().getSelectedIndex() == 0) {
            startSinglePlayerGame();
        } else {
            // Start multiplayer setup
            openConnectionSetup();
        }
    }

    /**
     * Hier wird die Main-game view geladen und im fenster angezeigt.
     * Ebenso wird der controller dafür geladen und das setupspiel darin wird aufgerufen
     * @throws IOException
     */
    private void startSinglePlayerGame() throws IOException {
        soundPlayer.playSound();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        //stage.setResizable(false);
        stage.setTitle("Spielfeld");
        stage.setScene(scene);

        mainGameController = fxmlLoader.getController();
        mainGameController.setupSpiel((int) groesseslider.getValue(), stage, cb.getSelectionModel().getSelectedIndex(), cb2.getSelectionModel().getSelectedIndex(), scene);
        stage.show();

        Stage thisstage = (Stage) cb.getScene().getWindow();
        thisstage.close();
    }




    /**
     * Hier wird die EstablishConnection view geladen in der dem Serer die grid groesse gegeben wurde.
     * Es wird ebenso gewartet bis ein Spieler sich verbindet.
     *  @throws IOException
     */
    private void openConnectionSetup() throws IOException {
        soundPlayer.playSound();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("establishConnection.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Verbindung Aufbauen");
        stage.setScene(scene);
        stage.show();

        Server.setGroesse((int) groesseslider.getValue());

        establishConnection controller = fxmlLoader.getController();
        controller.initialize(stage);
        Stage thisStage = (Stage) cb.getScene().getWindow();
        thisStage.close();
        waitForConnectionAndStartGame(stage);
    }


    /**
     * Wenn die Verbindung erfolgreich war dann soll hier die funktion startMultiplayer aufgerufen werden.
     * @param stage damit man die selbe stage benutzt
     * @throws IOException
     */
    private void waitForConnectionAndStartGame(Stage stage) throws IOException {
        new Thread(() -> {
            while (!Server.connectionEstablished) {
                try {
                    Thread.sleep(100); // Checke im takt
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // wenn connection erfolgreich dann start Muliplayer
            Platform.runLater(() -> {
                try {
                    startMultiplayerGame(stage, (int) groesseslider.getValue());
                    Server.releaseLatch();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    /**
     * Hier wird die Multiplayer view geladen und der Controller geladen.
     * Der controller ruft setupSpiel auf um das spiel zu initialisieren.
     * @param connectionStage stage der connection
     * @param groesse grid groesse
     * @throws IOException
     */
    public void startMultiplayerGame(Stage connectionStage, int groesse) throws IOException {
        soundPlayer.playSound();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("multiplayer-main-game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Spielfeld");
        stage.setScene(scene);

        multipayerMainGameController = fxmlLoader.getController();
        multipayerMainGameController.setupSpiel(groesse, stage, cb.getSelectionModel().getSelectedIndex(), cb2.getSelectionModel().getSelectedIndex(), scene);
        stage.show();

        connectionStage.close();
    }

    /**
     * Zurück Button funktion der zur hello view führt
     * @throws IOException
     */
    public void onBackPressed() throws IOException {
        soundPlayer.playSound();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Hauptmenü");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
        Stage thisstage = (Stage) cb.getScene().getWindow();
        thisstage.close();
    }

    /**
     * Wenn der Button "Spiel laden" gedrückt wurde dann wird ein Dialog eröffnet.
     * @throws IOException
     */
    public void openLoadFileDialog() throws IOException {
        soundPlayer.playSound();
        ladenhinweislabel.setText("Bitte eine Save-Datei im Dialog öffnen...");
        Map<String, Object> data = saveFileManager.openFileChooser();
        if(data != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-game-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Spielfeld");
            stage.setScene(scene);

            mainGameController = fxmlLoader.getController();
            mainGameController.setupSpiel(stage, scene, data);
            stage.show();

            Stage thisstage = (Stage) cb.getScene().getWindow();
            thisstage.close();
        }else{
            ladenhinweislabel.setText("Datei konnte nicht geladen werden!");
        }

    }
}
