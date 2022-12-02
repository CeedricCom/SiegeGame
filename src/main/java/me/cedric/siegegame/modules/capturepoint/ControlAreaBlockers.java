package me.cedric.siegegame.modules.capturepoint;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.MovingObjectPositionBlock;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.map.GameMap;
import org.bukkit.Location;
import org.bukkit.Material;

public class ControlAreaBlockers {

    public void stopBlockChanges(SiegeGamePlugin plugin, ControlAreaHandler handler, GameMap gameMap) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.BLOCK_CHANGE) {
            @Override
            public void onPacketSending(PacketEvent event) {

                PacketContainer packet = event.getPacket();

                Cuboid cuboid = handler.getCuboid();

                BlockPosition blockPosition = packet.getBlockPositionModifier().read(0);
                Location location = blockPosition.toLocation(gameMap.getWorld());

                if (cuboid.colliding3d(Vector3D.getFromBlockLocation(location)))
                    event.setCancelled(true);
            }
        });
    }

    public void stopInteractions(SiegeGamePlugin plugin, ControlAreaHandler handler, GameMap gameMap) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.USE_ITEM) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                Cuboid cuboid = handler.getCuboid();

                MovingObjectPositionBlock blockPosition = packet.getMovingBlockPositions().read(0);
                Location location = blockPosition.getBlockPosition().toLocation(gameMap.getWorld());

                if (location.getBlock().getType().equals(Material.AIR))
                    return;

                if (cuboid.colliding3d(Vector3D.getFromBlockLocation(location)))
                    event.setCancelled(true);
            }
        });
    }

}
