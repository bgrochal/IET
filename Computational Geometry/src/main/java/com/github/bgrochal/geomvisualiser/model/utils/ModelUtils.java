package com.github.bgrochal.geomvisualiser.model.utils;

/**
 * @author Bartłomiej Grochal
 */
public abstract class ModelUtils {

    private static final String invisiblePointLabel = "INV";
    private static final String invisibleObjectLabel = "inv";

    private static int invisiblePointNumber = 0;
    private static int invisibleObjectNumber = 0;


    public static boolean isInvisibleLabel(String label) {
        return label.startsWith(invisiblePointLabel) || label.startsWith(invisibleObjectLabel);
    }

    public static String generatePointLabel() {
        return invisiblePointLabel + invisiblePointNumber++;
    }

    public static String generateObjectLabel() {
        return invisibleObjectLabel + invisibleObjectNumber++;
    }

    public static String getInvisibleObjectLabel() {
        return invisibleObjectLabel;
    }

    public static int getInvisibleObjectNumber() {
        return invisibleObjectNumber;
    }

    public static void setInvisibleObjectNumber(int invisibleObjectNumber) {
        ModelUtils.invisibleObjectNumber = invisibleObjectNumber;
    }

}
