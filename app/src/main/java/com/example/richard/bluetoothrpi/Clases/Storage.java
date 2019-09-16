package com.example.richard.bluetoothrpi.Clases;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Storage {

    private static FileOutputStream brpiFile = null;
    public static String fileData = "";

    public static File myFileData = null;


    public static void crearDirectorio(String id)
    {
        File dir = new File("/storage/emulated/0/Android/data/com.bluetoothrpi.android.data"); ///storage/emulated/0/Android/data

        if(!dir.isDirectory()) //Comprueba si el directorio existe.
        {
            //Entra si el directorio no existe.
            if(!dir.mkdir()) {

            }
            else
            {
                //Si el directorio se crea por primera vez, crea un archivo inicializandolo con el ID del usuario.
                File root = new File(Environment.getExternalStorageDirectory(), "Android/data/com.bluetoothrpi.android.data"); //TODO: /storage/emulated/0/
                if (!root.exists()) {
                    root.mkdirs();
                }

                try {

                    //POSICION
                    fileData = "stappfilePosicion("+id+").txt";
                    myFileData = new File(root, fileData);
                    myFileData.createNewFile();
                    brpiFile = new FileOutputStream(myFileData);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(brpiFile));
                    //writer.append(id);
                    writer.newLine();

                    writer.flush();
                    writer.close();
                    brpiFile.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            try {

                File root = new File(Environment.getExternalStorageDirectory(), "Android/data/com.bluetoothrpi.android.data"); //TODO: /storage/emulated/0/
                if (!root.exists()) {
                    root.mkdirs();
                }

                fileData = "brpiFile("+id+").txt";
                myFileData = new File(root, fileData);

                if(!myFileData.exists())
                {
                    myFileData.createNewFile();
                }

                String a ="";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
