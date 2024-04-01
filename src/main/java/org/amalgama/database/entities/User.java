package org.amalgama.database.entities;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column(name = "login")
    private String Login;

    @Column(name = "email")
    private String Email;

    @Column(name = "password")
    private String Password;

    @Column(name = "fname")
    private String FName;

    @Column(name = "sname")
    private String SName;

    @Column(name = "mname")
    private String MName;

    @Column(name = "post")
    private String Post;

    @Column(name = "description")
    private String Description;

    @Column(name = "score")
    private Long Score;

    @Column(name = "balance")
    private Long Balance;

    @Column(name = "cash")
    private Long Cash;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission Permission;

    @Column(name = "settings_data")
    private String SettingsData;

    @Column(name = "register_timestamp")
    private Long RegisterTimestamp;

    @Column(name = "last_seen_timestamp")
    private Long LastLoginTimestamp;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "t_users_groups",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"))
    private List<Group> Groups = new ArrayList<>();

    public User() {
        Login = "";
        Email = "";
        Password = "";
        FName = "";
        SName = "";
        MName = "";
        Post = "";
        Description = "";
        Score = 0L;
        Balance = 0L;
        Cash = 0L;
        Permission = new Permission();
        SettingsData = "";
        RegisterTimestamp = 0L;
        LastLoginTimestamp = 0L;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public void setLogin(String Login) {
        this.Login = Login;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public void setSName(String SName) {
        this.SName = SName;
    }

    public void setMName(String MName) {
        this.MName = MName;
    }

    public void setPost(String Post) {
        this.Post = Post;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public void setScore(Long Score) {
        this.Score = Score;
    }

    public void setBalance(Long Balance) {
        this.Balance = Balance;
    }

    public void setCash(Long Cash) {
        this.Cash = Cash;
    }

    public void setPermission(Permission Permission) {
        this.Permission = Permission;
    }

    public void setSettingsData(String SettingsData) {
        this.SettingsData = SettingsData;
    }

    public void setRegisterTimestamp(Long RegisterTimestamp) {
        this.RegisterTimestamp = RegisterTimestamp;
    }

    public void setLastLoginTimestamp(Long LastLoginTimestamp) {
        this.LastLoginTimestamp = LastLoginTimestamp;
    }


    public void setGroups(List<Group> Groups) {
        this.Groups = Groups;
    }


    public Long getId() {
        return Id;
    }

    public String getLogin() {
        return Login;
    }

    public String getEmail() {
        return Email;
    }

    public String getPassword() {
        return Password;
    }

    public String getFName() {
        return FName;
    }

    public String getSName() {
        return SName;
    }

    public String getMName() {
        return MName;
    }

    public String getPost() {
        return Post;
    }

    public String getDescription() {
        return Description;
    }

    public Long getScore() {
        return Score;
    }

    public Long getBalance() {
        return Balance;
    }

    public Long getCash() {
        return Cash;
    }

    public Permission getPermission() {
        return Permission;
    }

    public String getSettingsData() {
        return SettingsData;
    }

    public Long getRegisterTimestamp() {
        return RegisterTimestamp;
    }

    public Long getLastLoginTimestamp() {
        return LastLoginTimestamp;
    }


    public List<Group> getGroups() {
        return Groups;
    }
}
