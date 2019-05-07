package com.ashsample.googlemapprototype.datastorage;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import static java.lang.System.out;

public class DataStorage {

    public static final String TAG = "AshiqDataStorage";

    public static String getStoredDirections(Context c){
        try {
            FileInputStream in = c.openFileInput("data.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    in));
            //int chartemp ;
            StringBuilder data= new StringBuilder();
           /* while( (chartemp = in.read())!= -1){
                data.append((char)chartemp);
            }*/
            String line = "";
            while ((line = br.readLine()) != null) {
                data.append(line);
            }

            in.close();
            Log.i(TAG,"Data :getStoredDirections"+data.toString());
            return data.toString();

        }catch(Exception e){
            Log.i(TAG,"Exception in saveDirections::"+e.toString());
        }
        return null;
    }

    public  static void saveDirections(Context c,String data){
       try {
           FileOutputStream out = c.openFileOutput("data.txt", Context.MODE_PRIVATE);
           out.write(data.getBytes());
           out.close();
       }catch(Exception e){
           Log.i(TAG,"Exception in saveDirections::"+e.toString());       }


    }
}
