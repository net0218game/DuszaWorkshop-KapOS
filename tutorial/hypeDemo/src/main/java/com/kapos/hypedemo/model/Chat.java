package com.kapos.hypedemo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Chat {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private MessageType type;
    private String content;
    private String sender;
    private String receiver;
    private String senderNickname;
    public Chat(Integer id, String content, String sender, String receiver){
        this.id=id;
        this.content=content;
        this.sender=sender;
        this.receiver=receiver;
    }

    public Chat() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(id, chat.id) && Objects.equals(content, chat.getContent()) && Objects.equals(sender, chat.getSender()) && Objects.equals(receiver, chat.getReceiver());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, sender, receiver);
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public enum MessageType {
        CHAT,
        JOIN,
        SYSTEMMESSAGE
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}