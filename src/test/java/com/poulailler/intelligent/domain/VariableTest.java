package com.poulailler.intelligent.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.poulailler.intelligent.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VariableTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Variable.class);
        Variable variable1 = new Variable();
        variable1.setId(1L);
        Variable variable2 = new Variable();
        variable2.setId(variable1.getId());
        assertThat(variable1).isEqualTo(variable2);
        variable2.setId(2L);
        assertThat(variable1).isNotEqualTo(variable2);
        variable1.setId(null);
        assertThat(variable1).isNotEqualTo(variable2);
    }
}
