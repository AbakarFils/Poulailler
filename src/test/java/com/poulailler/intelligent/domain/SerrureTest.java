package com.poulailler.intelligent.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SerrureTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Serrure.class);
        Serrure serrure1 = new Serrure();
        serrure1.setId(1L);
        Serrure serrure2 = new Serrure();
        serrure2.setId(serrure1.getId());
        assertThat(serrure1).isEqualTo(serrure2);
        serrure2.setId(2L);
        assertThat(serrure1).isNotEqualTo(serrure2);
        serrure1.setId(null);
        assertThat(serrure1).isNotEqualTo(serrure2);
    }
}
