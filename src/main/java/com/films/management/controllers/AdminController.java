package com.films.management.controllers;

import com.films.management.Application;
import com.films.management.models.Admin;
import com.films.management.settings.Settings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import org.apache.cayenne.query.ObjectSelect;
import spark.Request;
import spark.Response;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class AdminController {

    public static String login(Request req, Response res) {
        res.type("application/json");
        Gson gson = new Gson();
        HashMap<String, String> request;
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        request = gson.fromJson(req.body(), type);
        HashMap<String, String> data = new HashMap<>();
        String email = request.get("email");
        String password = hashPassword(request.get("password"));
        Admin admin = ObjectSelect.query(Admin.class).where(Admin.EMAIL.eq(email)).selectOne(Application.context);
        if (admin != null) {
            String clientPassword = admin.getPassword();
            if (clientPassword.equals(password)) {
                    Signer signer = HMACSigner.newSHA512Signer(Settings.secret);
                    JWT jwt = new JWT().setSubject(admin.getEmail()).setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusMonths(2));
                    String token = JWT.getEncoder().encode(jwt, signer);
                    data.put("code", "1");
                    data.put("message", token);
            } else {
                data.put("code", "3");
                data.put("message", "wrong credentials");
            }
        } else {
            data.put("code", "4");
            data.put("message", "wrong credentials");
        }
        return gson.toJson(data);
    }

    private static String hashPassword(String password) {
        Charset utf8 = StandardCharsets.UTF_8;
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] result = messageDigest.digest(password.getBytes(utf8));
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : result) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }
}
