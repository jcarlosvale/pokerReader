package com.poker.reader.parser.util;

import static com.poker.reader.domain.util.Chen.calculateChenFormulaFrom;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChenTest {

    @Test
    void verifyChen() {
        assertThat(calculateChenFormulaFrom("AA"))
                .isEqualTo(20);
        assertThat(calculateChenFormulaFrom("AKs"))
                .isEqualTo(12);
        assertThat(calculateChenFormulaFrom("TT"))
                .isEqualTo(10);
        assertThat(calculateChenFormulaFrom("75s"))
                .isEqualTo(6);
        assertThat(calculateChenFormulaFrom("72o"))
                .isEqualTo(-1);
        assertThat(calculateChenFormulaFrom("22"))
                .isEqualTo(5);
        assertThat(calculateChenFormulaFrom("84s"))
                .isEqualTo(2);
    }

}