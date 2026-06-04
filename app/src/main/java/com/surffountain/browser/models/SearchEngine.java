package com.surffountain.browser.models;

public class SearchEngine {
    public static final String BRAVE = "brave";
    public static final String GOOGLE = "google";
    public static final String BING = "bing";
    public static final String DUCKDUCKGO = "duckduckgo";
    public static final String YAHOO = "yahoo";
    public static final String STARTPAGE = "startpage";
    public static final String ECOSIA = "ecosia";
    public static final String CUSTOM = "custom";

    private String id;
    private String name;
    private String searchUrl;
    private String suggestUrl;
    private String faviconUrl;
    private boolean isDefault;
    private boolean isCustom;

    public SearchEngine(String id, String name, String searchUrl, String suggestUrl, String faviconUrl) {
        this.id = id;
        this.name = name;
        this.searchUrl = searchUrl;
        this.suggestUrl = suggestUrl;
        this.faviconUrl = faviconUrl;
    }

    public static SearchEngine[] getDefaultEngines() {
        return new SearchEngine[]{
            new SearchEngine(BRAVE, "Brave Search",
                "https://search.brave.com/search?q=%s",
                "https://search.brave.com/api/suggest?q=%s",
                "https://brave.com/favicon.ico"),
            new SearchEngine(GOOGLE, "Google",
                "https://www.google.com/search?q=%s",
                "https://suggestqueries.google.com/complete/search?client=firefox&q=%s",
                "https://www.google.com/favicon.ico"),
            new SearchEngine(BING, "Bing",
                "https://www.bing.com/search?q=%s",
                "https://api.bing.com/osjson.aspx?query=%s",
                "https://www.bing.com/favicon.ico"),
            new SearchEngine(DUCKDUCKGO, "DuckDuckGo",
                "https://duckduckgo.com/?q=%s",
                "https://duckduckgo.com/ac/?q=%s&type=list",
                "https://duckduckgo.com/favicon.ico"),
            new SearchEngine(YAHOO, "Yahoo",
                "https://search.yahoo.com/search?p=%s",
                "https://ff.search.yahoo.com/gossip?output=fxjson&command=%s",
                "https://www.yahoo.com/favicon.ico"),
            new SearchEngine(STARTPAGE, "Startpage",
                "https://www.startpage.com/sp/search?query=%s",
                null,
                "https://www.startpage.com/favicon.ico"),
            new SearchEngine(ECOSIA, "Ecosia",
                "https://www.ecosia.org/search?q=%s",
                "https://ac.ecosia.org/autocomplete?q=%s&type=list",
                "https://www.ecosia.org/favicon.ico")
        };
    }

    public String buildSearchUrl(String query) {
        try {
            String encoded = java.net.URLEncoder.encode(query, "UTF-8");
            return searchUrl.replace("%s", encoded);
        } catch (Exception e) {
            return searchUrl.replace("%s", query);
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSearchUrl() { return searchUrl; }
    public String getSuggestUrl() { return suggestUrl; }
    public String getFaviconUrl() { return faviconUrl; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
    public boolean isCustom() { return isCustom; }
    public void setCustom(boolean custom) { isCustom = custom; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSearchUrl(String searchUrl) { this.searchUrl = searchUrl; }
}
