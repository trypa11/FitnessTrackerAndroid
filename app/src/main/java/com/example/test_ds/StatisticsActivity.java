package com.example.test_ds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.*;
import java.util.ArrayList;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class StatisticsActivity extends AppCompatActivity {
    private static String curruser;
    private static String user_ms_time;
    private static ArrayList<String> users = new ArrayList<String>();
    private static ArrayList<Integer> seg_num_list = new ArrayList<Integer>();



    private static ArrayList<Float> userDistances = new ArrayList<Float>();
    private static ArrayList<Float> userElevations = new ArrayList<Float>();
    private static ArrayList<Float> userTimes = new ArrayList<Float>();
    private static double averageDistance;
    private static double averageElevation;
    private static double averageTime;
    private static ArrayList<String> user_s = new ArrayList<>();
    private static ArrayList<String> user_s_t = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        //get results from file
        Uri uri = Uri.parse("file:///storage/emulated/0/Download/results.txt");
        String results = readTextFile(uri);


        extractValues(results);
        BarChart barChart = findViewById(R.id.barChart);
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equals(curruser)) {
                TextView user = findViewById(R.id.usernameTextView);
                user.setText(String.valueOf(users.get(i)));
                TextView time = findViewById(R.id.totalTimeTextView);
                time.setText(String.valueOf(user_ms_time));
                TextView dist = findViewById(R.id.totalDistanceTextView);
                dist.setText(String.valueOf(userDistances.get(i)));
                TextView elev = findViewById(R.id.totalElevationTextView);
                elev.setText(String.valueOf(userElevations.get(i)));

            }
        }
        String user_s_t_string = "";
        for (int i = 0; i < user_s_t.size(); i++) {
            user_s_t_string += user_s_t.get(i) + "\n";
        }

        TextView seg_t= findViewById(R.id.segmentTimeView);
        seg_t.setText(user_s_t_string);

        String user_s_string = "";
        for (int i = 0; i < user_s.size(); i++) {
            user_s_string += user_s.get(i) + "\n";
        }
        TextView seg= findViewById(R.id.segmentUserTextView);
        seg.setText(user_s_string);
        String seg_num_string = "";
        for (int i = 0; i < seg_num_list.size(); i++) {
            seg_num_string += seg_num_list.get(i) + "\n";
        }
        TextView seg_num= findViewById(R.id.segmentNumTextView);
        seg_num.setText(seg_num_string);
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int j = 0; j < users.size(); j++) {
            if (users.get(j).equals(curruser)) {
                entries.add(new BarEntry(0f , (float) percentage(userDistances.get(j),(float) averageDistance)));
                entries.add(new BarEntry(1f , (float) percentage(userElevations.get(j),(float) averageElevation)));
                entries.add(new BarEntry(2f , (float) percentage(userTimes.get(j),(float) averageTime)));
            }
        }
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Distance");
        labels.add("Elevation");
        labels.add("Time");




        // Create a data set with the entries and labels
        BarDataSet dataSet = new BarDataSet(entries, "Precentage of Average Values of Users");
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf(value);
            }
        });

        // Customize the appearance of the bar chart
        BarData barData = new BarData(dataSet);
        barChart.setExtraBottomOffset(8f); // Adjust as needed
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();






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

    private String readTextFile(Uri uri) {
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
            if (reader != null) {
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
        //clear all the arrays
        users.clear();
        userDistances.clear();
        userElevations.clear();
        userTimes.clear();
        user_s.clear();
        user_s_t.clear();
        seg_num_list.clear();
        String[] wordsToRemove = {
                "Average Distance:",
                "Average Total Elevation:",
                "Average Total Time:",
                "User:",
                "Total Distance:",
                "Total Elevation:",
                "Total Time:",
                "Segment:",
                "Time:"
        };

        // Remove the words from the input string
        for (String word : wordsToRemove) {
            input = input.replace(word, "");
        }

        // Split the modified string by spaces
        String[] parts = input.trim().split("\\s+");

        // Extract and store the desired values in variables
        curruser = parts[0];
        averageDistance = Double.parseDouble(parts[1]);
        averageElevation = Double.parseDouble(parts[2]);
        averageTime = Double.parseDouble(parts[3]);
        int i = 4;
        boolean flag = true;
        int seg_num = 1;
        //create segment number to string
        String seg_num_string = Integer.toString(seg_num);
        while (i < parts.length && flag) {
            //if find this string "Segment" then break
            if (parts[i].equals(seg_num_string)) {
                flag = false;
                seg_num=i;
                break;
            }
            String user = parts[i];
            float totalDistance = Float.parseFloat(parts[i + 1]);
            float totalElevation = Float.parseFloat(parts[i + 2]);
            float totalTime = Float.parseFloat(parts[i + 3]);
            if(curruser.equals(user)){
                user_ms_time = msToTime( (long)totalTime);
            }
            i += 4;
            users.add(user);
            userDistances.add(totalDistance);
            userElevations.add(totalElevation);
            userTimes.add((float)totalTime);
        }
        i=seg_num+1;
        seg_num=2;
        seg_num_string = Integer.toString(seg_num);
        while (i < parts.length )
        {
            if (parts[i].equals(seg_num_string)) {
                seg_num++;
                seg_num_string = Integer.toString(seg_num);
                i ++;
            } else {
            String u_seg = parts[i];
            Long seg_time = Long.parseLong(parts[i+1]);
            user_s_t.add(msToTime(seg_time));
            user_s.add(u_seg);
            seg_num_list.add(seg_num-1);
            i += 2;
            }
        }
        //print the variables
        System.out.println("Current User: " + curruser);
        System.out.println("Average distance: " + averageDistance);
        System.out.println("Average elevation: " + averageElevation);
        System.out.println("Average time: " + averageTime);
        for (int j = 0; j < users.size(); j++) {
            System.out.println("User: " + users.get(j));
            System.out.println("Total distance: " + userDistances.get(j));
            System.out.println("Total elevation: " + userElevations.get(j));
            System.out.println("Total time: " + userTimes.get(j));
        }
        //print segment number ,user and time
        for (int j = 0; j < user_s.size(); j++) {
            System.out.println("Segment: " + seg_num_list.get(j));
            System.out.println("User: " + user_s.get(j));
            System.out.println("Time: " + user_s_t.get(j));
        }



    }

    //create a method that changes time ms to hh:mm:ss
    public static String msToTime(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        String time = hours + ":" + minutes + ":" + seconds;
        return time;
    }

    public static double percentage(float value1, float value2) {
        double percentage = (value2 / value1) * 100;
        return percentage;
    }


}
