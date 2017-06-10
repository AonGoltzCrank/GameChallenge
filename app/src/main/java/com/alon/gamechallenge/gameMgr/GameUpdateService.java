package com.alon.gamechallenge.gameMgr;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import static com.alon.gamechallenge.Constants.ACTION;
import static com.alon.gamechallenge.Constants.ACTION_ADD_PARATROOPER;
import static com.alon.gamechallenge.Constants.ACTION_CAN_CONTINUE;
import static com.alon.gamechallenge.Constants.ACTION_KILL;
import static com.alon.gamechallenge.Constants.ACTION_SET_NEW_SHIP_X;
import static com.alon.gamechallenge.Constants.ACTION_SET_PARA_LOST_MARK;
import static com.alon.gamechallenge.Constants.ACTION_START_PLANE;
import static com.alon.gamechallenge.Constants.ACTION_STOP_PLANE;
import static com.alon.gamechallenge.Constants.ACTION_STOP_SHIP;
import static com.alon.gamechallenge.Constants.BROADCAST_PLANE_X;
import static com.alon.gamechallenge.Constants.EXTRA_FLIP_PLANE_IMAGE;
import static com.alon.gamechallenge.Constants.EXTRA_MAX_X_POS;
import static com.alon.gamechallenge.Constants.EXTRA_NEW_SHIP_X;
import static com.alon.gamechallenge.Constants.EXTRA_PARATROOPER_ID;
import static com.alon.gamechallenge.Constants.EXTRA_PARATROOPER_LOST_Y;
import static com.alon.gamechallenge.Constants.EXTRA_PARATROOPER_WIDTH;
import static com.alon.gamechallenge.Constants.EXTRA_PARATROOPER_X;
import static com.alon.gamechallenge.Constants.EXTRA_PARATROOPER_Y;
import static com.alon.gamechallenge.Constants.EXTRA_PLANE_WIDTH;
import static com.alon.gamechallenge.Constants.EXTRA_PLANE_X;
import static com.alon.gamechallenge.Constants.EXTRA_SHIP_WIDTH;
import static com.alon.gamechallenge.Constants.EXTRA_SHIP_X;

/**
 * The {@link IntentService} used to run threads and send update to the ui.
 */
public class GameUpdateService extends IntentService {

    /**
     * Should the plane's X be updated.
     */
    static boolean mUpdatePlaneX = false;
    /**
     * Should the ship's X be updated.
     */
    static boolean mUpdateShipX = false;
    /**
     * Possible thread collision.*
     * Should the plane's image be flipped.
     */
    static volatile boolean mFlipPlaneImage;
    /**
     * The plane's current X.
     * Not on the screen.
     */
    static volatile float mPlaneCurrentX;

    /**
     * The ship's current X.
     * Not on the screen.
     */
    static volatile float mShipCurrentX;
    /**
     * The ship's width.
     */
    static float mShipWidth;
    /**
     * The X position the ship needs to move to.
     */
    static float mShipMoveToX;

    /**
     * A boolean to indicate whether or not a new paratrooper should be added to the game.
     */
    static volatile boolean mSignalNewPara;
    /**
     * An arraylist of all saved paratrooper indexes.
     * Note that this means all the saved paratroopers that were yet to be notified that they have been saved.
     */
    static volatile ArrayList<Integer> mSavedParatroopers;
    /**
     * An arraylist of all lost paratrooper indexes.
     * Note that this means all the lost paratroopers that were yet to be notified that they have been lost.
     */
    static volatile ArrayList<Integer> mLostParatroopers;
    /**
     * An arraylist of all the {@link Paratrooper}s that need their Y position updated.
     */
    static ArrayList<Paratrooper> mParatroopers;
    /**
     * A float for: {@link GameManager#mParaLostY}.
     *
     * @see GameManager#setParaLoseMark(Activity, int) .
     */
    static float mParatrooperLostMark;
    /**
     * The paratrooper's image width.
     */
    static float mParatrooperWidth = -1f;

