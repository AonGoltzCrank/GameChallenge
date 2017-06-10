package com.alon.gamechallenge.gameMgr;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView;

import static com.alon.gamechallenge.Constants.ACTION;
import static com.alon.gamechallenge.Constants.ACTION_KILL;
import static com.alon.gamechallenge.Constants.ACTION_SET_NEW_SHIP_X;
import static com.alon.gamechallenge.Constants.ACTION_SET_PARA_LOST_MARK;
import static com.alon.gamechallenge.Constants.ACTION_START_PLANE;
import static com.alon.gamechallenge.Constants.ACTION_STOP_SHIP;
import static com.alon.gamechallenge.Constants.BROADCAST_GAME_OVER;
import static com.alon.gamechallenge.Constants.BROADCAST_KILL_GAME;
import static com.alon.gamechallenge.Constants.BROADCAST_PLANE_X;
import static com.alon.gamechallenge.Constants.BROADCAST_SHIP_X;
import static com.alon.gamechallenge.Constants.EXTRA_LOST_GAME_POINTS;
import static com.alon.gamechallenge.Constants.EXTRA_MAX_X_POS;
import static com.alon.gamechallenge.Constants.EXTRA_NEW_SHIP_X;
import static com.alon.gamechallenge.Constants.EXTRA_PARATROOPER_LOST_Y;
import static com.alon.gamechallenge.Constants.EXTRA_PLANE_WIDTH;
import static com.alon.gamechallenge.Constants.EXTRA_PLANE_X;
import static com.alon.gamechallenge.Constants.EXTRA_SHIP_WIDTH;
import static com.alon.gamechallenge.Constants.EXTRA_SHIP_X;

/**
 * The Game Manager object. It is not a singleton but it has an implementation in place to prevent multiple game manager objects.
 * The Manager is used to get the current score, lives, updating the ship and plane poisitons, killing the game when the player leaves the screen, and more.
 */

public class GameManager {

    /**
     * Is the player in a game right now.
     */
    private static boolean mInGame = false;

    /**
     * The player's lives.
     */
    private int mLives;
    /**
     * The player's score.
     */
    private int mScore;

    /**
     * The line the above which the paratrooper is lost.
     */
    private int mParaLostY;

    /**
     * The screen's width, used for the plane's position update.
     */
    private float mScreenWidth;

    /**
     * The plane's {@link ImageView}.
     */
    private ImageView mPlane;

    /**
     * The ship's {@link ImageView}.
     */
    private ImageView mShip;

    /**
     * Instantiates a game manager and sets the {@link #mScreenWidth}.
     *
     * @param screenWidth
     *         - the screens width (without the navigation bar.
     *
     * @throws RuntimeException
     *         if there is already a game in progress.
     */
    public GameManager(float screenWidth) {
        if (mInGame) {
            throw new RuntimeException("Tried starting a new game while one is in progress.");
        }
        mInGame = true;

        mLives = 3;
        mScore = 0;

        mScreenWidth = screenWidth;
    }

    /**
     * @return {@link #mLives} in String.
     */
    public String lives() {
        return Integer.toString(mLives);
    }

    /**
     * @return {@link #mScore} in String.
     */
    public String score() {
        return Integer.toString(mScore);
    }

    /**
     * Reduces the lives of the player by one, if the player has no more lives it kills the {@link GameUpdateService}.
     *
     * @param caller
     *         - the caller activity, used to send the broadcasts and intents.
     */
    public void lostALife(Activity caller) {
        mLives--;
        if (mLives == 0) {
            Intent kill = new Intent(caller, GameUpdateService.class);
            kill.putExtra(ACTION, ACTION_KILL);
            caller.startService(kill);

            Intent lostGame = new Intent(BROADCAST_GAME_OVER);
            lostGame.putExtra(EXTRA_LOST_GAME_POINTS, mScore);
            LocalBroadcastManager.getInstance(caller).sendBroadcast(lostGame);
        }
    }


    /**
     * Sets the minimum y position, above which the paratrooper is lost.
     * It also sends it to {@link GameUpdateService}.
     *
     * @param caller
     *         - the caller activity, used to send the broadcasts and intents.
     * @param mark
     *         - the paratrooper lost mark.
     */
    public void setParaLoseMark(Activity caller, int mark) {
        mParaLostY = mark;
        Intent updateLostMark = new Intent(caller, GameUpdateService.class);
        updateLostMark.putExtra(EXTRA_PARATROOPER_LOST_Y, mark).putExtra(ACTION, ACTION_SET_PARA_LOST_MARK);
        caller.startService(updateLostMark);
    }

