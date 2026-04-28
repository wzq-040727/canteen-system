package com.canteen.system.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueryTextUtilTest {

    @Test
    void shouldReturnFalseForNullOrBlank() {
        assertFalse(QueryTextUtil.hasText(null));
        assertFalse(QueryTextUtil.hasText(""));
        assertFalse(QueryTextUtil.hasText("   "));
    }

    @Test
    void shouldReturnTrueForNormalText() {
        assertTrue(QueryTextUtil.hasText("鱼香肉丝"));
        assertTrue(QueryTextUtil.hasText("  麻辣烫"));
    }
}
