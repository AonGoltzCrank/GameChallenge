package com.alon.gamechallenge;

import android.app.Activity;

/**
 * All the constants used in the app, in order to recude the change of human typing error.
 */

public final class Constants {

    /**
     * A broadcast update for any {@link com.alon.gamechallenge.gameMgr.GameBroadcastReceiver} to unregister themselves.
     */
    public static final String BROADCAST_KILL_GAME = "kill";
    /**
     * Default prefix for everything (almost).
     */
    private static final String PACKAGE = "com.alon.gamechallenge.";
    /**
     * An action type for {@link com.alon.gamechallenge.gameMgr.GameUpdateService}.
     */
    public static final String ACTION = PACKAGE + "actionType";
    /**
     * Tells {@link com.alon.gamechallenge.gameMgr.GameUpdateService} to start if it hasn't yet.
     */
    public static final String ACTION_START_PLANE = PACKAGE + "initPlane";
    /**
     * Sets the new ship's x position
     *
     * @see com.alon.gamechallenge.gameMgr.GameManager#moveShip(Activity, float).
     */
    public static final String ACTION_SET_NEW_SHIP_X = PACKAGE + "setShipX";
    /**
     * Basically stops the {@link com.alon.gamechallenge.gameMgr.GameUpdateService.WorkerThread}.
     */
    public static final String ACTION_STOP_PLANE = PACKAGE + "stopPlane";
    /**
     * Stops the ship position update in {@link com.alon.gamechallenge.gameMgr.GameUpdateService.WorkerThread}.
     */
    public static final String ACTION_STOP_SHIP = PACKAGE + "stopShip";
    /**
     * Stops {@link com.alon.gamechallenge.gameMgr.GameUpdateService}.
     */
    public static final String ACTION_KILL = PACKAGE + "KILL";
    /**
     * Sets {@link com.alon.gamechallenge.gameMgr.GameUpdateService#mTimerCanContinue} to true so that
     * {@link com.alon.gamechallenge.gameMgr.BroadcastTimerTask} can continue sending information.
     */
    public static final String ACTION_CAN_CONTINUE = PACKAGE + "continue";
    /**
     * Signals {@link GameFragment} that it needs to create a new paratrooper.
     */
    public static final String ACTION_ADD_PARATROOPER = PACKAGE + "newPara";
    /**
     * @see com.alon.gamechallenge.gameMgr.GameManager#setParaLoseMark(Activity, int) .
     */
    public static final String ACTION_SET_PARA_LOST_MARK = PACKAGE + "lostMark";
    /**
     * The plane's X position.
     */
    public static final String EXTRA_PLANE_X = PACKAGE + "planeX";
    /**
     * The plane's width.
     */
    public static final String EXTRA_PLANE_WIDTH = PACKAGE + "planeWidth";
    /**
     * Should the plane be flipper.
     */
    public static final String EXTRA_FLIP_PLANE_IMAGE = PACKAGE + "flipPlane";
    /**
     * The screen's maximum width.
     */
    public static final String EXTRA_MAX_X_POS = PACKAGE + "maxX";
    /**
     * The ship's current X.
     */
    public static final String EXTRA_SHIP_X = PACKAGE + "shipX";
    /**
     * The X the ship is suppose to move to.
     */
    public static final String EXTRA_NEW_SHIP_X = PACKAGE + "shipMoveToX";
    /**
     * The ship's width.
     */
    public static final String EXTRA_SHIP_WIDTH = PACKAGE + "shipWidth";
    /**
     * The paratrooper's X position.
     */
    public static final String EXTRA_PARATROOPER_X = PACKAGE + "paraX";
    /**
     * The paratrooper's Y position.
     */
    public static final String EXTRA_PARATROOPER_Y = PACKAGE + "paraY";
    /**
     * The paratrooper's index in {@link GameFragment#mParatroopers}.
     */
    public static final String EXTRA_PARATROOPER_ID = PACKAGE + "paraID";
    /**
     * @see com.alon.gamechallenge.gameMgr.GameManager#setParaLoseMark(Activity, int) .
     */
    public static final String EXTRA_PARATROOPER_LOST_Y = PACKAGE + "paraLostMark";
    /**
     * The paratrooper's width.s
     */
    public static final String EXTRA_PARATROOPER_WIDTH = PACKAGE + "paraWidth";
    /**
     * The amount of points the player had when he lost the game.
     */
    public static final String EXTRA_LOST_GAME_POINTS = PACKAGE + "lostGamePoints";
    /**
     * A broadcast update for {@link com.alon.gamechallenge.gameMgr.GameUpdateReceiver} to update the plane's X.
     */
    public static final String BROADCAST_PLANE_X = PACKAGE + "planeXCoord";
    /**
     * A broadcast update for {@link com.alon.gamechallenge.gameMgr.GameUpdateReceiver} to update the ship's X.
     */
    public static final String BROADCAST_SHIP_X = PACKAGE + "shipXCoord";
    /**
     * A broadcast update for {@link com.alon.gamechallenge.GameFragment.ParatrooperUpdateReceiver} to create a new paratrooper.
     */
    public static final String BROADCAST_NEW_PARA = PACKAGE + "newPara";
    /**
     * A broadcast update for {@link com.alon.gamechallenge.GameFragment.ParatrooperUpdateReceiver} to inform that a paratrooper has been lost.
     */
    public static final String BROADCAST_LOST_PARA = PACKAGE + "lostPara";
    /**
     * A broadcast update for {@link com.alon.gamechallenge.GameFragment.ParatrooperUpdateReceiver} to inform that a paratrooper has been saved.
     */
    public static final String BROADCAST_SAVED_PARA = PACKAGE + "savedPara";
    /**
     * A broadcast update for {@link com.alon.gamechallenge.GameFragment.ParatrooperUpdateReceiver} to inform that a paratrooper's Y needs to be updated.
     */
    public static final String BROADCAST_UPDATE_PARA = PACKAGE + "updatePara";
    /**
     * A broadcast update for {@link com.alon.gamechallenge.GameFragment.ParatrooperUpdateReceiver} to inform that the game is over.
     */
    public static final String BROADCAST_GAME_OVER = PACKAGE + "gameOver";
}
