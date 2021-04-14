# DataAPI
A custom API made in Ktor using Kotlin.

## Index
* [Pre-requisites](https://github.com/Wikijito7/DataAPI/blob/main/README.md#pre-requisites)
* [How to run it](https://github.com/Wikijito7/DataAPI/blob/main/README.md#how-to-run-it)
* [How it works](https://github.com/Wikijito7/DataAPI/blob/main/README.md#how-it-works)
* [TODO](https://github.com/Wikijito7/DataAPI/blob/main/README.md#todo)

## Pre-requisites
* Java 8+.
* Knowledge using a terminal.
* A little bit of time.
* Optional: Coffee to drink while executing the app.

## How to run it
* First of all, let's make sure we've got java install. To do so, run `java --version`, if we see a message like `openjdk 11.0.10 2021-01-19` or Oracle's version, it's fine, we can continue. If not, install the last java version and come back.
* Execute the jar:
    * Option 1: Download the latest jar uploaded (TODO) 
    * Option 2: Clone the project and compile the code. To do so, download it from the green button at the top or execute `git clone https://github.com/Wikijito7/DataAPI.git`. Once downloaded, we can modify some parameters of the [config](https://github.com/Wikijito7/DataAPI/blob/main/resources/application.conf) file or leave it as it is. After that, open a terminal and execute `./gradlew clean shadowJar` if you're in UNIX, Linux Distro or MacOS, or `./gradlew.bat clean shadowJar` if you're on Windows.
* We're almost done! Now execute the jar, to do so open a terminal and execute `java -jar DataAPI-$version.jar`. Remember to replace `$version` with the version you're downloaded/compiled.
* We've finished! Now you can start using the API.

## How it works
This API is a REST API, it means that serves data using JSON. To use it, we have to make calls to different URLs with a Method. In order to communicate with the API we have to add to the header the `Authorization Bearer $token`, replacing `$token` with our token. Let's see the routing closely.

### Requests WITHOUT Auth

#### Get
* `/`: `HELLO WORLD!`. We can use this direction to make sure we can communicate with the API.
* `/token`: This is how we obtain our token and a hash. The hash is our client-id, we can obtain our token with the hash, and the hash with the token. **DO NOT LOSE THEM**, once lost, we cannot access to our data.
* `/token/{hash}`: As i mentioned before, we can obtain our token if we know our hash. This is the way.
### Requests WITH Auth
If we're not authenticated, it will respond with a `401 Unauthorized`.
#### Get
* `/data`: It responds with the data in a list, even if it is empty. It can respond with a `404 Not Found` if somehow the token is valid but the hash doesn't exists in the database.

#### Post
* `/data`: Upload data to the API. You can upload as many data as you want, always as a list. It respond with the data just uploaded as JSON if everything went OK,`400 Bad Request` if something went wrong. It MUST follow the next pattern:
```
[
    {
        "id": 0,
        "title": "name",
        "description": "description",
        "urlImage": "https://someImageUrl",
        "isFavorite": true/false
    },...
]
```
* `/hash`: This is the way to get our hash. 

#### Put
* `/data`: It updates the entire data object given, using the ID as item's identificator, so it cannot be changed. It MUST follow the next pattern:
```
{
    "id": 0,
    "title": "name",
    "description": "description",
    "urlImage": "https://someImageUrl",
    "isFavorite": true/false
}
```
* `/data/{id}`: It updates the `isFavorite` parameter from `false` to `true`. It cannot be changed back nor change a `true` one to `false`. It responds with the updated item or a `404 Not Found` if the item doesn't exist.

#### Delete
* `/data/{id}`: It delete the item that has the given id. Once deleted it cannot be recovered. It responds with the deleted itemId or a `404 Not Found` if the item doesn't exist.

## TODO:
* [ ] Upload JAR.
* Something else?
