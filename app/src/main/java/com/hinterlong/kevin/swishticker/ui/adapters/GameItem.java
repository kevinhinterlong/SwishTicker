package com.hinterlong.kevin.swishticker.ui.adapters;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hinterlong.kevin.swishticker.QueryEngine;
import com.hinterlong.kevin.swishticker.R;
import com.hinterlong.kevin.swishticker.data.Game;
import com.hinterlong.kevin.swishticker.data.Team;
import com.hinterlong.kevin.swishticker.ui.modules.TeamDetailActivity;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class GameItem extends AbstractFlexibleItem<GameItem.GameViewHolder> {
    private final Game game;

    public GameItem(Game game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameItem gameItem = (GameItem) o;
        return Objects.equals(game, gameItem.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_game;
    }

    @Override
    public GameViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new GameViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, GameViewHolder holder, int position, List<Object> payloads) {
        Team home = QueryEngine.getInstance().getTeam(game.getHomeTeamId());
        Team away = QueryEngine.getInstance().getTeam(game.getAwayTeamId());
        setUnderlineText(holder.homeTeamName, home.getName());
        setUnderlineText(holder.awayTeamName, away.getName());
        holder.homeTeamScore.setText(String.valueOf(game.getHomeScore()));
        holder.awayTeamScore.setText(String.valueOf(game.getAwayScore()));
    }

    private void setUnderlineText(TextView textView, String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }

    public static class GameViewHolder extends FlexibleViewHolder {
        @BindView(R.id.home_team_name) public TextView homeTeamName;
        @BindView(R.id.away_team_name) public TextView awayTeamName;
        @BindView(R.id.home_team_score) public TextView homeTeamScore;
        @BindView(R.id.away_team_score) public TextView awayTeamScore;

        public GameViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);

            homeTeamName.setOnClickListener(v -> {
                GameItem gameItem = (GameItem) adapter.getItem(getAdapterPosition());
                TeamDetailActivity.withTeam(view.getContext(), gameItem.game.getHomeTeamId());
            });
            awayTeamName.setOnClickListener(v -> {
                GameItem gameItem = (GameItem) adapter.getItem(getAdapterPosition());
                TeamDetailActivity.withTeam(view.getContext(), gameItem.game.getAwayTeamId());
            });

            view.setOnClickListener(v -> {
                GameItem gameItem = (GameItem) adapter.getItem(getAdapterPosition());
                Toast.makeText(view.getContext(), "TODO: Get Game id and go to game detail view", Toast.LENGTH_SHORT).show();
                // GameDetailActivity.withGame(view.getContext(), );
            });
        }
    }
}
