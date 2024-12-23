package me.cedric.siegegame.model.player.kits.db;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.player.kits.Kit;
import me.cedric.siegegame.model.player.kits.PlayerKitManager;
import me.cedric.siegegame.model.player.kits.db.entitiy.ItemEntity;
import me.cedric.siegegame.model.player.kits.db.entitiy.KitEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class KitRepository {

    private final SessionFactory sessionFactory;

    public KitRepository(SessionFactory sessionFactory, SiegeGamePlugin plugin) {
        this.sessionFactory = sessionFactory;
    }

    public KitEntity get(UUID kitId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        KitEntity entity = session.get(KitEntity.class, kitId);
        transaction.commit();
        session.close();

        return entity;
    }

    public void save(KitEntity kit) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.merge(kit);
        transaction.commit();
        session.close();
    }

    public void delete(UUID id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        KitEntity entity = session.get(KitEntity.class, id);
        session.remove(entity);
        transaction.commit();
        session.close();
    }

    public List<KitEntity> findByPlayer(UUID uuid) {
        try (Session session = sessionFactory.openSession()) {
            String sql = "SELECT k FROM KitEntity k WHERE k.player = :uuid";
            Query<KitEntity> query = session.createQuery(sql, KitEntity.class);
            query.setParameter("uuid", uuid);

            return query.getResultList();
        }
    }
}
