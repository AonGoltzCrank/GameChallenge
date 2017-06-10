package com.alon.gamechallenge.gameMgr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.alon.gamechallenge.Constants.ACTION;
import static com.alon.gamechallenge.Constants.ACTION_CAN_CONTINUE;

/**
 * A {@link BroadcastReceiver} that is used as a base class for broadcast-receivers about the game status, or updates about the ui or game.
 */

public class GameBroadcastReceiver extends BroadcastReceiver {

    protected GameManager mGameMgr;

    public void setGameManager(GameManager mgr) {
        mGameMgr = mgr;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceCanContinue = new Intent(context, GameUpdateService.class);
        serviceCanContinue.putExtra(ACTION, ACTION_CAN_CONTINUE);
        context.startService(serviceCanContinue);
    }
}
