package org.amalgama.database.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "t_chats")
public class Chat {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User User;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group Group;

    @Column(name = "is_group")
    private boolean IsGroup;

    public Chat(Group Group) {
        this.User = null;
        this.Group = Group;
        this.IsGroup = true;
    }

    public Chat(User User) {
        this.User = User;
        this.Group = null;
        this.IsGroup = false;
    }

    public Long getId() {
        return Id;
    }

    public User getUser() {
        return User;
    }

    public Group getGroup() {
        return Group;
    }

    public boolean isGroup() {
        return IsGroup;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public void setUser(User User) {
        this.User = User;
    }

    public void setGroup(Group Group) {
        this.Group = Group;
    }

    public void setGroup(boolean IsGroup) {
        this.IsGroup = IsGroup;
    }
}
