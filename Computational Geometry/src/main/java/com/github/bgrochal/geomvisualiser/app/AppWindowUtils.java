package com.github.bgrochal.geomvisualiser.app;

import com.github.bgrochal.geomvisualiser.app.common.controller.AppWindowController;
import com.github.bgrochal.geomvisualiser.config.ConfigMessages.MESSAGES;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

/**
 * @author Bartłomiej Grochal
 */
public abstract class AppWindowUtils {

    private static FXMLLoader loader = new FXMLLoader(AppWindowUtils.class.getResource("common/view/AppWindowLayout.fxml"));


    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }


    static void setAppWindowTitle(Stage appWindowStage) {
        appWindowStage.setTitle(MESSAGES.getMessage(""));
    }

    static void setAppWindowScene(Stage appWindowStage) throws IOException {
        appWindowStage.setScene(new Scene(loader.load()));
        appWindowStage.setMaximized(true); // Sets proper width and height of the scene.
    }

    static AppWindowController getController() {
        return loader.getController();
    }

}
