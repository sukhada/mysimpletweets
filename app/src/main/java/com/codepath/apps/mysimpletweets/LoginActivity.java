package com.codepath.apps.mysimpletweets;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActionBarActivity;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ImageView ivTweet = (ImageView) findViewById(R.id.logo);
		Button btLogin = (Button) findViewById(R.id.btLogin);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(
				ObjectAnimator.ofFloat(ivTweet, "scaleY", 1.0f, 3.0f)
						.setDuration(500)
		);

		set.playTogether(
				ObjectAnimator.ofFloat(ivTweet, "scaleX", 1.0f, 3.0f)
						.setDuration(500)
		);

		set.playTogether(
				ObjectAnimator.ofFloat(ivTweet, "scaleY", 3.0f, 0.3f)
						.setDuration(1000)
		);

		set.playTogether(
				ObjectAnimator.ofFloat(ivTweet, "scaleX", 3.0f, 0.3f)
						.setDuration(1000)
		);

		set.start();
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_timeline, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		Intent i = new Intent(this, TimelineActivity.class);
		startActivity(i);
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}
}
