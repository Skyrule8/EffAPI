package Effapi;

import Effapi.Manga.Manga;
import Effapi.Manga.Token;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import javax.ws.rs.HeaderParam;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MangaDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    UserTransaction userTransaction;


    public List<Manga> GetManga() {
        return entityManager.createQuery("select mg from Manga mg", Manga.class).getResultList();
    }

    public boolean addManga(Manga manga) {
        try {
            userTransaction.begin();
            entityManager.persist(manga);
            userTransaction.commit();
            return true;
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "JPA Error" + e.getMessage());
            return false;
        }
    }

    public boolean UpdateManga(Manga manga) {
        try {
            userTransaction.begin();
            Manga mangaEntity = entityManager.find(Manga.class, manga.getId());

            if(mangaEntity == null)
                return false;

            mangaEntity.setNom(manga.Nom);
            mangaEntity.setDescription(manga.Description);
            mangaEntity.setRating(manga.Rating);
            mangaEntity.setTomes(manga.Tomes);

            entityManager.persist(mangaEntity);
            userTransaction.commit();
            return true;
        }catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "JPA Error" + e.getMessage());
            return false;
        }
    }

    public boolean DeleteManga(Manga manga) {
        try {
            userTransaction.begin();
            Manga mangaEntity = entityManager.find(Manga.class, manga.getId());

            if(mangaEntity == null)
                return false;

            entityManager.remove(mangaEntity);
            userTransaction.commit();
            return true;
        }catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "JPA Error" + e.getMessage());
            return false;
        }
    }

    public boolean getCredentials(String userAgent) {
        String base64Credentials = userAgent.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);

        // credentials = username:password
        final String[] values = credentials.split(":", 2);

        var test = entityManager.createQuery("select log from Token log", Token.class).getResultList();

        String login = values[0];
        String password = values[1];
        if(!Objects.equals(login, test.get(0).Login) || !Objects.equals(test.get(0).Token, password)) {
            String uuid = UUID.randomUUID().toString();
            System.out.println("uuid = " + uuid);
            return false;
        }

        return true;
    }
}
