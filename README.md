# Weather-data-api
A spring-boot application for servicing weather data 

# Development
## APIs
#### Get Current Weather data
GET Request

Path("api/weather/{latitude}/coordinates/{longitude}/current")

Descritpion: To get the latest weather data by gps cooridaintes: latitude and longitude


JSON Response
```
{
  "statusType": "OK",
  "entity": {
    "content": {
      "weatherDescription": "broken clouds",
      "latitude": 37.7749,
      "longitude": -122.4194,
      "country": "US",
      "humidity": 87,
      "temp": 286,
      "tempMin": 285,
      "tempMax": 288,
      "sunRise": 1554385828,
      "sunSet": 1554431671,
      "timeStamp": "2019-04-04 11:29:36"
    }
  },
  "entityType": "com.lc.spring.model.response.WeatherDataResponse",
  "status": 200,
  "metadata": {
    
  }
}
```



#### Get average temerapure from multiple coordinates 
POST Request

Path("api/average-temp/coordinates")

Descritpion: Get current averege temerapure from multiple coordinates

Payload

```
{
   "coordinateList" : [[40.71,-74.00],[34.05, -118.24],[37.77,-122.41], [51.5074, 0.1278], [48.8566, 2.3522],[52.5200, 13.4050],[36.1699,-115.1398], [47.6062,-122.3321], [32.7767, -96.7970], [39.7392, -104.9903],[30.2672, -97,7431], [42.3601, -71.0589], [61.2181, -149.9003], [42.3314, -83.0458], [39.9526, -75.1652], [33.7490, -84.3880],[43.6532, -79.3832], [49.2827, -123.1207], [33.4484, -112.0740], [38.9072, 77.0369], [30.0444, 31.2357]]
}
```

JSON Response

```
{
    "statusType": "OK",
    "entity": {
        "content": 45.85000000000002
    },
    "entityType": "com.lc.spring.model.response.WeatherDataResponse",
    "status": 200,
    "metadata": {}
}
```


#### Get high temperature across the globe
GET Request

Path("api/weather/time/{timeStamp}")

Descritpion: Get a list of high temperatures across the globe at a particular time

JSON Response

```
{
  "statusType": "OK",
  "entity": {
    "content": [
      288,
      289,
      290,
      300,
      287,
      287,
      286,
      295,
      290,
      298,
      287,
      290,
      283,
      289,
      293,
      301
    ]
  },
  "entityType": "com.lc.spring.model.response.WeatherDataResponse",
  "status": 200,
  "metadata": {
    
  }
}
```

### Error responses
Typically, an error response will have a code and a msg in the body. But the http code could be 200 or some of the 400. 
See `Error codes list` below. 
```
{
     "code": "ACCESS_DENIED",
     "msg": "Access denied"
}
```

Error codes list

| http code | Error code | Message | Comments |
| ----------|------------|---------| --------- |
| 400| BAD_REQUEST | BAD_REQUEST| 400 will be returned for validation or business error|
| 401| ACCESS_DENIED | ACCESS_DENIED| 401 will be returned for unauthorized access|
| 500| INTERAL_SEVER_ERROR | INTERAL_SEVER_ERROR| 500 will be returned for Unexpected failure|
| 503| SERVICE_UNAVAILABLE | SERVICE_UNAVAILABLE| 503 will be returned for Service unavailable|


## External APIs

This project uses 3rd party (OpenWeatherMap.com) Api to get live weather data. We are limited to 60 calls per mintutes for free access.

## Uber H3 Geospatial Indexing

This project uses Uber H3 geospatial indexing to store latitude and longitude in the data model and database

Please refer to https://uber.github.io/h3/#/ for usage and documentation

## Lombok
The project uses [Lombok](https://projectlombok.org/) for code generation.
Please be sure to install lombok intellij plugin first.

Here are the steps to install plugin in eclipse 


Exit Eclipse(if it is open) and downloaded jar from https://projectlombok.org/download

execute command: java -jar lombok.jar

This command will open window as shown here https://projectlombok.org/setup/eclipse, install and quit the installer.

Add jar to build path/add it to pom.xml.

restart eclipse.

Go to Eclipse --> About Eclipse --> check 'Lombok v1.16.18 "Dancing Elephant" is installed. https://projectlombok.org/'

# Running Locally
Run `mvn spring-boot:run`

# TODO
* Increase Unit test converage
* Implement Integration Test
* Swagger Support
