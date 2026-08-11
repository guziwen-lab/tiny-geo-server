package com.supermap.analyze.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SqlInjectionCheckHelperTest {

    @Test
    void acceptsSafeIdentifiers() {
        assertDoesNotThrow(() -> SqlInjectionCheckHelper.checkTableName("dataset_2024", "analyze_1"));
        assertDoesNotThrow(() -> SqlInjectionCheckHelper.checkColumnName("DLBM", "tbdlmj_split"));
    }

    @Test
    void rejectsUnsafeIdentifiers() {
        assertThrows(IllegalArgumentException.class,
                () -> SqlInjectionCheckHelper.checkTableName("dataset; DROP TABLE dataset"));
        assertThrows(IllegalArgumentException.class,
                () -> SqlInjectionCheckHelper.checkColumnName("name--comment"));
    }
}
