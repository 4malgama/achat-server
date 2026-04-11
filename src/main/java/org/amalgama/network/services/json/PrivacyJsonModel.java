package org.amalgama.network.services.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrivacyJsonModel {
    @JsonProperty("see_profile_photo")

    private String seeProfilePhoto = "everyone";

    @JsonProperty("send_message")
    private String sendMessage = "everyone";

    @JsonProperty("see_profile_description")
    private String seeProfileDescription = "everyone";

    @JsonProperty("invite_to_groups")
    private String inviteToGroups = "everyone";

    @JsonProperty("send_friend_request")
    private String sendFriendRequest = "everyone";

    @JsonProperty("see_online_status")
    private String seeOnlineStatus = "everyone";

    @JsonProperty("see_profile_post")
    private String seeProfilePost = "everyone";

    @JsonProperty("see_profile_comments")
    private String seeProfileComments = "everyone";

    @JsonProperty("leave_comments")
    private String leaveComments = "everyone";

    @JsonProperty("display_name")
    private String displayName = "";

    public String getSeeProfilePhoto() {
        return seeProfilePhoto;
    }

    public void setSeeProfilePhoto(String seeProfilePhoto) {
        this.seeProfilePhoto = seeProfilePhoto;
    }

    public String getSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(String sendMessage) {
        this.sendMessage = sendMessage;
    }

    public String getSeeProfileDescription() {
        return seeProfileDescription;
    }

    public void setSeeProfileDescription(String seeProfileDescription) {
        this.seeProfileDescription = seeProfileDescription;
    }

    public String getInviteToGroups() {
        return inviteToGroups;
    }

    public void setInviteToGroups(String inviteToGroups) {
        this.inviteToGroups = inviteToGroups;
    }

    public String getSendFriendRequest() {
        return sendFriendRequest;
    }

    public void setSendFriendRequest(String sendFriendRequest) {
        this.sendFriendRequest = sendFriendRequest;
    }

    public String getSeeOnlineStatus() {
        return seeOnlineStatus;
    }

    public void setSeeOnlineStatus(String seeOnlineStatus) {
        this.seeOnlineStatus = seeOnlineStatus;
    }

    public String getSeeProfilePost() {
        return seeProfilePost;
    }

    public void setSeeProfilePost(String seeProfilePost) {
        this.seeProfilePost = seeProfilePost;
    }

    public String getSeeProfileComments() {
        return seeProfileComments;
    }

    public void setSeeProfileComments(String seeProfileComments) {
        this.seeProfileComments = seeProfileComments;
    }

    public String getLeaveComments() {
        return leaveComments;
    }

    public void setLeaveComments(String leaveComments) {
        this.leaveComments = leaveComments;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
