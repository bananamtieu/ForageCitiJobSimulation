package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class App extends Application {

    // A queue to store stock prices and their corresponding timestamps
    private static Queue<String> stockQueue = new LinkedList<>();
    private static XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private static long startTime;
    private static final String API_KEY = "T6QE0HMN6NBIL41U"; // Replace with your Alpha Vantage API key

    @Override
    public void start(Stage stage) {
        // Set the start time for the x-axis
        startTime = System.currentTimeMillis();

        // Set up the x and y axes
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (seconds)");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Stock Price");

        // Create the line chart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("IBM Stock Price");

        // Add the series to the chart
        series.setName("IBM Stock Price");
        lineChart.getData().add(series);

        // Set up the stage and scene
        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Real-Time Stock Price Graph");
        stage.show();

        // Start the timer to update the graph every 5 seconds
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateGraph());
            }
        }, 0, TimeUnit.SECONDS.toMillis(5));
    }

    private void updateGraph() {
        try {
            // Query the IBM stock price from Alpha Vantage API
            String symbol = "IBM"; // IBM stock symbol
            String urlString = String.format("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", symbol, API_KEY);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            // Parse the JSON response to extract the stock price
            String jsonResponse = content.toString();
            double price = parsePriceFromResponse(jsonResponse);
            long currentTime = System.currentTimeMillis();
            long elapsedTime = (currentTime - startTime) / 1000; // time in seconds

            // Store the stock price and timestamp in the queue
            String stockData = "Timestamp: " + currentTime + ", Price: " + price;
            stockQueue.add(stockData);

            // Add the new data point to the graph
            series.getData().add(new XYChart.Data<>(elapsedTime, price));

            // Optionally, print the stock data
            System.out.println(stockData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double parsePriceFromResponse(String jsonResponse) {
        // Expected pattern in the JSON response
        String searchString = "\"05. price\": \"";
        int startIndex = jsonResponse.indexOf(searchString);
    
        if (startIndex != -1) {
            // Adjust the start index to point after the search string
            startIndex += searchString.length();
            int endIndex = jsonResponse.indexOf("\"", startIndex);
    
            // Ensure that the end index is valid
            if (endIndex != -1) {
                String priceString = jsonResponse.substring(startIndex, endIndex);
                try {
                    return Double.parseDouble(priceString);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing price: " + priceString);
                }
            } else {
                System.err.println("End index not found in response.");
            }
        } else {
            System.err.println("Price information not found in response.");
        }
    
        // Return a default value or throw an exception depending on your needs
        return 0.0;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
