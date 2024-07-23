package org.example.mtgspotscrapper.model.mtgapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class SimpleMtgApiService implements MtgApiService {
    @Override
    public ImportantCardData getImportantCardData(String cardName) {
        HttpURLConnection connection = null;
        try {
            // Construct the URL
            String encodedCardName = java.net.URLEncoder.encode(cardName, StandardCharsets.UTF_8);
            URL url = new URI("https://api.magicthegathering.io/v1/cards?name=" + encodedCardName).toURL();

            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Parse the response to JSON
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Get the "cards" array
            JSONArray cardsArray = jsonResponse.getJSONArray("cards");

            // Extract the first card object
            JSONObject card = (JSONObject) cardsArray.get(0);

            if (jsonResponse.has("cards")) {

                return new ImportantCardData(
                        card.getString("name"),
                        card.getInt("cmc"),
                        null,
                        null,
                        card.getString("type"),
                        null,
                        card.getString("imageUrl")
                        );
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        MtgApiService mtgApiService = new SimpleMtgApiService();

        System.out.println(mtgApiService.getImportantCardData("Llanowar Elves"));
        System.out.println(mtgApiService.getImportantCardData("Black Lotus"));
        System.out.println(mtgApiService.getImportantCardData("Beast Within"));
    }
}
