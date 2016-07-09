package io.trewartha.positional;

public class UnitConverter {

    private static final double FEET_PER_METER = 3.2808399200439453;

    public static double metersToFeet(double meters) {
        return meters * FEET_PER_METER;
    }
}
