package com.example.gaizkaalabort_higherlower;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Ranking_personal_fragment extends Fragment {
    TextView textView;
    View fragmentView;
    ArrayAdapter adapter;
    ListView listView;
    ArrayList<String> puntuaciones= new ArrayList<>();
    String usuario;
    public interface listenerDelFragment{
        //Metodo que se implementa en actividad que cuente con este fragment, para realizar accione en caso de seleccionar una fila
        void borradoElemento(String txtUsuario,int numPuntos);
    }
    private listenerDelFragment elListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Recogida de fragmento de ranking personal
        fragmentView = inflater.inflate(R.layout.fragment_ranking_personal, container, false);

        //Recogida de texto para mostrar usuario seleccionado
        textView =fragmentView.findViewById(R.id.usuarioElegido);

        //Recoger lista del fragmento
        listView=fragmentView.findViewById(R.id.listview_personal);

        //Adaptar los arrays a la lista simple
        adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,puntuaciones);

        //Añadir eliminacion de la fila en la base de datos al mantener seleccionado una fila
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                elListener.borradoElemento(usuario, Integer.parseInt(puntuaciones.get(i)));
                puntuaciones.remove(i);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        listView.setAdapter(adapter);

        return fragmentView;
    }

    //Error para controlar que se implemente el metodo de la interfaz
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            elListener=(Ranking_personal_fragment.listenerDelFragment) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString()+ "debe implementar listenerDelFragment");
        }
    }

    //Metodo para actualizar el fragmento en base al usuario seleccionado, pasado como parametro
    public void setmethod(String textstr){
        //Reiniciar puntuaciones
        puntuaciones.clear();

        //Actualizar campo de texto de usuario elegido
        usuario = textstr;
        textView.setText(getString(R.string.userElegido) + " " + textstr);

        //Inicializar base de datos
        BD GestorBD = new BD (getContext(), "NombreBD", null, 1);
        SQLiteDatabase db = GestorBD.getWritableDatabase();

        //Cursor para seleccionar los puntos de dicho usuario
        String consultaRP = "select puntos from Puntuaciones Where nombre='" + textstr + "' order by puntos desc";
        Cursor c = db.rawQuery(consultaRP, null);

        if(c.getCount()>0) {
            //Si tiene varios resultados
            if (c.moveToFirst()) {
                do {
                    // Por cada uno: obtenemos valor
                    int punto = c.getInt(0);
                    // Se añade al array que se mostrara en la lista
                    puntuaciones.add(String.valueOf(punto));
                } while (c.moveToNext());
            }
        }

        //Cerrar cursor y base de datos
        c.close();
        db.close();

        //Actualizar lista del fragmento con los cambio del array
        adapter.notifyDataSetChanged();
    }
}