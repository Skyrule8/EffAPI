package Effapi;

import Effapi.Manga.Manga;
import Effapi.Manga.Token;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Path("mangas")
public class SampleResource {

	@Inject
	private MangaDao mangaDao;

	@HeaderParam("Authorization") String userAgent;
	@Context HttpHeaders headers;

	final GsonBuilder builder = new GsonBuilder();
	final Gson gson = builder.create();

	@GET
	public Response message() {
		String getToken = headers.getRequestHeader("Token").get(0);
		if(mangaDao.checkToken(getToken))
			return Response.ok(gson.toJson(mangaDao.GetManga())).build();
		else
			return Response.status(401).entity("Bad Credentials").build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createCustomer(String manga) {
		String getToken = headers.getRequestHeader("Token").get(0);
		if(mangaDao.checkToken(getToken))
			return Response.ok(mangaDao.addManga(getMangaEntity(manga))).build();
		else
			return Response.status(401).entity("Bad Credentials").build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateCustomer(String manga) {
		String getToken = headers.getRequestHeader("Token").get(0);
		if(mangaDao.checkToken(getToken))
			return Response.ok(mangaDao.UpdateManga(getMangaEntity(manga))).build();
		else
			return Response.status(401).entity("Bad Credentials").build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteManga(String manga){
		String getToken = headers.getRequestHeader("Token").get(0);
		if(mangaDao.checkToken(getToken))
			return Response.ok(mangaDao.DeleteManga(getMangaEntity(manga))).build();
		else
			return Response.status(401).entity("Bad Credentials").build();
	}

	@POST
	@Path("getToken")
	public Response getToken() {
		var getToken = mangaDao.getCredentials(userAgent);
		if(getToken != null)
			return Response.status(200)
					.entity("Here is your token : " + getToken + " ! :)")
					.build();
		else
			return Response.status(401)
					.entity("Check your crendentials")
					.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("auth")
	public Response addUser(String user) {
		return Response.ok(mangaDao.createUser(getTokenEntity(user))).entity("Utilisateur créé 😀").build();
	}

	public Manga getMangaEntity(String json){
		Gson gson = new Gson();
		return gson.fromJson(json, Manga.class);
	}

	public Token getTokenEntity(String json){
		Gson gson = new Gson();
		return gson.fromJson(json, Token.class);
	}

}
