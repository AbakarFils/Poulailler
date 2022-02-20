package com.poulailler.intelligent.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HumiditeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HumiditeDTO.class);
        HumiditeDTO humiditeDTO1 = new HumiditeDTO();
        humiditeDTO1.setId(1L);
        HumiditeDTO humiditeDTO2 = new HumiditeDTO();
        assertThat(humiditeDTO1).isNotEqualTo(humiditeDTO2);
        humiditeDTO2.setId(humiditeDTO1.getId());
        assertThat(humiditeDTO1).isEqualTo(humiditeDTO2);
        humiditeDTO2.setId(2L);
        assertThat(humiditeDTO1).isNotEqualTo(humiditeDTO2);
        humiditeDTO1.setId(null);
        assertThat(humiditeDTO1).isNotEqualTo(humiditeDTO2);
    }
}
