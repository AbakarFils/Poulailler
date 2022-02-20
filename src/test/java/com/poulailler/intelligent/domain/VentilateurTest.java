package com.poulailler.intelligent.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VentilateurTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ventilateur.class);
        Ventilateur ventilateur1 = new Ventilateur();
        ventilateur1.setId(1L);
        Ventilateur ventilateur2 = new Ventilateur();
        ventilateur2.setId(ventilateur1.getId());
        assertThat(ventilateur1).isEqualTo(ventilateur2);
        ventilateur2.setId(2L);
        assertThat(ventilateur1).isNotEqualTo(ventilateur2);
        ventilateur1.setId(null);
        assertThat(ventilateur1).isNotEqualTo(ventilateur2);
    }
}
