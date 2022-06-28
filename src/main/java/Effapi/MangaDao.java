package Effapi;

import Effapi.Manga.Manga;
import Effapi.Manga.Token;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
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

            if(manga.Nom != null)
                mangaEntity.setNom(manga.Nom);
            if(manga.Description != null)
                mangaEntity.setDescription(manga.Description);
            if(manga.Rating != 0)
                mangaEntity.setRating(manga.Rating);
            if(manga.Tomes != 0)
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
            System.out.println(mangaEntity);

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

    public String getCredentials(Token userAgent) {
        // final String[] values = getAuth(userAgent);
        var login = userAgent.getLogin();
        var pwd = userAgent.getPassword();
        System.out.println(login);
        System.out.println(pwd);

        List<Token> tokens = entityManager.createQuery("select log from Token log", Token.class).getResultList();

        for(Token log : tokens)
        {
            if(Objects.equals(log.getLogin(), login) && Objects.equals(log.getPassword(), pwd)) {
                if(log.getToken() == null)
                    return createToken(log.getId());
                else
                    return log.getToken();
            }
        }
        return null;
    }

    public boolean deleteToken(String userAgent, String token) {
        Long id = null;
        List<Token> tokens = entityManager.createQuery("select log from Token log", Token.class).getResultList();
        final String[] values = getAuth(userAgent);

        for(Token log : tokens)
        {
            if(Objects.equals(log.getLogin(), values[0]) && Objects.equals(log.getPassword(), values[1])) {
                if(log.getToken() != null)
                    id = log.getId();
            }
        }

        try {
            userTransaction.begin();
            Token tokenEntity = entityManager.find(Token.class, id);
            if(!Objects.equals(tokenEntity.getToken(), token))
                return false;

            tokenEntity.setToken(null);
            userTransaction.commit();

            return true;
        }catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "JPA Error" + e.getMessage());
            return false;
        }
    }

    public boolean checkToken(String userAgent) {
        List<Token> tokens = entityManager.createQuery("select log from Token log", Token.class).getResultList();
        for(Token tke : tokens)
            return Objects.equals(tke.getToken(), userAgent);
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
    private String[] getAuth(String userAgent) {
        String base64Credentials = userAgent.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);

        // credentials = username:password
        return credentials.split(":", 2);


        /*String base64Credentials = userAgent.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);

        // credentials = username:password
        return credentials.split(":", 2);*/
    }
}