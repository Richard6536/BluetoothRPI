package com.example.richard.bluetoothrpi.Clases;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private static FileOutputStream brpiFile2 = null;
    public static String fileData2 = "";
    public static File myFileData2 = null;

    public static void crearDirectorio()
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

                    fileData = "registros_celular.txt";
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

                fileData = "registros_celular.txt";
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

        crearDirectorio2();

    }
    public static void crearDirectorio2()
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

                    fileData2 = "registros_array.txt";
                    myFileData2 = new File(root, fileData2);
                    myFileData2.createNewFile();
                    brpiFile2 = new FileOutputStream(myFileData2);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(brpiFile2));
                    //writer.append(id);
                    writer.newLine();

                    writer.flush();
                    writer.close();
                    brpiFile2.close();

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

                fileData2 = "registros_array.txt";
                myFileData2 = new File(root, fileData2);

                if(!myFileData2.exists())
                {
                    myFileData2.createNewFile();
                }

                String a ="";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void EscribirEnArchivo(JSONObject registrosJSON) {
        try {

            FileOutputStream fileinput = new FileOutputStream(myFileData);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileinput));

            writer.append("");
            writer.append(registrosJSON+"");
            writer.append("\n");
            writer.append("\n");

            writer.close();
            fileinput.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void EscribirEnArchivoLista(double seconds, JSONArray array) {
        try {

            StringBuilder textLista = new StringBuilder();
            BufferedReader br = null;

            br = new BufferedReader(new FileReader(myFileData2));
            String line;

            while ((line = br.readLine()) != null) {
                textLista.append(line);
            }

            br.close();

            String text = textLista.toString();

            text = text + "\n" + array;

            FileOutputStream fileinput = new FileOutputStream(myFileData2);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileinput));

            writer.append(text+": " + seconds +" seg. -----");


            writer.close();
            fileinput.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject leerArchivo() {
        try {

            File file = myFileData;

            StringBuilder textLista = new StringBuilder();
            BufferedReader br = null;

            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                textLista.append(line);
            }

            br.close();

            if(textLista.toString().length() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject(textLista.toString());
                    return jsonObject;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                return new JSONObject();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
