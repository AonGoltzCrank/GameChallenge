package com.alon.gamechallenge;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alon.gamechallenge.gameMgr.GameBroadcastReceiver;
import com.alon.gamechallenge.gameMgr.GameManager;
import com.alon.gamechallenge.gameMgr.GameUpdateService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.alon.gamechallenge.Constants.ACTION;
import static com.alon.gamechallenge.Constants.ACTION_ADD_PARATROOPER;
import static com.alon.gamechallenge.Constants.BROADCAST_GAME_OVER;
import static com.alon.gamechallenge.Constants.BROADCAST_KILL_GAME;
import static com.alon.gamechallenge.Constants.BROADCAST_LOST_PARA;
import static com.alon.gamechallenge.Constants.BROADCAST_NEW_PARA;
import static com.alon.gamechallenge.Constants.BROADCAST_SAVED_PARA;
import static com.alon.gamechallenge.Constants.BROADCAST_UPDATE_PARA;
import static com.alon.gamechallenge.Constants.EXTRA_LOST_GAME_POINTS;
import static com.alon.gamechallenge.Constants.EXTRA_PARATROOPER_ID;
import static com.alon.gamechallenge.Constants.EXTRA_PARATROOPER_X;
import static com.alon.gamechallenge.Constants.EXTRA_PARATROOPER_Y;

/**
 * The actual game.
 */

public class GameFragment extends Fragment implements Callback, View.OnTouchListener {

    private ImageView mSea;
    private ImageView mShip;
    private ImageView mPlane;

    private TextView mScore;
    private TextView mLives;

    private ProgressBar mLoading;

    private int mDrawnElementCount = 0;

    private DisplayUtils mDisplayUtils;

    private GameManager mGameMgr;

    private ArrayList<ImageView> mParatroopers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ui = inflater.inflate(R.layout.fragment_game, container, false);

        mDisplayUtils = DisplayUtils.getInstance(getActivity());
        mParatroopers = new ArrayList<>();

        DisplayMetrics metrics = mDisplayUtils.getMetrics(false);

        int newX = metrics.widthPixels / 6;
        int newY = metrics.heightPixels / 6;

        mLoading = (ProgressBar) ui.findViewById(R.id.game_LoadingImages);
        mSea = (ImageView) ui.findViewById(R.id.game_Sea);
        mShip = (ImageView) ui.findViewById(R.id.game_Ship);
        mPlane = (ImageView) ui.findViewById(R.id.game_Plane);
        mScore = (TextView) ui.findViewById(R.id.game_Score);
        mLives = (TextView) ui.findViewById(R.id.game_Lives);

        mShip.setOnTouchListener(this);

