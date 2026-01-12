package Services;

import Models.Patient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.*;
import java.util.List;

public class PatientServerClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static List<Patient> fetchPatients(String doctorUsername) throws Exception {
        String url = "https://bioeng-fguys-app.impaas.uk/api/patients?doctor=" + doctorUsername;

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new RuntimeException("fetchPatients failed: HTTP " + resp.statusCode() + " body=" + resp.body());
        }

        Type listType = new TypeToken<List<Patient>>(){}.getType();
        return gson.fromJson(resp.body(), listType);
    }
}