package org.amalgama.database.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "t_profile_comments")
public class ProfileComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User User;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private User Target;

    @Column(name = "timestamp")
    private Long Timestamp;

    @Column(name = "content")
    private String Content;

    public ProfileComment(User user, User target, Long timestamp, String content) {
        this.User = user;
        this.Target = target;
        this.Timestamp = timestamp;
        this.Content = content;
    }

    public Long getId() {
        return Id;
    }

    public User getUser() {
        return User;
    }

    public User getTarget() {
        return Target;
    }

    public Long getTimestamp() {
        return Timestamp;
    }

    public String getContent() {
        return Content;
    }

    public void setId(Long id) {
        Id = id;
    }

    public void setUser(User user) {
        User = user;
    }

    public void setTarget(User target) {
        Target = target;
    }

    public void setTimestamp(Long timestamp) {
        Timestamp = timestamp;
    }

    public void setContent(String content) {
        Content = content;
    }
}