        Picasso.with(getContext()).load(R.drawable.sea).resize(newX * 6, newY).onlyScaleDown().into(mSea, this);
        Picasso.with(getContext()).load(R.drawable.boat).resize(newX, newY).onlyScaleDown().into(mShip, this);
        Picasso.with(getContext()).load(R.drawable.plane).resize(newX, newY).onlyScaleDown().into(mPlane, this);
        return ui;
    }

    @Override
    public void onSuccess() {
        mDrawnElementCount++;
        if (mDrawnElementCount == 3) {
            DisplayMetrics metrics = mDisplayUtils.getMetrics(false);

            mShip.setX((metrics.widthPixels - mShip.getDrawable().getMinimumWidth()) / 2);
            mPlane.setX((metrics.widthPixels - mPlane.getDrawable().getMinimumWidth()) / 2);

            mShip.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                public void onGlobalLayout() {
                    mGameMgr.setParaLoseMark(getActivity(), mShip.getTop());
                    mShip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            mGameMgr = new GameManager(metrics.widthPixels - mDisplayUtils.getSoftButtonsHeight(getActivity(), DisplayUtils.NAV_BAR_PORTRAIT_HEIGHT));
            mGameMgr.setPlane(mPlane);
            mGameMgr.setShip(mShip);
            mGameMgr.start(getActivity());

            mScore.setText(getString(R.string.score, mGameMgr.score()));
            mLives.setText(getString(R.string.lives, mGameMgr.lives()));

            mSea.setVisibility(View.VISIBLE);
            mShip.setVisibility(View.VISIBLE);
            mPlane.setVisibility(View.VISIBLE);
            mLoading.setVisibility(View.GONE);

            ParatrooperUpdateReceiver updateReceiver = new ParatrooperUpdateReceiver();
            updateReceiver.setGameManager(mGameMgr);

            IntentFilter filter = new IntentFilter();
            filter.addAction(BROADCAST_LOST_PARA);
            filter.addAction(BROADCAST_NEW_PARA);
            filter.addAction(BROADCAST_SAVED_PARA);
            filter.addAction(BROADCAST_UPDATE_PARA);
            filter.addAction(BROADCAST_GAME_OVER);
            filter.addAction(BROADCAST_KILL_GAME);

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateReceiver, filter);
        }
    }

    @Override
    public void onError() {
        mDrawnElementCount--;
        if (mDrawnElementCount == 3) {
            Toast.makeText(getContext(), getString(R.string.errLoadingImages), Toast.LENGTH_SHORT).show();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            GameMenuFragment newFrag = new GameMenuFragment();
            transaction.replace(R.id.fragContainer, newFrag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE)
            mGameMgr.moveShip(getActivity(), event.getX());
        else if (event.getAction() == MotionEvent.ACTION_UP)
            mGameMgr.stopShip(getActivity());
        return true;
    }


    ////////////////////////////////
    //  Paratrooper Controls      //
    ////////////////////////////////

    /**
     * Updates paratrooper information according to the parameters.
     *
     * @param savedFlags
     *         - are the paratroopers saved, lost or need update.
     * @param indexValues
     *         - the paratrooper's indexes (match {@link #mParatroopers}).
     * @param newYPositions
     *         - the paratrooper's new Y positions.
     */
    private void updateParatrooper(Boolean[] savedFlags, int[] indexValues, float[] newYPositions) {
        abstract class RunnableWithInfo implements Runnable {
            int[] indexes;
            Boolean[] savedFlag;
            float[] newYPositions;

            RunnableWithInfo(Boolean[] flag, int[] i, float[] y) {
                savedFlag = flag;
                indexes = i;
                newYPositions = y;
            }
        }

        getActivity().runOnUiThread(new RunnableWithInfo(savedFlags, indexValues, newYPositions) {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void run() {
                for (int i = 0; i < indexes.length; i++) {
                    Boolean isSaved = savedFlag[i];
                    if (isSaved == Boolean.FALSE)
                        mGameMgr.lostALife(getActivity());
                    else if (isSaved == Boolean.TRUE)
                        mGameMgr.savedParatrooper();
                    else {
                        ImageView paratrooper = mParatroopers.get(indexes[i]);
                        paratrooper.setY(newYPositions[i]);
                        continue;
                    }
                    ((RelativeLayout) getView().findViewById(R.id.game_Container)).removeView(mParatroopers.get(indexes[i]));
                    mScore.setText(getString(R.string.score, mGameMgr.score()));
                    mLives.setText(getString(R.string.lives, mGameMgr.lives()));
                }
            }
        });
    }

    /**
     * A {@link GameBroadcastReceiver} used to run {@link #updateParatrooper(Boolean[], int[], float[])}.
     */
    public class ParatrooperUpdateReceiver extends GameBroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mGameMgr == null) {
                Log.e(this.getClass().getName(), "No Game Manager set.");
                return;
            }
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BROADCAST_LOST_PARA:
                    case BROADCAST_SAVED_PARA:
                    case BROADCAST_UPDATE_PARA:
                        int[] indexValues = intent.getIntArrayExtra(EXTRA_PARATROOPER_ID);
                        float[] newYPositions = intent.getFloatArrayExtra(EXTRA_PARATROOPER_Y);
                        Boolean[] savedFlags = new Boolean[indexValues.length];
                        for (int i = 0; i < indexValues.length; i++)
                            savedFlags[i] = (action.equals(BROADCAST_UPDATE_PARA) ? null : (action.equals(BROADCAST_LOST_PARA) ? Boolean.FALSE : Boolean.TRUE));
                        GameFragment.this.updateParatrooper(savedFlags, indexValues, newYPositions);
                        break;
                    case BROADCAST_NEW_PARA:
                        GameFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @SuppressWarnings("ConstantConditions")
                            @Override
                            public void run() {
                                Context context = GameFragment.this.getContext();
                                ImageView paratrooper = new ImageView(context);

                                DisplayMetrics metrics = mDisplayUtils.getMetrics(false);
                                int newX = metrics.widthPixels / 6;
                                int newY = metrics.heightPixels / 6;

                                Picasso.with(context).load(R.drawable.parachutist).resize(newX, newY).onlyScaleDown().into(paratrooper);
                                paratrooper.setX(mPlane.getX());
                                paratrooper.setY(mPlane.getY());

                                mParatroopers.add(paratrooper);

                                ((RelativeLayout) getView().findViewById(R.id.game_Container)).addView(paratrooper);

                                Intent addedParatrooperIntent = new Intent(context, GameUpdateService.class);
                                addedParatrooperIntent.putExtra(ACTION, ACTION_ADD_PARATROOPER)
                                        .putExtra(EXTRA_PARATROOPER_X, mPlane.getX())
                                        .putExtra(EXTRA_PARATROOPER_Y, mPlane.getY())
                                        .putExtra(EXTRA_PARATROOPER_ID, mParatroopers.indexOf(paratrooper));
                                context.startService(addedParatrooperIntent);
                            }
                        });
                        break;

                    case BROADCAST_GAME_OVER:
                        GameMenuFragment frag = new GameMenuFragment();
                        Bundle lostBundle = new Bundle();
                        lostBundle.putInt(EXTRA_LOST_GAME_POINTS, intent.getIntExtra(EXTRA_LOST_GAME_POINTS, 0));
                        frag.setArguments(lostBundle);
                        FragmentTransaction transaction = GameFragment.this.getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragContainer, frag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case BROADCAST_KILL_GAME:
                        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
                        break;
                }
            }
        }
    }
}
