package org.amalgama.network.services;

import org.amalgama.database.DBService;
import org.amalgama.database.entities.User;
import org.amalgama.network.services.json.PrivacyJsonModel;
import org.amalgama.network.services.json.SettingsJsonModel;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.logging.Logger;

public class UserAccessService {
    public static AccessData accessBetween(User viewer, User target) {
        if (viewer == null || target == null) {
            throw new IllegalArgumentException("Viewer or target is null");
        }

        if (Objects.equals(viewer.getId(), target.getId())) {
            return new AccessData();
        }

        AccessData data = new AccessData();
        String settings = target.getSettingsData();
        ObjectMapper mapper = new ObjectMapper();

        try {
            SettingsJsonModel settingsModel = mapper.readValue(settings, SettingsJsonModel.class);

            //TODO: check friends?

            DBService db = DBService.getInstance();
            boolean isBlacklisted = db.isBlackListed(target, viewer);
            boolean isFriends = db.isFriends(target, viewer);

            if (isBlacklisted) {
                data.displayName = "<YOU'RE BLACKLISTED>";
                return data;
            }

            final PrivacyJsonModel privacy = settingsModel.getPrivacySettings();
            if (privacy == null) {
                return data;
            }

            data.displayName = privacy.getDisplayName();

            if (isFriends) {
                data.accessPhoto = !privacy.getSeeProfilePhoto().equalsIgnoreCase("nobody");
                data.accessDescription = !privacy.getSeeProfileDescription().equalsIgnoreCase("nobody");
                data.accessViewComments = !privacy.getSeeProfileComments().equalsIgnoreCase("nobody");
                data.accessLeaveComments = !privacy.getLeaveComments().equalsIgnoreCase("nobody");
                data.accessPost = !privacy.getSeeProfilePost().equalsIgnoreCase("nobody");
                data.accessAddFriends = !privacy.getSendFriendRequest().equalsIgnoreCase("nobody");
                data.accessOnlineStatus = !privacy.getSeeOnlineStatus().equalsIgnoreCase("nobody");
                data.accessSendMessage = !privacy.getSendMessage().equalsIgnoreCase("nobody");
                data.accessInvites = !privacy.getInviteToGroups().equalsIgnoreCase("nobody");
            } else {
                data.accessPhoto = privacy.getSeeProfilePhoto().equalsIgnoreCase("everyone");
                data.accessDescription = privacy.getSeeProfileDescription().equalsIgnoreCase("everyone");
                data.accessViewComments = privacy.getSeeProfileComments().equalsIgnoreCase("everyone");
                data.accessLeaveComments = privacy.getLeaveComments().equalsIgnoreCase("everyone");
                data.accessPost = privacy.getSeeProfilePost().equalsIgnoreCase("everyone");
                data.accessAddFriends = privacy.getSendFriendRequest().equalsIgnoreCase("everyone");
                data.accessOnlineStatus = privacy.getSeeOnlineStatus().equalsIgnoreCase("everyone");
                data.accessSendMessage = privacy.getSendMessage().equalsIgnoreCase("everyone");
                data.accessInvites = privacy.getInviteToGroups().equalsIgnoreCase("everyone");
            }
        } catch (Exception e) {
            Logger.getGlobal().warning("Failed to parse user settings: " + e.getMessage());
            return data;
        }

        return data;
    }
}
