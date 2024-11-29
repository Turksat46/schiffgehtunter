package com.turksat46.schiffgehtunter;
import com.turksat46.schiffgehtunter.filemanagement.SaveFileManager;
import com.turksat46.schiffgehtunter.other.Difficulty;
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

    public void onStrategieChanged(){
        if(cb2.getSelectionModel().getSelectedIndex() == 0){
            kiDifficultyUI.setVisible(true);
        }else{
            kiDifficultyUI.setVisible(false);
        }
    }

    public void onPlayPressed() throws IOException {
        startGame();
    }

    public void startGame() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-game-view.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Spielerfeld");
        stage.setScene(new Scene(fxmlLoader.load()));

        mainGameController = fxmlLoader.getController();
        mainGameController.setupSpiel((int)groesseslider.getValue(), stage, cb.getSelectionModel().getSelectedIndex() ,cb2.getSelectionModel().getSelectedIndex() );
        stage.show();



        Stage thisstage = (Stage) cb.getScene().getWindow();
        thisstage.close();
    }

    public void onBackPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Hello-view.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Hauptmenü");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
        Stage thisstage = (Stage) cb.getScene().getWindow();
        thisstage.close();
    }

    public void openLoadFileDialog() {
        ladenhinweislabel.setText("Bitte eine Save-Datei im Dialog öffnen...");
        saveFileManager.openFileChooser();

    }
}
