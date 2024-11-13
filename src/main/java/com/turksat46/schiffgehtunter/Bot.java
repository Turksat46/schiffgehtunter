package com.turksat46.schiffgehtunter;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.Tensor.*;

public class Bot {

    SavedModelBundle model;

    public Bot(){
        model = SavedModelBundle.load("model.h5");
    }

    /*
    public int[] predictMove(int[][] gameState) {
        // Konvertiere gameState in einen Float-Tensor und flache ihn ab
        Tensor inputTensor = Tensor.create(gameState, DataType.FLOAT);
        inputTensor = inputTensor.reshape(new long[]{1, gameState.length * gameState[0].length});

        // Führe die Vorhersage durch
        Tensor outputTensor = model.session().runner()
                .feed("input", inputTensor)
                .fetch("output")
                .run().get(0);

        // Extrahiere die vorhergesagten Koordinaten (Anpassung je nach Modellarchitektur)
        float[] prediction = outputTensor.copyToFloatArray();
        int predictedRow = (int) prediction[0];
        int predictedCol = (int) prediction[1];

        // Rückgabe der vorhergesagten Koordinaten
        return new int[]{predictedRow, predictedCol};
    }

     */
}
