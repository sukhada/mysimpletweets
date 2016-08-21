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
    @Column(name = "uID", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    private long uid;
    @Column(name = "Screename")
    private String screenName;
    @Column(name = "ProfileImageURL")
    private String profileImageUrl;

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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
