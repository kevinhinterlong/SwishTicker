package com.hinterlong.kevin.swishticker.ui.adapters;

import android.view.View;
import android.widget.TextView;

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

public class TeamItem extends AbstractFlexibleItem<TeamItem.TeamViewHolder> {
    private final Team team;
    private final int teamId;

    public TeamItem(Team team, int teamId) {
        this.team = team;
        this.teamId = teamId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamItem teamItem = (TeamItem) o;
        return Objects.equals(team, teamItem.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_team;
    }

    @Override
    public TeamViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new TeamViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, TeamViewHolder holder, int position, List<Object> payloads) {
        holder.teamName.setText(team.getName());
        holder.teamSize.setText(String.valueOf(team.getPlayers().size()));
        List<Game> games = QueryEngine.getInstance().getGames();
        // TODO win/loss counter is kinda painful since we don't know the team id
        holder.numberWon.setText("0");
        holder.numberLost.setText("0");
    }

    public static class TeamViewHolder extends FlexibleViewHolder {
        @BindView(R.id.team_name) public TextView teamName;
        @BindView(R.id.team_size) public TextView teamSize;
        @BindView(R.id.number_won) public TextView numberWon;
        @BindView(R.id.number_lost) public TextView numberLost;

        public TeamViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);

            view.setOnClickListener(v -> {
                TeamItem teamItem = (TeamItem) adapter.getItem(getAdapterPosition());
                TeamDetailActivity.withTeam(view.getContext(), teamItem.teamId);
            });
        }
    }
}
