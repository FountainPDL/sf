package com.surffountain.browser.utils;

import android.util.Patterns;
import android.webkit.URLUtil;

import com.surffountain.browser.models.SearchEngine;

public class UrlUtils {

    public static String buildUrl(String input, String searchEngineId) {
        if (input == null || input.isEmpty()) return "surf://home";

        input = input.trim();

        // Already a full URL
        if (input.startsWith("http://") || input.startsWith("https://") ||
            input.startsWith("surf://") || input.startsWith("about:") ||
            input.startsWith("file://") || input.startsWith("data:")) {
            return input;
        }

        // Looks like a domain
        if (looksLikeDomain(input)) {
            return "https://" + input;
        }

        // Treat as search
        SearchEngine engine = getEngineById(searchEngineId);
        return engine.buildSearchUrl(input);
    }

    private static boolean looksLikeDomain(String input) {
        if (!input.contains(" ") && input.contains(".")) {
            if (Patterns.WEB_URL.matcher("https://" + input).matches()) {
                return true;
            }
        }
        return false;
    }

    public static String getDisplayUrl(String url) {
        if (url == null) return "";
        try {
            android.net.Uri uri = android.net.Uri.parse(url);
            String host = uri.getHost();
            if (host != null) {
                if (host.startsWith("www.")) host = host.substring(4);
                return host;
            }
        } catch (Exception ignored) {}
        return url;
    }

    public static String getDomain(String url) {
        try {
            android.net.Uri uri = android.net.Uri.parse(url);
            String host = uri.getHost();
            if (host != null && host.startsWith("www.")) return host.substring(4);
            return host != null ? host : url;
        } catch (Exception e) {
            return url;
        }
    }

    public static boolean isHttps(String url) {
        return url != null && url.startsWith("https://");
    }

    public static boolean isSearchUrl(String url, String searchEngineId) {
        if (url == null) return false;
        SearchEngine engine = getEngineById(searchEngineId);
        String baseSearchUrl = engine.getSearchUrl().replace("%s", "").replace("?q=", "");
        return url.startsWith(baseSearchUrl);
    }

    private static SearchEngine getEngineById(String id) {
        SearchEngine[] engines = SearchEngine.getDefaultEngines();
        for (SearchEngine e : engines) {
            if (e.getId().equals(id)) return e;
        }
        return engines[0]; // Default to Brave
    }

    public static String getFaviconUrl(String pageUrl) {
        String domain = getDomain(pageUrl);
        return "https://www.google.com/s2/favicons?domain=" + domain + "&sz=64";
    }
}
