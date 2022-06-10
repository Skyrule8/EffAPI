package Effapi;

import Effapi.Manga.Manga;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;
import javax.ws.rs.*;
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

	final GsonBuilder builder = new GsonBuilder();
	final Gson gson = builder.create();

	@GET
	public Response message() {
		return Response.ok(gson.toJson(mangaDao.GetManga())).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createCustomer(String manga) {
		return Response.ok(mangaDao.addManga(getMangaEntity(manga))).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateCustomer(String manga) {
		return Response.ok(mangaDao.UpdateManga(getMangaEntity(manga))).build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteManga(String manga){
		return Response.ok(mangaDao.DeleteManga(getMangaEntity(manga))).build();
	}

	@GET
	@Path("log")
	public Response addUser() {
		mangaDao.getCredentials(userAgent);
		return Response.status(200)
				.entity("addUser is called, userAgent : " )
				.build();
	}

	public Manga getMangaEntity(String json){
		Gson gson = new Gson();
		return gson.fromJson(json, Manga.class);
	}

}
