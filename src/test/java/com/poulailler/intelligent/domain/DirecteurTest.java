package com.poulailler.intelligent.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DirecteurTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Directeur.class);
        Directeur directeur1 = new Directeur();
        directeur1.setId(1L);
        Directeur directeur2 = new Directeur();
        directeur2.setId(directeur1.getId());
        assertThat(directeur1).isEqualTo(directeur2);
        directeur2.setId(2L);
        assertThat(directeur1).isNotEqualTo(directeur2);
        directeur1.setId(null);
        assertThat(directeur1).isNotEqualTo(directeur2);
    }
}
