package com.github.bgrochal.geomvisualiser.model;

import com.github.bgrochal.geomvisualiser.model.utils.Color;
import com.github.bgrochal.geomvisualiser.model.utils.ModelUtils;

/**
 * @author Bartłomiej Grochal
 */
public abstract class PlotableModel {

    String label = null;
    Color color;


    PlotableModel() {
        label = null;
        color = new Color(0, 0, 0);
    }


    public abstract String getPlotCommand();

    public String getLabel() {
        if (label == null) {
            label = ModelUtils.generateObjectLabel();
        }

        return label;
    }

    public Color getColor() {
        return color;
    }

}
