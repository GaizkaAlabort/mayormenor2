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


public class Ranking_global_fragment extends Fragment {

    View fragmentView;
    AdaptadorListView adapter;
    ListView listView;
    ArrayList<String> countryname= new ArrayList<String>();
    int[] imagen = new int[] {R.drawable.corona,R.drawable.copa,R.drawable.medalla,R.drawable.contact};
    ArrayList<Integer> puntuaciones= new ArrayList<>();

    public interface listenerDelFragment{

        void seleccionarElemento(String elemento);
    }
    private listenerDelFragment elListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView= inflater.inflate(R.layout.fragment_ranking_global, container, false);

        listView=fragmentView.findViewById(R.id.listview_item);

        rellenar();

        adapter= new AdaptadorListView(getActivity().getApplicationContext(),countryname,imagen,puntuaciones);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                elListener.seleccionarElemento(countryname.get(i));
            }
        });

        return fragmentView;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            elListener=(listenerDelFragment) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString()+ "debe implementar listenerDelFragment");
        }
    }

    public void rellenar(){
        BD GestorBD = new BD (getContext(), "NombreBD", null, 1);
        SQLiteDatabase db = GestorBD.getWritableDatabase();
        Cursor c = db.rawQuery("select nombre, puntos from Puntuaciones as pul Where puntos iN (select max(puntos) from Puntuaciones where nombre=pul.Nombre) order by pul.Puntos desc", null);
        if(c.getCount()>0) {
            if (c.moveToFirst()) {
                do {
                    // Passing values
                    String nombre = c.getString(0);
                    int punto = c.getInt(1);
                    // Do something Here with values
                    countryname.add(nombre);
                    puntuaciones.add(punto);
                } while (c.moveToNext());
            }
        }
        c.close();
        db.close();
    }

}