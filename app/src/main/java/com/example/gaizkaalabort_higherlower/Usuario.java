package com.example.gaizkaalabort_higherlower;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//Clase de pantalla usuario despues de loguearse
public class Usuario extends AppCompatActivity implements Ranking_global_fragment.listenerDelFragment,
                                                        Ranking_personal_fragment.listenerDelFragment{

    private static String idioma;
    private static String usuario;
    private static final String CANAL_ID = "101";
    private static String enlace = "";
    SQLiteDatabase bd;
    String currentPhotoPath;
    int COD_CAMARA = 67;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        storageReference = FirebaseStorage.getInstance().getReference();

        //Cargar barra de accion en la parte superior y añadir accion
        setSupportActionBar(findViewById(R.id.mytoolbar));

        //Recogida de variables pasadas como extra
        Bundle extras = getIntent().getExtras();
        if (extras != null && idioma!=extras.getString("idiomaLogin")) {
            idioma = extras.getString("idiomaLogin");
        }
        if (extras != null) {
            usuario = extras.getString("usuario");
        }

        //Actualizar variable usuario para mostrar por pantalla y guardarla al girar
        TextView usuarioIntroducido = findViewById(R.id.nombre);
        String texto = getString(R.string.usuario) + " "+usuario;
        usuarioIntroducido.setText(texto);

        //Inicializamos base de datos
        BD GestorBD = new BD (this, "NombreBD", null, 1);
        bd = GestorBD.getWritableDatabase();

        //Inicializar canal de notificacion
        //crearCanalNotificacion();
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

    //Boton "Compartir" si el usuario tiene la app de twitter se le llevara a la aplicacion con el hashtag higherLower,
    // sino se le abrira el twitter de la web.
    public void compartir (View view){
        Intent browserIntent;
        try {
            // get the Twitter app if possible
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://search?query=%23HigherLower"));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(browserIntent);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com")));
        }
    }

    /* CODIGO para obtener imagen y subirla a firebase obtenida de
     * una serie de videos.
     * https://www.youtube.com/watch?v=s1aOlr3vbbk&list=PLlGT4GXi8_8eopz0Gjkh40GG6O5KhL1V1&index=1&ab_channel=SmallAcademy
     */
    public void nueva_foto (View view){
        //Metodo ejecutado al pulsar en cambiar fondo
        pedirPermiso();
    }

    public void pedirPermiso(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.CAMERA}, COD_CAMARA);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCod, @NonNull String[] permisos, @NonNull int[] grantResults) {
        Log.i("PRUE", String.valueOf(requestCod));
        if (requestCod == COD_CAMARA) {
            Log.i("PRUE", grantResults.length + ", ," + grantResults[0]);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, getString(R.string.permisosCamara), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private File createImageFile() throws IOException {
        // Crear nombre para la imagen
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        Log.i("PRUE",imageFileName);
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Guarda el archivo
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.gaizkaalabort_higherlower.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 555);
            }
        }
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

                    //Comprobamos si ese usuario tiene esa puntuacion registrada
                    String consulta = "SELECT * FROM Puntuaciones WHERE nombre='"+ usuario +"' AND puntos=" + puntuacion;
                    Cursor cu = bd.rawQuery(consulta,null);

                    if(cu.getCount()>0){
                        //Existe registrado esa puntuacion para el usuario:
                        Log.i("Usuario", "Existe Puntuacion");
                    } else {
                        //No esta registrada
                        Log.i("Usuario", "Registrar Puntuacion");

                        //Comprobar si es el mayor
                        String consultaMayor = "SELECT * FROM Puntuaciones WHERE puntos>=" + puntuacion;
                        Cursor ma = bd.rawQuery(consultaMayor,null);
                        //Al acabar la partida si el resultado es el mayor comparado con los demas, entonces se manda notificacion.
                        if(ma.getCount()>0){
                            Log.i("RECORD", "Existen mejores puntuaciones");
                        } else {
                            Log.i("RECORD", "Nuevo Record!!");
                            Data datos = new Data.Builder()
                                    .putString("Nombre",usuario)
                                    .putString("Contraseña",puntuacion)
                                    .putString("accion","notificacion")
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
                                            }
                                        }
                                    });
                            WorkManager.getInstance(this).enqueue(otwr);
                        }

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
        } else if (requestCode == 444) {
            //Actualizar rankings, tras llegar de ranking personal
            finish();
            startActivity(getIntent());
        } else if (requestCode == 555) {
            if(resultCode == Activity.RESULT_OK){
                File imagen = new File(currentPhotoPath);
                ImageView elImageView = findViewById(R.id.prueba);
                elImageView.setImageURI(Uri.fromFile(imagen));
                Log.i("URL","url de la imagen: "+ Uri.fromFile(imagen));

                Intent mediaScan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScan.setData(Uri.fromFile(imagen));
                this.sendBroadcast(mediaScan);

                StorageReference image = storageReference.child("imagenes/" + imagen.getName());
                image.putFile(Uri.fromFile(imagen)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("Subida Firebase","El url de la imagen es " + uri.toString());
                                enlace = uri.toString();
                            }
                        });

                        Toast.makeText(Usuario.this,getString(R.string.corrFirebase),Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Usuario.this,getString(R.string.errFirebase),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void goToUrl (View view) {
        if (!enlace.equals("")){
            Uri uriUrl = Uri.parse(enlace);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            launchBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(launchBrowser);
        } else {
            Log.i("ENLACE","La imagen no tiene enlace");
        }
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
            startActivityForResult(i,444);
        }
    }

    @Override
    public void borradoElemento(String txtUsuario, int numPuntos) {
        bd.execSQL("DELETE FROM Puntuaciones WHERE nombre='"+ txtUsuario +"' AND puntos="+numPuntos);
    }
}