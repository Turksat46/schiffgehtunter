package com.turksat46.schiffgehtunter.other;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Scale;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

    private Image textureImage;

    public Group loadModel(String objFilePath, InputStream textureFilePath) {
        Group modelGroup = new Group();

        // Textur laden
        try {
            textureImage = new Image(textureFilePath);
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Textur: " + e.getMessage());
        }

        // OBJ-Datei laden und MeshView erstellen
        MeshView meshView = loadOBJ(objFilePath);
        if (meshView != null) {
            modelGroup.getChildren().add(meshView);
        }

        return modelGroup;
    }

    private MeshView loadOBJ(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Datei nicht gefunden: " + filePath);
            return null;
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
            return null;
        }

        return createMeshView(vertices, texCoords, faces);
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
        meshView.setCullFace(CullFace.NONE);

        if (textureImage != null) {
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseMap(textureImage);
            meshView.setMaterial(material);
        } else {
            meshView.setMaterial(new PhongMaterial(Color.RED)); // Fallback-Material
        }
        return meshView;
    }
}

