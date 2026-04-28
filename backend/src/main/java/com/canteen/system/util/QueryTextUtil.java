package com.canteen.system.util;

import org.springframework.util.StringUtils;

public final class QueryTextUtil {

    private QueryTextUtil() {
    }

    public static boolean hasText(String value) {
        return StringUtils.hasText(value);
    }
}
