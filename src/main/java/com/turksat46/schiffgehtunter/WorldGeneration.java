package com.turksat46.schiffgehtunter;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WorldGeneration extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();

        Scene scene = new Scene(
                new StackPane(group),
                400, 600,
                true,
                SceneAntialiasing.BALANCED
        );

        scene.setFill(Color.LIGHTBLUE);
        Box sand = new Box(25, 25, 25);
        System.out.println(sand.getTranslateX());
        sand.setTranslateX(500.0);
        System.out.println(sand.getTranslateX());
        sand.setMaterial(new PhongMaterial(Color.SANDYBROWN));
        group.getChildren().add(sand);
        scene.setCamera(new PerspectiveCamera());

        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}