package com.leonmontealegre.game.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LobbyActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "LobbyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
    }

}
