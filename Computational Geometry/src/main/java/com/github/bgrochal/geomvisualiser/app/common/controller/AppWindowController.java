package com.github.bgrochal.geomvisualiser.app.common.controller;

import com.github.bgrochal.algorithm.GeomAlgorithm;
import com.github.bgrochal.geomvisualiser.app.AppWindowUtils;
import com.github.bgrochal.geomvisualiser.geogebra.panel.GGPanelFactory;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.geogebra.desktop.GeoGebraPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Bartłomiej Grochal
 */
public class AppWindowController {

    @FXML private SwingNode ggPanel;
    @FXML private Button nextSnapshot;
    @FXML private Button previousSnapshot;
    @FXML private Button showResult;
    @FXML private Button runSweepAlgorithm;
    @FXML private Button runTriangulationAlgorithm;
    @FXML private Button runPointLocationAlgorithm;

    private GeomAlgorithm algorithm;


    @FXML
    public void initialize() {
        SwingUtilities.invokeLater(() -> {
            GeoGebraPanel panel = GGPanelFactory.getInstance();
            Dimension screenSize = AppWindowUtils.getScreenSize();
            panel.setMinimumSize(new Dimension(screenSize.width, screenSize.height - 150));
            ggPanel.setContent(panel);
        });
    }


    @FXML
    protected void handleNextSnapshotButtonClick() {
        algorithm.displayNextSnapshot();
    }

    @FXML
    protected void handlePreviousSnapshotButtonClick(ActionEvent actionEvent) {
        algorithm.displayPreviousSnapshot();
    }

    @FXML
    protected void handleShowResultButtonClick(ActionEvent actionEvent) {
        algorithm.displayResult();
    }

    @FXML
    protected void handleRunSweepAlgorithmButtonClick(ActionEvent actionEvent) {
        algorithm.run();
    }

    @FXML
    protected void handleRunTriangulationAlgorithmButtonClick(ActionEvent actionEvent) {
        algorithm.run();
    }

    @FXML
    protected void handleRunPointLocationAlgorithmButtonClick(ActionEvent actionEvent) {
        algorithm.run();
    }


    public void setAlgorithm(GeomAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

}
