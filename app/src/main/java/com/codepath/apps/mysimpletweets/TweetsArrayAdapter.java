package com.codepath.apps.mysimpletweets;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by skulkarni on 8/17/16.
 */
public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.TweetViewHolder> {

    private List<Tweet> tweets;
    private Context context;

    public List<Tweet> getTweets() {
        return tweets;
    }

    public Context getContext() {
        return context;
    }

    public void update(ArrayList<Tweet> newTweets) {
        tweets = (ArrayList) newTweets;
    }

    public static class TweetViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfileImage;
        private TextView tvUserName;
        private TextView tvName;
        private TextView tvBody;
        private TextView tvDate;
        private TextView tvFavoriteCount;
        private TextView tvRetweetCount;
        private ImageView ivReply;
        private ImageView ivRetweet;
        private ImageView ivFavorite;
        private ImageView ivPreview;


        public TweetViewHolder(final View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvScreenname);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvDate = (TextView) itemView.findViewById(R.id.tvRelativeDate);
            tvFavoriteCount = (TextView) itemView.findViewById(R.id.tvFavoriteCount);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            ivReply = (ImageView) itemView.findViewById(R.id.iv_reply);
            ivRetweet = (ImageView) itemView.findViewById(R.id.iv_retweet);
            ivFavorite = (ImageView) itemView.findViewById(R.id.iv_favorite);
            ivPreview = (ImageView) itemView.findViewById(R.id.ivPreviewImage);
        }

        public void update(Tweet tweet, Context context) {
            ivProfileImage.setImageResource(android.R.color.transparent);
            if (tweet.getUser() != null) {
                tvUserName.setText("@" + tweet.getUser().getScreenName());
                tvName.setText(tweet.getUser().getName());
                Glide.with(context).load(tweet.getUser().getProfileImageUrl())
                        .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                        .into(ivProfileImage);
            }
            tvBody.setText(tweet.getBody());
            tvDate.setText(Tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
            tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));
            tvFavoriteCount.setText(Integer.toString(tweet.getFavouritesCount()));

            if (tweet != null && tweet.getMedia() != null) {
                Glide.with(context).load(tweet.getMedia())
                        .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                        .into(ivPreview);
            }
            else {
                ivPreview.setImageResource(0);
            }

            if (tweet != null && tweet.getFavorited()) {
                tvFavoriteCount.setTextColor(0xFFFF0000);
                ivFavorite.setColorFilter(0xFFFF0000);
            }
            else {
                tvFavoriteCount.setTextColor(0xFFA8A8A8);
                ivFavorite.setColorFilter(0xFFA8A8A8);
            }

            if (tweet != null && tweet.getRetweeted()) {
                tvRetweetCount.setTextColor(0xFF008000);
                ivRetweet.setColorFilter(0xFF008000);
            }
            else {
                tvRetweetCount.setTextColor(0xFFA8A8A8);
                ivRetweet.setColorFilter(0xFFA8A8A8);
            }
        }
    }

    public TweetsArrayAdapter(Context newContext, List<Tweet> newTweets) {
        tweets = newTweets;
        context = newContext;
    }
    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        // Return a new holder instance
        return new TweetViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(final TweetViewHolder viewHolder, final int position) {
        final Tweet tweet = tweets.get(position);
        final TimelineActivity ta = (TimelineActivity) getContext();
        final TwitterClient tc = TwitterApplication.getRestClient();

        viewHolder.update(tweet, getContext());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), TweetDetailActivity.class);
                Tweet t = tweets.get(position);
                i.putExtra("tweet", tweet);
                getContext().startActivity(i);
            }
        });

        viewHolder.ivRetweet.setOnClickListener(new View.OnClickListener() {
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
                        String newRetweetVal = Integer.toString((Integer.parseInt(viewHolder.tvRetweetCount.getText().toString())+1));
                        viewHolder.tvRetweetCount.setText(newRetweetVal);
                        set.playTogether(
                                ObjectAnimator.ofObject(viewHolder.ivRetweet, "colorFilter", new ArgbEvaluator(),
                                        /*Red*/0xFF32CD32, /*Gold*/0xFF008000)
                                        .setDuration(200)
                        );

                        set.playTogether(
                                ObjectAnimator.ofObject(viewHolder.tvRetweetCount, "textColor", new ArgbEvaluator(),
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

        viewHolder.ivFavorite.setOnClickListener(new View.OnClickListener() {
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
                        String newRetweetVal = Integer.toString((Integer.parseInt(viewHolder.tvFavoriteCount.getText().toString())+1));
                        viewHolder.tvFavoriteCount.setText(newRetweetVal);
                        set.playTogether(
                                ObjectAnimator.ofObject(viewHolder.ivFavorite, "colorFilter", new ArgbEvaluator(),
                                        /*Red*/0xFFFFF0F5, /*Gold*/0xFFFF0000)
                                        .setDuration(200)
                        );

                        set.playTogether(
                                ObjectAnimator.ofObject(viewHolder.tvFavoriteCount, "textColor", new ArgbEvaluator(),
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

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("screen_name", tweet.getUser().getScreenName());
                getContext().startActivity(i);
            }
        });

        viewHolder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = ta.getSupportFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance("Some Title");
                Bundle args = new Bundle();
                args.putLong("replyUID", tweet.getUid());
                args.putString("replyName", tweet.getUser().getName());
                args.putString("replyScreenName", tweet.getUser().getScreenName());
                composeFragment.setArguments(args);
                composeFragment.show(fm, "fragment_compose");
            }
        });

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.rgb(64,153,255),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent i = new Intent(getContext(), ProfileActivity.class);
                                i.putExtra("screen_name", text.replace("@", ""));
                                getContext().startActivity(i);
                            }
                        }).into(viewHolder.tvBody);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"), Color.rgb(64,153,255),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent i = new Intent(getContext(), SearchActivity.class);
                                i.putExtra("q", text.replace("#", ""));
                                getContext().startActivity(i);
                            }
                        }).into(viewHolder.tvBody);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

}
