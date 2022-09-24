package me.cedric.siegegame.border;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BlockChangePacketAdapter extends PacketAdapter {

    private final SiegeGame plugin;

    public BlockChangePacketAdapter(Plugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.BLOCK_CHANGE);
        this.plugin = (SiegeGame) plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        BlockPosition location = packet.getBlockPositionModifier().read(0);

        Player player = event.getPlayer();
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        WorldGame worldGame = plugin.getGameManager().getWorldGame(player.getWorld());

        if (worldGame == null)
            return;

        if (player.hasPermission(Permissions.BORDER_BYPASS.getPermission()))
            return;

        if (worldGame.getBorder().canLeave())
            return;

        boolean isWall = gamePlayer.getBorderHandler().getFakeBlockManager().getBlocks().stream().anyMatch(iFakeBlock ->
                iFakeBlock.getLocation().equals(new Location(player.getWorld(), location.getX(), location.getY(), location.getZ())));

        if (isWall)
            event.setCancelled(true);

    }

}















