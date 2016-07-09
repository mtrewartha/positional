package io.trewartha.positional;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UnitConverterTest {

    @Test
    public void metersToFeet_convertsMetersToFeet() throws Exception {
        assertThat(UnitConverter.metersToFeet(1.0f)).isEqualTo(3.28084f);
    }
}