package com.example.richard.bluetoothrpi.Clases;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class Vehiculo {
    public static class ActualizarPosicion extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... parametros) {

            JSONObject datos = null;
            String JsonResponse = "";
            Log.d(TAG, "ActualizarPosicionAsyncTask_Parametros " + parametros);

            String p = parametros[0];
            Log.d(TAG, "ActualizarPosicionAsyncTask_userId");

            //TODO: TERCER MENSAJE
            //ejecutarMetodoPos("mensajeEntroAsyncPosicion");
            try {

                JSONArray jsonArray = new JSONArray(p);

                datos = new JSONObject();
                //datos.put("Id", userId);
                datos.put("VehiculoId", 1);
                datos.put("ListaDatos", jsonArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpURLConnection urlConnection = null;
            //Parámetros
            BufferedReader reader = null;
            OutputStream os = null;

            try {
                URL url = new URL("http://autoelectrico.tk/odata/Autos/ActualizarDatosRaspberryDirecto");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(6000);
                urlConnection.setReadTimeout(6000);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(datos.toString().getBytes());
                os.flush();
                Log.d("TAG", "datosActualizado" + datos+"");

                InputStream inputStream = urlConnection.getInputStream();

                //TODO: CUARTO MENSAJE
                //ejecutarMetodoPos("mensajePasoInputStream");

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine = "";
                while ((inputLine = reader.readLine()) != null)
                {
                    buffer.append(inputLine);
                }

                try {

                    String m = buffer.toString();
                    JSONObject resultadoJSON = new JSONObject(m);
                    String mensaje = resultadoJSON.getString("value");
                    return mensaje;
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    return "errorMensaje";
                }

            } catch (IOException e) {
                e.printStackTrace();
                //TODO QUINTO MENSAJE
                //ejecutarMetodoPos("mensajeEntroCatchPosicion");
                return "ErrorCatch:"+e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("TAG", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String respuestaOdata)
        {
            try
            {
                //new MapActivity.pasoOnPostPosicionAsync().execute("000","000");
            }
            catch (Exception e)
            {
                Log.e(TAG, "onPostExecute: "+e.getMessage());
            }

        }
    }

}
