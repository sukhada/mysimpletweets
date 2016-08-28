package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.codepath.apps.mysimpletweets.ComposeFragment;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by skulkarni on 8/24/16.
 */
public class TweetsListFragment extends Fragment implements ComposeFragment.CreateTweetDialogListener{

    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private RecyclerView lvTweets;
    private TwitterClient client;
    private SwipeRefreshLayout swipeContainer;

    public SwipeRefreshLayout getSwipeContainer() {
        return swipeContainer;
    }

    long lastID = 0;
    Toolbar toolbar;
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
        client = TwitterApplication.getRestClient();
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        Configuration.Builder config = new Configuration.Builder(getActivity());
        config.addModelClasses(Tweet.class, User.class);
        ActiveAndroid.initialize(config.create());

        lvTweets = (RecyclerView) v.findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
        lvTweets.setAdapter(aTweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        lvTweets.setLayoutManager(linearLayoutManager);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_timeline, menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.miCompose:
                FragmentManager fm = getChildFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance("Some Title");
                composeFragment.show(fm, "fragment_compose");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        setHasOptionsMenu(true);
    }

    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        aTweets.notifyDataSetChanged();
    }

    @Override
    public void onFinishComposeDialog(String inputText) {
        lastID = 0;
        getTweets().clear();
        getaTweets().notifyDataSetChanged();

        populateTimeline();
    }
}
