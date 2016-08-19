package com.codepath.apps.mysimpletweets;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.models.Tweet;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by skulkarni on 8/18/16.
 */
public class TweetDetailActivity extends AppCompatActivity {
    private Tweet tweet;
    @BindView(R.id.ivDetailProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvDetailScreenname) TextView tvUserName;
    @BindView(R.id.tvDetailName) TextView tvName;
    @BindView(R.id.tvDetailBody) TextView tvBody;
    @BindView(R.id.tvDetailRelativeDate) TextView tvRelativeDate;
    @BindView(R.id.ivPreviewImage) ImageView ivPreviewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_tweet);
        ButterKnife.bind(this);

        tweet = (Tweet) getIntent().getSerializableExtra("tweet");

        tvUserName.setText("@" + tweet.getUser().getScreenName());
        tvName.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvRelativeDate.setText(Tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
        Glide.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00aced")));
    }


}
