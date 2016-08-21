package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private TextView replyTo;
    private TextView charCount;
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
        Bundle args = this.getArguments();
        final Long replyUID = args.getLong("replyUID");
        String replyToName = args.getString("replyName");
        String replyToUserName = args.getString("replyScreenName");


        replyTo = (TextView) view.findViewById(R.id.tv_reply_to);
        charCount = (TextView) view.findViewById(R.id.tvCharCount);
        newTweet = (EditText) view.findViewById(R.id.et_new_tweet);
        btSave = (Button) view.findViewById(R.id.bv_save_tweet);
        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);

        client = TwitterApplication.getRestClient();

        if (replyToName != null) {
            replyTo.setText("In reply to " + replyToName);
            newTweet.setText("@" + replyToUserName + " ");
        }

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

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
                }, newTweet.getText().toString(), replyUID);
            }
        });



        newTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int char_count = 140 - charSequence.length();
                if (char_count < 0) {
                    charCount.setTextColor(0xFFFF0000);
                    btSave.setEnabled(false);
                }
                else {
                    charCount.setTextColor(0xFF000000);
                    btSave.setEnabled(true);
                }
                charCount.setText(Integer.toString(char_count));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        newTweet.setSelection(newTweet.getText().length());
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }
}
