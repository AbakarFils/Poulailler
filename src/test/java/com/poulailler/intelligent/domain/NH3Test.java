package com.poulailler.intelligent.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NH3Test {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NH3.class);
        NH3 nH31 = new NH3();
        nH31.setId(1L);
        NH3 nH32 = new NH3();
        nH32.setId(nH31.getId());
        assertThat(nH31).isEqualTo(nH32);
        nH32.setId(2L);
        assertThat(nH31).isNotEqualTo(nH32);
        nH31.setId(null);
        assertThat(nH31).isNotEqualTo(nH32);
    }
}
