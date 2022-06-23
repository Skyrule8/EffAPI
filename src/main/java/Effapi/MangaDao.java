package Effapi;

import Effapi.Manga.Manga;
import Effapi.Manga.Token;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.nio.charset.Charset;
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

    private String createToken(long id){
        String token = null;

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        int length = 7;

        for(int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }
        token  = sb.toString();
        try {
            userTransaction.begin();
            Token tokenEntity = entityManager.find(Token.class, id);

            if(tokenEntity == null)
                return null;

            tokenEntity.setToken(token);

            entityManager.persist(tokenEntity);
            userTransaction.commit();
            return token;
        }catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "JPA Error" + e.getMessage());
            return null;
        }
    }

    public String getCredentials(String userAgent) {
        String token = null;
        String base64Credentials = userAgent.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);

        // credentials = username:password
        final String[] values = credentials.split(":", 2);

        var tokens = entityManager.createQuery("select log from Token log", Token.class).getResultList();

        for(var log : tokens)
        {
            if(Objects.equals(log.getLogin(), values[0]) && Objects.equals(log.getPassword(), values[1])) {
                if(log.getToken() == null)
                    return createToken(log.getId());
                else
                    return log.getToken();
            }
        }

        return null;
    }

    public boolean checkToken(String userAgent) {
        var tokens = entityManager.createQuery("select log from Token log", Token.class).getResultList();
        for(var tke : tokens) {
            return Objects.equals(tke.getToken(), userAgent);
        }
        return false;
    }

    public Object createUser(Token token) {
            try {
                if(token.getToken() != null)
                    return false;

                userTransaction.begin();
                entityManager.persist(token);
                userTransaction.commit();
                return true;
            } catch (Exception e) {
                Logger.getGlobal().log(Level.SEVERE, "JPA Error" + e.getMessage());
                return false;
            }
        }
    }