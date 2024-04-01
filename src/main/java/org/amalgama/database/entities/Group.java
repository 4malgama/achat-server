package org.amalgama.database.entities;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column(name = "link")
    private String Link;

    @Column(name = "name")
    private String Name;

    @Column(name = "is_private")
    private boolean Private;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "t_users_groups",
            joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private List<User> Users = new ArrayList<>();

    public Group(String Link, String Name, boolean Private) {
        this.Link = Link;
        this.Name = Name;
        this.Private = Private;
    }

    public Group() {
    }

    public Long getId() {
        return Id;
    }

    public String getLink() {
        return Link;
    }

    public String getName() {
        return Name;
    }

    public boolean isPrivate() {
        return Private;
    }


    public List<User> getUsers() {
        return Users;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public void setLink(String Link) {
        this.Link = Link;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setPrivate(boolean Private) {
        this.Private = Private;
    }

    public void setUsers(List<User> Users) {
        this.Users = Users;
    }
}
