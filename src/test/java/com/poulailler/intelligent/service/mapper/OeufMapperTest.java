package com.poulailler.intelligent.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OeufMapperTest {

    private OeufMapper oeufMapper;

    @BeforeEach
    public void setUp() {
        oeufMapper = new OeufMapperImpl();
    }
}
