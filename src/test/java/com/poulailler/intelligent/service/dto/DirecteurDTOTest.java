package com.poulailler.intelligent.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DirecteurDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DirecteurDTO.class);
        DirecteurDTO directeurDTO1 = new DirecteurDTO();
        directeurDTO1.setId(1L);
        DirecteurDTO directeurDTO2 = new DirecteurDTO();
        assertThat(directeurDTO1).isNotEqualTo(directeurDTO2);
        directeurDTO2.setId(directeurDTO1.getId());
        assertThat(directeurDTO1).isEqualTo(directeurDTO2);
        directeurDTO2.setId(2L);
        assertThat(directeurDTO1).isNotEqualTo(directeurDTO2);
        directeurDTO1.setId(null);
        assertThat(directeurDTO1).isNotEqualTo(directeurDTO2);
    }
}
