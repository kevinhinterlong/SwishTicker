package com.hinterlong.kevin.swishticker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hinterlong.kevin.swishticker.HistoryFragment.OnListFragmentInteractionListener;
import com.hinterlong.kevin.swishticker.query.Game;
import com.hinterlong.kevin.swishticker.query.QueryEngine;
import com.hinterlong.kevin.swishticker.query.Team;

import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Game} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class GameRVAdapter extends RecyclerView.Adapter<GameRVAdapter.ViewHolder> {

    private final List<Game> mValues;
    private final OnListFragmentInteractionListener mListener;

    public GameRVAdapter(List<Game> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Game game = mValues.get(position);
        Team home = QueryEngine.getInstance().getTeam(game.getHomeTeamId());
        Team away = QueryEngine.getInstance().getTeam(game.getAwayTeamId());
        holder.mItem = game;
        holder.homeTeamName.setText(home.getName());
        holder.awayTeamName.setText(away.getName());
        holder.homeTeamScore.setText(String.valueOf(game.getHomeScore()));
        holder.awayTeamScore.setText(String.valueOf(game.getAwayScore()));

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView homeTeamName;
        public final TextView awayTeamName;
        public final TextView homeTeamScore;
        public final TextView awayTeamScore;
        public Game mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            homeTeamName = view.findViewById(R.id.home_team_name);
            awayTeamName = view.findViewById(R.id.away_team_name);;
            homeTeamScore = view.findViewById(R.id.home_team_score);
            awayTeamScore = view.findViewById(R.id.away_team_score);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + awayTeamName.getText() + "'";
        }
    }
}
