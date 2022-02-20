package com.poulailler.intelligent.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OeufDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OeufDTO.class);
        OeufDTO oeufDTO1 = new OeufDTO();
        oeufDTO1.setId(1L);
        OeufDTO oeufDTO2 = new OeufDTO();
        assertThat(oeufDTO1).isNotEqualTo(oeufDTO2);
        oeufDTO2.setId(oeufDTO1.getId());
        assertThat(oeufDTO1).isEqualTo(oeufDTO2);
        oeufDTO2.setId(2L);
        assertThat(oeufDTO1).isNotEqualTo(oeufDTO2);
        oeufDTO1.setId(null);
        assertThat(oeufDTO1).isNotEqualTo(oeufDTO2);
    }
}
