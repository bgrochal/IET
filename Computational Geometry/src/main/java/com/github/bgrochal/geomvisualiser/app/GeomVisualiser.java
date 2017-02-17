package com.github.bgrochal.geomvisualiser.app;

import com.github.bgrochal.algorithm.GeomAlgorithm;
import com.github.bgrochal.algorithm.location.separators.PointLocationAlgorithm;
import com.github.bgrochal.geomvisualiser.app.common.controller.AppWindowController;
import javafx.application.Application;
import javafx.stage.Stage;

import static com.github.bgrochal.geomvisualiser.app.AppWindowUtils.*;

/**
 * @author Bartłomiej Grochal
 */
public class GeomVisualiser extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setAppWindowTitle(primaryStage);
        setAppWindowScene(primaryStage);

        /*
         * Convex hull algorithm.
         *
         * LinkedList<Point> testPoints = (LinkedList<Point>) new FileParser("1A.txt").parsePoints();
         * GeomAlgorithm algorithm = new GrahamAlgorithm(testPoints);
         *
         * algorithm.run();
         * algorithm.displayResult();
         */

        /*
         * Sweep line algorithm.
         * GeomAlgorithm algorithm = new SweepLineAlgorithm();
         */

        /*
         * Triangulation algorithm.
         * GeomAlgorithm algorithm = new TriangulationAlgorithm();
         */

        /*
         * Point location algorithm.
         */

        GeomAlgorithm algorithm = new PointLocationAlgorithm();

        AppWindowController mainController = getController();
        mainController.setAlgorithm(algorithm);

        primaryStage.show();
    }

}
