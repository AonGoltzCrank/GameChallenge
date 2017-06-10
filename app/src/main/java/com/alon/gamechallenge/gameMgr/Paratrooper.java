package com.alon.gamechallenge.gameMgr;

import static com.alon.gamechallenge.gameMgr.GameUpdateService.mParatrooperLostMark;
import static com.alon.gamechallenge.gameMgr.GameUpdateService.mParatrooperWidth;
import static com.alon.gamechallenge.gameMgr.GameUpdateService.mShipCurrentX;
import static com.alon.gamechallenge.gameMgr.GameUpdateService.mShipWidth;

/**
 * A paratrooper object, contains it's position and a helper methods for it's position relative to the ship and the {@link GameManager#mParaLostY}.
 */

class Paratrooper {

    private final int mIndex;
    private final float mCurrentX;
    private float mCurrentY = -1;


    Paratrooper(int i, float x, float y) {
        mIndex = i;
        mCurrentX = x;
        mCurrentY = y;
    }

    /**
     * Moves the paratrooper down the screen by 1 unit.
     */
    void moveDown() {
        float PARATROOPER_SPEED = 1f;
        mCurrentY += PARATROOPER_SPEED;
    }


    /**
     * @return has the paratrooper been lost to the sea.
     */
    boolean isLost() {
        return !isWithinShipRange() && isBelowLostMark();
    }

    /**
     * @return has the paratrooper been saved by the ship.
     */
    boolean isSaved() {
        return isWithinShipRange() && isBelowLostMark();
    }

    /**
     * @return the paratroopers index in {@link com.alon.gamechallenge.GameFragment#mParatroopers}.
     */
    int getIndex() {
        return mIndex;
    }

    //Getters and helpers
    private boolean isWithinShipRange() {
        return (mCurrentX <= mShipWidth + mShipCurrentX && mCurrentX + mParatrooperWidth >= mShipCurrentX);
    }

    private boolean isBelowLostMark() {
        return mCurrentY > mParatrooperLostMark;
    }

    float getY() {
        return mCurrentY;
    }
    //Ends getters and helpers.
}
