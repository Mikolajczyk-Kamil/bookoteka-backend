package com.mikolajczyk.redude.backend.sources.googleBooks.mapper;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.dto.BookDto;
import com.mikolajczyk.redude.backend.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleBookMapper {

    private final BookMapper bookMapper;

    public List<Book> mapToListBook(JSONArray json) {
        System.out.println(json.length());
        List<BookDto> bookDtoList = new ArrayList<>();
        for (int i=0; i < json.length(); i++)
            bookDtoList.add(mapToBook(json.getJSONObject(i)));
        return bookMapper.mapToListBook(bookDtoList);
    }

    private BookDto mapToBook(JSONObject jsonBook) {
        if (jsonBook.getJSONObject("volumeInfo").has("title")) {
            String googleId = jsonBook.get("id").toString();
            String title = jsonBook.getJSONObject("volumeInfo").get("title").toString();
            String authors = "";
            String description = "";
            String publisher = "";
            String published = "";
            String categories = "";
            Map<String, String> identifier = new HashMap<>();
            String imageLink = "";
            boolean isEbook;
            String buyLink = "";
            String retailPrice = "";
            if (jsonBook.getJSONObject("volumeInfo").has("authors"))
                authors = jsonBook.getJSONObject("volumeInfo").getJSONArray("authors").toString().replace("\"", "");
            if (jsonBook.getJSONObject("volumeInfo").has("description"))
                description = jsonBook.getJSONObject("volumeInfo").get("description").toString();
            if (jsonBook.getJSONObject("volumeInfo").has("publisher"))
                publisher = jsonBook.getJSONObject("volumeInfo").get("publisher").toString();
            if (jsonBook.getJSONObject("volumeInfo").has("publishedDate"))
                published = jsonBook.getJSONObject("volumeInfo").get("publishedDate").toString();
            if (jsonBook.getJSONObject("volumeInfo").has("categories"))
                categories = jsonBook.getJSONObject("volumeInfo").getJSONArray("categories").toString().replace("\"", "");
            if (jsonBook.getJSONObject("volumeInfo").has("industryIdentifiers")) {
                int length = jsonBook.getJSONObject("volumeInfo").getJSONArray("industryIdentifiers").length();
                for (int i = 0; i < length; i++) {
                    String type = jsonBook.getJSONObject("volumeInfo").getJSONArray("industryIdentifiers").getJSONObject(i).get("type").toString();
                    String value = jsonBook.getJSONObject("volumeInfo").getJSONArray("industryIdentifiers").getJSONObject(i).get("identifier").toString();
                    if (type.contains("ISBN"))
                        identifier.put("ISBN", value);
                    else if (type.equals("OTHER"))
                        identifier.put("OTHER", value);
                }
            }
            if (jsonBook.getJSONObject("volumeInfo").has("imageLinks") && jsonBook.getJSONObject("volumeInfo").getJSONObject("imageLinks").has("thumbnail"))
                imageLink = jsonBook.getJSONObject("volumeInfo").getJSONObject("imageLinks").get("thumbnail").toString();
            String tmp = jsonBook.getJSONObject("saleInfo").get("isEbook").toString();
            isEbook = Boolean.getBoolean(tmp);
            if (isEbook)
                buyLink = jsonBook.getJSONObject("saleInfo").get("buyLink").toString();
            if (jsonBook.getJSONObject("saleInfo").get("saleability").equals("FOR_SALE"))
                retailPrice = jsonBook.getJSONObject("saleInfo").getJSONObject("retailPrice").get("amount").toString();
            return new BookDto(
                    googleId,
                    identifier.get("ISBN"),
                    identifier.get("OTHER"),
                    title,
                    authors,
                    description,
                    publisher,
                    published,
                    categories,
                    imageLink,
                    retailPrice,
                    buyLink);
        }
        return new BookDto();
    }
}
