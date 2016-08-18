package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by skulkarni on 8/17/16.
 */
public class ComposeFragment extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText newTweet;
    private Button btSave;
    private TwitterClient client;

    public ComposeFragment() {

    }

    public interface CreateTweetDialogListener {
        void onFinishComposeDialog(String inputText);
    }


    public static ComposeFragment newInstance(String title) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            CreateTweetDialogListener listener = (CreateTweetDialogListener) getActivity();
            listener.onFinishComposeDialog(newTweet.getText().toString());
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;

        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTweet = (EditText) view.findViewById(R.id.et_new_tweet);
        btSave = (Button) view.findViewById(R.id.bv_save_tweet);
        client = TwitterApplication.getRestClient();

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.postStatusUpdate(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", response.toString());
                        CreateTweetDialogListener listener = (CreateTweetDialogListener) getActivity();
                        listener.onFinishComposeDialog(newTweet.getText().toString());
                        dismiss();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", errorResponse.toString());
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                }, newTweet.getText().toString());
            }
        });
        newTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }
}
