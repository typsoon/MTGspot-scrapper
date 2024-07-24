package org.example.mtgspotscrapper.model.mtgapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.model.records.CardData;
import org.json.JSONArray;
import org.json.JSONObject;

public class SimpleMtgApiService implements MtgApiService {
    @Override
    public CardData getCardData(String cardName) {
        HttpURLConnection connection = null;
        try {
            // Construct the URL
            String encodedCardName = URLEncoder.encode(cardName, StandardCharsets.UTF_8);
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
//            Collection<JSONObject> filtered = new ArrayList<>();

            JSONObject answer = null;
            for (var element : cardsArray) {
                answer = (JSONObject) element;
                if (answer.getString("name").equals(cardName)) {
                    break;
//                    filtered.add(answer);
                }
            }

//            filtered.forEach(element -> App.logger.debug(element.toString()));

            // Extract the first card object
            if (answer == null)
                return null;

            return new CardData(
                answer.getInt("multiverseid"),
                answer.getString("name"),
                0,
                0,
                answer.getString("imageUrl")
            );

        } catch (Exception e) {
            App.logger.debug(Arrays.toString(e.getStackTrace()));
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        MtgApiService mtgApiService = new SimpleMtgApiService();

        System.out.println(mtgApiService.getCardData("Wastes"));
        System.out.println(mtgApiService.getCardData("Llanowar Elves"));
        System.out.println(mtgApiService.getCardData("Black Lotus"));
        System.out.println(mtgApiService.getCardData("Beast Within"));
    }
}
