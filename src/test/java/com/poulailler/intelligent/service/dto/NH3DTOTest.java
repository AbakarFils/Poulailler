package com.poulailler.intelligent.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NH3DTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NH3DTO.class);
        NH3DTO nH3DTO1 = new NH3DTO();
        nH3DTO1.setId(1L);
        NH3DTO nH3DTO2 = new NH3DTO();
        assertThat(nH3DTO1).isNotEqualTo(nH3DTO2);
        nH3DTO2.setId(nH3DTO1.getId());
        assertThat(nH3DTO1).isEqualTo(nH3DTO2);
        nH3DTO2.setId(2L);
        assertThat(nH3DTO1).isNotEqualTo(nH3DTO2);
        nH3DTO1.setId(null);
        assertThat(nH3DTO1).isNotEqualTo(nH3DTO2);
    }
}
