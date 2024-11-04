package com.turksat46.schiffgehtunter;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;

public class MainGameController {
    @FXML
    public GridPane spielerstackpane;
    Spielfeld spielfeld;
    //Drei States: place = Schiffe platzieren offense = Angriff defense = Verteidigung bzw. auf Angriff vom Gegner warten
    public String[] state = {"place", "offense", "defense"};
    public String[] mode = {"PvsC", "PvsP", "CvsC"};
    public int currentState;
    public int currentMode;



    public void setupSpiel(int groesse, Stage stage){
        spielfeld = new Spielfeld(groesse, stage, spielerstackpane);
        currentState = 0;
        pausieren(stage);


    }

    public void pausieren(Stage stage){
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

    public int getCurrentState(){
        return currentState;
    }

    public void setCurrentMode(int modeposition){
        currentMode = modeposition;
        System.out.println("Mode selected and set to: " + mode[currentMode]);
    }

    public void handleClick(int posx, int posy){
        System.out.println("Clicked at: " + posx + ", " + posy);
        //
        /*TODO: prüfe die aktuelle State*/

        switch (currentState){
            //Schiffe setzen
            case 0:
                break;

            //Schiffe erschießen
            case 1:
                break;

            //Schiffe beobachten (Spieler: Klicks ignorieren)
            case 2:
                break;

            default:
                //Errorstate, weil es nicht mehr als 3 states gibt
        }
    }

}
