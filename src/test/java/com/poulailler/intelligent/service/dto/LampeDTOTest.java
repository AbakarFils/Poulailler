package com.poulailler.intelligent.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LampeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LampeDTO.class);
        LampeDTO lampeDTO1 = new LampeDTO();
        lampeDTO1.setId(1L);
        LampeDTO lampeDTO2 = new LampeDTO();
        assertThat(lampeDTO1).isNotEqualTo(lampeDTO2);
        lampeDTO2.setId(lampeDTO1.getId());
        assertThat(lampeDTO1).isEqualTo(lampeDTO2);
        lampeDTO2.setId(2L);
        assertThat(lampeDTO1).isNotEqualTo(lampeDTO2);
        lampeDTO1.setId(null);
        assertThat(lampeDTO1).isNotEqualTo(lampeDTO2);
    }
}
