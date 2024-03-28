## Introduction

The weather app is a java based application which displays weather data about any city the user inputs. It fetches data from an external weather api (OpenMeteo) and displays it in a GUI. Users have to enter a location name, and the app retrieves and displays information about current weather condition, temperature, relative humidity as well as wind speed. This documentation outlines the project's architecture, technologies used, and how to run this application on your own device.

## Technologies used

The weather app uses following libraries and technologies:
- **Java 21**
- **JSON Simple** - to easily read and prase through JSON data
- **HTTPURLConnection** - to make http requests to fetch data from OpenMeteo API

## Class Summaries

**App Launcher**
- Used for launching the app. It displays the main application

**WeatherAppGUI**
- Handles the layout and GUI components, including text fields, buttons and more. It also handles the user interface for inputting location. 

**WeatherApp**
- Handles backend logic for fetching weather data from an external API. It includes method to fetch weather data and location through api requests, converting weather codes into understandable weather conditions, and manage api requests. 

## Topics covered through this project

- Java Object Oriented Programming
- Designing GUI using java libraries
- Java API integration
- Error handling

## How to run it on your own device

Download this repository and run it. All the necessary files are on the repository.
