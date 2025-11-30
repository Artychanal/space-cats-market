package com.artur.java.spacecatsmarket.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorTest {

    @Test
    void newIdShouldGenerateUniqueUuid() {
        var first = IdGenerator.newId();
        var second = IdGenerator.newId();

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first).isNotEqualTo(second);
    }
}
