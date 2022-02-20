package com.poulailler.intelligent.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NH3MapperTest {

    private NH3Mapper nH3Mapper;

    @BeforeEach
    public void setUp() {
        nH3Mapper = new NH3MapperImpl();
    }
}
