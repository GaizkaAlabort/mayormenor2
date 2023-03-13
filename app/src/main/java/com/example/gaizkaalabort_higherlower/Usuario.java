package com.example.gaizkaalabort_higherlower;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

//Clase de pantalla usuario despues de loguearse
public class Usuario extends AppCompatActivity implements Ranking_global_fragment.listenerDelFragment{

    private static String idioma;
    private static String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        //Cargar barra de accion en la parte superior y añadir accion
        setSupportActionBar(findViewById(R.id.mytoolbar));

        //Recogida de variables pasadas como extra
        Bundle extras = getIntent().getExtras();
        if (extras != null && idioma==null) {
            idioma = extras.getString("idiomaLogin");
        }
        if (extras != null) {
            usuario = extras.getString("usuario");
        }

        //Actualizar variable usuario para mostrar por pantalla y guardarla al girar
        TextView usuarioIntroducido = findViewById(R.id.nombre);
        String texto = getString(R.string.usuario) + " "+usuario;
        usuarioIntroducido.setText(texto);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //Almacenamos idioma de la aplicacion y usuario
        Log.i("Login", "Guardando...");
        savedInstanceState.putString("idioma", idioma);
        savedInstanceState.putString("usuario", usuario);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("Login", "Recuperando...");
        //Recuperamos idioma de la aplicacion
        idioma = savedInstanceState.getString("idioma");

        //Recuperamos usuario y actualizamos texto para mostrar
        usuario = savedInstanceState.getString("usuario");
        TextView usuarioIntroducido = findViewById(R.id.nombre);
        String texto = getString(R.string.usuario) + " "+usuario;
        usuarioIntroducido.setText(texto);

        Locale nuevaloc = new Locale(idioma);
        actualizarIdioma(nuevaloc);
    }

    //Boton "Jugar" para empezar el Juego
    public void jugar (View view){
        Intent acceso = new Intent (this, Juego.class);
        acceso.putExtra("idiomaUsuario",idioma);
        startActivityForResult(acceso,333);
    }

    //Recogida de los Intent
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 333) {
            //Llega desde el juego
            Log.i("Usuario", "Fin partida");
            if (resultCode == -1) {
                //Se ha pulsado en cancelar el juego o pulsar boton hacia atras
                Log.i("Usuario", "Cancelado");
            } else if (resultCode == 1) {
                //Se ha terminado el juego
                String puntuacion = data.getStringExtra("puntuacion");

                if (Integer.parseInt(puntuacion)>0){
                    //Si la puntuacion es mayor a cero
                    Log.i("Usuario", "Puntuacion " + puntuacion);

                    //Inicializamos base de datos
                    BD GestorBD = new BD (this, "NombreBD", null, 1);
                    SQLiteDatabase bd = GestorBD.getWritableDatabase();

                    //Comprobamos si ese usuario tiene esa puntuacion registrada
                    String consulta = "SELECT * FROM Puntuaciones WHERE nombre='"+ usuario +"' AND puntos=" + puntuacion;
                    Cursor cu = bd.rawQuery(consulta,null);

                    if(cu.getCount()>0){
                        //Existe registrado esa puntuacion para el usuario:
                        Log.i("Usuario", "Existe Puntuacion");
                    } else {
                        //No esta registrada
                        Log.i("Usuario", "Registrar Puntuacion");

                        //Realizar insercion en tabla
                        ContentValues contenido = new ContentValues();
                        contenido.put("Nombre", usuario);
                        contenido.put("Puntos", Integer.parseInt(puntuacion));
                        long resultado = bd.insert("Puntuaciones", null, contenido);

                        if (resultado == -1) {
                            //ERROR DE INSERCION
                            Toast.makeText(getApplicationContext(), getString(R.string.falloGuardado), Toast.LENGTH_LONG).show();
                        } else {
                            //Se ha realizado exitosamente
                            Toast.makeText(getApplicationContext(), getString(R.string.exitoGuardado), Toast.LENGTH_LONG).show();

                            //Actualizar rankings
                            finish();
                            startActivity(getIntent());
                        }
                    }
                } else {
                    //Si la puntuacion es de cero
                    Log.i("Usuario", "Cero puntos");
                }

            }
        }
    }

    @Override
    public boolean onKeyDown (int key, KeyEvent event) {
        if (key == event.KEYCODE_BACK){
            //Al pulsar la tecla de volver a la actividad anterior, aparecera el dialogo de abandonarJuego
            usuario = null;
            finish();
        }
        return super.onKeyDown(key,event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Añadir menu en la barra de accion
        getMenuInflater().inflate(R.menu.definicion_usuario, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id= item.getItemId();
        //Acciones de la barra de accion
        switch (id){
            case R.id.cerraSesion:{
                //Boton de cerrar sesion
                usuario = null;
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void actualizarIdioma(Locale nuevoIdiomaSel){
        Locale.setDefault(nuevoIdiomaSel);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevoIdiomaSel);
        configuration.setLayoutDirection(nuevoIdiomaSel);
        Context context = getBaseContext().createConfigurationContext(configuration);getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        finish();
        startActivity(getIntent());
    }

    //Al pulsar un elemento de la lista de ranking global, se ejecuta este metodo
    public void seleccionarElemento(String elemento){
        //Comprobar la rotacion del movil
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            //Si esta en horizontal, el otro FRAGMENT esta tambien en la pantalla
            Ranking_personal_fragment second=(Ranking_personal_fragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);
            second.setmethod(elemento);
        } else{
            //Si esta en vertical, el otro FRAGMENT no existe en la pantalla, por lo que se lanza una nueva actividad (RankingPersonal)
            Intent i= new Intent(this, RankingPersonal.class);
            i.putExtra("contenido",elemento);
            startActivity(i);
        }
    }
}