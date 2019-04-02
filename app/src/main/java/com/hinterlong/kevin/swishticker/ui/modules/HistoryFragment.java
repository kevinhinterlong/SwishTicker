package com.hinterlong.kevin.swishticker.ui.modules;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hinterlong.kevin.swishticker.QueryEngine;
import com.hinterlong.kevin.swishticker.R;
import com.hinterlong.kevin.swishticker.ui.adapters.GameItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * A fragment representing a list of Items.
 */
public class HistoryFragment extends Fragment {
    private final FlexibleAdapter<GameItem> adapter = new FlexibleAdapter<>(null);
    @BindView(R.id.game_list) RecyclerView recyclerView;
    private Unbinder unbinder;

    public HistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateDataSet(getGamesList());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        adapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public List<GameItem> getGamesList() {
        List<GameItem> list = new ArrayList<>();
        for (int gameId : QueryEngine.getInstance().getGameIds()) {
            GameItem gameItem = new GameItem(QueryEngine.getInstance().getGame(gameId), gameId);
            list.add(gameItem);
        }
        return list;
    }
}
