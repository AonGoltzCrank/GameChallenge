package com.alon.gamechallenge.gameMgr;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.TimerTask;

import static com.alon.gamechallenge.Constants.BROADCAST_LOST_PARA;
import static com.alon.gamechallenge.Constants.BROADCAST_NEW_PARA;
import static com.alon.gamechallenge.Constants.BROADCAST_PLANE_X;
import static com.alon.gamechallenge.Constants.BROADCAST_SAVED_PARA;
import static com.alon.gamechallenge.Constants.BROADCAST_SHIP_X;
import static com.alon.gamechallenge.Constants.BROADCAST_UPDATE_PARA;
import static com.alon.gamechallenge.Constants.EXTRA_FLIP_PLANE_IMAGE;
import static com.alon.gamechallenge.Constants.EXTRA_PARATROOPER_ID;
import static com.alon.gamechallenge.Constants.EXTRA_PARATROOPER_Y;
import static com.alon.gamechallenge.Constants.EXTRA_PLANE_X;
import static com.alon.gamechallenge.Constants.EXTRA_SHIP_X;

/**
 * A Timer task that is run every 16 milliseconds (assuming it doesn't finish by then), that is used to update the UI using the {@link GameUpdateReceiver}.
 */

class BroadcastTimerTask extends TimerTask {

    private GameUpdateService mService;

    BroadcastTimerTask(GameUpdateService service) {
        mService = service;
    }

    @Override
    public void run() {
        if (GameUpdateService.mTimerCanContinue) {
            broadcastPlaneX();
            if (GameUpdateService.mUpdateShipX)
                broadcastShipX();
            if (GameUpdateService.mSignalNewPara)
                broadcastNewParatrooper();
            if (GameUpdateService.mLostParatroopers.size() > 0)
                broadcastParatroopersUpdate(Boolean.FALSE, GameUpdateService.mLostParatroopers);
            if (GameUpdateService.mSavedParatroopers.size() > 0)
                broadcastParatroopersUpdate(Boolean.TRUE, GameUpdateService.mSavedParatroopers);
            if (GameUpdateService.mParatroopers.size() > 0)
                broadcastParatroopersUpdate(null, GameUpdateService.mParatroopers);

            GameUpdateService.mTimerCanContinue = false;
        }
    }

    /**
     * Creates a broadcast intent for a given list and a saved status.
     * The list can either be of type {@link Paratrooper} or of type {@link Integer}.
     * The list type is determined by the <i>savedStatus</i> parameter.
     *
     * @param savedStatus
     *         - the status of the paratrooper(s) in question, null means just an update, true means saved, false means lost.
     * @param list
     *         - the list the was sent to be updated to the ui whether delete the image, or just update it.
     */
    @SuppressWarnings("unchecked")
    private void broadcastParatroopersUpdate(Boolean savedStatus, ArrayList<?> list) {
        ArrayList<Paratrooper> mParatroopers = null;
        ArrayList<Integer> mParatrooperIndexes = null;
        boolean isIndexes;
        if (savedStatus != null) {
            mParatrooperIndexes = (ArrayList<Integer>) list;
            isIndexes = true;
        } else {
            mParatroopers = (ArrayList<Paratrooper>) list;
            isIndexes = false;
        }
        int size = isIndexes ? mParatrooperIndexes.size() : mParatroopers.size();
        int[] updated = new int[size];
        float[] updatedYs = new float[size];
        for (int i = 0; i < size; i++) {
            updated[i] = (isIndexes ? mParatrooperIndexes.get(i) : mParatroopers.get(i).getIndex());
            if (!isIndexes)
                updatedYs[i] = mParatroopers.get(i).getY();
        }
        if (isIndexes)
            list.clear();
        Intent updateBroadcast = new Intent(savedStatus == null ? BROADCAST_UPDATE_PARA : (savedStatus.equals(Boolean.FALSE) ? BROADCAST_LOST_PARA : BROADCAST_SAVED_PARA));
        updateBroadcast.putExtra(EXTRA_PARATROOPER_ID, updated);
        if (!isIndexes)
            updateBroadcast.putExtra(EXTRA_PARATROOPER_Y, updatedYs);
        LocalBroadcastManager.getInstance(mService).sendBroadcast(updateBroadcast);

    }

    /**
     * Sends a broadcast to notify the addition of a new paratrooper.
     */
    private void broadcastNewParatrooper() {
        Intent signalPara = new Intent(BROADCAST_NEW_PARA);
        LocalBroadcastManager.getInstance(mService).sendBroadcast(signalPara);
        GameUpdateService.mSignalNewPara = false;
    }

    /**
     * Broadcasts the new ship X position.
     */
    private void broadcastShipX() {
        Intent shipPosUpdate = new Intent(BROADCAST_SHIP_X);
        shipPosUpdate.putExtra(EXTRA_SHIP_X, GameUpdateService.mShipCurrentX);
        LocalBroadcastManager.getInstance(mService).sendBroadcast(shipPosUpdate);
    }

    /**
     * Broadcasts the new plane X.
     */
    private void broadcastPlaneX() {
        Intent planePosUpdateIntent = new Intent(BROADCAST_PLANE_X);
        planePosUpdateIntent.putExtra(EXTRA_PLANE_X, GameUpdateService.mPlaneCurrentX).putExtra(EXTRA_FLIP_PLANE_IMAGE, GameUpdateService.mFlipPlaneImage);
        LocalBroadcastManager.getInstance(mService).sendBroadcast(planePosUpdateIntent);
    }
}
