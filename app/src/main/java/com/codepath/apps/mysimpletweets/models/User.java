package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by skulkarni on 8/17/16.
 */
@Table(name = "User")
public class User extends Model implements Serializable {
    @Column(name = "Name")
    private String name;
    private long uid;
    @Column(name = "Screename")
    private String screenName;
    @Column(name = "ProfileImageURL")
    private String profileImageUrl;
    @Column(name = "Tagline")
    private String tagline;
    @Column(name = "FollowersCount")
    private int followersCount;
    @Column(name = "FollowingCount")
    private int followingCount;

    public String getTagline() {
        return tagline;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // Record Finders
    public static User byId(long id) {
        return new Select().from(User.class).where("id = ?", id).executeSingle();
    }
    public User(){
        super();
    }

    public User(JSONObject json) {
        super();
        try {
            this.name = json.getString("name");
            this.uid = json.getLong("id");
            this.screenName = json.getString("screen_name");
            this.profileImageUrl = json.getString("profile_image_url");
            this.tagline = json.getString("description");
            this.followersCount = json.getInt("followers_count");
            this.followingCount = json.getInt("friends_count");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static User fromJSON(JSONObject json) {
        User u = new User();
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
            u.tagline = json.getString("description");
            u.followersCount = json.getInt("followers_count");
            u.followingCount = json.getInt("friends_count");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return u;
    }
}
