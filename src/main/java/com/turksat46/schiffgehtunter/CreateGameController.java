package com.turksat46.schiffgehtunter;
import com.turksat46.schiffgehtunter.filemanagement.SaveFileManager;
import com.turksat46.schiffgehtunter.netzwerk.Server;
import com.turksat46.schiffgehtunter.netzwerk.establishConnection;
import com.turksat46.schiffgehtunter.other.Difficulty;
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


public class CreateGameController {

    Music soundPlayer = Music.getInstance();

    // cb = Schwierigkeit
    // cb2 = Spielstrategie

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

    ObservableList<Difficulty> difficulties = FXCollections.observableArrayList();
    ObservableList<String> skillLevels = FXCollections.observableArrayList("Noob", "Average", "Hardcore");
    ObservableList<String> gameModes = FXCollections.observableArrayList("Spieler vs. Computer", "Spieler vs. Spieler", "Computer vs. Computer");


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

    public void onStrategieChanged() {
        if (cb2.getSelectionModel().getSelectedIndex() == 0) {
            kiDifficultyUI.setVisible(true);
        } else {
            kiDifficultyUI.setVisible(false);
        }
    }

    public void onPlayPressed() throws IOException {
        soundPlayer.playSound();
        startGame();
    }

    public void startGame() throws IOException {
        if (cb2.getSelectionModel().getSelectedIndex() == 0) {
            startSinglePlayerGame();
        } else {
            // Start multiplayer setup
            openConnectionSetup();
        }
    }

    private void startSinglePlayerGame() throws IOException {
        soundPlayer.playSound();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Spielfeld");
        stage.setScene(scene);

        mainGameController = fxmlLoader.getController();
        mainGameController.setupSpiel((int) groesseslider.getValue(), stage, cb.getSelectionModel().getSelectedIndex(), cb2.getSelectionModel().getSelectedIndex(), scene);
        stage.show();

        Stage thisstage = (Stage) cb.getScene().getWindow();
        thisstage.close();
    }

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

    private void waitForConnectionAndStartGame(Stage stage) throws IOException {
        new Thread(() -> {
            while (!Server.connectionEstablished) {
                try {
                    Thread.sleep(100); // Check periodically
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Once connection is established, start multiplayer game
            Platform.runLater(() -> {
                try {
                    startMultiplayerGame(stage, (int) groesseslider.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    //Überarbeiten sodass auch schiffe übergeben werden können
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

    public void openLoadFileDialog() {
        soundPlayer.playSound();
        ladenhinweislabel.setText("Bitte eine Save-Datei im Dialog öffnen...");
        saveFileManager.openFileChooser();

    }
}
