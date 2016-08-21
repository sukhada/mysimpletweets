package com.codepath.apps.mysimpletweets.models;

import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.ocpsoft.prettytime.PrettyTime;


import static android.text.format.DateUtils.*;

/**
 * Created by skulkarni on 8/17/16.
 */
@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
    @Column(name = "Body")
    private String body;
    @Column(name = "uID", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    private long uid;
    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;
    @Column(name = "CreatedAt")
    private String createdAt;

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    @Column(name = "RetweetCount")
    private int retweetCount;
    @Column(name = "FavoritesCount")
    private int favouritesCount;

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    @Column(name = "Retweeted")
    private Boolean retweeted;
    @Column(name = "Favorited")
    private Boolean favorited;
    @Column(name = "Media")
    private String media;


    public String getMedia() {
        return media;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public Tweet(){
        super();
    }

    public Tweet(JSONObject jsonObject) {
        super();
        try {
            this.body = jsonObject.getString("text");
            this.uid = jsonObject.getLong("id");
            this.createdAt = jsonObject.getString("created_at");
            User u = new User(jsonObject.getJSONObject("user"));
            u.save();
            this.user = u;
            this.retweetCount = jsonObject.getInt("retweet_count");
            this.favouritesCount = jsonObject.getInt("favorite_count");
            this.retweeted = jsonObject.getBoolean("retweeted");
            this.favorited = jsonObject.getBoolean("favorited");

            JSONObject extended_entities =  jsonObject.getJSONObject("extended_entities");
            String media = null;
            if (extended_entities != null) {
                JSONArray mediaArr = extended_entities.getJSONArray("media");

                if (mediaArr != null) {
                    JSONObject m = (JSONObject) mediaArr.get(0);
                    this.media = m.getString("media_url");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<Tweet> getLatestTweets() {
        return new Select()
                .from(Tweet.class)
                .orderBy("CreatedAt DESC")
                .limit(50)
                .execute();
    }

    // Record Finders
    public static Tweet byId(long id) {
        return new Select().from(Tweet.class).where("id = ?", id).executeSingle();
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = new Tweet(tweetJson);
                tweet.save();
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            long nowMillis = System.currentTimeMillis();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    nowMillis, SECOND_IN_MILLIS).toString();

            relativeDate = relativeDate.replace("minutes", "m");
            relativeDate = relativeDate.replace("minute", "m");
            relativeDate = relativeDate.replace("hours", "h");
            relativeDate = relativeDate.replace("hour", "h");
            relativeDate = relativeDate.replace("seconds", "s");
            relativeDate = relativeDate.replace("second", "s");
            relativeDate = relativeDate.replace("ago", "");
            relativeDate = relativeDate.replace(" ", "");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
