package org.amalgama.database.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "t_bans")
public class Ban {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User User;

    @Column(name = "ip")
    private String IP;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User Admin;

    @Column(name = "reason")
    private String Reason;

    @Column(name = "start_timestamp")
    private Long StartTime;

    @Column(name = "expiration_timestamp")
    private Long ExpireTime;

    public Long getId() {
        return Id;
    }

    public User getUser() {
        return User;
    }

    public String getIP() {
        return IP;
    }

    public User getAdmin() {
        return Admin;
    }

    public String getReason() {
        return Reason;
    }

    public Long getStartTime() {
        return StartTime;
    }

    public Long getExpireTime() {
        return ExpireTime;
    }

    public Ban(User user, String IP, User admin, String reason, Long StartTime, Long ExpireTime) {
        this.User = user;
        this.IP = IP;
        this.Admin = admin;
        this.Reason = reason;
        this.StartTime = StartTime;
        this.ExpireTime = ExpireTime;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public void setUser(User user) {
        this.User = user;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setAdmin(User admin) {
        this.Admin = admin;
    }

    public void setReason(String reason) {
        this.Reason = reason;
    }

    public void setStartTime(Long StartTime) {
        this.StartTime = StartTime;
    }

    public void setExpireTime(Long ExpireTime) {
        this.ExpireTime = ExpireTime;
    }
}
