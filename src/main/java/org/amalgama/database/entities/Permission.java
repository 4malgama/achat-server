package org.amalgama.database.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "t_permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column(name = "rank")
    private String Rank;

    @Column(name = "permission_data")
    private String Permission;


    public Long getId() {
        return Id;
    }
    public void setId(Long id) {
        this.Id = id;
    }


    public String getRank() {
        return Rank;
    }
    public void setRank(String rank) {
        this.Rank = rank;
    }


    public String getPermission() {
        return Permission;
    }
    public void setPermission(String permission) {
        this.Permission = permission;
    }
}
