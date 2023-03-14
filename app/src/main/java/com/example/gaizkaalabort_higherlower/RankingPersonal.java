package com.example.gaizkaalabort_higherlower;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

//Clase para la pantalla en caso de tener el movil en vertical y pulsar sobre un item del fragmento de ranking global en Usuario
public class RankingPersonal extends AppCompatActivity implements Ranking_personal_fragment.listenerDelFragment{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_personal);

        //Recogida de variable pasada por intent y actualizar fragmento de ranking personal
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Ranking_personal_fragment second=(Ranking_personal_fragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);
            second.setmethod(extras.getString("contenido"));
        }
    }

    //Accion de salir de pantalla en caso de pulsar cerrar
    public void salir(View view){
        finish();
    }

    @Override
    public void borradoElemento(String txtUsuario, int numPuntos) {
        BD GestorBD = new BD (this, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorBD.getWritableDatabase();
        bd.execSQL("DELETE FROM Puntuaciones WHERE nombre='"+ txtUsuario +"' AND puntos="+numPuntos);
    }
}