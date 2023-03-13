package com.example.gaizkaalabort_higherlower;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

//Fragment de ranking global
public class Ranking_global_fragment extends Fragment {

    View fragmentView;
    AdaptadorListView adapter;
    ListView listView;
    ArrayList<String> usuarios= new ArrayList<String>();
    int[] imagen = new int[] {R.drawable.corona,R.drawable.copa,R.drawable.medalla,R.drawable.contact};
    ArrayList<Integer> puntuaciones= new ArrayList<>();

    public interface listenerDelFragment{
        //Metodo que se implementa en actividad que cuente con este fragment, para realizar accione en caso de seleccionar una fila
        void seleccionarElemento(String elemento);
    }
    private listenerDelFragment elListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Recoger layout del fragmento de ranking global
        fragmentView= inflater.inflate(R.layout.fragment_ranking_global, container, false);

        //Recoger lista del fragmento
        listView=fragmentView.findViewById(R.id.listview_item);

        //Rellenar los arrays, usuarios y puntuacion, de las mejores puntuaciones de cada jugador
        rellenar();

        //Adaptar los arrays a la lista con filas tipo items_ranking_global
        adapter= new AdaptadorListView(getActivity().getApplicationContext(),usuarios,imagen,puntuaciones);

        listView.setAdapter(adapter);

        //Realizar metodo del interface en caso de hacer click en una fila
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                elListener.seleccionarElemento(usuarios.get(i));
            }
        });

        return fragmentView;
    }

    //Error para controlar que se implemente el metodo de la interfaz
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            elListener=(listenerDelFragment) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString()+ "debe implementar listenerDelFragment");
        }
    }

    //Metodo de rellenado de arrays para mostrar en lista
    public void rellenar(){
        //Inicializar Base de Datos
        BD GestorBD = new BD (getContext(), "NombreBD", null, 1);
        SQLiteDatabase db = GestorBD.getWritableDatabase();

        //Recoger cursor con consulta
        Cursor c = db.rawQuery("select nombre, puntos from Puntuaciones as pul Where puntos iN (select max(puntos) from Puntuaciones where nombre=pul.Nombre) order by pul.Puntos desc", null);

        if(c.getCount()>0) {
            //En caso de encontrar valores en consulta:
            if (c.moveToFirst()) {
                do {
                    // Para cada uno: recoger valores
                    String nombre = c.getString(0);
                    int punto = c.getInt(1);
                    // Añadirlos a los arrays correspondientes
                    usuarios.add(nombre);
                    puntuaciones.add(punto);
                } while (c.moveToNext());
            }
        }

        //Cerrar base de datos y cursor
        c.close();
        db.close();
    }

}