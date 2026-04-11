package org.amalgama.database.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "t_users_friends")
public class Friends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long Id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User User;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User Friend;

    @Column(name = "friends_since")
    private long FriendsSince;

    public Friends(User user, User friend, long friendsSince) {
        this.User = user;
        this.Friend = friend;
        this.FriendsSince = friendsSince;
    }


    public Friends() {}

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        this.Id = id;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        this.User = user;
    }

    public User getFriend() {
        return Friend;
    }

    public void setFriend(User friend) {
        this.Friend = friend;
    }

    public long getFriendsSince() {
        return FriendsSince;
    }

    public void setFriendsSince(long friendsSince) {
        this.FriendsSince = friendsSince;
    }
}
