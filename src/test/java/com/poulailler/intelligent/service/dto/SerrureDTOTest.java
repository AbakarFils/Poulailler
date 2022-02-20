package com.poulailler.intelligent.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SerrureDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SerrureDTO.class);
        SerrureDTO serrureDTO1 = new SerrureDTO();
        serrureDTO1.setId(1L);
        SerrureDTO serrureDTO2 = new SerrureDTO();
        assertThat(serrureDTO1).isNotEqualTo(serrureDTO2);
        serrureDTO2.setId(serrureDTO1.getId());
        assertThat(serrureDTO1).isEqualTo(serrureDTO2);
        serrureDTO2.setId(2L);
        assertThat(serrureDTO1).isNotEqualTo(serrureDTO2);
        serrureDTO1.setId(null);
        assertThat(serrureDTO1).isNotEqualTo(serrureDTO2);
    }
}
