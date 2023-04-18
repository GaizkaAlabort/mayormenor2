package com.example.gaizkaalabort_higherlower;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

//Clase de la pantalla registro
public class Registro extends AppCompatActivity {

    private static String idioma;
    EditText usuarioIntroducido;
    static String usuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Recogida de idioma
        Bundle extras = getIntent().getExtras();
        if (extras != null && idioma!=extras.getString("idiomaLogin")) {
            idioma = extras.getString("idiomaLogin");
            usuario = "";
            Log.i("Registro", idioma);
        }

        //Recogida de usuario en case de rotar o dejar en segundo plano
        usuarioIntroducido = findViewById(R.id.editTextUsuario);
        usuarioIntroducido.setText(usuario);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //Almacenamos idioma de la aplicacion
        savedInstanceState.putString("idioma", idioma);
        Log.i("Registro save", idioma);

        //Almacenamos el usuario (en caso de haberlo introducido)
        usuario = usuarioIntroducido.getText().toString();
        savedInstanceState.putString("usuario", usuario);
        Log.i("Registro save", usuario);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Recuperamos idioma de la aplicacion y usuario a registrar
        idioma = savedInstanceState.getString("idioma");
        Log.i("Registro recuperar", idioma);

        usuario = savedInstanceState.getString("usuario");
        usuarioIntroducido.setText(usuario);
        Log.i("Registro recuperar", usuario);

        //Actualizamos idioma
        Locale nuevaloc = new Locale(idioma);
        actualizarIdioma(nuevaloc);
    }

    public void registro (View view){
        Log.i("Registro", " Ha pulsado registrarse ");
        //Recogida de variables de los campos
        EditText nombre = findViewById(R.id.editTextUsuario);
        String usuario = nombre.getText().toString();
        EditText contra = findViewById(R.id.editTextContrasena);
        String contraseña = contra.getText().toString();
        EditText contra2 = findViewById(R.id.editTextContrasena2);
        String reContraseña = contra2.getText().toString();

        if (usuario.equals("")||contraseña.equals("")||reContraseña.equals(""))
        {
            //Si no se introduce nada:
            Toast.makeText(getApplicationContext(),getString(R.string.registroVacio),Toast.LENGTH_LONG).show();
        } else {

            if(contraseña.equals(reContraseña)){
                //Si coinciden las contraseñas:

                Data datos = new Data.Builder()
                        .putString("Nombre",usuario)
                        .putString("Contraseña",contraseña)
                        .putString("accion","crear")
                        .build();

                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWS.class)
                        .setInputData(datos)
                        .build();

                WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                        .observe(this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if(workInfo != null && workInfo.getState().isFinished()){
                                    Log.i("REGISTRO JSON", "Fin crear");
                                    Log.i("REGISTRO JSON", "¿Correcto? " + workInfo.getOutputData().getBoolean("valor", false) + ", " + workInfo.getOutputData().getString("texto"));
                                    Boolean resultado = workInfo.getOutputData().getBoolean("valor", false);
                                    String codigo = workInfo.getOutputData().getString("texto");

                                    comprobador(resultado, codigo);

                                }
                            }
                        });
                WorkManager.getInstance(this).enqueue(otwr);

            } else {
                //Si no coinciden las contraseñas:
                //  - Vaciamos campos de contraseña
                contra.setText("");
                contra2.setText("");
                //  - Informamos error
                Toast.makeText(getApplicationContext(),getString(R.string.registroContraNoCoincide),Toast.LENGTH_LONG).show();
            }
        }
    }

    public void comprobador(Boolean resultado, String texto){
        EditText nombre = findViewById(R.id.editTextUsuario);
        EditText contra = findViewById(R.id.editTextContrasena);
        EditText contra2 = findViewById(R.id.editTextContrasena2);

        if (resultado){
            Log.i("Registro", "Registro correcto");
            //Cerrar intent como resultado de registrarse correctamente
            Intent intent = new Intent();
            intent.putExtra("idiomaRegistro",idioma);
            setResult(-1,intent);
            finish();
        } else {
            Log.i("Registro", "ALGO MAL");

            if(Objects.equals(texto, "registroFallo")){
                Toast.makeText(getApplicationContext(),getString(R.string.registroFallo),Toast.LENGTH_LONG).show();
            } else if(Objects.equals(texto, "registroNombreExiste")){
                //Si existe el nombre de usuario introducido:
                //  - Vaciamos campos
                nombre.setText("");
                contra.setText("");
                contra2.setText("");
                //  - Informamos error
                Toast.makeText(getApplicationContext(),getString(R.string.registroNombreExiste),Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),texto,Toast.LENGTH_LONG).show();
            }
        }
    }

    //Boton de cerrar para volver a la pantalla de login
    public void cancelar (View view){
        Intent intent = new Intent();
        setResult(1,intent);
        finish();
    }

    //Se actualiza el idioma al llegar a la pantalla de registro
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