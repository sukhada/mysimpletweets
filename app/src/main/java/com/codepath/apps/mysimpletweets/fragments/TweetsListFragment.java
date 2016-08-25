package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.codepath.apps.mysimpletweets.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skulkarni on 8/24/16.
 */
public class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private RecyclerView lvTweets;

    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    public TweetsArrayAdapter getaTweets() {
        return aTweets;
    }

    public RecyclerView getLvTweets() {
        return lvTweets;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);

        Configuration.Builder config = new Configuration.Builder(getActivity());
        config.addModelClasses(Tweet.class, User.class);
        ActiveAndroid.initialize(config.create());

        lvTweets = (RecyclerView) v.findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
        lvTweets.setAdapter(aTweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        lvTweets.setLayoutManager(linearLayoutManager);
        final Context self = getActivity();
        // Setup refresh listener which triggers new data loading

        return v;
    }


    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        aTweets.notifyDataSetChanged();
    }
}
