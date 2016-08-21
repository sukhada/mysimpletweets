package com.codepath.apps.mysimpletweets;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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
    @BindView(R.id.iv_detail_retweet) ImageView ivDetailRetweet;
    @BindView(R.id.tvDetailRetweetCount) TextView tvDetailRetweetCount;
    @BindView(R.id.iv_detail_favorite) ImageView ivDetailFavorite;
    @BindView(R.id.tvDetailFavoriteCount) TextView tvDetailFavoriteCount;
    @BindView(R.id.iv_detail_reply) ImageView ivDetailReply;
    @BindView(R.id.ivDetailPreviewImage) ImageView ivDetailPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_tweet);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        tweet = (Tweet) getIntent().getSerializableExtra("tweet");

        final TwitterClient tc = new TwitterClient(this);
        tvUserName.setText("@" + tweet.getUser().getScreenName());
        tvName.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvRelativeDate.setText(Tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
        tvDetailRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));
        tvDetailFavoriteCount.setText(Integer.toString(tweet.getFavouritesCount()));
        Glide.with(this).load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
                .into(ivProfileImage);

        ActionBar bar = getSupportActionBar();

        if (tweet.getMedia() != null) {
            Glide.with(this).load(tweet.getMedia())
                    .bitmapTransform(new RoundedCornersTransformation(this, 15, 0))
                    .into(ivDetailPreview);
        }
        else {
            ivDetailPreview.setImageResource(0);
        }


        if (tweet.getFavorited()) {
            tvDetailFavoriteCount.setTextColor(0xFFFF0000);
            ivDetailFavorite.setColorFilter(0xFFFF0000);
        }
        else {
            tvDetailFavoriteCount.setTextColor(0xFFA8A8A8);
            ivDetailFavorite.setColorFilter(0xFFA8A8A8);
        }

        if (tweet.getRetweeted()) {
            tvDetailRetweetCount.setTextColor(0xFF008000);
            ivDetailRetweet.setColorFilter(0xFF008000);
        }
        else {
            tvDetailRetweetCount.setTextColor(0xFFA8A8A8);
            ivDetailRetweet.setColorFilter(0xFFA8A8A8);
        }

        ivDetailRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tc.postRetweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        AnimatorSet set = new AnimatorSet();
                        String newRetweetVal = Integer.toString((Integer.parseInt(tvDetailRetweetCount.getText().toString())+1));
                        tvDetailRetweetCount.setText(newRetweetVal);
                        set.playTogether(
                                ObjectAnimator.ofObject(ivDetailRetweet, "colorFilter", new ArgbEvaluator(),
                                        /*Red*/0xFF32CD32, /*Gold*/0xFF008000)
                                        .setDuration(200)
                        );

                        set.playTogether(
                                ObjectAnimator.ofObject(tvDetailRetweetCount, "textColor", new ArgbEvaluator(),
                                        /*Red*/0xFF32CD32, /*Gold*/0xFF008000)
                                        .setDuration(200)
                        );
                        set.start();
                        tweet.setRetweeted(true);
                        tweet.setRetweetCount(tweet.getRetweetCount()+1);
                        super.onSuccess(statusCode, headers, response);
                    }
                }, tweet.getUid());
            }
        });

        ivDetailFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tc.postFavorite(new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        AnimatorSet set = new AnimatorSet();
                        String newRetweetVal = Integer.toString((Integer.parseInt(tvDetailFavoriteCount.getText().toString())+1));
                        tvDetailFavoriteCount.setText(newRetweetVal);
                        set.playTogether(
                                ObjectAnimator.ofObject(ivDetailFavorite, "colorFilter", new ArgbEvaluator(),
                                        /*Red*/0xFFFFF0F5, /*Gold*/0xFFFF0000)
                                        .setDuration(200)
                        );

                        set.playTogether(
                                ObjectAnimator.ofObject(tvDetailFavoriteCount, "textColor", new ArgbEvaluator(),
                                        /*Red*/0xFFFFF0F5, /*Gold*/0xFFFF0000)
                                        .setDuration(200)
                        );
                        set.start();
                        tweet.setFavorited(true);
                        tweet.setFavouritesCount(tweet.getFavouritesCount()+1);
                        super.onSuccess(statusCode, headers, response);
                    }
                }, tweet.getUid());
            }
        });

        ivDetailReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance("Some Title");
                Bundle args = new Bundle();
                args.putLong("replyUID", tweet.getUid());
                args.putString("replyName", tweet.getUser().getName());
                args.putString("replyScreenName", tweet.getUser().getScreenName());
                composeFragment.setArguments(args);
                composeFragment.show(fm, "fragment_compose");
            }
        });

    }


}
