package com.poulailler.intelligent.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LampeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Lampe.class);
        Lampe lampe1 = new Lampe();
        lampe1.setId(1L);
        Lampe lampe2 = new Lampe();
        lampe2.setId(lampe1.getId());
        assertThat(lampe1).isEqualTo(lampe2);
        lampe2.setId(2L);
        assertThat(lampe1).isNotEqualTo(lampe2);
        lampe1.setId(null);
        assertThat(lampe1).isNotEqualTo(lampe2);
    }
}
