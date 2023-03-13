package com.example.gaizkaalabort_higherlower;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

//Adaptador para la listview de Ranking Global
public class AdaptadorListView extends BaseAdapter {

    private Context contexto;
    private LayoutInflater inflater;
    private ArrayList<String> datos;
    private int[] imagenes;
    private ArrayList<Integer> puntuaciones;

    //Constructor
    public AdaptadorListView(Context pcontext, ArrayList<String> pdatos, int[] pimagenes, ArrayList<Integer> puntos)
    {
        contexto = pcontext;
        datos = pdatos;
        imagenes=pimagenes;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        puntuaciones = puntos;
    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public Object getItem(int i) {
        return datos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //Actualizacion de cada item de la list view.
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=inflater.inflate(R.layout.item_ranking_global,null);
        TextView nombre = (TextView) view.findViewById(R.id.title);
        ImageView img = (ImageView) view.findViewById(R.id.imagen);
        TextView desc = (TextView) view.findViewById(R.id.descripcion);

        nombre.setText(datos.get(i));
        if (i<3){
            img.setImageResource(imagenes[i]);
        } else {
            img.setImageResource(imagenes[3]);
        }
        desc.setText(String.valueOf(puntuaciones.get(i)));

        return view;
    }
}
