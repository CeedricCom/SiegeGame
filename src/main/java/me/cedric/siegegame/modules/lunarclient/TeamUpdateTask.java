package me.cedric.siegegame.modules.lunarclient;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloLocation;
import com.lunarclient.apollo.module.team.TeamMember;
import com.lunarclient.apollo.module.team.TeamModule;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.player.GamePlayer;
import me.cedric.siegegame.model.teams.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TeamUpdateTask extends BukkitRunnable {

    private final WorldGame worldGame;

    public TeamUpdateTask(WorldGame worldGame) {
        this.worldGame = worldGame;
    }

    @Override
    public void run() {
        for (Team team : worldGame.getTeams()) {
            refresh(team);
        }
    }

    private void refresh(Team team) {
        List<TeamMember> teammates = team.getPlayers().stream().filter(gamePlayer -> gamePlayer.getBukkitPlayer().isOnline())
                .map(gamePlayer -> createTeamMember(gamePlayer.getBukkitPlayer()))
                .toList();

        teammates.forEach(teamMember -> Apollo.getPlayerManager().getPlayer(teamMember.getPlayerUuid())
                .ifPresent(apolloPlayer -> Apollo.getModuleManager().getModule(TeamModule.class).updateTeamMembers(apolloPlayer, teammates)));
    }

    private TeamMember createTeamMember(Player member) {
        Location location = member.getLocation();

        return TeamMember.builder()
                .playerUuid(member.getUniqueId())
                .displayName(Component.text()
                        .content(member.getName())
                        .color(NamedTextColor.WHITE)
                        .build())
                .markerColor(Color.GREEN)
                .location(ApolloLocation.builder()
                        .world(location.getWorld().getName())
                        .x(location.getX())
                        .y(location.getY())
                        .z(location.getZ())
                        .build())
                .build();
    }


}
