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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by skulkarni on 8/28/16.
 */
public class SearchTimelineFragment extends TweetsListFragment  implements ComposeFragment.CreateTweetDialogListener {
    private RecyclerView lvTweets;
    private TwitterClient client;
    private SwipeRefreshLayout swipeContainer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
    }

    public static SearchTimelineFragment newInstance(String q) {
        SearchTimelineFragment fragment = new SearchTimelineFragment();
        Bundle args = new Bundle();
        args.putString("q", q);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

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
            getSwipeContainer().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    lastID = 0;
                    reloadTweets();
                }
            });
            // Configure the refreshing colors
            getSwipeContainer().setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }


        if (isNetworkAvailable()) {
            populateTimeline();
        }
        else {
            getTweets().clear();
            List<Tweet> arrTweets = (ArrayList) Tweet.getLatestTweets();
            addAll(arrTweets);
            getaTweets().notifyDataSetChanged();
            Toast.makeText(getActivity(), "Connect to internet to view latest tweets", Toast.LENGTH_LONG).show();
        }

        return v;
    }

    private void populateTimeline() {
        String query = getArguments().getString("q");
        client.searchTweets(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray statuses = response.getJSONArray("statuses");
                    ArrayList<Tweet> tweetParsed = Tweet.fromJSONArray(statuses);
                    if (tweetParsed != null && tweetParsed.size() > 0) {
                        lastID = tweetParsed.get(tweetParsed.size()-1).getUid();
                        addAll(tweetParsed);
                        getaTweets().notifyDataSetChanged();
                    }
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public void onFinishComposeDialog(String inputText) {
        lastID = 0;
        getTweets().clear();
        getaTweets().notifyDataSetChanged();

        populateTimeline();
    }

}
