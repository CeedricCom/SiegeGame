package me.cedric.siegegame.model.player.kits.db;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.player.kits.KitController;
import me.cedric.siegegame.model.player.kits.KitService;
import me.cedric.siegegame.model.player.kits.db.entitiy.ItemEntity;
import me.cedric.siegegame.model.player.kits.db.entitiy.KitEntity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class PersistenceManager {

    private final SiegeGamePlugin plugin;
    private SessionFactory sessionFactory;

    private KitRepository kitRepository;
    private KitController kitController;

    private KitService kitService;

    public PersistenceManager(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    public void initialise() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(KitEntity.class);
        configuration.addAnnotatedClass(ItemEntity.class);

        sessionFactory = configuration.buildSessionFactory();

        this.kitRepository = new KitRepository(sessionFactory, plugin);
        this.kitService = new KitService(kitRepository);
        this.kitController = new KitController(plugin, kitService);
    }

    public KitController getKitController() {
        return kitController;
    }
}
