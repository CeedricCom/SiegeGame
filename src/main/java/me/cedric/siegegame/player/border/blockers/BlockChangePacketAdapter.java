package me.cedric.siegegame.player.border.blockers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.player.border.Border;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.entity.Player;

public class BlockChangePacketAdapter extends PacketAdapter {

    private final SiegeGamePlugin plugin;

    public BlockChangePacketAdapter(SiegeGamePlugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.BLOCK_CHANGE);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        BlockPosition location = packet.getBlockPositionModifier().read(0);

        Player player = event.getPlayer();

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());

        if (gamePlayer == null)
            return;

        if (player.hasPermission(Permissions.BORDER_BYPASS.getPermission()))
            return;

        for (Border border : gamePlayer.getBorderHandler().getBorders()) {
            if (border.blockChangesAllowed())
                continue;

            if (border.getBoundingBox().getMaxX() == location.getX() || border.getBoundingBox().getMinX() == location.getX() ||
                    border.getBoundingBox().getMaxZ() == location.getZ() || border.getBoundingBox().getMinZ() == location.getZ())
                event.setCancelled(true);
        }

    }

}















