package com.alon.gamechallenge;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static com.alon.gamechallenge.Constants.EXTRA_LOST_GAME_POINTS;

/**
 * The game menu fragment.
 */

public class GameMenuFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ui = inflater.inflate(R.layout.fragment_game_menu, container, false);
        Button mPlay = (Button) ui.findViewById(R.id.gameMenu_play);
        Button mQuit = (Button) ui.findViewById(R.id.gameMenu_quit);
        mPlay.setOnClickListener(this);
        mQuit.setOnClickListener(this);

        if (getArguments() != null)
            Toast.makeText(getActivity(), getString(R.string.lost_game, getArguments().getInt(EXTRA_LOST_GAME_POINTS)), Toast.LENGTH_SHORT).show();
        return ui;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gameMenu_play:
                GameFragment newFrag = new GameFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragContainer, newFrag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.gameMenu_quit:
                getActivity().finish();
                break;
        }
    }
}
