package com.films.management.controllers;

import com.films.management.Application;
import com.films.management.models.Client;
import com.films.management.models.Film;
import com.films.management.settings.Settings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.fusionauth.jwt.InvalidJWTException;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import io.fusionauth.jwt.hmac.HMACVerifier;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class ClientController {

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
        Client client = ObjectSelect.query(Client.class).where(Client.EMAIL.eq(email)).selectOne(Application.context);
        if (client != null) {
            String clientPassword = client.getPassword();
            if (clientPassword.equals(password)) {
                if (client.isActive()) {
                    Signer signer = HMACSigner.newSHA512Signer(Settings.secret);
                    JWT jwt = new JWT().setSubject(client.getEmail()).setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusMonths(2));
                    String token = JWT.getEncoder().encode(jwt, signer);
                    data.put("code", "1");
                    data.put("message", token);
                } else {
                    data.put("code", "2");
                    data.put("message", "email is not verified yet");
                }
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

    public static String register(Request req, Response res) {
        res.type("application/json");
        Gson gson = new Gson();
        HashMap<String, String> request;
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        request = gson.fromJson(req.body(), type);
        HashMap<String, String> data = new HashMap<>();
        String firstName = request.get("firstName");
        String lastName = request.get("lastName");
        String email = request.get("email");
        String password = request.get("password");
        if (!firstName.isEmpty()) {
            Client client = Application.context.newObject(Client.class);
            client.setFirstName(firstName);
            client.setLastName(lastName);
            client.setEmail(email);
            client.setPassword(hashPassword(password));
            client.setActive(false);
            Application.context.commitChanges();
            Signer signer = HMACSigner.newSHA512Signer(Settings.secret);
            JWT jwt = new JWT().setSubject(client.getEmail()).setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusMonths(2));
            String token = JWT.getEncoder().encode(jwt, signer);
            sendMail("", client.getEmail(), "Films Management : Verify your email address", "Hi,\nPlease verify your email address by visiting this link: http://localhost:8000/client/verify/" + token + " ,\nSincerely !");
            data.put("code", "1");
            data.put("message", "the operation completed successfully");
        } else {
            data.put("code", "2");
            data.put("message", "empty fields");
        }
        return gson.toJson(data);
    }

    public static String verify(Request req, Response res) {
        res.type("application/json");
        Gson gson = new Gson();
        HashMap<String, String> data = new HashMap<>();
        String token = req.params(":token");
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
            Client client = ObjectSelect.query(Client.class).where(Client.EMAIL.eq(authenticatedUserEmail)).and(Client.ACTIVE.eq(false)).selectOne(Application.context);
            if (client != null) {
                client.setActive(true);
                Application.context.commitChanges();
                data.put("code", "1");
                data.put("message", "the operation completed successfully");
                return gson.toJson(data);
            } else {
                data.put("code", "4");
                data.put("message", "already activated");
                return gson.toJson(data);
            }
        }
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

    public static String contact(Request req, Response res) {
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
            HashMap<String, String> request;
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            request = gson.fromJson(req.body(), type);
            String name = request.get("name");
            String email = request.get("email");
            String subject = request.get("subject");
            String message = request.get("message");
            sendMail(email, Settings.email, subject, name+ " said :\n" + message);
            data.put("code", "1");
            data.put("message", "the operation completed successfully");
            return gson.toJson(data);
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

    private static void sendMail(String from, String to, String subject, String content) {
        try {
            Email email = new SimpleEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(465);
            email.setAuthenticator(new DefaultAuthenticator(Settings.email, Settings.password));
            email.setSSLOnConnect(true);
            email.setFrom(from.equals("") ? Settings.email : from);
            email.setSubject(subject);
            email.setMsg(content);
            email.addTo(to);
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}
