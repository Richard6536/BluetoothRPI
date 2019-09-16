package com.example.richard.bluetoothrpi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.richard.bluetoothrpi.Clases.Storage;
import com.example.richard.bluetoothrpi.Clases.Vehiculo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

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
    private ConstraintLayout constraintContentConnecting, constraintContent;

    private BluetoothSocket socket;
    private InputStream is;
    private OutputStreamWriter os;
    private boolean servicioDetenido = true;
    private boolean contentOculto = true;

    BluetoothServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintContentConnecting = (ConstraintLayout)findViewById(R.id.ConstraintContentConnecting);
        constraintContent = (ConstraintLayout)findViewById(R.id.ConstraintContent);

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
                }

            }
        });

        Log.d("0092bluet","start application");
        

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
    //https://pastebin.com/Nqjv0ejM

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
                        os.write("ok");

                        os.flush();
                        os.close();

                    }

                    Log.d("0092bluet","Read: " + sb.toString());
                    //addViewOnUiThread("TrackingFlow. Read: " + sb.toString());
                    //Show message on UIThread

                    try {

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

                            JSONObject jsonObjectEnviar = new JSONObject();
                            jsonObjectEnviar.put("FechaHoraString", fecha);
                            jsonObjectEnviar.put("EstimacionSoc", estimacionSoc);
                            jsonObjectEnviar.put("ConfIntervalSoc1", confIntervalSoc1);
                            jsonObjectEnviar.put("ConfIntervalSoc1", confIntervalSoc2);
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

                        int v = 0;
                        new Vehiculo.ActualizarPosicion().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonArrayEnviar.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
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
