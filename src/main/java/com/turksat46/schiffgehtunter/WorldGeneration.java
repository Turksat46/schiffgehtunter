package com.turksat46.schiffgehtunter;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class WorldGeneration extends Application {

    @Override
    public void start(Stage stage) {
        // Pane als Container für die Szene
        // Pane als Container für die Szene
        Pane pane = new Pane();

        // Rechteck erstellen
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(50);
        rectangle.setHeight(50);
        rectangle.setX(300);
        rectangle.setY(300);
        rectangle.setFill(Color.RED);
        rectangle.setStroke(Color.BLACK);

        // Rechteck erstellen
        Rectangle rectangle1 = new Rectangle();
        rectangle1.setWidth(50);
        rectangle1.setHeight(50);
        rectangle1.setFill(Color.RED);
        rectangle1.setStroke(Color.BLACK);

        // Rechteck erstellen
        Rectangle rectangle2 = new Rectangle();
        rectangle2.setWidth(50);
        rectangle2.setHeight(50);
        rectangle2.setFill(Color.RED);
        rectangle2.setStroke(Color.BLACK);

        // Rechteck erstellen
        Rectangle rectangle3 = new Rectangle();
        rectangle3.setWidth(50);
        rectangle3.setHeight(50);
        rectangle3.setFill(Color.RED);
        rectangle3.setStroke(Color.BLACK);

        HBox hbox = new HBox(rectangle1, rectangle2, rectangle3);


        //Drag-and-Drop-Logik für das Rechteck
        hbox.setOnMousePressed(event -> {
            hbox.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        hbox.setOnMouseDragged(event -> {
            double[] start = (double[]) hbox.getUserData();
            double deltaX = event.getSceneX() - start[0];
            double deltaY = event.getSceneY() - start[1];

            // Rechteck verschieben
            hbox.setLayoutX(hbox.getLayoutX() + deltaX);
            hbox.setLayoutY(hbox.getLayoutY() + deltaY);
            hbox.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        // Rechteck dem Pane hinzufügen
        pane.getChildren().add(hbox);

        System.out.println(hbox.getLayoutX());
        pane.getChildren().add(rectangle);

        // Szene und Stage erstellen
        Scene scene = new Scene(pane, 800, 600);
        stage.setTitle("Drag and Drop mit Rechteck und Boxen");
        stage.setScene(scene);
        stage.show();
    }

    private Box createBox(double x, double y) {
        Box box = new Box(300, 50, 50);
        box.setMaterial(new PhongMaterial(Color.DODGERBLUE));
        box.getTransforms().add(new Translate(x, y, 0));
        return box;
    }

    private void moveBox(Box box, double deltaX, double deltaY) {
        Translate translate = (Translate) box.getTransforms().get(0);
        translate.setX(translate.getX() + deltaX);
        translate.setY(translate.getY() + deltaY);

    }

    public static void main(String[] args) {
        launch(args);
    }
}