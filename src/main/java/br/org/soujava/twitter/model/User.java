package br.org.soujava.twitter.model;

import br.org.soujava.twitter.utils.Constants;
import br.org.soujava.twitter.utils.DateUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @JsonProperty("id")
    private long id;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("name")
    private String name;

    @JsonProperty("screen_name")
    private String screenName;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return this.screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }


    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public boolean equals(Object other) {

        if ((other == null) || !(other instanceof User)) {
            return false;
        }

        User otherTwitterUser = (User) other;

        return id == otherTwitterUser.getId();
    }

}
