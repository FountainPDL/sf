package com.surffountain.browser.models;

public class PrivacyStats {
    private int adsBlocked;
    private int trackersBlocked;
    private int httpsUpgrades;
    private int cookiesBlocked;
    private int scriptsBlocked;
    private int fingerprintsBlocked;
    private long dataSavedBytes;
    private long sessionStart;

    public PrivacyStats() {
        this.sessionStart = System.currentTimeMillis();
    }

    public void incrementAdsBlocked() { adsBlocked++; }
    public void incrementTrackersBlocked() { trackersBlocked++; }
    public void incrementHttpsUpgrades() { httpsUpgrades++; }
    public void incrementCookiesBlocked() { cookiesBlocked++; }
    public void incrementScriptsBlocked() { scriptsBlocked++; }
    public void incrementFingerprintsBlocked() { fingerprintsBlocked++; }
    public void addDataSaved(long bytes) { dataSavedBytes += bytes; }

    public int getAdsBlocked() { return adsBlocked; }
    public int getTrackersBlocked() { return trackersBlocked; }
    public int getHttpsUpgrades() { return httpsUpgrades; }
    public int getCookiesBlocked() { return cookiesBlocked; }
    public int getScriptsBlocked() { return scriptsBlocked; }
    public int getFingerprintsBlocked() { return fingerprintsBlocked; }
    public long getDataSavedBytes() { return dataSavedBytes; }
    public long getSessionStart() { return sessionStart; }

    public String getFormattedDataSaved() {
        if (dataSavedBytes < 1024) return dataSavedBytes + " B";
        if (dataSavedBytes < 1024 * 1024) return (dataSavedBytes / 1024) + " KB";
        return String.format("%.1f MB", dataSavedBytes / (1024.0 * 1024.0));
    }

    public int getTotalBlocked() {
        return adsBlocked + trackersBlocked + cookiesBlocked + scriptsBlocked + fingerprintsBlocked;
    }

    public void setAdsBlocked(int adsBlocked) { this.adsBlocked = adsBlocked; }
    public void setTrackersBlocked(int trackersBlocked) { this.trackersBlocked = trackersBlocked; }
    public void setHttpsUpgrades(int httpsUpgrades) { this.httpsUpgrades = httpsUpgrades; }
    public void setCookiesBlocked(int cookiesBlocked) { this.cookiesBlocked = cookiesBlocked; }
    public void setScriptsBlocked(int scriptsBlocked) { this.scriptsBlocked = scriptsBlocked; }
    public void setFingerprintsBlocked(int fingerprintsBlocked) { this.fingerprintsBlocked = fingerprintsBlocked; }
    public void setDataSavedBytes(long dataSavedBytes) { this.dataSavedBytes = dataSavedBytes; }
}
