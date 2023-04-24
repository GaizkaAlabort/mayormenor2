package com.example.gaizkaalabort_higherlower;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;
import java.util.Objects;

//Clase de la pantalla de logueo
public class Login extends AppCompatActivity
                          implements idiomaDialogo.idiomaListener{
    private static String idioma = "";
    private static final String CANAL_ID = "101";
    EditText usuarioIntroducido;
    static String usuario = "";
    idiomaDialogo nuevoIdioma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Recogida de variable en caso de rotar o estar en segundo plano
        usuarioIntroducido = findViewById(R.id.editTextUsuario);
        usuarioIntroducido.setText(usuario);

        crearCanalNotificacion();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //Almacenamos idioma de la aplicacion
            //En caso de no haber elegido idioma, se vera en que idioma estaba para asignarlo
        recogidaIdioma();
        savedInstanceState.putString("idioma", idioma);

        //Almacenamos usuario introducido
        usuario = usuarioIntroducido.getText().toString();
        savedInstanceState.putString("usuario", usuario);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Recuperamos idioma de la aplicacion y usuario introducido (en caso de haberlo introducido)
        idioma = savedInstanceState.getString("idioma");

        usuario = savedInstanceState.getString("usuario");
        usuarioIntroducido.setText(usuario);

        Locale nuevaloc = new Locale(idioma);
        actualizarIdioma(nuevaloc);
    }

    public void loguearse (View view){
        /*BD GestorBD = new BD (this, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorBD.getReadableDatabase();*/

        //Se recoge nombre usuario
        EditText nombre = findViewById(R.id.editTextUsuario);
        String usuarioIntro = String.valueOf(nombre.getText());
        Log.i("Login", "Usuario que quiere acceder: " + usuarioIntro);

        //y contraseña introducida
        EditText contra = findViewById(R.id.editTextContrasena);
        String contraseña = String.valueOf(contra.getText());

        /*String[] argumentos = new String[] {usuario};
        String[] campos = new String[] {"Contraseña"};
        Cursor cu = bd.query("Usuarios", campos,"Nombre=?",argumentos,null,null,null);
        if(cu.getCount()>0){
            //SI existe usuario
            cu.moveToNext();
            String con = cu.getString(0);
        }*/

        Data datos = new Data.Builder()
                .putString("Nombre",usuarioIntro)
                .putString("Contraseña",contraseña)
                .putString("accion","acceso")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWS.class)
                                        .setInputData(datos)
                                        .build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            Log.i("LOGIN JSON", "Fin acceso");
                            Log.i("LOGIN JSON", "¿Correcto? " + workInfo.getOutputData().getBoolean("valor", false) + ", " + workInfo.getOutputData().getString("texto"));
                            Boolean resultado = workInfo.getOutputData().getBoolean("valor", false);
                            String codigo = workInfo.getOutputData().getString("texto");

                            comprobador(resultado, codigo, usuarioIntro);

                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    public void comprobador(Boolean resultado, String texto, String usuarioIntroducido){
        EditText contra = findViewById(R.id.editTextContrasena);
        EditText nombre = findViewById(R.id.editTextUsuario);

        if (resultado){
            Log.i("Login", "Login correcto");
            Intent acceso = new Intent (this, Usuario.class);
            acceso.putExtra("idiomaLogin",idioma);
            acceso.putExtra("usuario",usuarioIntroducido);
            nombre.setText("");
            contra.setText("");
            getToken();
            FirebaseMessaging.getInstance().subscribeToTopic("ranking").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"Se ha suscrito al topic de ranking.",Toast.LENGTH_LONG).show();
                }
            });
            startActivityForResult(acceso,222);
        } else {
            Log.i("Login", "ALGO MAL");

            if(Objects.equals(texto, "conIncorrecta")){
                contra.setText("");
                Toast.makeText(getApplicationContext(),getString(R.string.conIncorrecta),Toast.LENGTH_LONG).show();
            } else if(Objects.equals(texto, "noExisteUsuario")){
                nombre.setText("");
                contra.setText("");
                Toast.makeText(getApplicationContext(),getString(R.string.noExisteUsuario),Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),texto,Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.d("Token","Error al obtener token");
                }

                String token=task.getResult();
                Log.d("Token", token);
            }
        });
    }

    private void crearCanalNotificacion(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "canalNotificacionFirebase";
            String descript = "Recibido notificacion de firebase";
            int importance= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel(CANAL_ID,name,importance);
            canal.setDescription(descript);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal);

        }
    }

    public void registrarse (View view){
        recogidaIdioma();

        //Si se pulsa registrar, se recoge idioma y se pasa a la respectiva pantalla
        Log.i("Login", "Idioma:" + idioma);
        Intent registro = new Intent (this, Registro.class);
        registro.putExtra("idiomaLogin",idioma);
        startActivityForResult(registro,111);
    }

    //Recogida de idioma en caso de no elegir idioma
    public void recogidaIdioma(){
        if (idioma.equals("")){
            TextView lengua = findViewById(R.id.usuario);
            if (lengua.getText().toString().equals("User:")){
                idioma = "en";
            } else {
                idioma = "es";
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111){
            //Se viene de registro
            Log.i("Login", "Resultado registro ");

            if (resultCode == -1){
                //Se creo la cuenta
                Log.i("Login", "Fin registro ");
                Toast.makeText(getApplicationContext(),getString(R.string.exitoRegistro),Toast.LENGTH_LONG).show();
            } else if (resultCode == 1){
                //Se cancelo el registro
                Log.i("Login", "Fin registro, cancelado registro");
            }
        } else if (requestCode == 222){
            //Se viene de cerrar sesion
            Log.i("Login", "Cierre sesion");
        }
    }

    public void cambiarIdioma (View view){
        nuevoIdioma = new idiomaDialogo();
        nuevoIdioma.show(getSupportFragmentManager(),"idioma");
    }

    //Dialogo al pulsar boton Idioma
    public void seleccionarIdioma(DialogFragment dialog){
        Log.i("Login", "Idioma seleccionado:" + nuevoIdioma.seleccion);

        Locale nuevoIdiomaSel = null;

        //Opciones de idiomas
        if (nuevoIdioma.seleccion == getString(R.string.esp)){
           idioma = "es";
           nuevoIdiomaSel = new Locale("es");
        } else if (nuevoIdioma.seleccion == getString(R.string.eng)){
           idioma = "en";
           nuevoIdiomaSel = new Locale("en");
        }
        nuevoIdioma.dismiss();

        assert nuevoIdiomaSel != null;
        actualizarIdioma(nuevoIdiomaSel);
    }

    //Funcion para actualizar el idioma, sirve para simplificar codigo y no repetir.
    public void actualizarIdioma(Locale nuevoIdiomaSel){
        Locale.setDefault(nuevoIdiomaSel);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevoIdiomaSel);
        configuration.setLayoutDirection(nuevoIdiomaSel);
        Context context = getBaseContext().createConfigurationContext(configuration);getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        finish();
        startActivity(getIntent());
    }
}