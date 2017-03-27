package com.italankin.lazyworker.app.utils

final class StringUtils {

    static String escapeMarkdown(String s) {
        s.replaceAll(/([*_\[`\]])/) { full, word ->
            return "\\$word"
        }
    }

    private StringUtils() {
        throw new AssertionError()
    }

}
