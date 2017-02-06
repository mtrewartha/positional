package io.trewartha.positional;

class UnitConverter {

    private static final double FEET_PER_METER = 3.2808399200439453;
    private static final double MPH_PER_MPS = 0.44704;

    static double metersToFeet(double meters) {
        return meters * FEET_PER_METER;
    }

    static double metersPerSecondToMilesPerHour(double metersPerSecond) {
        return metersPerSecond * MPH_PER_MPS;
    }
}
