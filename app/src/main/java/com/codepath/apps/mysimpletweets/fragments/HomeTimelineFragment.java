package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.ComposeFragment;
import com.codepath.apps.mysimpletweets.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by skulkarni on 8/25/16.
 */
public class HomeTimelineFragment extends TweetsListFragment implements ComposeFragment.CreateTweetDialogListener{

    private TwitterClient client;
    private SwipeRefreshLayout swipeContainer;
    long lastID = 0;
    private RecyclerView lvTweets;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        lvTweets = getLvTweets();
        lvTweets.setLayoutManager(linearLayoutManager);
        lvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline();
            }
        });
        // Setup refresh listener which triggers new data loading
        if (isNetworkAvailable()) {
            swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    lastID = 0;
                    populateTimeline();
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }


        if (isNetworkAvailable()) {
            populateTimeline();
        }
        else {
            List<Tweet> arrTweets = (ArrayList) Tweet.getLatestTweets();
            getTweets().clear();
            addAll(arrTweets);
            getaTweets().notifyDataSetChanged();
            Toast.makeText(getActivity(), "Connect to internet to view latest tweets", Toast.LENGTH_LONG).show();
        }

        return v;
    }

    public void reloadTweets() {
        populateTimeline();
    }
    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> tweetParsed = Tweet.fromJSONArray(response);
                if (tweetParsed != null && tweetParsed.size() > 0) {
                    Log.d("DEBUG", Integer.toString(tweetParsed.size()));
                    lastID = tweetParsed.get(tweetParsed.size()-1).getUid();
                    addAll(tweetParsed);
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }, lastID-1);
    }

    @Override
    public void onFinishComposeDialog(String inputText) {
        lastID = 0;
        getTweets().clear();
        getaTweets().notifyDataSetChanged();

        populateTimeline();
    }
}
