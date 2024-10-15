package com.turksat46.schiffe;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Nigg");
    }

    @FXML
    protected void onExitButtonClick() {
        System.exit(0);
    }
}