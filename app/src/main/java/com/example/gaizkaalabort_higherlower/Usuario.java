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

public class Usuario extends AppCompatActivity implements Ranking_global_fragment.listenerDelFragment{

    private static String idioma;
    private static String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        setSupportActionBar(findViewById(R.id.mytoolbar));

        Bundle extras = getIntent().getExtras();
        if (extras != null && idioma==null) {
            idioma = extras.getString("idiomaLogin");
        }
        if (extras != null) {
            usuario = extras.getString("usuario");
        }
        
        TextView usuarioIntroducido = findViewById(R.id.nombre);
        String texto = getString(R.string.usuario) + " "+usuario;
        usuarioIntroducido.setText(texto);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //Almacenamos idioma de la aplicacion
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

        usuario = savedInstanceState.getString("usuario");
        TextView usuarioIntroducido = findViewById(R.id.nombre);
        String texto = getString(R.string.usuario) + " "+usuario;
        usuarioIntroducido.setText(texto);

        Locale nuevaloc = new Locale(idioma);
        actualizarIdioma(nuevaloc);
    }

    public void jugar (View view){
        Intent acceso = new Intent (this, Juego.class);
        acceso.putExtra("idiomaUsuario",idioma);
        startActivityForResult(acceso,333);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 333) {
            Log.i("Usuario", "Fin partida");
            if (resultCode == -1) {
                Log.i("Usuario", "Cancelado");
            } else if (resultCode == 1) {
                String puntuacion = data.getStringExtra("puntuacion");

                if (Integer.parseInt(puntuacion)>0){
                    Log.i("Usuario", "Puntuacion " + puntuacion);

                    BD GestorBD = new BD (this, "NombreBD", null, 1);
                    SQLiteDatabase bd = GestorBD.getWritableDatabase();

                    String consulta = "SELECT * FROM Puntuaciones WHERE nombre='"+ usuario +"' AND puntos=" + puntuacion;
                    Cursor cu = bd.rawQuery(consulta,null);

                    if(cu.getCount()>0){
                        //Existe registrado es apuntuacion para el usuario:
                        Log.i("Usuario", "Existe Puntuacion");
                    } else {
                        Log.i("Usuario", "Registrar Puntuacion");
                        ContentValues contenido = new ContentValues();
                        contenido.put("Nombre", usuario);
                        contenido.put("Puntos", Integer.parseInt(puntuacion));
                        long resultado = bd.insert("Puntuaciones", null, contenido);

                        if (resultado == -1) {
                            //ERROR DE INSERCION
                            Toast.makeText(getApplicationContext(), getString(R.string.falloGuardado), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.exitoGuardado), Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(getIntent());
                        }
                    }
                } else {
                    Log.i("Usuario", "Cero puntos");
                }

            }
        }
    }

    @Override
    public boolean onKeyDown (int key, KeyEvent event) {
        if (key == event.KEYCODE_BACK){
            usuario = null;
            finish();
        }
        return super.onKeyDown(key,event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.definicion_usuario, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id= item.getItemId();
        switch (id){
            case R.id.cerraSesion:{
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

    public void seleccionarElemento(String elemento){
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            //EL OTRO FRAGMENT EXISTE
            Ranking_personal_fragment second=(Ranking_personal_fragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);
            second.setmethod(elemento);
        } else{
            //EL OTRO FRAGMENT NO EXISTE, HAY QUE LANZAR LA ACTIVIDAD QUE LO CONTIENE
            Intent i= new Intent(this, RankingPersonal.class);
            i.putExtra("contenido",elemento);
            startActivity(i);
        }
    }
}