package com.poulailler.intelligent.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DirecteurMapperTest {

    private DirecteurMapper directeurMapper;

    @BeforeEach
    public void setUp() {
        directeurMapper = new DirecteurMapperImpl();
    }
}
