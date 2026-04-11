package org.amalgama.database.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "t_users_blacklist")
public class Blacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User User;


    @ManyToOne
    @JoinColumn(name = "blocked_id")
    private User Blocked;

    @Column(name = "block_start_time")
    private Long BlockStartTime;

    @Column(name = "block_end_time")
    private Long BlockEndTime;

    public Blacklist(User user, User blocked, Long blockStartTime, Long blockEndTime) {
        this.User = user;
        this.Blocked = blocked;
        this.BlockStartTime = blockStartTime;
        this.BlockEndTime = blockEndTime;
    }


    public Blacklist() {}

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public org.amalgama.database.entities.User getUser() {
        return User;
    }

    public void setUser(org.amalgama.database.entities.User user) {
        User = user;
    }

    public org.amalgama.database.entities.User getBlocked() {
        return Blocked;
    }

    public void setBlocked(org.amalgama.database.entities.User blocked) {
        Blocked = blocked;
    }

    public Long getBlockStartTime() {
        return BlockStartTime;
    }

    public void setBlockStartTime(Long blockStartTime) {
        this.BlockStartTime = blockStartTime;
    }

    public Long getBlockEndTime() {
        return BlockEndTime;
    }

    public void setBlockEndTime(Long blockEndTime) {
        this.BlockEndTime = blockEndTime;
    }
}
