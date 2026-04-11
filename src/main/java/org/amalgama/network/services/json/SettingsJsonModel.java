package org.amalgama.network.services.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingsJsonModel {
    @JsonProperty("superadmin")
    private boolean superAdmin;

    @JsonProperty("admin")
    private boolean admin;

    @JsonProperty("privacy_settings")
    private PrivacyJsonModel privacySettings;

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public PrivacyJsonModel getPrivacySettings() {
        return privacySettings;
    }

    public void setPrivacySettings(PrivacyJsonModel privacySettings) {
        this.privacySettings = privacySettings;
    }
}
