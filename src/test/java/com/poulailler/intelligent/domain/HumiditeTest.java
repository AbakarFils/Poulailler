package com.poulailler.intelligent.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HumiditeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Humidite.class);
        Humidite humidite1 = new Humidite();
        humidite1.setId(1L);
        Humidite humidite2 = new Humidite();
        humidite2.setId(humidite1.getId());
        assertThat(humidite1).isEqualTo(humidite2);
        humidite2.setId(2L);
        assertThat(humidite1).isNotEqualTo(humidite2);
        humidite1.setId(null);
        assertThat(humidite1).isNotEqualTo(humidite2);
    }
}
