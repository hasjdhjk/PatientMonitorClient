package NetWork;

import com.google.gson.Gson;
import NetWork.ServerConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

// base URL is provided by ServerConfig
    private static final Gson gson = new Gson();

    private static String postJson(String endpoint, String jsonBody) throws IOException {
        URL url = new URL(ServerConfig.url(endpoint));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes());
        }

        int code = conn.getResponseCode();

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream()
        ));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        conn.disconnect();
        return sb.toString();
    }


    // requests
    public static class LoginRequest {
        String email;
        String password;

        LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class RegisterRequest {
        String email;
        String password;
        String givenName;
        String familyName;

        RegisterRequest(String email, String password, String givenName, String familyName) {
            this.email = email;
            this.password = password;
            this.givenName = givenName;
            this.familyName = familyName;
        }
    }

    public static class ResetRequest {
        String email;

        ResetRequest(String email) {
            this.email = email;
        }
    }

    public static class ResetPasswordRequest {
        String token;
        String newPassword;

        ResetPasswordRequest(String token, String newPassword) {
            this.token = token;
            this.newPassword = newPassword;
        }
    }

    public static class DeleteAccountRequest {
        String email;
        String password;
        String confirm;

        DeleteAccountRequest(String email, String password, String confirm) {
            this.email = email;
            this.password = password;
            this.confirm = confirm;
        }
    }


    // authetication

    // login
    public static LoginResponse login(String email, String password) {
        try {
            LoginRequest req = new LoginRequest(email, password);
            String body = gson.toJson(req);

            String json = postJson("/login", body);
            return gson.fromJson(json, LoginResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null; // will be handled by UI
        }
    }

    public static SimpleResponse register(String email, String password,
                                          String givenName, String familyName) {
        try {
            RegisterRequest req = new RegisterRequest(email, password, givenName, familyName);
            String body = gson.toJson(req);

            String json = postJson("/register", body);
            return gson.fromJson(json, SimpleResponse.class);

        } catch (Exception e) {
            System.out.println("REGISTER FAILED:");
            e.printStackTrace();
            return null;
        }
    }

    // request password reset
    public static SimpleResponse requestPasswordReset(String email) {
        try {
            ResetRequest req = new ResetRequest(email);
            String body = gson.toJson(req);

            String json = postJson("/requestPasswordReset", body);
            return gson.fromJson(json, SimpleResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

     // reset password
    public static SimpleResponse resetPassword(String token, String newPassword) {
        try {
            ResetPasswordRequest req = new ResetPasswordRequest(token, newPassword);
            String body = gson.toJson(req);

            String json = postJson("/resetPassword", body);
            return gson.fromJson(json, SimpleResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // permanently delete doctor account
    public static SimpleResponse deleteAccount(String email, String password) {
        try {
            DeleteAccountRequest req = new DeleteAccountRequest(email, password, "DELETE");
            String body = gson.toJson(req);

            String json = postJson("/deleteAccount", body);
            return gson.fromJson(json, SimpleResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // response
    public static class LoginResponse {
        public String status;
        public String message;

        public Integer doctorId;
        public String givenName;
        public String familyName;
    }

    public static class SimpleResponse {
        public String status;
        public String message;
    }
}
