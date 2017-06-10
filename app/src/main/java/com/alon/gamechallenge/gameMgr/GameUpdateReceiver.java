package com.alon.gamechallenge.gameMgr;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import static com.alon.gamechallenge.Constants.BROADCAST_KILL_GAME;
import static com.alon.gamechallenge.Constants.BROADCAST_PLANE_X;
import static com.alon.gamechallenge.Constants.BROADCAST_SHIP_X;
import static com.alon.gamechallenge.Constants.EXTRA_FLIP_PLANE_IMAGE;
import static com.alon.gamechallenge.Constants.EXTRA_PLANE_X;
import static com.alon.gamechallenge.Constants.EXTRA_SHIP_X;

/**
 * A {@link GameBroadcastReceiver} used to get updates about the game's ui.
 */
public class GameUpdateReceiver extends GameBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mGameMgr == null) {
            Log.e(this.getClass().getName(), "No Game Manager set.");
            return;
        }
        String action = intent.getAction();
        switch (action) {
            case BROADCAST_PLANE_X:
                mGameMgr.updatePlane(intent.getFloatExtra(EXTRA_PLANE_X, 0),
                        intent.getBooleanExtra(EXTRA_FLIP_PLANE_IMAGE, false));
                break;
            case BROADCAST_SHIP_X:
                mGameMgr.updateShip(intent.getFloatExtra(EXTRA_SHIP_X, 0));
                break;
            case BROADCAST_KILL_GAME:
                LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
                return;
        }
        super.onReceive(context, intent);
    }

}