    /**
     * Starts the game by sending the {@link GameUpdateService} needed information (X,width, Max screen width, and paratrooper's lost mark {@link #mParaLostY}).
     * It also registers the {@link GameUpdateReceiver}.
     *
     * @param caller
     *         - the caller activity, used to send the broadcasts and intents.
     */
    public void start(Activity caller) {
        Intent startService = new Intent(caller, GameUpdateService.class);
        startService.putExtra(ACTION, ACTION_START_PLANE)
                .putExtra(EXTRA_PLANE_X, mPlane.getX())
                .putExtra(EXTRA_PLANE_WIDTH, (float) mPlane.getDrawable().getMinimumWidth())
                .putExtra(EXTRA_MAX_X_POS, mScreenWidth)
                .putExtra(EXTRA_PARATROOPER_LOST_Y, mParaLostY);
        caller.startService(startService);

        GameUpdateReceiver updateReceiver = new GameUpdateReceiver();
        updateReceiver.setGameManager(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_PLANE_X);
        filter.addAction(BROADCAST_SHIP_X);
        filter.addAction(BROADCAST_KILL_GAME);
        LocalBroadcastManager.getInstance(caller).registerReceiver(updateReceiver, filter);
    }

    /**
     * Called when the user touches the {@link #mShip} ImageView on the screen and drags his finger.
     * It is used as sort of an initializer for {@link  GameUpdateService}, and send's the ship's information (X, width, X to move to).
     *
     * @param caller
     *         - the caller activity, used to send the broadcast and intents.
     * @param x
     *         - the new X of the ship.
     */
    public void moveShip(Activity caller, float x) {
        Intent updateService = new Intent(caller, GameUpdateService.class);
        updateService.putExtra(ACTION, ACTION_SET_NEW_SHIP_X)
                .putExtra(EXTRA_SHIP_X, mShip.getX())
                .putExtra(EXTRA_SHIP_WIDTH, (float) mShip.getDrawable().getMinimumWidth())
                .putExtra(EXTRA_NEW_SHIP_X, x);
        caller.startService(updateService);
    }

    /**
     * Called when the user lifts his finger, or when the ship has reached the user's finger.
     *
     * @param caller
     *         - the caller activity, used to send the broadcasts and intents.
     */
    public void stopShip(Activity caller) {
        Intent stopPartOfService = new Intent(caller, GameUpdateService.class);
        stopPartOfService.putExtra(ACTION, ACTION_STOP_SHIP);
        caller.startService(stopPartOfService);
    }

    /**
     * Increases the player's points by 10 because a paratrooper has been saved.
     */
    public void savedParatrooper() {
        mScore += 10;
    }

    /**
     * Called when the activity is in its {@link Activity#onStop()} phase.
     * Used to tell all the receivers to unregister themselves.
     *
     * @param caller
     *         - the caller activity, used to send the broadcasts and intents.
     */
    public void kill(Activity caller) {
        Intent kill = new Intent(caller, GameUpdateService.class);
        kill.putExtra(ACTION, ACTION_KILL);
        caller.startService(kill);

        Intent killReceivers = new Intent(BROADCAST_KILL_GAME);
        LocalBroadcastManager.getInstance(caller).sendBroadcast(killReceivers);
    }

    // Setters
    public void setPlane(ImageView plane) {
        mPlane = plane;
    }

    public void setShip(ImageView ship) {
        mShip = ship;
    }
    // Ends setters.

    /**
     * Updates the plane's X position, and flips it if needed.
     *
     * @param newX
     *         - the plane's new X position.
     * @param flip
     *         - should the plane's image be flipped.
     */
    void updatePlane(float newX, boolean flip) {
        if (flip)
            mPlane.setScaleX(mPlane.getScaleX() * -1);
        mPlane.setX(newX);
    }

    /**
     * Updates the plane's X position.
     *
     * @param newX
     *         - the ship's new X position.
     */
    void updateShip(float newX) {
        mShip.setX(newX);
    }


}
