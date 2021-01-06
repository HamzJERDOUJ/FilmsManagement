package com.films.management.routes;

import com.films.management.controllers.AdminController;
import com.films.management.controllers.ClientController;
import com.films.management.controllers.FilmController;
import com.google.gson.Gson;

import java.util.HashMap;

import static spark.Spark.*;

public class Routes {
    public static void map() {
        post("/client/login", ClientController::login);
        post("/client/register", ClientController::register);
        get("/client/verify/:token", ClientController::verify);

        post("/admin/login", AdminController::login);

        get("/films/:id", FilmController::getFilm);
        get("/films", FilmController::getFilms);
        post("/films", FilmController::addFilm);
        delete("/films/:id", FilmController::deleteFilm);

        post("/contact", ClientController::contact);

        notFound((req, res) -> {
            res.type("application/json");
            res.status(404);
            Gson gson = new Gson();
            HashMap<String, String> data = new HashMap<>();
            data.put("message", "not found");
            data.put("code", "404");
            return gson.toJson(data);
        });

        internalServerError((req, res) -> {
            res.type("application/json");
            res.status(500);
            Gson gson = new Gson();
            HashMap<String, String> data = new HashMap<>();
            data.put("message", "internal server error");
            data.put("code", "500");
            return gson.toJson(data);
        });

        get("/", (req, res) -> {
            res.type("application/json");
            Gson gson = new Gson();
            HashMap<String, String> data = new HashMap<>();
            data.put("message", "working");
            data.put("code", "200");
            return gson.toJson(data);
        });

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });
        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    }
}
