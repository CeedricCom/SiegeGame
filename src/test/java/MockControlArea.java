import com.sk89q.worldedit.math.BlockVector3;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.display.ColorUtil;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.map.GameMap;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.modules.capturepoint.ControlAreaHandler;
import me.cedric.siegegame.modules.capturepoint.Cuboid;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MockControlArea extends ControlAreaHandler {

    public MockControlArea(SiegeGamePlugin plugin, Cuboid cuboid, int maxStages, int groundLayer, GameMap map, String name, String displayName) {
        super(plugin, cuboid, maxStages, groundLayer, map, name, displayName);
    }

    @Override
    public void addStage() {
        boolean prevCaptured = captured();
        currentStage++;

        if(currentStage > maxStages) {
            return;
        }

        if(currentStage == maxStages && controllingSide != null) {
            captured = true;
        }

        int pivot = (currentStage - 1) * blocksPerStage;
        alterStage(pivot);

        if(captured() && !prevCaptured) {
            alterPoint();
            capturePoint();
        }
    }

    @Override
    protected void alterPoint() {

    }

    @Override
    protected void alterStage(int pivot) {
        int xmax = (int) cuboid.getLengthX();
        int imax = currentStage == maxStages ? fillArray.length : pivot + blocksPerStage;

        System.out.println("xmax: " + xmax);
        System.out.println("imax: " + imax);
        for (int i = pivot; i < imax; i++) {
            int z = (fillArray[i] / xmax);
            int x = fillArray[i] % xmax;

            System.out.println("i: " + i + ", fillArray[i]: " + fillArray[i] + " x: " + x + " z: " + z);
            System.out.println("COORDINATES: " + (x + cuboid.getMinX()) + ", " + (this.groundLayer) + ", " + (z + cuboid.getMinZ()));
        }
    }

    @Override
    public void onPlayerCap(GamePlayer gamePlayer) {

    }

    @Override
    public void onTeamCap(Team team) {

    }
}
