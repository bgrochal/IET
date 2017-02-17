package com.github.bgrochal.geomvisualiser.geogebra.panel;

import org.geogebra.desktop.GeoGebraPanel;

/**
 * @author Bartłomiej Grochal
 */
public abstract class GGPanelFactory {

    private static volatile GeoGebraPanel instance;

    private GGPanelFactory() {
    }

    public static synchronized GeoGebraPanel getInstance() {
        if (instance == null) {
            instance = new GeoGebraPanel();
            instance.buildGUI();
        }

        return instance;
    }

}
