package com.example.gaizkaalabort_higherlower;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//Clase de las pantallas del juego de elegir si el mostrado tiene mas vistas o menos que la foto superior
public class Juego extends AppCompatActivity
                    implements abandonarJuego.abandonarListener{

    private static String idioma;
    List<String[]> rows = new ArrayList<>();
    private static int numeroA = -1;
    private static int numeroB = -1;
    private static int puntos = 0;
    public String channelID = "IdCanal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Prueba", "CREANDO");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        //Se recoge el idioma como extra
        Bundle extras = getIntent().getExtras();
        if (extras != null && idioma!=extras.getString("idiomaUsuario")) {
            idioma = extras.getString("idiomaUsuario");
        }

        //En caso de haberlo rotado se mostraran las opciones anteriores
        Log.d("Prueba", String.format("Opcion A: %s, Opcion B: %s", numeroA, numeroB));

        //Metodo de recoleccion del archivo de texto, se recogen las opciones que se tendran, vistas y la imagen referente
        recogerInfo();
        for (int i = 0; i < rows.size(); i++) {
            Log.d("Prueba", String.format("row %s: %s, %s; img: %s", i, rows.get(i)[0], rows.get(i)[1],rows.get(i)[2]));
        }

        //Si no viene informado (no se ha guardado por rotacion de movil...)
        if (numeroA == -1){
            //Iniciar recogida de opcion A:
            numeroA = (int) (Math.random() * 24);
            Log.d("Prueba", String.format("Opcion A: %s, %s; img: %s", rows.get(numeroA)[0], rows.get(numeroA)[1],rows.get(numeroA)[2]));
        }

        //Se mostrara por pantalla la imagen e info de la opcion A y se obtendra nuevo valor (o valor actual) de opcion B
        numeroB = obtenerCandidatos (numeroA,numeroB);

        //Iniciar recogida de puntos y se muestra.
        TextView puntuacion = findViewById(R.id.puntuacion);
        puntuacion.setText(getString(R.string.puntos) +" "+ puntos);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED) {
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
        }

    }

    //Metodo de recoleccion del archivo de texto
    private void recogerInfo(){
        //Se obtiene el archivo
        InputStream fich = getResources().openRawResource(R.raw.busquedas);
        BufferedReader buff = new BufferedReader(new InputStreamReader(fich));

        String splitby = ",";
        String linea;

        //Por cada linea de texto
        try {
            //Recogida
            buff.readLine();

            //Recogida de cada columna
            while ((linea=buff.readLine())!=null){
                String[] row = linea.split(splitby);
                rows.add(row);
            }

            //Cierre
            fich.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Funcion extra para reducir codigo.
    public int obtenerCandidatos (int candidato1, int candidato2){
        //Se actualiza imagen para opcion A
        ImageView img = findViewById(R.id.imageViewA);
        int resID = getResources().getIdentifier(rows.get(candidato1)[2], "drawable", getPackageName());
        img.setImageResource(resID);

        // y el texto correspondiente con la informacion
        TextView opcionA = findViewById(R.id.textViewA);
        String mensaje = rows.get(candidato1)[0] + ": " + rows.get(candidato1)[1];
        opcionA.setText(mensaje);

        //Para la opcion B:
        if (candidato2 == -1){
            //Se recoge el valor aleatorio y si es el mismo se vuelve a coger. (Pero solo si no viene informado)
            candidato2 = (int) (Math.random() * 24);

            while (candidato2== candidato1){
                candidato2 = (int) (Math.random() * 24);
            }
            Log.i("Prueba", String.format("Opcion B: %s, %s; img: %s", rows.get(candidato2)[0], rows.get(candidato2)[1],rows.get(candidato2)[2]));
        }

        //Se actualiza imagen por pantalla para opcion B
        img = findViewById(R.id.imageViewB);
        resID = getResources().getIdentifier(rows.get(candidato2)[2], "drawable", getPackageName());
        img.setImageResource(resID);

        // y el texto correspondiente con la informacion
        TextView opcionB = findViewById(R.id.textViewB);
        opcionB.setText(rows.get(candidato2)[0]);

        //Se devuelve el valor nuevo de opcion B
        return candidato2;
    }

    //Se ejecuta al pulsar opcion "Mas"
    public void mas (View view){
        //Se comprueba si has acertado o no
        if (Integer.parseInt(rows.get(numeroB)[1]) > Integer.parseInt(rows.get(numeroA)[1])){
            Log.i("Prueba", "MAYOR CORRECTO");
            //Si aciertas, se cambia el numero de ser la opcion B a ser la opcion A,
            // para despues obtener la nueva opcion B, actualizar las variables por pantalla
            numeroA = numeroB;
            numeroB = obtenerCandidatos (numeroA,-1);

            //Aumentamos puntuacion por acertar y se actualiza texto
            TextView puntuacion = findViewById(R.id.puntuacion);
            puntos += 1;
            puntuacion.setText(getString(R.string.puntos) +" "+ puntos);
        } else {
            //Si no has acertado, se sale de la actividad pasando la puntuacion obtenida
            salidaSegura();
        }
    }

    //Se ejecuta al pulsar opcion "Menos"
    public void menos (View view){
        if (Integer.parseInt(rows.get(numeroB)[1]) < Integer.parseInt(rows.get(numeroA)[1])){
            Log.i("Prueba", "MENOR CORRECTO");
            //Si aciertas, se cambia el numero de ser la opcion B a ser la opcion A,
            // para despues obtener la nueva opcion B, actualizar las variables por pantalla
            numeroA = numeroB;
            numeroB = obtenerCandidatos (numeroA,-1);
            //Aumentamos puntuacion por acertar y se actualiza texto
            TextView puntuacion = findViewById(R.id.puntuacion);
            puntos += 1;
            puntuacion.setText(getString(R.string.puntos) +" "+ puntos);
        } else {
            //Si no has acertado, se sale de la actividad pasando la puntuacion obtenida
            salidaSegura();
        }
    }

    //Funcion de salida de la actividad devolviendo valores importantes
    // (se acaba el juego por derrota)
    public void salidaSegura(){
        //Si has fallado en el primer intento no se genera notificacion, sino un toast.
        if(puntos>0){
            generarNotificacion();
        } else {
            Toast.makeText(getApplicationContext(),0 + " " + getString(R.string.fallo1),Toast.LENGTH_LONG).show();
        }

        //Se reinician los valores de las opciones
        numeroA=-1;
        numeroB=-1;
        Log.i("Prueba", "FIN");

        //Se devuelve a la actividad que llamo a Juego (Usuario), la puntuacion obtenida, finalizando actividad.
        Intent intent = new Intent();
        intent.putExtra("puntuacion",String.valueOf(puntos));
        puntos=0;
        setResult(1,intent);
        finish();
    }

    //Si la puntuacion obtenida es mayor a 0, entonces generamos notificacion.
    public void generarNotificacion(){
        NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //Notificacion
        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, channelID);
        elBuilder.setSmallIcon(R.mipmap.logo)
                .setContentTitle(getString(R.string.finalpartida))
                .setContentText(getString(R.string.fallo0)+", "+ puntos + " "+getString(R.string.fallo1))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true);

        //Forma de mostrar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel elCanal = new NotificationChannel(channelID, "NombreCanal", NotificationManager.IMPORTANCE_DEFAULT);
            elCanal.setDescription("Fin partida");
            elManager.createNotificationChannel(elCanal);
        }

        //Canal por donde mostrar
        elManager.notify(1, elBuilder.build());
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        Log.i("Prueba", "GUARDANDO...");
        super.onSaveInstanceState(savedInstanceState);
        //Almacenamos idioma de la aplicacion y valores de opciones y puntos
        savedInstanceState.putString("idioma", idioma);
        savedInstanceState.putString("opcionA",String.valueOf(numeroA));
        savedInstanceState.putString("opcionB",String.valueOf(numeroB));
        savedInstanceState.putString("puntos",String.valueOf(puntos));
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.i("Prueba", "CARGANDO...");
        super.onRestoreInstanceState(savedInstanceState);
        //Recuperamos idioma de la aplicacion y valores de opciones y puntos
        idioma = savedInstanceState.getString("idioma");

        puntos = Integer.parseInt(savedInstanceState.getString("puntos"));
        numeroA = Integer.parseInt(savedInstanceState.getString("opcionA"));
        numeroB = Integer.parseInt(savedInstanceState.getString("opcionB"));

        Locale nuevaloc = new Locale(idioma);
        actualizarIdioma(nuevaloc);
    }

    //Funcion para actualizar idioma en caso de ser distinta
    public void actualizarIdioma(Locale nuevoIdiomaSel){
        Locale.setDefault(nuevoIdiomaSel);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevoIdiomaSel);
        configuration.setLayoutDirection(nuevoIdiomaSel);
        Context context = getBaseContext().createConfigurationContext(configuration);getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onKeyDown (int key, KeyEvent event) {
        if (key == event.KEYCODE_BACK){
            //Al pulsar la tecla de volver a la actividad anterior, aparecera el dialogo de abandonarJuego
            Log.i("Prueba", "SALIENDO...");
            abandonarJuego abandono = new abandonarJuego();
            abandono.show(getSupportFragmentManager(),"abandonar");
        }
        return super.onKeyDown(key,event);
    }

    //Funcion de salir(), relacionada al dialogo de pulsar el boton de volver a la actividad anterior
    @Override
    public void salir(DialogFragment dialog) {
        //Inicializa las variables
        numeroA=-1;
        numeroB=-1;
        puntos=0;
        //Añade codigo al intent para volver a Usuario
        Intent intent = new Intent();
        setResult(-1,intent);
        finish();
    }

    //Boton oculto al pulsar en "puntos", abre el dialogo de abandonarJuego
    public void salirForzado(View view){
        Log.i("Prueba", "SALIENDO...");
        abandonarJuego abandono = new abandonarJuego();
        abandono.show(getSupportFragmentManager(),"abandonar");
    }
}