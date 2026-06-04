package com.surffountain.browser.utils;

public class ReaderModeExtractor {

    public static String cleanText(String rawText) {
        if (rawText == null) return "";
        // Remove excessive whitespace
        return rawText
                .replaceAll("\\s{3,}", "\n\n")
                .replaceAll("\\t", " ")
                .replaceAll("[\\u200B-\\u200D\\uFEFF]", "")
                .trim();
    }

    public static boolean isArticlePage(String url, String content) {
        if (url == null || content == null) return false;
        // Heuristic: if content is >500 chars and has paragraphs, it's probably an article
        return content.length() > 500 && content.contains("\n");
    }
}
