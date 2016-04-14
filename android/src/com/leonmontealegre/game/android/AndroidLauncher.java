package com.leonmontealegre.game.android;

import android.os.Bundle;

import com.backendless.BackendlessUser;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.leonmontealegre.game.Game;

public class AndroidLauncher extends AndroidApplication {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		BackendlessUser user = (BackendlessUser)getIntent().getSerializableExtra(Options.USER_EXTRA);
		Game game = new Game();
		game.login((String)user.getProperty(Options.USERNAME_KEY));
		initialize(game, config);
	}

}