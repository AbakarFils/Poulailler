package com.poulailler.intelligent.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TemperatureDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TemperatureDTO.class);
        TemperatureDTO temperatureDTO1 = new TemperatureDTO();
        temperatureDTO1.setId(1L);
        TemperatureDTO temperatureDTO2 = new TemperatureDTO();
        assertThat(temperatureDTO1).isNotEqualTo(temperatureDTO2);
        temperatureDTO2.setId(temperatureDTO1.getId());
        assertThat(temperatureDTO1).isEqualTo(temperatureDTO2);
        temperatureDTO2.setId(2L);
        assertThat(temperatureDTO1).isNotEqualTo(temperatureDTO2);
        temperatureDTO1.setId(null);
        assertThat(temperatureDTO1).isNotEqualTo(temperatureDTO2);
    }
}
