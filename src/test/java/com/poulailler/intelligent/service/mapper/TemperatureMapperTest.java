package com.poulailler.intelligent.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemperatureMapperTest {

    private TemperatureMapper temperatureMapper;

    @BeforeEach
    public void setUp() {
        temperatureMapper = new TemperatureMapperImpl();
    }
}
