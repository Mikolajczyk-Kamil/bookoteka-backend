package com.mikolajczyk.redude.backend.sources.googleBooks.engine;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.sources.googleBooks.mapper.GoogleBookMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExtendedSearchEngine {

    private final GoogleBookMapper mapper;
    private final String source = "https://www.googleapis.com/books/v1/volumes";
    private final String addons = "&langRestrict=en&maxResults=40&printType=books";

    public List<Book> doRequest(String value) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        value = value.replaceAll(" ", "%20");
        value = "?q=" + value + addons;
        String url = source + value;
        HttpResponse<JsonNode> response = Unirest.get(url)
                .asJson();
        return parseResponse(response);
    }

    private List<Book> parseResponse(HttpResponse<JsonNode> response) {
        if (response.getBody().getObject().has("items")) {
            JSONArray json = response.getBody().getObject().getJSONArray("items");
            return mapper.mapToListBook(json);
        }
        return List.of();
    }
}
