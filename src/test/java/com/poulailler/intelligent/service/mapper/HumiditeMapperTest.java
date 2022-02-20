package com.poulailler.intelligent.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HumiditeMapperTest {

    private HumiditeMapper humiditeMapper;

    @BeforeEach
    public void setUp() {
        humiditeMapper = new HumiditeMapperImpl();
    }
}
