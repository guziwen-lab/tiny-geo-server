package com.supermap.analyze.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UniqueFieldNameHelperTest {

    @Test
    void appendsIncreasingSuffixesForDuplicateNames() {
        UniqueFieldNameHelper helper = new UniqueFieldNameHelper();

        assertEquals("dlbm", helper.uniqueFieldName("dlbm"));
        assertEquals("dlbm_1", helper.uniqueFieldName("dlbm"));
        assertEquals("dlbm_2", helper.uniqueFieldName("dlbm"));
    }
}
