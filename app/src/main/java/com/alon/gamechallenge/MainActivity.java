package com.alon.gamechallenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;

import static com.alon.gamechallenge.Constants.BROADCAST_KILL_GAME;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragContainer) != null) {
            GameMenuFragment menuFrag = new GameMenuFragment();
            menuFrag.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragContainer, menuFrag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent kill = new Intent(BROADCAST_KILL_GAME);
        LocalBroadcastManager.getInstance(this).sendBroadcast(kill);
    }
}
