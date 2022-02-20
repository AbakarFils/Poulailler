package com.poulailler.intelligent.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VentilateurDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VentilateurDTO.class);
        VentilateurDTO ventilateurDTO1 = new VentilateurDTO();
        ventilateurDTO1.setId(1L);
        VentilateurDTO ventilateurDTO2 = new VentilateurDTO();
        assertThat(ventilateurDTO1).isNotEqualTo(ventilateurDTO2);
        ventilateurDTO2.setId(ventilateurDTO1.getId());
        assertThat(ventilateurDTO1).isEqualTo(ventilateurDTO2);
        ventilateurDTO2.setId(2L);
        assertThat(ventilateurDTO1).isNotEqualTo(ventilateurDTO2);
        ventilateurDTO1.setId(null);
        assertThat(ventilateurDTO1).isNotEqualTo(ventilateurDTO2);
    }
}
