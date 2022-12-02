package me.cedric.siegegame.modules.capturepoint;

import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ControlTask extends BukkitRunnable {

    private final List<ControlAreaHandler> controlAreaHandlers;
    private final WorldGame worldGame;
    private int stagesUncontestedPartialCap = 0;

    public ControlTask(WorldGame worldGame, List<ControlAreaHandler> controlAreaHandlers) {
        this.controlAreaHandlers = controlAreaHandlers;
        this.worldGame = worldGame;
    }

    @Override
    public void run() {
        for (ControlAreaHandler area : controlAreaHandlers) {
            List<GamePlayer> playersOnCap = new ArrayList<>();
            Team currentTeamOnCap = null;
            boolean contest = false;
            for (GamePlayer gamePlayer : worldGame.getActivePlayers()) {
                Player p = gamePlayer.getBukkitPlayer();

                if (p.isDead() || gamePlayer.isDead())
                    continue;

                if (area.getCuboid().colliding3d(Vector3D.getFromPlayerLocation(p.getLocation()))) {
                    //for each player on cap, if no-one else has been spotted on cap then assume that this team
                    //is the only one capping
                    //if someone else has already been found by above then the banner is being contested and
                    //no points should be awarded
                    if(currentTeamOnCap == null) {
                        currentTeamOnCap = gamePlayer.getTeam();
                    } else if (currentTeamOnCap != gamePlayer.getTeam()) {
                        contest = true;
                    }
                    playersOnCap.add(gamePlayer);
                    area.onPlayerCap(gamePlayer);
                }
            }

            if(area.getCurrentStage() != area.getMaxStages()) {
                if (contest) {
                    for (GamePlayer p : playersOnCap)
                        p.getBukkitPlayer().playSound(Sound.sound(org.bukkit.Sound.BLOCK_STONE_PLACE.key(), Sound.Source.NEUTRAL, 1.0F, 1.0F), Sound.Emitter.self());
                } else {
                    for (GamePlayer p : playersOnCap) {
                        p.getBukkitPlayer().playSound(Sound.sound(org.bukkit.Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON.key(), Sound.Source.NEUTRAL, 1.0F, 1.0F), Sound.Emitter.self());
                    }
                }
            }

            area.setTeamOnCap(currentTeamOnCap);

            //if one team is solely on the capturing area and it is not being contested
            //then increase the stage
            if (currentTeamOnCap != null && !contest) {
                if (area.getControllingSide() == currentTeamOnCap) {
                    if (!area.captured()) {
                        area.addStage();
                    } else {
                        area.onTeamCap(currentTeamOnCap);
                    }
                    //otherwise if it has been fully uncaptured then set them as the capping team
                } else if (area.getCurrentStage() == 0) {
                    area.setControllingSide(currentTeamOnCap);
                    area.addStage();
                    //if neither of those two conditions meet then that means another team is
                    //controlling the area and the other team has partially capped which means
                    //that the cap progress should be removed
                } else {
                    area.subtractStage();
                }

                stagesUncontestedPartialCap = 0;
                //if noone is on the cap then the area will slowly degrade.
            } else if (currentTeamOnCap == null && area.getCurrentStage() > 0 && area.getCurrentStage() < area.getMaxStages()) {
                stagesUncontestedPartialCap++;
                if(stagesUncontestedPartialCap >= area.getStagesPerDegrade()) {
                    stagesUncontestedPartialCap = 0;
                    if(!area.captured()) {
                        area.subtractStage();
                    } else {
                        area.addStage();
                    }
                }
            }
        }
    }

    public void addControlArea(ControlAreaHandler handler) {
        controlAreaHandlers.add(handler);
    }

    public void removeControlArea(int index) {
        controlAreaHandlers.remove(index);
    }


}
