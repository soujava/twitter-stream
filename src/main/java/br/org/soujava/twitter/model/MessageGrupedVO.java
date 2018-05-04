package br.org.soujava.twitter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageGrupedVO {


    public MessageGrupedVO() {
    }

    public MessageGrupedVO(String messageID,
                           long createdAt,
                           String textMessage,
                           long userID,
                           long userCreationDate,
                           String userName,
                           String userScreenName) {
        this.messageID = messageID;
        this.createdAt = createdAt;
        this.textMessage = textMessage;
        this.userID = userID;
        this.userCreationDate = userCreationDate;
        this.userName = userName;
        this.userScreenName = userScreenName;
    }

    @JsonProperty("messageID")
    private String messageID;

    @JsonProperty("creationDate")
    private long createdAt;

    @JsonProperty("textMessage")
    private String textMessage;

    @JsonProperty("userID")
    private long userID;

    @JsonProperty("userCreationDate")
    private long userCreationDate;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("userScreenName")
    private String userScreenName;


    public String getUserScreenName() {
        return userScreenName;
    }

    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }


    public long getUserCreationDate() {
        return userCreationDate;
    }

    public void setUserCreationDate(long userCreationDate) {
        this.userCreationDate = userCreationDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageGrupedVO that = (MessageGrupedVO) o;
        return userID == that.userID &&
                Objects.equals(messageID, that.messageID);
    }

    @Override
    public int hashCode() {

        return Objects.hash(messageID, userID);
    }
}


