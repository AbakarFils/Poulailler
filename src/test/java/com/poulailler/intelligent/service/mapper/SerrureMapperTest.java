package com.poulailler.intelligent.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SerrureMapperTest {

    private SerrureMapper serrureMapper;

    @BeforeEach
    public void setUp() {
        serrureMapper = new SerrureMapperImpl();
    }
}
