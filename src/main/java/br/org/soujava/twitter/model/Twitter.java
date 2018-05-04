package br.org.soujava.twitter.model;

import br.org.soujava.twitter.utils.Constants;
import br.org.soujava.twitter.utils.DateUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Twitter {

    @JsonProperty("id_str")
    private String id;

    private String text;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("user")
    private User twitterUser;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getCreated() {
        return DateUtils.toEpochTime(createdAt, Constants.DATE_PATTERN);
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getTwitterUser() {
        return this.twitterUser;
    }

    public void setTwitterUser(User twitterUser) {
        this.twitterUser = twitterUser;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public boolean equals(Object other) {

        if ((other == null) || !(other instanceof Twitter)) {
            return false;
        }

        Twitter otherTweet = (Twitter) other;

        return id == otherTweet.getId();
    }


}
