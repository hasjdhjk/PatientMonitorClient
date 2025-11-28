package NetWork;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Collections;
import java.util.List;

public class ApiClient {

    // CHANGE WHEN DEPLOYED TO TSURU
    private static final String BASE_URL = "http://localhost:8080/PatientServer";

    private static final Gson gson = new Gson();

    // ------------------------------------------------------------
    // Helper: GET requests (returns raw string)
    // ------------------------------------------------------------
    private static String getJson(String endpoint) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    // ------------------------------------------------------------
    // Helper: POST requests (returns raw string)
    // ------------------------------------------------------------
    private static String postJson(String endpoint, String jsonBody) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes());
        }

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }


    // ======================================================================
    // AUTHENTICATION METHODS
    // ======================================================================

    // ---------------------------
    // 1. Login
    // ---------------------------
    public static LoginResponse login(String email, String password) {
        try {
            String body = gson.toJson(new Object() {
                String e = email;
                String p = password;
            });

            String json = postJson("/login", body);
            return gson.fromJson(json, LoginResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ---------------------------
    // 2. Register
    // ---------------------------
    public static SimpleResponse register(String email, String password,
                                          String givenName, String familyName) {

        try {
            String body = gson.toJson(new Object() {
                String em = email;
                String pw = password;
                String gn = givenName;
                String fn = familyName;
            });

            String json = postJson("/register", body);
            return gson.fromJson(json, SimpleResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ---------------------------
    // 3. Request Password Reset
    // ---------------------------
    public static SimpleResponse requestPasswordReset(String email) {
        try {
            String body = gson.toJson(new Object() {
                String em = email;
            });

            String json = postJson("/requestPasswordReset", body);
            return gson.fromJson(json, SimpleResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ---------------------------
    // 4. Reset Password
    // ---------------------------
    public static SimpleResponse resetPassword(String token, String newPassword) {
        try {
            String body = gson.toJson(new Object() {
                String t = token;
                String np = newPassword;
            });

            String json = postJson("/resetPassword", body);
            return gson.fromJson(json, SimpleResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // ======================================================================
    // RESPONSE CLASSES (client-side DTOs)
    // ======================================================================

    // Represent JSON returned by LoginServlet
    public static class LoginResponse {
        public String status;
        public String message;

        public Integer doctorId;
        public String givenName;
        public String familyName;
    }

    // Generic response for register, resetPassword, requestReset
    public static class SimpleResponse {
        public String status;
        public String message;
    }
}
