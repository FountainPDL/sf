package com.surffountain.browser.privacy;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class AdBlocker {

    private static final String TAG = "AdBlocker";
    private static AdBlocker instance;

    private final Context context;
    private final Set<String> blockedDomains = new HashSet<>();
    private final List<Pattern> blockedPatterns = new ArrayList<>();
    private final List<String> cosmeticFilters = new ArrayList<>();
    private final Map<String, List<String>> siteSpecificFilters = new HashMap<>();

    private boolean enabled = true;
    private boolean initialized = false;

    // Known tracker domains
    private static final String[] KNOWN_TRACKERS = {
        "google-analytics.com", "googletagmanager.com", "doubleclick.net",
        "googlesyndication.com", "adsystem.amazon.com", "facebook.com/tr",
        "connect.facebook.net", "analytics.twitter.com", "ads.twitter.com",
        "scorecardresearch.com", "quantserve.com", "outbrain.com",
        "taboola.com", "adroll.com", "criteo.com", "bing.com/bat.js",
        "hotjar.com", "fullstory.com", "mouseflow.com", "crazyegg.com",
        "segment.com", "mixpanel.com", "amplitude.com", "heap.io",
        "branch.io", "adjust.com", "appsflyer.com", "kochava.com",
        "adnxs.com", "rubiconproject.com", "pubmatic.com", "openx.net",
        "moatads.com", "moatpixel.com", "amazon-adsystem.com",
        "chartbeat.com", "newrelic.com", "pingdom.com", "mopub.com",
        "inmobi.com", "unity3d.com/ads", "ironsrc.com"
    };

    // Common cosmetic filter selectors
    private static final String[] COSMETIC_SELECTORS = {
        ".ad", ".ads", ".advertisement", ".advert", ".ad-container",
        ".ad-banner", ".ad-wrapper", ".ad-block", ".ad-slot",
        "#ad", "#ads", "#advertisement", "#advert", "#ad-container",
        "[class*='ad-']", "[id*='ad-']", "[class*='-ad']", "[id*='-ad']",
        ".sponsored", ".sponsor", "[data-ad]", "[data-ads]",
        ".promo", ".promotional", "[class*='promo']",
        ".banner-ad", ".google-ad", ".adsense"
    };

    private AdBlocker(Context context) {
        this.context = context.getApplicationContext();
    }

    public static AdBlocker getInstance(Context context) {
        if (instance == null) {
            synchronized (AdBlocker.class) {
                if (instance == null) {
                    instance = new AdBlocker(context);
                }
            }
        }
        return instance;
    }

    public void initialize() {
        if (initialized) return;
        Log.d(TAG, "Initializing AdBlocker...");

        // Load known trackers
        for (String tracker : KNOWN_TRACKERS) {
            blockedDomains.add(tracker);
        }

        // Build cosmetic filter CSS
        StringBuilder css = new StringBuilder();
        for (String selector : COSMETIC_SELECTORS) {
            cosmeticFilters.add(selector);
            css.append(selector).append(",");
        }
        if (css.length() > 0) css.deleteCharAt(css.length() - 1);
        css.append("{display:none!important;visibility:hidden!important;}");

        // Try to load EasyList from assets
        try {
            loadFilterList("easylist.txt");
        } catch (Exception e) {
            Log.w(TAG, "EasyList not found in assets, using built-in filters");
        }

        initialized = true;
        Log.d(TAG, "AdBlocker initialized with " + blockedDomains.size() + " domains");
    }

    private void loadFilterList(String filename) {
        try {
            InputStream is = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("!") || line.startsWith("[")) continue;

                if (line.startsWith("##")) {
                    // Cosmetic filter
                    cosmeticFilters.add(line.substring(2));
                } else if (line.contains("##")) {
                    // Site-specific cosmetic filter
                    String[] parts = line.split("##", 2);
                    String site = parts[0];
                    String filter = parts[1];
                    siteSpecificFilters.computeIfAbsent(site, k -> new ArrayList<>()).add(filter);
                } else if (!line.startsWith("@@")) {
                    // Network filter
                    parseNetworkFilter(line);
                }
            }
            reader.close();
        } catch (Exception e) {
            Log.w(TAG, "Failed to load filter list: " + filename);
        }
    }

    private void parseNetworkFilter(String filter) {
        // Simple domain extraction
        String cleaned = filter.replaceAll("[|^*]", "");
        if (cleaned.contains("/")) {
            String domain = cleaned.split("/")[0];
            if (isValidDomain(domain)) {
                blockedDomains.add(domain);
            }
        } else if (isValidDomain(cleaned)) {
            blockedDomains.add(cleaned);
        }
    }

    private boolean isValidDomain(String s) {
        return s.contains(".") && s.length() > 4 && !s.contains(" ");
    }

    public boolean shouldBlock(String url, Map<String, String> headers) {
        if (!enabled || url == null) return false;
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null) return false;
            if (host.startsWith("www.")) host = host.substring(4);

            // Check domain list
            if (blockedDomains.contains(host)) return true;

            // Check substring patterns
            for (Pattern p : blockedPatterns) {
                if (p.matcher(url).find()) return true;
            }

            // Check common ad URL patterns
            String path = uri.getPath();
            if (path != null) {
                if (path.contains("/ads/") || path.contains("/ad/") ||
                    path.endsWith(".ad") || path.contains("/tracking/") ||
                    path.contains("/pixel/") || path.contains("/beacon/") ||
                    path.contains("/analytics/")) {
                    return true;
                }
            }

        } catch (Exception e) {
            // Invalid URL
        }
        return false;
    }

    public String getCosmeticFilterCss(String pageUrl) {
        StringBuilder css = new StringBuilder();
        css.append(String.join(",", cosmeticFilters.subList(0,
                Math.min(cosmeticFilters.size(), 50))));
        if (css.length() > 0) {
            css.append("{display:none!important;}");
        }
        return css.toString();
    }

    public void addCustomFilter(String domain) {
        blockedDomains.add(domain);
    }

    public void removeCustomFilter(String domain) {
        blockedDomains.remove(domain);
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getBlockedDomainCount() { return blockedDomains.size(); }
    public boolean isInitialized() { return initialized; }
}
