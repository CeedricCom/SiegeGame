package me.cedric.siegegame.model.player.kits;

import me.cedric.siegegame.model.player.kits.db.KitRepository;
import me.cedric.siegegame.model.player.kits.db.entitiy.ItemEntity;
import me.cedric.siegegame.model.player.kits.db.entitiy.KitEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class KitService {

    private final KitRepository kitRepository;

    public KitService(KitRepository kitRepository) {
        this.kitRepository = kitRepository;
    }

    public void save(Kit kit, UUID playerId) {
        KitEntity kitEntity = new KitEntity();

        kitEntity.setUuid(kit.getKitUUID());
        kitEntity.setMapName(kit.getMapIdentifier());
        kitEntity.setPlayer(playerId);

        List<ItemEntity> itemEntities = kit.getContents().stream().map(itemStack -> {
            ItemEntity itemEntity = new ItemEntity();
            itemEntity.setItem(itemStack);
            return itemEntity;
        }).toList();
        kitEntity.setItems(itemEntities);

        kitRepository.save(kitEntity);
    }

    public void delete(UUID uuid) {
        kitRepository.delete(uuid);
    }

    public List<Kit> fetchPlayerKits(UUID player) {
        List<Kit> kits = new ArrayList<>();

        List<KitEntity> entities = kitRepository.findByPlayer(player);

        entities.forEach(kitEntity -> {
            kits.add(new Kit(
                            kitEntity.getMapName(),
                            kitEntity.getItems().stream().map(ItemEntity::getItem).collect(Collectors.toList()),
                            kitEntity.getUuid()
                    )
            );
        });

        return kits;
    }

}
