package com.example.test_ds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;

public class StatisticsActivity extends AppCompatActivity {
    private static ArrayList <String> users = new ArrayList<String>();

    private static ArrayList <Double> userDistances = new ArrayList<Double>();
    private static ArrayList <Double> userElevations = new ArrayList<Double>();
    private static ArrayList <Double> userTimes = new ArrayList<Double>();
    private static double averageDistance;
    private static double averageElevation;
    private static double averageTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        //get results from file
        Uri uri = Uri.parse( "file:///storage/emulated/0/Download/results.txt");
        String results = readTextFile(uri);

        //System.out.println("Results: "+results);
        //extract values from results
        extractValues(results);


        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private String readTextFile(Uri uri){
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
    public static void extractValues(String input) {
        // Words/phrases to remove
        String[] wordsToRemove = {
                "Average Distance:",
                "Average Total Elevation:",
                "Average Total Time:",
                "User:",
                "Total Distance:",
                "Total Elevation:",
                "Total Time:"
        };

        // Remove the words/phrases from the input string
        for (String word : wordsToRemove) {
            input = input.replace(word, "");
        }

        // Split the modified string by spaces
        String[] parts = input.trim().split("\\s+");

        // Extract and store the desired values in variables
        averageDistance = Double.parseDouble(parts[0]);
        averageElevation = Double.parseDouble(parts[1]);
        averageTime = Double.parseDouble(parts[2]);
        int i = 3;
        while (i < parts.length) {
            String user = parts[i];
            double totalDistance = Double.parseDouble(parts[i+1]);
            double totalElevation = Double.parseDouble(parts[i+2]);
            double totalTime = Double.parseDouble(parts[i+3]);
            i += 4;
            users.add(user);
            userDistances.add(totalDistance);
            userElevations.add(totalElevation);
            userTimes.add(totalTime);
        }
        //print the variables
        System.out.println("Average distance: " + averageDistance);
        System.out.println("Average elevation: " + averageElevation);
        System.out.println("Average time: " + averageTime);
        for (int j = 0; j < users.size(); j++) {
            System.out.println("User: " + users.get(j));
            System.out.println("Total distance: " + userDistances.get(j));
            System.out.println("Total elevation: " + userElevations.get(j));
            System.out.println("Total time: " + userTimes.get(j));
        }


    }



    }

