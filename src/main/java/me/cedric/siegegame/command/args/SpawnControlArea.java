package me.cedric.siegegame.command.args;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.Module;
import me.cedric.siegegame.modules.capturepoint.ControlAreaModule;
import me.cedric.siegegame.modules.capturepoint.Cuboid;
import me.cedric.siegegame.modules.capturepoint.EffectControlArea;
import me.cedric.siegegame.modules.capturepoint.Vector3D;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpawnControlArea extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public SpawnControlArea(SiegeGamePlugin plugin) {
        super("siegegame.admin.spawncontrolarea");
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();
        Player player = Bukkit.getPlayer(sentCommand.getSender().getUniqueId());

        Vector3D p1 = Vector3D.getFromPlayerLocation(player.getLocation().clone());
        Vector3D p2 = Vector3D.getFromPlayerLocation(player.getLocation().clone());

        p1.add(10, 8, 10);
        p2.add(-10, -1, -10);

        Cuboid cuboid = new Cuboid(p1, p2);
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.SATURATION, 9999 * 20, 0);

        EffectControlArea effectControlArea = new EffectControlArea(plugin, cuboid, 10, player.getLocation().getBlockY(),
                match.getGameMap(), "bomdia", "boanoite", potionEffect);

        for (Module module : match.getWorldGame().getModules()) {
            if (!(module instanceof ControlAreaModule))
                continue;

            ControlAreaModule controlAreaModule = (ControlAreaModule) module;
            controlAreaModule.addControlArea(effectControlArea);
            break;
        }

        effectControlArea.generate();
    }
}
