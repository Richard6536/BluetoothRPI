package com.example.richard.bluetoothrpi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.richard.bluetoothrpi.Clases.ControllerActivity;
import com.example.richard.bluetoothrpi.Clases.Storage;
import com.example.richard.bluetoothrpi.Clases.Vehiculo;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static String btAdress = "00:00:00:00:00:00";
    private static final UUID MY_UUID = UUID.fromString("08C2B2EF-7C87-3D00-0CDC-9A2ADC420BFF");
    public BluetoothDevice device;
    private static final int DISCOVERABLE_REQUEST_CODE = 0x1;
    private boolean continuar = true;

    private Button btnComenzar;
    private TextView txtConectando, txtFecha, txtEstimacionSoc, txtConfIntervalSoc1, txtConfIntervalSoc2, txtEstimacionSompa,
                    txtConfIntervalSompa1, txtConfIntervalSompa2, txtEstimacionRin, txtConfIntervalRin1, txtConfIntervalRin2, txtCorriente, txtVoltaje;

    private boolean CONTINUE_READ_WRITE = true;
    private LinearLayout constraintContentConnecting, constraintContent;

    private BluetoothSocket socket;
    private InputStream is;
    private OutputStreamWriter os;
    private boolean servicioDetenido = true;
    private boolean contentOculto = true;

    BluetoothServerSocket serverSocket;
    Double secondCount = 0.0;

    long actualMill = 0;
    boolean continuar_envio = true;
    private JSONArray registrosAEnviarGuardados;

    double promedioExitoAPP = 0;
    double mayorTiempoAPP = 0;
    double menorTiempoAPP = 1;
    double promedioTiempoAPP = 0;
    int totalEnviadosAPP = 0;
    int totalErroresAPP = 0;
    int totalRegistradosAPP = 0;

    private TextView txtPromedioExitoRPI, txtPromedioExitoAPP, txtTiempoMayorRegistroRPI, txtTiempoMayorRegistroAPP, txtTiempoMenorRegistroRPI,
            txtTiempoMenorRegistroAPP, txtPromedioTiempoEnvioRPI, txtPromedioTiempoEnvioAPP, txtTotalRegistrosEnviadosRPI, txtTotalRegistrosEnviadosAPP,
            txtTotalRegistradosRPI, txtTotalRegistradosAPP, txtTotalErroresRPI, txtTotalErroresAPP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ControllerActivity.activiyAbiertaActual = this;

        constraintContentConnecting = (LinearLayout) findViewById(R.id.ConstraintContentConnecting);
        constraintContent = (LinearLayout)findViewById(R.id.linearLayoutContent);

        constraintContent.setVisibility(View.GONE);
        txtConectando = (TextView)findViewById(R.id.txtConectando);
        txtConectando.setVisibility(View.GONE);

        txtFecha = (TextView)findViewById(R.id.txtFecha);
        txtEstimacionSoc = (TextView)findViewById(R.id.txtEstimacionSoc);
        txtConfIntervalSoc1 = (TextView)findViewById(R.id.txtConfIntervalSoc1);
        txtConfIntervalSoc2 = (TextView)findViewById(R.id.txtConfIntervalSoc2);
        txtEstimacionSompa = (TextView)findViewById(R.id.txtEstimacionSompa);
        txtConfIntervalSompa1 = (TextView)findViewById(R.id.txtConfIntervalSompa1);
        txtConfIntervalSompa2 = (TextView)findViewById(R.id.txtConfIntervalSompa2);
        txtEstimacionRin = (TextView)findViewById(R.id.txtEstimacionRin);
        txtConfIntervalRin1 = (TextView)findViewById(R.id.txtConfIntervalRin1);
        txtConfIntervalRin2 = (TextView)findViewById(R.id.txtConfIntervalRin2);
        txtCorriente = (TextView)findViewById(R.id.txtCorriente);
        txtVoltaje = (TextView)findViewById(R.id.txtVoltaje);

        txtPromedioExitoRPI = (TextView)findViewById(R.id.txtPromedioExitoRPI);
        txtPromedioExitoAPP = (TextView)findViewById(R.id.txtPromedioExitoAPP);
        txtTiempoMayorRegistroRPI = (TextView)findViewById(R.id.txtTiempoMayorRegistroRPI);
        txtTiempoMayorRegistroAPP = (TextView)findViewById(R.id.txtTiempoMayorRegistroAPP);
        txtTiempoMenorRegistroRPI = (TextView)findViewById(R.id.txtTiempoMenorRegistroRPI);
        txtTiempoMenorRegistroAPP = (TextView)findViewById(R.id.txtTiempoMenorRegistroAPP);
        txtPromedioTiempoEnvioRPI = (TextView)findViewById(R.id.txtPromedioTiempoEnvioRPI);
        txtPromedioTiempoEnvioAPP = (TextView)findViewById(R.id.txtPromedioTiempoEnvioAPP);
        txtTotalRegistrosEnviadosRPI = (TextView)findViewById(R.id.txtTotalRegistrosEnviadosRPI);
        txtTotalRegistrosEnviadosAPP = (TextView)findViewById(R.id.txtTotalRegistrosEnviadosAPP);
        txtTotalRegistradosRPI = (TextView)findViewById(R.id.txtTotalRegistradosRPI);
        txtTotalRegistradosAPP = (TextView)findViewById(R.id.txtTotalRegistradosAPP);
        txtTotalErroresRPI = (TextView)findViewById(R.id.txtTotalErroresRPI);
        txtTotalErroresAPP = (TextView)findViewById(R.id.txtTotalErroresAPP);


        btnComenzar = (Button)findViewById(R.id.btnComenzar);
        btnComenzar.setBackgroundColor(Color.parseColor("#4CAF50"));

        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(servicioDetenido){
                    servicioDetenido = false;
                    contentOculto = true;

                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivityForResult(discoverableIntent, DISCOVERABLE_REQUEST_CODE);
                }
                else{
                    servicioDetenido = true;
                    btnComenzar.setBackgroundColor(Color.parseColor("#4CAF50"));
                    btnComenzar.setText("COMENZAR");
                    txtConectando.setVisibility(View.GONE);

                    finish();
                    System.exit(0);
                }

            }
        });

        Log.d("0092bluet","start application");

        requestPermission();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Storage.crearDirectorio();
            //inicializarTxt();
            generateExcelFile(getApplicationContext(),"Registros.xls");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && permissions[0].equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // check whether storage permission granted or not.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Storage.crearDirectorio();
                        //inicializarTxt();
                        generateExcelFile(getApplicationContext(),"Registros.xls");
                    }
                    else{
                        finish();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        btnComenzar.setBackgroundColor(Color.parseColor("#f44336"));
        btnComenzar.setText("DETENER");

        txtConectando.setVisibility(View.VISIBLE);
        constraintContent.setVisibility(View.GONE);
        constraintContentConnecting.setVisibility(View.VISIBLE);

        Log.d("0092bluet","Creating thread to start listening...");

        new Thread(reader).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(socket != null){
            try{
                is.close();
                os.close();
                socket.close();
            }catch(Exception e){}
            CONTINUE_READ_WRITE = false;
        }
    }

    private Runnable reader = new Runnable() {
        public void run() {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            try {
                serverSocket = adapter.listenUsingRfcommWithServiceRecord("RosieProject", MY_UUID);
                Log.d("0092bluet","Listening...");
                //addViewOnUiThread("TrackingFlow. Listening...");
                //socket = serverSocket.accept();
                Log.d("0092bluet","Socket accepted...");
                //addViewOnUiThread("TrackingFlow. Socket accepted...");
                //is = socket.getInputStream();
                //os = new OutputStreamWriter(socket.getOutputStream());
                //new Thread(writter).start();
                int bufferSize = 1008;
                int bytesRead = -1;
                int bytesFinalRead = 0;

                byte[] buffer = new byte[bufferSize];
                Log.d("0092bluet","Keep reading the messages while connection is open...");
                //Keep reading the messages while connection is open...
                while(CONTINUE_READ_WRITE){
                    socket = serverSocket.accept();
                    //addViewOnUiThread("TrackingFlow. Socket accepted...");
                    is = socket.getInputStream();
                    String result = "";
                    final StringBuilder sb = new StringBuilder();

                    /*
                    while(continuar){
                        try{
                            bytesRead = is.read(buffer);
                        }
                        catch(EOFException e){
                            e.getMessage();
                        }

                        int t = 9;
                        if(bytesRead == -1){
                            int re = 4;
                            break;
                            //continuar = false;
                        }
                        else{
                            bytesFinalRead = bytesFinalRead + bytesRead;
                            String b = "d";
                        }
                    }

                    result = result + new String(buffer, 0, bytesFinalRead);
                    sb.append(result);
                    String a = "sad";*/

                    bytesRead = is.read(buffer);
                    if (bytesRead != -1) {

                        while (bytesRead == bufferSize){
                            result = result + new String(buffer, 0, bytesRead);
                            bytesRead = is.read(buffer);
                        }

                        result = result + new String(buffer, 0, bytesRead);
                        sb.append(result);

                        os = new OutputStreamWriter(socket.getOutputStream());

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        String date = sdf.format(new Date());
                        os.write(date);

                        os.flush();
                        os.close();

                    }

                    Log.d("0092bluet","Read: " + sb.toString());
                    //addViewOnUiThread("TrackingFlow. Read: " + sb.toString());
                    //Show message on UIThread

                        while(continuar_envio){
                            continuar_envio = false;

                            try {

                                String promedioExitoRPI = "0";
                                String mayorTiempoRPI = "0";
                                String menorTiempoRPI = "0";
                                String promedioTiempoRPI = "0";
                                String totalEnviadosRPI = "0";
                                String totalRegistradosRPI = "0";
                                String totalErroresRPI = "0";
                                String tiempoInicio = "0";
                                String tiempoFinal = "0";
                                String tiempoApp = "0";

                                JSONArray jsonArrayEnviar = new JSONArray();
                                JSONArray jsonArray = new JSONArray(sb.toString());

                                for(int x = 0; x <jsonArray.length(); x++){
                                    String str = jsonArray.getString(x);
                                    JSONObject jsonObject = new JSONObject(str);

                                    //JSONObject json = null;
                                    //json = jsonArray.getJSONObject(x);

                                    String fecha = jsonObject.getString("Fecha");
                                    String estimacionSoc = jsonObject.getString("EstimacionSoc");
                                    String confIntervalSoc1 = jsonObject.getString("ConfIntervalSoc1");
                                    String confIntervalSoc2 = jsonObject.getString("ConfIntervalSoc2");

                                    String estimacionSompa = jsonObject.getString("EstimacionSompa");
                                    String confIntervalSompa1 = jsonObject.getString("ConfIntervalSompa1");
                                    String confIntervalSompa2 = jsonObject.getString("ConfIntervalSompa2");

                                    String estimacionRin = jsonObject.getString("EstimacionRin");
                                    String confIntervalRin1 = jsonObject.getString("ConfIntervalRin1");
                                    String confIntervalRin2 = jsonObject.getString("ConfIntervalRin2");
                                    String corriente = jsonObject.getString("Corriente");
                                    String voltaje = jsonObject.getString("Voltaje");

                                    promedioExitoRPI = jsonObject.getString("PromedioExito");
                                    mayorTiempoRPI = jsonObject.getString("MayorTiempo");
                                    menorTiempoRPI = jsonObject.getString("MenorTiempo");
                                    promedioTiempoRPI = jsonObject.getString("PromedioTiempo");
                                    totalEnviadosRPI = jsonObject.getString("TotalEnviados");
                                    totalRegistradosRPI = jsonObject.getString("TotalRegistrados");
                                    totalErroresRPI = jsonObject.getString("TotalErrores");
                                    tiempoInicio = jsonObject.getString("TiempoInicio");
                                    tiempoFinal = jsonObject.getString("TiempoFinal");

                                    JSONObject jsonObjectEnviar = new JSONObject();
                                    jsonObjectEnviar.put("FechaHoraString", fecha);
                                    jsonObjectEnviar.put("EstimacionSoc", estimacionSoc);
                                    jsonObjectEnviar.put("ConfIntervalSoc1", confIntervalSoc1);
                                    jsonObjectEnviar.put("ConfIntervalSoc2", confIntervalSoc2);
                                    jsonObjectEnviar.put("EstimacionSompa", estimacionSompa);
                                    jsonObjectEnviar.put("ConfIntervalSompa1", confIntervalSompa1);
                                    jsonObjectEnviar.put("ConfIntervalSompa2", confIntervalSompa2);
                                    jsonObjectEnviar.put("EstimacionRin", estimacionRin);
                                    jsonObjectEnviar.put("ConfIntervalRin1", confIntervalRin1);
                                    jsonObjectEnviar.put("ConfIntervalRin2", confIntervalRin2);
                                    jsonObjectEnviar.put("Corriente", corriente);
                                    jsonObjectEnviar.put("Voltaje", voltaje);

                                    jsonArrayEnviar.put(jsonObjectEnviar);
                                }

                                actualMill = System.currentTimeMillis();
                                registrosAEnviarGuardados = jsonArrayEnviar;

                                try {

                                JSONObject registrosJSON = new JSONObject();

                                registrosJSON.put("PorcentajeExitoRPI", promedioExitoRPI);
                                registrosJSON.put("PorcentajeExitoAPP", promedioExitoAPP);

                                registrosJSON.put("MayorTiempoRegistroRPI", mayorTiempoRPI);
                                registrosJSON.put("MayorTiempoRegistroAPP", mayorTiempoAPP);

                                registrosJSON.put("MenorTiempoRegistroRPI", menorTiempoRPI);
                                registrosJSON.put("MenorTiempoRegistroAPP", menorTiempoAPP);

                                registrosJSON.put("PromedioTiempoEnvioRPI", promedioTiempoRPI);
                                registrosJSON.put("PromedioTiempoEnvioAPP", promedioTiempoAPP);

                                registrosJSON.put("TotalRegistrosEnviadosRPI", totalEnviadosRPI);
                                registrosJSON.put("TotalRegistrosEnviadosAPP", totalEnviadosAPP);

                                registrosJSON.put("TotalRegistradosRPI", totalRegistradosRPI);
                                registrosJSON.put("TotalRegistradosAPP", totalRegistradosAPP);

                                registrosJSON.put("TotalErroresRPI", totalErroresRPI);
                                registrosJSON.put("TotalErroresAPP", totalErroresAPP);

                                registrosJSON.put("TiempoInicio", tiempoInicio);
                                registrosJSON.put("TiempoFinal", tiempoFinal);
                                registrosJSON.put("TiempoApp", 0);


                                writeExcelFile(getApplicationContext(), "Registros.xls", registrosJSON, jsonArrayEnviar);

                                //Storage.EscribirEnArchivo(registrosJSON);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            new Vehiculo.ActualizarPosicion().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonArrayEnviar.toString());

                        }catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }

                    showData(sb);
                }

                is.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("0092bluet","Error: " + e.getMessage());
            }
        }
    };

    public void showData(StringBuilder sb){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONArray jsonArray = new JSONArray(sb.toString());
                    for(int x = 0; x <jsonArray.length(); x++){

                        String str = jsonArray.getString(x);
                        JSONObject json = new JSONObject(str);

                        String fecha = json.getString("Fecha");
                        String estimacionSoc = json.getString("EstimacionSoc");
                        String confIntervalSoc1 = json.getString("ConfIntervalSoc1");
                        String confIntervalSoc2 = json.getString("ConfIntervalSoc2");
                        String estimacionSompa = json.getString("EstimacionSompa");
                        String confIntervalSompa1 = json.getString("ConfIntervalSompa1");
                        String confIntervalSompa2 = json.getString("ConfIntervalSompa2");
                        String estimacionRin = json.getString("EstimacionRin");
                        String confIntervalRin1 = json.getString("ConfIntervalRin1");
                        String confIntervalRin2 = json.getString("ConfIntervalRin2");
                        String corriente = json.getString("Corriente");
                        String voltaje = json.getString("Voltaje");

                        if(contentOculto){
                            contentOculto = false;

                            txtConectando.setVisibility(View.GONE);
                            constraintContentConnecting.setVisibility(View.GONE);
                            constraintContent.setVisibility(View.VISIBLE);
                        }

                        txtPromedioExitoRPI.setText(json.getString("PromedioExito"));
                        txtPromedioExitoAPP.setText(promedioExitoAPP+"");
                        txtTiempoMayorRegistroRPI.setText(json.getString("MayorTiempo"));
                        txtTiempoMayorRegistroAPP.setText(mayorTiempoAPP+"");
                        txtTiempoMenorRegistroRPI.setText(json.getString("MenorTiempo"));
                        txtTiempoMenorRegistroAPP.setText(menorTiempoAPP+"");
                        txtPromedioTiempoEnvioRPI.setText(json.getString("PromedioTiempo"));
                        txtPromedioTiempoEnvioAPP.setText(promedioTiempoAPP+"");
                        txtTotalRegistrosEnviadosRPI.setText(json.getString("TotalEnviados"));
                        txtTotalRegistrosEnviadosAPP.setText(totalEnviadosAPP+"");
                        txtTotalRegistradosRPI.setText(json.getString("TotalRegistrados"));
                        txtTotalRegistradosAPP.setText(totalRegistradosAPP+"");
                        txtTotalErroresRPI.setText(json.getString("TotalErrores"));
                        txtTotalErroresAPP.setText(totalErroresAPP+"");

                        txtFecha.setText(fecha);
                        txtEstimacionSoc.setText(estimacionSoc);
                        txtConfIntervalSoc1.setText(confIntervalSoc1);
                        txtConfIntervalSoc2.setText(confIntervalSoc2);
                        txtEstimacionSompa.setText(estimacionSompa);
                        txtConfIntervalSompa1.setText(confIntervalSompa1);
                        txtConfIntervalSompa2.setText(confIntervalSompa2);
                        txtEstimacionRin.setText(estimacionRin);
                        txtConfIntervalRin1.setText(confIntervalRin1);
                        txtConfIntervalRin2.setText(confIntervalRin2);
                        txtCorriente.setText(corriente);
                        txtVoltaje.setText(voltaje);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private Runnable writter = new Runnable() {

        @Override
        public void run() {
            /*
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            try {
                serverSocket = adapter.listenUsingRfcommWithServiceRecord("RosieProject", MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                socket = serverSocket.accept();
                os = new OutputStreamWriter(socket.getOutputStream());

                //os.write("Message From Server" + (index++) + "\n");
                os.write("Error Fatal");
                os.flush();
                //Thread.sleep(1000);

                os.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            */

            while(CONTINUE_READ_WRITE){
                try {
                    os = new OutputStreamWriter(socket.getOutputStream());

                    os.write("Message From Server"  + "\n");
                    //os.write("Error Fatal");
                    os.flush();

                    os.close();
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void inicializarTxt(){
        JSONObject registrosJSON = new JSONObject();

        try {

            registrosJSON.put("PorcentajeExitoRPI", 0);
            registrosJSON.put("PorcentajeExitoAPP", 0);

            registrosJSON.put("MayorTiempoRegistroRPI", 0);
            registrosJSON.put("MayorTiempoRegistroAPP", 0);

            registrosJSON.put("MenorTiempoRegistroRPI", 0);
            registrosJSON.put("MenorTiempoRegistroAPP", 1);

            registrosJSON.put("PromedioTiempoEnvioRPI", 0);
            registrosJSON.put("PromedioTiempoEnvioAPP", 0);

            registrosJSON.put("TotalRegistrosEnviadosRPI", 0);
            registrosJSON.put("TotalRegistrosEnviadosAPP", 0);

            registrosJSON.put("TotalRegistradosRPI", 0);
            registrosJSON.put("TotalRegistradosAPP", 0);

            registrosJSON.put("TotalErroresRPI", 0);
            registrosJSON.put("TotalErroresAPP", 0);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Storage.EscribirEnArchivo(registrosJSON);
    }

    public void respuestaWebService(String respuestaOdata){

        // Creating Input Stream
        File file = new File(getApplicationContext().getExternalFilesDir(null), "Registros.xls");
        try {
            FileInputStream  myInput = new FileInputStream(file);
            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            Sheet mySheet = myWorkBook.getSheetAt(0);
            Row row = mySheet.getRow(mySheet.getLastRowNum());

            if(row != null){

                Cell PorcentajeExitoAPPCell = row.getCell(1);
                Cell MayorTiempoRegistroAPPCell = row.getCell(3);
                Cell MenorTiempoRegistroAPPCell = row.getCell(5);
                Cell PromedioTiempoEnvioAPPCell = row.getCell(7);
                Cell TotalRegistrosEnviadosAPPCell = row.getCell(9);
                Cell TotalRegistradosAPP = row.getCell(11);
                Cell TotalErroresAPP = row.getCell(13);


                String porcentajeExitoSTR = PorcentajeExitoAPPCell.getStringCellValue();
                String mayorTiempoRegistroSTR = MayorTiempoRegistroAPPCell.getStringCellValue();
                String menorTiempoRegistroSTR = MenorTiempoRegistroAPPCell.getStringCellValue();
                String promedioTiempoEnvioSTR = PromedioTiempoEnvioAPPCell.getStringCellValue();
                String totalRegistrosEnviadosSTR = TotalRegistrosEnviadosAPPCell.getStringCellValue();
                String totalRegistradosSTR = TotalRegistradosAPP.getStringCellValue();
                String totalErroresSTR = TotalErroresAPP.getStringCellValue();

                promedioExitoAPP = Double.parseDouble(porcentajeExitoSTR);
                mayorTiempoAPP = Double.parseDouble(mayorTiempoRegistroSTR);
                menorTiempoAPP = Double.parseDouble(menorTiempoRegistroSTR);
                promedioTiempoAPP = Double.parseDouble(promedioTiempoEnvioSTR);
                totalEnviadosAPP = Integer.parseInt(totalRegistrosEnviadosSTR);
                totalErroresAPP = Integer.parseInt(totalErroresSTR);
                totalRegistradosAPP = Integer.parseInt(totalRegistradosSTR);

            }

            totalRegistradosAPP = totalRegistradosAPP + 1;

            if(respuestaOdata.equals("OK")){

                totalEnviadosAPP = totalEnviadosAPP + 1;

                long mill = System.currentTimeMillis();
                long milliseconds = mill - actualMill;
                double seconds = milliseconds / 1000.0;
                secondCount = secondCount + seconds;

                guardarRegistrosLista(seconds, registrosAEnviarGuardados);

                promedioTiempoAPP = secondCount/totalEnviadosAPP;
                promedioExitoAPP = (totalEnviadosAPP / totalRegistradosAPP) * 100;

                if(mayorTiempoAPP < seconds){
                    mayorTiempoAPP = seconds;
                }

                if(menorTiempoAPP > seconds){
                    menorTiempoAPP = seconds;
                }

            }
            else{
                totalErroresAPP = totalErroresAPP + 1;
            }

            continuar_envio = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean generateExcelFile(Context context, String fileName) {

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Promedios");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("% Exito RPI");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("% Exito APP");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Tiempo mayor registro RPI (seg.)");
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Tiempo mayor registro APP (seg.)");
        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue("Tiempo menor registro RPI (seg.)");
        c.setCellStyle(cs);

        c = row.createCell(5);
        c.setCellValue("Tiempo menor registro APP (seg.)");
        c.setCellStyle(cs);

        c = row.createCell(6);
        c.setCellValue("Promedio tiempo de envio RPI (seg.)");
        c.setCellStyle(cs);

        c = row.createCell(7);
        c.setCellValue("Promedio tiempo de envio APP (seg.)");
        c.setCellStyle(cs);

        c = row.createCell(8);
        c.setCellValue("Total registros enviados RPI");
        c.setCellStyle(cs);

        c = row.createCell(9);
        c.setCellValue("Total registros enviados APP");
        c.setCellStyle(cs);

        c = row.createCell(10);
        c.setCellValue("Total registrados RPI");
        c.setCellStyle(cs);

        c = row.createCell(11);
        c.setCellValue("Total registrados APP");
        c.setCellStyle(cs);

        c = row.createCell(12);
        c.setCellValue("Total errores RPI");
        c.setCellStyle(cs);

        c = row.createCell(13);
        c.setCellValue("Total errores APP");
        c.setCellStyle(cs);

        c = row.createCell(14);
        c.setCellValue("Tiempo Inicio");
        c.setCellStyle(cs);

        c = row.createCell(15);
        c.setCellValue("Tiempo Final");
        c.setCellStyle(cs);

        c = row.createCell(16);
        c.setCellValue("Tiempo APP");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));
        sheet1.setColumnWidth(4, (15 * 500));
        sheet1.setColumnWidth(5, (15 * 500));
        sheet1.setColumnWidth(6, (15 * 500));
        sheet1.setColumnWidth(7, (16 * 500));
        sheet1.setColumnWidth(8, (15 * 500));
        sheet1.setColumnWidth(9, (15 * 500));
        sheet1.setColumnWidth(10, (15 * 500));
        sheet1.setColumnWidth(11, (15 * 500));
        sheet1.setColumnWidth(12, (15 * 500));
        sheet1.setColumnWidth(13, (15 * 500));
        sheet1.setColumnWidth(14, (15 * 500));
        sheet1.setColumnWidth(15, (15 * 500));
        sheet1.setColumnWidth(16, (15 * 500));


        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }


        return success;
    }

    public void writeExcelFile(Context context, String filename, JSONObject registrosJson, JSONArray jsonArrayEnviar){

        // Creating Input Stream
        File file = new File(context.getExternalFilesDir(null), filename);
        try {
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Row row = mySheet.createRow(mySheet.getLastRowNum() + 1);

            Cell c = null;

            c = row.createCell(0);
            c.setCellValue(registrosJson.getString("PorcentajeExitoRPI"));

            c = row.createCell(1);
            c.setCellValue(registrosJson.getString("PorcentajeExitoAPP"));

            c = row.createCell(2);
            c.setCellValue(registrosJson.getString("MayorTiempoRegistroRPI"));

            c = row.createCell(3);
            c.setCellValue(registrosJson.getString("MayorTiempoRegistroAPP"));

            c = row.createCell(4);
            c.setCellValue(registrosJson.getString("MenorTiempoRegistroRPI"));

            c = row.createCell(5);
            c.setCellValue(registrosJson.getString("MenorTiempoRegistroAPP"));

            c = row.createCell(6);
            c.setCellValue(registrosJson.getString("PromedioTiempoEnvioRPI"));

            c = row.createCell(7);
            c.setCellValue(registrosJson.getString("PromedioTiempoEnvioAPP"));

            c = row.createCell(8);
            c.setCellValue(registrosJson.getString("TotalRegistrosEnviadosRPI"));

            c = row.createCell(9);
            c.setCellValue(registrosJson.getString("TotalRegistrosEnviadosAPP"));

            c = row.createCell(10);
            c.setCellValue(registrosJson.getString("TotalRegistradosRPI"));

            c = row.createCell(11);
            c.setCellValue(registrosJson.getString("TotalRegistradosAPP"));

            c = row.createCell(12);
            c.setCellValue(registrosJson.getString("TotalErroresRPI"));

            c = row.createCell(13);
            c.setCellValue(registrosJson.getString("TotalErroresAPP"));

            c = row.createCell(14);
            c.setCellValue(registrosJson.getString("TiempoInicio"));

            c = row.createCell(15);
            c.setCellValue(registrosJson.getString("TiempoFinal"));

            c = row.createCell(16);
            c.setCellValue(registrosJson.getString("TiempoApp"));


            // Create a path where we will place our List of objects on external storage
            File file2 = new File(context.getExternalFilesDir(null), filename);
            FileOutputStream os = null;

            try {
                os = new FileOutputStream(file2);
                myWorkBook.write(os);
                Log.w("FileUtils", "Writing file" + file);
                //success = true;
            } catch (IOException e) {
                Log.w("FileUtils", "Error writing " + file, e);
            } catch (Exception e) {
                Log.w("FileUtils", "Failed to save file", e);
            } finally {
                try {
                    if (null != os)
                        os.close();
                } catch (Exception ex) {
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void guardarRegistrosLista(double seconds, JSONArray array){
        Storage.EscribirEnArchivoLista(seconds, array);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