    /**
     * Can the {@link BroadcastTimerTask} send another update. This is used in order to ensure that the system isn't flooded by updates from the service.
     * This also helps maintain a certain FPS count [Optimal will be 60 fps].
     */
    static volatile boolean mTimerCanContinue = true;
    /**
     * Is the {@link WorkerThread} alive.
     */
    private static boolean mThreadAlive = true;
    /**
     * @see WorkerThread
     */
    private static WorkerThread mWorkerThread;
    /**
     * The timer that runs the {@link BroadcastTimerTask}.
     */
    private static Timer timer;
    /**
     * RN-Jesus.
     * Used to determine if a new paratrooper should be spawned.
     */
    private static Random mRNG;
    /**
     * Is the plane headed right or left (true or false respectively).
     */
    private boolean mIsPlaneHeadedRight;
    /**
     * The float that will be multipled by the plane's speed to get it's movement direction (minus is left, plus is right).
     */
    private float mPlaneSpeedPrefix = 1;
    /**
     * The plane's width, used to make sure it doesn't go off the screen.
     */
    private float mPlaneWidth;
    /**
     * The screen's maximum width, so that the plane turns back around.
     */
    private float mMaxX;


    public GameUpdateService() {
        super("GameUpdateService");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        if (mRNG == null) mRNG = new Random();
        if (mParatroopers == null) mParatroopers = new ArrayList<>();
        if (mSavedParatroopers == null) mSavedParatroopers = new ArrayList<>();
        if (mLostParatroopers == null) mLostParatroopers = new ArrayList<>();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getStringExtra(ACTION);
            switch (action) {
                case ACTION_SET_NEW_SHIP_X:
                    float newX = intent.getFloatExtra(EXTRA_NEW_SHIP_X, 0);
                    if (newX == 0) {
                        mUpdateShipX = false;
                        return;
                    }
                    if (!mUpdateShipX) {
                        mShipCurrentX = intent.getFloatExtra(EXTRA_SHIP_X, 0);
                        mShipWidth = intent.getFloatExtra(EXTRA_SHIP_WIDTH, 0);
                        mUpdateShipX = true;
                    }
                    mShipMoveToX = mShipCurrentX + newX;
                    break;
                case ACTION_START_PLANE:
                    if (!mUpdatePlaneX) {
                        mUpdatePlaneX = true;

                        mPlaneWidth = intent.getFloatExtra(EXTRA_PLANE_WIDTH, 0);
                        mPlaneCurrentX = intent.getFloatExtra(EXTRA_PLANE_X, 0);
                        mMaxX = intent.getFloatExtra(EXTRA_MAX_X_POS, 800);

                        mParatrooperLostMark = intent.getIntExtra(EXTRA_PARATROOPER_LOST_Y, -1);

                        startWorkerThread();
                        updatePlaneDirection();

                        if (timer == null) {
                            timer = new Timer();
                            timer.scheduleAtFixedRate(new BroadcastTimerTask(this), 0, 16);
                        }
                    }
                    break;
                case ACTION_ADD_PARATROOPER:
                    float x = intent.getFloatExtra(EXTRA_PARATROOPER_X, -1);
                    float y = intent.getFloatExtra(EXTRA_PARATROOPER_Y, -1);
                    int index = intent.getIntExtra(EXTRA_PARATROOPER_ID, 0);
                    if (mParatrooperWidth == -1)
                        mParatrooperWidth = intent.getFloatExtra(EXTRA_PARATROOPER_WIDTH, -1);
                    mParatroopers.add(new Paratrooper(index, x, y));
                    break;
                case ACTION_SET_PARA_LOST_MARK:
                    mParatrooperLostMark = intent.getIntExtra(EXTRA_PARATROOPER_LOST_Y, -1);
                    break;
                case ACTION_CAN_CONTINUE:
                    mTimerCanContinue = true;
                    break;
                case ACTION_KILL:
                    mThreadAlive = false;
                    mWorkerThread.interrupt();
                    break;
                case ACTION_STOP_PLANE:
                    mUpdatePlaneX = false;
                    break;
                case ACTION_STOP_SHIP:
                    mUpdateShipX = false;
                    break;

            }
        }
    }

    /**
     * Used to broadcast to the {@link GameUpdateReceiver} if the plane's image needs to be flipped before Y update starts.
     */
    private void updatePlaneDirection() {
        mIsPlaneHeadedRight = mRNG.nextBoolean();

        if (!mIsPlaneHeadedRight)
            mPlaneSpeedPrefix = -1;

        if (mIsPlaneHeadedRight) {
            Intent planePosUpdateIntent = new Intent(BROADCAST_PLANE_X);
            planePosUpdateIntent.putExtra(EXTRA_PLANE_X, mPlaneCurrentX).putExtra(EXTRA_FLIP_PLANE_IMAGE, true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(planePosUpdateIntent);
        }

    }

    /**
     * Starts {@link #mWorkerThread}
     */
    private void startWorkerThread() {
        if (mWorkerThread != null && mWorkerThread.isAlive())
            mWorkerThread.interrupt();

        mWorkerThread = new WorkerThread();
        mWorkerThread.start();
    }

    ////////////////////////////////////
    // Worker Thread UI Value Update  //
    ////////////////////////////////////

    /**
     * The Worker Thread, used as sort of a render loop.
     */
    private class WorkerThread extends Thread {
        @Override
        public void run() {
            mThreadAlive = true;
            while (mThreadAlive) {
                if (mUpdatePlaneX)
                    updatePlaneX();
                if (mUpdateShipX)
                    updateShipX();
                if (mRNG.nextInt() % 487 == 0)  // about 1 in every 500 thread iterations
                    mSignalNewPara = true;
                if (mParatroopers.size() > 0)
                    updateParatrooperLists();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * Updates the paratrooper's list according to the paratroopers state (lost,saved, or falling).
         */
        private void updateParatrooperLists() {
            for (int i = 0; i < mParatroopers.size(); i++) {
                Paratrooper para = mParatroopers.get(i);
                para.moveDown();
                if (para.isLost()) {
                    mParatroopers.remove(para);
                    mLostParatroopers.add(para.getIndex());
                } else if (para.isSaved()) {
                    mParatroopers.remove(para);
                    mSavedParatroopers.add(para.getIndex());
                }
            }
        }

        /**
         * Updates the ship's X position if it is far enough from the position it's suppose to be at.
         */
        private void updateShipX() {
            float mShipSpeedPrefix = mShipCurrentX + (mShipWidth / 2) < mShipMoveToX ? 1 : -1;
            float delta = delta();
            if (delta > 1.5f) {
                float SHIP_SPEED = 3f;
                mShipCurrentX += (SHIP_SPEED * mShipSpeedPrefix);
            } else
                mUpdateShipX = false;
        }

        /**
         * Updates the plane position and flips it if it has reached either side of the screen.
         */
        private void updatePlaneX() {
            float PLANE_SPEED = 1.5f;
            float newPlaneX = mPlaneCurrentX + (PLANE_SPEED * mPlaneSpeedPrefix);
            mFlipPlaneImage = false;
            if (newPlaneX > mMaxX - mPlaneWidth || newPlaneX < 0) {
                mPlaneSpeedPrefix = (newPlaneX < 0) ? 1 : -1;
                mIsPlaneHeadedRight = mPlaneSpeedPrefix == 1;
                newPlaneX = mPlaneCurrentX + (PLANE_SPEED * mPlaneSpeedPrefix * 2);
                mFlipPlaneImage = true;
            }
            mPlaneCurrentX = newPlaneX;
        }

        /**
         * @return the distance between where the ship is suppose to move to, and where it's center is now.
         */
        private float delta() {
            return Math.abs((mShipCurrentX + (mShipWidth / 2)) - mShipMoveToX);
        }
    }
}
