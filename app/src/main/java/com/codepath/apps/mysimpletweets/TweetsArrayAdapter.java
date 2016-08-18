package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        private ImageView ivPreviewImage;

        public TweetViewHolder(final View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvScreenname);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvDate = (TextView) itemView.findViewById(R.id.tvRelativeDate);
            ivPreviewImage = (ImageView) itemView.findViewById(R.id.ivPreviewImage);
        }

        public void update(Tweet tweet, Context context) {
            ivProfileImage.setImageResource(android.R.color.transparent);
            if (tweet.getUser() != null) {
                tvUserName.setText("@" + tweet.getUser().getScreenName());
                tvName.setText(tweet.getUser().getName());
                Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
            }
            tvBody.setText(tweet.getBody());
            tvDate.setText(Tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
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
    public void onBindViewHolder(TweetViewHolder viewHolder, final int position) {
        final Tweet tweet = tweets.get(position);
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
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

}
