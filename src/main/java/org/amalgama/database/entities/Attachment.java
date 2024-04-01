package org.amalgama.database.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "t_attachments")
public class Attachment {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message Message;

    @Column(name = "type")
    private String Type;

    public Attachment() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Message getMessage() {
        return Message;
    }

    public void setMessage(Message message) {
        Message = message;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
