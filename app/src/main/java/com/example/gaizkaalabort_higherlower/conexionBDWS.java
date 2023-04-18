package com.example.gaizkaalabort_higherlower;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class conexionBDWS extends Worker {

    public conexionBDWS(Context context, WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String usu = getInputData().getString("Nombre");
        String contra = getInputData().getString("Contraseña");
        String dir = getInputData().getString("accion");

        String parametro = "nombre=" + usu + "&contraseña=" + contra;

        String direccion = "";
        if (Objects.equals(dir, "crear")){
            direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/galabort001/WEB/crearUsuario.php";
            Log.i("JSON", "Se accede a crearUsuario.php");
        } else if (Objects.equals(dir, "acceso")){
            direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/galabort001/WEB/comprobarUsuario.php";
            Log.i("JSON", "Se accede a comprobarUsuario.php");
        } else {
            Data errorAccion = new Data.Builder()
                    .putString("mens", "Se debe elegir accion del worker")
                    .build();
            return Result.success(errorAccion);
        }

        HttpURLConnection urlConnection = null;
        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametro);
            out.flush();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String response = stringBuilder.toString();
                Log.i("JSON", response);

                // Procesar la respuesta según tus necesidades
                JSONObject jsonResponse = new JSONObject(response);

                Boolean resultado = (Boolean) jsonResponse.get("value");
                Log.i("JSON", "¿Correcto? " + resultado);

                String mensaje = (String) jsonResponse.get("mens");
                Log.i("JSON", mensaje);

                reader.close();
                inputStream.close();
                out.close();
                urlConnection.disconnect();

                Data datos = new Data.Builder()
                        .putBoolean("valor",resultado)
                        .putString("texto",mensaje)
                        .build();

                return Result.success(datos);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return null;

    }

}
