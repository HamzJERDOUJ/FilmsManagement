package com.films.management.controllers;

import com.films.management.Application;
import com.films.management.models.Admin;
import com.films.management.models.Film;
import com.films.management.settings.Settings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.fusionauth.jwt.InvalidJWTException;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACVerifier;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.query.ObjectSelect;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class FilmController {

    public static String getFilms(Request req, Response res) {
        res.type("application/json");
        Gson gson = new Gson();
        HashMap<String, String> data = new HashMap<>();
        String token = req.headers("Authorization");
        if (token == null) {
            data.put("code", "2");
            data.put("message", "token is required");
            return gson.toJson(data);
        }
        Verifier verifier = HMACVerifier.newVerifier(Settings.secret);
        try {
            JWT.getDecoder().decode(token, verifier);
        } catch (InvalidJWTException e) {
            data.put("code", "3");
            data.put("message", "token is invalid");
            return gson.toJson(data);
        }
        List<Film> films = ObjectSelect.query(Film.class).select(Application.context);
        return gson.toJson(films);

    }

    public static String getFilm(Request req, Response res) {
        res.type("application/json");
        Gson gson = new Gson();
        HashMap<String, String> data = new HashMap<>();
        String token = req.headers("Authorization");
        String authenticatedUserEmail = getAuthenticatedUserEmail(token);
        if (authenticatedUserEmail.equals("")) {
            data.put("code", "2");
            data.put("message", "token is required");
            return gson.toJson(data);
        } else if (authenticatedUserEmail == null) {
            data.put("code", "3");
            data.put("message", "token is invalid");
            return gson.toJson(data);
        } else {
            String id = req.params(":id");
            Film film = Cayenne.objectForPK(Application.context, Film.class, Integer.parseInt(id));
            if (film != null) {
                return gson.toJson(film);
            } else {
                data.put("code", "5");
                data.put("message", "no such element found");
                return gson.toJson(data);
            }
        }
    }

    public static String addFilm(Request req, Response res) {
        res.type("application/json");
        Gson gson = new Gson();
        HashMap<String, String> data = new HashMap<>();
        String token = req.headers("Authorization");
        String authenticatedUserEmail = getAuthenticatedUserEmail(token);
        if (authenticatedUserEmail.equals("")) {
            data.put("code", "2");
            data.put("message", "token is required");
            return gson.toJson(data);
        } else if (authenticatedUserEmail == null) {
            data.put("code", "3");
            data.put("message", "token is invalid");
            return gson.toJson(data);
        } else {
            Admin admin = ObjectSelect.query(Admin.class).where(Admin.EMAIL.eq(authenticatedUserEmail)).selectOne(Application.context);
            if (admin != null) {
                HashMap<String, String> request;
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                request = gson.fromJson(req.body(), type);
                String photoName = generateRandomName();
                String videoName = generateRandomName();
                String photo = request.get("photo");
                String link = request.get("link");
                try {
                    byte[] decodedBytes = Base64.getDecoder().decode(photo.split("base64,")[1]);
                    File file = new File(new File("").getAbsolutePath() + "/files/photos/" + photoName + ".jpg");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(decodedBytes);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    decodedBytes = Base64.getDecoder().decode(link.split("base64,")[1]);
                    file = new File(new File("").getAbsolutePath() + "/files/videos/" + videoName + ".mp4");
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(decodedBytes);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String name = request.get("name");
                String description = request.get("description");
                String duration = request.get("duration");
                String quality = request.get("quality");
                String age = request.get("age");
                String year = request.get("year");
                String rating = request.get("rating");
                String filmType = request.get("type");
                Film film = Application.context.newObject(Film.class);
                film.setName(name);
                film.setDescription(description);
                film.setDuration(Float.parseFloat(duration));
                film.setAge(Integer.parseInt(age));
                film.setLink(videoName);
                film.setPhoto(photoName);
                film.setYear(Integer.parseInt(year));
                film.setRating(Float.parseFloat(rating));
                film.setType(filmType);
                film.setQuality(quality);
                Application.context.commitChanges();
                data.put("code", "1");
                data.put("message", "the operation completed successfully");
                return gson.toJson(data);
            } else {
                data.put("code", "4");
                data.put("message", "not allowed");
                return gson.toJson(data);
            }
        }
    }

    public static String deleteFilm(Request req, Response res) {
        res.type("application/json");
        Gson gson = new Gson();
        HashMap<String, String> data = new HashMap<>();
        String id = req.params(":id");
        String token = req.headers("Authorization");
        String authenticatedUserEmail = getAuthenticatedUserEmail(token);
        if (authenticatedUserEmail.equals("")) {
            data.put("code", "2");
            data.put("message", "token is required");
            return gson.toJson(data);
        } else if (authenticatedUserEmail == null) {
            data.put("code", "3");
            data.put("message", "token is invalid");
            return gson.toJson(data);
        } else {
            Admin admin = ObjectSelect.query(Admin.class).where(Admin.EMAIL.eq(authenticatedUserEmail)).selectOne(Application.context);
            if (admin != null) {
                Film film = Cayenne.objectForPK(Application.context, Film.class, Integer.parseInt(id));
                if (film != null) {
                    Application.context.deleteObject(film);
                    Application.context.commitChanges();
                    data.put("code", "1");
                    data.put("message", "the operation completed successfully");
                    return gson.toJson(data);
                } else {
                    data.put("code", "5");
                    data.put("message", "no such element found");
                    return gson.toJson(data);
                }
            } else {
                data.put("code", "4");
                data.put("message", "not allowed");
                return gson.toJson(data);
            }
        }
    }

    private static String getAuthenticatedUserEmail(String token) {
        if (token == null) return "";
        Verifier verifier = HMACVerifier.newVerifier(Settings.secret);
        String email = null;
        try {
            email = JWT.getDecoder().decode(token, verifier).subject;
        } catch (InvalidJWTException e) {
            return email;
        }
        return email;
    }

    private static String generateRandomName() {
        int len = 10;
        String text = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            stringBuilder.append(text.charAt(random.nextInt(text.length())));
        return stringBuilder.toString();
    }
}
