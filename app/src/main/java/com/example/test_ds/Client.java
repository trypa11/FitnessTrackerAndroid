package com.example.test_ds;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;
import android.net.Uri;

import java.io.*;
import java.net.*;



public class Client extends Thread {

    private String gpxString;
    private File gpxFile;


    Client(String gpx )  {
        this.gpxString = gpx;
    }

    public void run() {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        Socket requestSocket = null;
        try {
            requestSocket = new Socket("192.168.1.9", 1234);

            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream((requestSocket.getInputStream()));
            //send string to server
            out.writeUTF(gpxString);
            out.flush();
            //receive file from server
            gpxFile = (File) in.readObject();
            System.out.println("File received from server");
            //write file to device
            String path = Environment.getExternalStorageDirectory().toString() + "/Download";
            File file = new File(path, "apotelesmata.txt");
            file = gpxFile;
            //FileOutputStream fos = new FileOutputStream(file);
            //fos.write(gpxFile.toString().getBytes());
            //fos.close();

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (requestSocket != null) {
                    requestSocket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }



    }

