package me.cedric.siegegame.modules.capturepoint;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.display.ColorUtil;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.map.GameMap;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public abstract class ControlAreaHandler implements Listener {

    protected String name;
    protected String displayName;
    protected int currentStage = 0;
    protected Team controllingSide;
    protected boolean captured = false;
    protected final int groundLayer;
    protected int[] fillArray;
    protected final int length;
    protected final int blocksPerStage;
    protected int midpointX;
    protected int midpointZ;
    protected final Cuboid cuboid;
    protected final GameMap map;
    protected final int maxStages = 10;
    protected final int ticksBetweenStages = 20;
    protected final int stagesPerDegrade = 1;
    protected Team teamOnCap;
    protected final org.bukkit.World bukkitWorld;
    protected final SiegeGamePlugin plugin;
    protected Location center;
    protected ControlAreaBlockers blockers = new ControlAreaBlockers();

    public ControlAreaHandler(SiegeGamePlugin plugin, Cuboid cuboid, int maxStages, int groundLayer, GameMap map, String name, String displayName) {
        this.controllingSide = null;
        this.plugin = plugin;
        this.groundLayer = groundLayer;
        this.bukkitWorld = map.getWorld();
        this.map = map;
        this.cuboid = cuboid;
        this.displayName = displayName;
        this.name = name;
        length = (int) (cuboid.getLengthX() * cuboid.getLengthZ());
        blocksPerStage = (int) Math.floor((float)length / maxStages);
        fillArray = new int[length];
        fill(length);
        fillArray = shuffle(fillArray.length, fillArray);
        center = new Location(bukkitWorld, midpointX + cuboid.getMinX(), groundLayer, midpointZ + cuboid.getMinZ());
    }
    //fills the array from 1 to n
    public void fill(int n) {
        midpointX = (int) Math.floor(cuboid.getLengthX() / 2F);
        midpointZ = (int) Math.floor(cuboid.getLengthZ() / 2);
        int midpoint = length * (midpointZ + midpointX);
        System.out.println("Midpoint: " + midpoint);
        System.out.println("Midpoint X: " + midpointX);
        System.out.println("Midpoint Z: " + midpointZ);
        int count = 0;
        for(int i = 0; i < n; i++) {
            if(i != midpoint) {
                fillArray[count] = i;
                count++;
            }
        }
    }
    //fischer-yates shuffle algorithm with seeding
    public int[] shuffle(int seed,int[] arr) {
        int m = arr.length;
        int i = 0;
        int t = 0;
        while(m>0) { //while there is still elements to shuffle
            m--;
            i = (int) Math.floor(MathUtil.random(seed)*m);
            t=arr[m];
            arr[m]=arr[i];
            arr[i]=t;
            seed++;
        }
        return arr;
    }
    //This will fill a control area with a single layer of the primary block at ground layer
    //and a single layer of the secondary block one above ground layer
    public void generate() {
        for (int x = (int) cuboid.getMinX(); x < cuboid.getMaxX(); x++) {
            for (int z = (int) cuboid.getMinZ(); z < cuboid.getMaxZ(); z++) {
                //editWorld.setBlock(BlockVector3.at(x, groundLayer, z), whiteWool.getDefaultState());
                bukkitWorld.getBlockAt(x, groundLayer, z).setType(Material.WHITE_WOOL);
                WorldGame worldGame = plugin.getGameManager().getCurrentMatch().getWorldGame();
                for (GamePlayer gamePlayer : worldGame.getPlayers()) {
                    gamePlayer.getFakeBlockManager().addBlock(Material.WHITE_WOOL, map.getWorld(), x, this.groundLayer, z);
                }
            }
        }

        // Beacon
        bukkitWorld.getBlockAt((int) (midpointX + cuboid.getMinX()), groundLayer, (int) (midpointZ + cuboid.getMinZ())).setType(Material.BEACON);

        for(int x = midpointX-1;x<=midpointX+1;x++) {
            for(int z=midpointZ-1;z<=midpointZ+1;z++) {
                bukkitWorld.getBlockAt((int) (x + cuboid.getMinX()),this.groundLayer-1, (int) (z + cuboid.getMinZ())).setType(Material.IRON_BLOCK);
            }
        }

        blockers.stopInteractions(plugin, this, map);
        //blockers.stopBlockChanges(plugin, this, map);
    }

    //logic for incrementing the stage if the control area is being captured
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

    protected void alterPoint() {
        WorldGame worldGame = plugin.getGameManager().getCurrentMatch().getWorldGame();
        for (GamePlayer gamePlayer : worldGame.getActivePlayers()) {
            Player player = gamePlayer.getBukkitPlayer();
            Team team = gamePlayer.getTeam();
            Material wool = ColorUtil.getRelationalWool(team, teamOnCap);
            Location location = new Location(bukkitWorld, midpointX + cuboid.getMinX(), this.groundLayer, midpointZ + cuboid.getMinZ());
            //FakeBlockManager fakeBlockManager = gamePlayer.getBorderHandler().getFakeBlockManager();
            //fakeBlockManager.setVisible(map.getWorld(), midpointX + (int) cuboid.getMinX(), groundLayer, midpointZ + (int) cuboid.getMinZ(), true);
            //fakeBlockManager.setMaterial(map.getWorld(), midpointX + (int) cuboid.getMinX(), groundLayer, midpointZ + (int) cuboid.getMinZ(), wool);
            player.sendBlockChange(location, plugin.getServer().createBlockData(wool));
        }
    }

    public void subtractStage() {
        boolean prevCaptured = captured;

        int pivot = (currentStage-1)*blocksPerStage;
        alterStage(pivot);

        currentStage--;

        if(controllingSide!=null && currentStage==0) {
            captured = false;
        }

        if(!captured() && prevCaptured) {
            alterPoint();
            uncapture();
        }
    }
    public void uncapture() {

        for(GamePlayer p : controllingSide.getPlayers()) {
            p.getBukkitPlayer().playSound(Sound.sound(org.bukkit.Sound.BLOCK_ANVIL_HIT.key(), Sound.Source.NEUTRAL, 1.0F, 1.0F));
        }
        controllingSide = null;
    }
    public void capturePoint() {
        for(GamePlayer p : controllingSide.getPlayers()) {
            p.getBukkitPlayer().playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP.key(), Sound.Source.NEUTRAL, 1.0F, 1.0F));
            p.getBukkitPlayer().sendMessage("Lost control area");
        }
    }

    protected void alterStage(int pivot) {
        int xmax = (int) cuboid.getLengthX();
        int imax = currentStage == maxStages ? fillArray.length : pivot + blocksPerStage;
        for (int i = pivot; i < imax; i++) {
            int z = (fillArray[i] / xmax);
            int x = fillArray[i] % xmax;

            WorldGame worldGame = plugin.getGameManager().getCurrentMatch().getWorldGame();
            for (GamePlayer gamePlayer : worldGame.getActivePlayers()) {
                Player player = gamePlayer.getBukkitPlayer();
                Team team = gamePlayer.getTeam();
                Material wool = ColorUtil.getRelationalWool(team, teamOnCap);
                Location location = new Location(bukkitWorld, x + cuboid.getMinX(), this.groundLayer, z + cuboid.getMinZ());

                if (location.getBlock().getType().equals(Material.BEACON))
                    continue;

                player.sendBlockChange(location, plugin.getServer().createBlockData(wool));
                //FakeBlockManager fakeBlockManager = gamePlayer.getBorderHandler().getFakeBlockManager();
                //fakeBlockManager.setVisible(map.getWorld(), midpointX + (int) cuboid.getMinX(), groundLayer, midpointZ + (int) cuboid.getMinZ(), true);
                //fakeBlockManager.setMaterial(map.getWorld(), midpointX + (int) cuboid.getMinX(), groundLayer, midpointZ + (int) cuboid.getMinZ(), wool);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        alterStage((currentStage-1)*blocksPerStage);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Vector3D vector3D = new Vector3D(location.getX(), location.getY(), location.getZ());

        if (cuboid.colliding2d(vector3D))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();
        Vector3D vector3D = new Vector3D(location.getX(), location.getY(), location.getZ());

        if (cuboid.colliding2d(vector3D))
            event.setCancelled(true);
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        Location location = event.getBlock().getLocation();
        Vector3D vector3D = new Vector3D(location.getX(), location.getY(), location.getZ());

        if (cuboid.colliding2d(vector3D))
            event.setCancelled(true);
    }

    public abstract void onPlayerCap(GamePlayer gamePlayer);

    public abstract void onTeamCap(Team team);

    public boolean captured() {
        return captured;
    }

    public boolean uncontested() {
        return currentStage == 0;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public int getMaxStages() {
        return maxStages;
    }

    public Team getControllingSide() {
        return controllingSide;
    }

    public void setControllingSide(Team controllingSide) {
        this.controllingSide = controllingSide;
    }

    public void setTeamOnCap(Team teamOnCap) {
        this.teamOnCap = teamOnCap;
    }

    public Team getTeamOnCap() {
        return teamOnCap;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public String getName() {
        return name;
    }

    public int getStagesPerDegrade() {
        return stagesPerDegrade;
    }
}
