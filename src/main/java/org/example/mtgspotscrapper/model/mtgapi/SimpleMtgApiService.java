package org.example.mtgspotscrapper.model.mtgapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.model.records.CardData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMtgApiService implements MtgApiService {
    private static final Logger log = LoggerFactory.getLogger(SimpleMtgApiService.class);

    @Override
    public CardData getCardData(String cardName) {
        return getCardDataBrute(cardName);
//        return getCardDataFilter(cardName);
    }

    @Override
    public CardData getCardData(Integer multiverseId) {
        Card answer = CardAPI.getCard(multiverseId);
        if (answer == null) {
            return null;
        }

        return new CardData(
            answer.getMultiverseid(),
            answer.getName(),
            answer.getImageUrl()
        );
    }

    @SuppressWarnings("unused")
    public final CardData getCardDataFilter(String cardName) {
        List<String> filter = new ArrayList<>();
        filter.add("name="+cardName);

        Card answer = CardAPI.getAllCards(filter).stream().filter(card -> card.getName().equals(cardName)).findFirst().orElse(null);
        if (answer == null || answer.getMultiverseid() < 0) {
            return null;
        }

//        return new CardData(
//            answer.getMultiverseid(),
//            answer.getName(),
//            answer.getImageUrl()
//        );

        return new CardData(
            answer.getMultiverseid(),
            Objects.requireNonNull(answer.getName()),
            Objects.requireNonNull(answer.getImageUrl())
        );
    }

    public final CardData getCardDataBrute(String cardName) {
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
        String cardName = "Assassin's Trophy";

        Consumer<String> test = what -> {
            Pattern nameV1 = Pattern.compile("^" + cardName+ "(\\s\\(.*\\))?$");
            Matcher matcher = nameV1.matcher(what);

            log.info("Equals: {}, matches: {}", what.equals(cardName), matcher.matches());
        };

        test.accept("Assassin's Trophy (V.2)");
        test.accept("Assassin's Trophy (v.1)");
        test.accept("Assassin's Trophy (V.4)");
        test.accept("Assassin's Trophy Token");
        test.accept("Assassin's Trophy");

//        MtgApiService mtgApiService = new SimpleMtgApiService();
//
//        System.out.println(mtgApiService.getCardData("Wastes"));
//        System.out.println(mtgApiService.getCardData("Llanowar Elves"));
//        System.out.println(mtgApiService.getCardData("Black Lotus"));
//        System.out.println(mtgApiService.getCardData("Beast Within"));
    }
}
