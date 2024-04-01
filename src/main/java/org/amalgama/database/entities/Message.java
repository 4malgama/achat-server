package org.amalgama.database.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "t_messages")
public class Message {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User User;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat Chat;

    @Column(name = "content")
    private String Content;

    @Column(name = "timestamp")
    private Long Timestamp;

    @Column(name = "reply_id")
    private Long ReplyId;

    @Column(name = "forward_id")
    private Long ForwardId;

    @Column(name = "forward_is_hidden")
    private boolean ForwardHidden;

    public Message() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }

    public Chat getChat() {
        return Chat;
    }

    public void setChat(Chat chat) {
        Chat = chat;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(Long timestamp) {
        Timestamp = timestamp;
    }

    public Long getReplyId() {
        return ReplyId;
    }

    public void setReplyId(Long replyId) {
        ReplyId = replyId;
    }

    public Long getForwardId() {
        return ForwardId;
    }

    public void setForwardId(Long forwardId) {
        ForwardId = forwardId;
    }

    public boolean isForwardHidden() {
        return ForwardHidden;
    }

    public void setForwardHidden(boolean forwardHidden) {
        ForwardHidden = forwardHidden;
    }
}
