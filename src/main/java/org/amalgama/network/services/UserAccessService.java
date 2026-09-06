package org.amalgama.network.services;

import org.amalgama.database.DBService;
import org.amalgama.database.entities.User;
import org.amalgama.network.services.json.PrivacyJsonModel;
import org.amalgama.network.services.json.SettingsJsonModel;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.logging.Logger;

public class UserAccessService {
    private static boolean allows(String rule, boolean isFriend) {
        return "everyone".equalsIgnoreCase(rule) || (isFriend && "friends".equalsIgnoreCase(rule));
    }

    public static AccessData accessBetween(User viewer, User target) {
        AccessData data = new AccessData();

        if (viewer == null || target == null || viewer.getId() == null || target.getId() == null) {
            throw new IllegalArgumentException("Viewer or target is null");
        }

        if (Objects.equals(viewer.getId(), target.getId())) {
            data.accessPhoto = true;
            data.accessDescription = true;
            data.accessViewComments = true;
            data.accessLeaveComments = true;
            data.accessPost = true;
            data.accessAddFriends = true;
            data.accessOnlineStatus = true;
            data.accessSendMessage = true;
            data.accessInvites = true;
            return data;
        }

        try {
            DBService db = DBService.getInstance();

            boolean isBlacklisted = db.isBlackListed(target, viewer);

            if (isBlacklisted) {
                data.displayName = "<YOU'RE BLACKLISTED>";
                return data;
            }

            String settings = target.getSettingsData();
            if (settings == null || settings.isBlank()) {
                return data;
            }

            ObjectMapper mapper = new ObjectMapper();
            SettingsJsonModel settingsModel = mapper.readValue(settings, SettingsJsonModel.class);

            if (settingsModel == null || settingsModel.getPrivacySettings() == null) {
                return data;
            }

            PrivacyJsonModel privacy = settingsModel.getPrivacySettings();
            boolean isFriend = db.isFriends(target, viewer);

            data.displayName = Objects.toString(privacy.getDisplayName(), "");

            data.accessPhoto = allows(privacy.getSeeProfilePhoto(), isFriend);
            data.accessDescription = allows(privacy.getSeeProfileDescription(), isFriend);
            data.accessViewComments = allows(privacy.getSeeProfileComments(), isFriend);
            data.accessLeaveComments = allows(privacy.getLeaveComments(), isFriend);
            data.accessPost = allows(privacy.getSeeProfilePost(), isFriend);
            data.accessAddFriends = allows(privacy.getSendFriendRequest(), isFriend);
            data.accessOnlineStatus = allows(privacy.getSeeOnlineStatus(), isFriend);
            data.accessSendMessage = allows(privacy.getSendMessage(), isFriend);
            data.accessInvites = allows(privacy.getInviteToGroups(), isFriend);

            return data;
        } catch (Exception e) {
            Logger.getGlobal().warning("Failed to evaluate privacy for user: " + target.getId());
            return new AccessData();
        }
    }
}
