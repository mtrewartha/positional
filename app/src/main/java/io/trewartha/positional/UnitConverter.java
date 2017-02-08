package io.trewartha.positional;

class UnitConverter {

    private static final float FEET_PER_METER = 3.2808399200439453f;
    private static final float MPH_PER_MPS = 2.236936f;
    private static final float KPH_PER_MPS = 3.6f;

    static float metersToFeet(float meters) {
        return meters * FEET_PER_METER;
    }

    static float metersPerSecondToMilesPerHour(float metersPerSecond) {
        return metersPerSecond * MPH_PER_MPS;
    }

    static float metersPerSecondToKilometersPerHour(float metersPerSecond) {
        return metersPerSecond * KPH_PER_MPS;
    }
}
