Redudo
===

#### Simple book manager.

Table of Contents
---
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Endpoints](#endpoints)

General info
---
The Redudo is made for managing your books. After sign in via Google you can search for them in the database
(if there is not the book, app use [Google Books Api](https://developers.google.com/books "Google Books Api")).\
Then you can add the book to one of three lists:
* books to read
* books already reading
* done books


Technologies
---
Project is created with:
* Java (version 11)
* Spring
* Hibernate
* RestApi

Setup
---
To run project just save repository on your disc and compile in your development environment.

Endpoints
---
###### Authorization by the "Authorization" header with token value from Login via Google
###### `METHOD path` `authorization required` `[bodyRequest] class` `returType onSuccess / onFailed`
### Account endpoints
`POST /v1/account` `authorization required` `long userId/0`\
Sign in or sing up new user

`DELETE /v1/account` `authorization required` `long userId/0`\
Delete account

`GET /v1/account/toRead` `authorization required` `List<BookDto> booksDto/emptyList`\
Get user books from 'to read' list

`PUT /v1/account/toRead` `authorization required` `BookDto` `long bookId/0`\
Add the book to user 'to read' list

`DELETE /v1/account/toRead/{googleId}` `authorization required`\
Remove the book from user 'to read' list by its google-id

`GET /v1/account/during` `authorization required` `List<BookDto> booksDto/emptyList`\
Get user books from 'already reading' list

`PUT /v1/account/during` `authorization required` `BookDto`\
Add the book to user 'already reading' list

`DELETE /v1/account/during/{googleId}` `authorization required` `long bookId/0`\
Remove the book from user 'already reading' list by its google-id

`GET /v1/account/done` `authorization required` `List<BookDto> booksDto/emptyList`\
Get user books from 'done' list

`PUT /v1/account/done` `authorization required` `BookDto` `long bookId/0`\
Add the book to user 'done' list

`DELETE /v1/account/done/{googleId}` `authorization required` `long bookId/0`\
Remove the book from user 'done' list by its google-id

### Books endpoints

`GET /v1/books` `authorization required` `List<BookDto> booksDto/emptyList`\
Get list of books by path variable:
* any phrase `q`
* title `title`
* author `author`
* category `category` 
    * (books only from an internal database)
* isbn `isbn`

Param `external=true` forces searching only in Google Books database.\
Params can be mixed.


`GET /v1/books/{googleId}` `authorization required` `List<RatingDto> ratings/emptyList`\
Get the book rating by its googleId 

`POST /v1/books/{googleId}` `authorization required` `RatingDto` `long ratingId/0`\
Post the book rating by its googleId 

`DELETE /v1/books` `authorization required` `RatingDto` `long ratingId/0`\
Delete the book rating

### Currencies endpoints

`GET /v1/currency` `Map<String, Float> currenciesMap/emptyMap`\
Get current value of EUR, GBP and USD

`GET /v1/currency/{code}` `float currency/0`\
Get current value of EUR/GBP/USD