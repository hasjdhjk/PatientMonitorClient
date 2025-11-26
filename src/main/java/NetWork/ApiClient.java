package NetWork;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/PatientServer";

    public static List<Patient> getPatients() {

        try {
            URL url = new URL(BASE_URL + "/getPatients");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Patient>>(){}.getType();

            return gson.fromJson(response.toString(), listType);

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
