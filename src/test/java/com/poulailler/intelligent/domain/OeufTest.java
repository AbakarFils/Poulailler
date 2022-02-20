package com.poulailler.intelligent.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OeufTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Oeuf.class);
        Oeuf oeuf1 = new Oeuf();
        oeuf1.setId(1L);
        Oeuf oeuf2 = new Oeuf();
        oeuf2.setId(oeuf1.getId());
        assertThat(oeuf1).isEqualTo(oeuf2);
        oeuf2.setId(2L);
        assertThat(oeuf1).isNotEqualTo(oeuf2);
        oeuf1.setId(null);
        assertThat(oeuf1).isNotEqualTo(oeuf2);
    }
}
