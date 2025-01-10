package com.turksat46.schiffgehtunter;

import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class objTester extends Application {

    private Group root;
    private Rotate rotateX, rotateY;
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private Image textureImage;
    private MeshView meshView;
    private Scale scaleTransform;
    private String selectedAxis = "X"; // Standardmäßig die X-Achse

    @Override
    public void start(Stage primaryStage) {
        root = new Group();
        SubScene subScene = new SubScene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.LIGHTGRAY);

        Camera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(1000.0);
        camera.translateZProperty().set(-5);
        subScene.setCamera(camera);

        // Licht hinzufügen
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        root.getChildren().add(ambientLight);

        // Rotationstransformationen
        rotateX = new Rotate(0, Rotate.X_AXIS);
        rotateY = new Rotate(0, Rotate.Y_AXIS);
        root.getTransforms().addAll(rotateX, rotateY);

        // Mausereignisse für die Rotation
        subScene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });

        subScene.setOnMouseDragged(event -> {
            rotateX.setAngle(anchorAngleX - (event.getSceneY() - anchorY) * 0.5);
            rotateY.setAngle(anchorAngleY + (event.getSceneX() - anchorX) * 0.5);
        });

        // Textur laden
        try {
            textureImage = new Image(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/3dobjekte/oak.png")); // Ersetze dies mit dem Pfad zu deiner Textur
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Textur: " + e.getMessage());
        }

        // OBJ-Datei laden
        loadOBJ(getClass().getResource("/com/turksat46/schiffgehtunter/3dobjekte/boat.obj").getFile()); // Ersetze dies mit dem tatsächlichen Pfad

        // Slider für das Stretchen erstellen
        Slider stretchSlider = new Slider(2, 5, 1); // Min, Max, Initialwert
        stretchSlider.setShowTickMarks(true);
        stretchSlider.setShowTickLabels(true);
        stretchSlider.setMajorTickUnit(1);
        stretchSlider.setMinorTickCount(0);
        stretchSlider.setSnapToTicks(true);

        // ChoiceBox für die Auswahl der Stretch-Richtung
        ChoiceBox<String> axisChoiceBox = new ChoiceBox<>();
        axisChoiceBox.getItems().addAll("X", "Y", "Z");
        axisChoiceBox.setValue(selectedAxis); // Standardwert setzen
        axisChoiceBox.setOnAction(event -> selectedAxis = axisChoiceBox.getValue());

        // Scale-Transformation erstellen und anwenden
        scaleTransform = new Scale(1, 1, 1); // Initialer Scale-Faktor
        if (meshView != null) {
            meshView.getTransforms().add(scaleTransform);
        }

        // Listener für den Slider
        stretchSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double scaleFactor = newValue.doubleValue();
            if (meshView != null && scaleTransform != null) {
                if (selectedAxis.equals("X")) {
                    scaleTransform.setX(scaleFactor);
                } else if (selectedAxis.equals("Y")) {
                    scaleTransform.setY(scaleFactor);
                } else if (selectedAxis.equals("Z")) {
                    scaleTransform.setZ(scaleFactor);
                }
            }
        });

        HBox controlsRow = new HBox(10, stretchSlider, axisChoiceBox); // Slider und ChoiceBox nebeneinander
        VBox controls = new VBox(10, controlsRow);
        Group sceneRoot = new Group(subScene, controls);
        Scene scene = new Scene(sceneRoot, 800, 680); // Mehr Platz für die Controls
        primaryStage.setTitle("OBJ Viewer with Texture and Directional Stretch");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadOBJ(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Datei nicht gefunden: " + filePath);
            return;
        }

        List<Point3D> vertices = new ArrayList<>();
        List<Point3D> texCoords = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("v ")) {
                    String[] parts = line.substring(2).split("\\s+");
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double z = Double.parseDouble(parts[2]);
                    vertices.add(new Point3D(x, y, z));
                } else if (line.startsWith("vt ")) {
                    String[] parts = line.substring(3).split("\\s+");
                    double u = Double.parseDouble(parts[0]);
                    double v = Double.parseDouble(parts[1]);
                    texCoords.add(new Point3D(u, 1 - v, 0)); // V-Koordinate in OBJ ist normalerweise invertiert
                } else if (line.startsWith("f ")) {
                    String[] parts = line.substring(2).split("\\s+");
                    int[] faceVertices = new int[parts.length * 2]; // Für Vertex- und Textur-Indizes
                    for (int i = 0; i < parts.length; i++) {
                        String[] indices = parts[i].split("/");
                        // OBJ-Indices sind 1-basiert, daher -1
                        faceVertices[i * 2] = Integer.parseInt(indices[0]) - 1;     // Vertex-Index
                        if (indices.length > 1 && !indices[1].isEmpty()) {
                            faceVertices[i * 2 + 1] = Integer.parseInt(indices[1]) - 1; // Textur-Index
                        } else {
                            faceVertices[i * 2 + 1] = 0; // Fallback, falls keine Texturkoordinaten angegeben sind
                        }
                    }
                    faces.add(faceVertices);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Fehler beim Laden der OBJ-Datei: " + e.getMessage());
            return;
        }

        meshView = createMeshView(vertices, texCoords, faces);
        root.getChildren().add(meshView);
        if (scaleTransform != null && meshView != null) {
            meshView.getTransforms().add(scaleTransform); // Scale auch hier anwenden, falls loadOBJ zuerst aufgerufen wird
        }
    }

    private MeshView createMeshView(List<Point3D> vertices, List<Point3D> texCoords, List<int[]> faces) {
        TriangleMesh mesh = new TriangleMesh();

        // Vertices hinzufügen
        for (Point3D vertex : vertices) {
            mesh.getPoints().addAll((float) vertex.getX(), (float) vertex.getY(), (float) vertex.getZ());
        }

        // Texturkoordinaten hinzufügen
        if (texCoords.isEmpty()) {
            mesh.getTexCoords().addAll(0, 0); // Fallback, falls keine Texturkoordinaten im OBJ
        } else {
            for (Point3D texCoord : texCoords) {
                mesh.getTexCoords().addAll((float) texCoord.getX(), (float) texCoord.getY());
            }
        }

        // Faces hinzufügen (Triangulierung von Polygonen)
        for (int[] face : faces) {
            if (face.length >= 6) { // Jedes Vertex hat einen Vertex- und Textur-Index
                int vCount = face.length / 2;
                int v0 = face[0];
                int vt0 = face[1];
                for (int i = 1; i < vCount - 1; i++) {
                    int v1 = face[i * 2];
                    int vt1 = face[i * 2 + 1];
                    int v2 = face[(i + 1) * 2];
                    int vt2 = face[(i + 1) * 2 + 1];
                    mesh.getFaces().addAll(
                            v0, vt0, v1, vt1, v2, vt2
                    );
                }
            }
        }

        MeshView meshView = new MeshView(mesh);
        meshView.setCullFace(CullFace.NONE); // Hier wird das Double-Sided-Rendering aktiviert

        if (textureImage != null) {
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseMap(textureImage);
            meshView.setMaterial(material);
        } else {
            meshView.setMaterial(new PhongMaterial(Color.RED)); // Fallback-Material
        }
        return meshView;
    }

    public static void main(String[] args) {
        launch(args);
    }
}