package com.poulailler.intelligent.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LampeMapperTest {

    private LampeMapper lampeMapper;

    @BeforeEach
    public void setUp() {
        lampeMapper = new LampeMapperImpl();
    }
}
