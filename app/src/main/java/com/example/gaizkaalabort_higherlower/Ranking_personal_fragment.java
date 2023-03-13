package com.example.gaizkaalabort_higherlower;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_ranking_personal, container, false);

        textView =fragmentView.findViewById(R.id.usuarioElegido);

        listView=fragmentView.findViewById(R.id.listview_personal);

        adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,puntuaciones);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                puntuaciones.remove(i);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        listView.setAdapter(adapter);

        return fragmentView;
    }

    public void setmethod(String textstr){
        textView.setText(getString(R.string.userElegido) + " " + textstr);

        BD GestorBD = new BD (getContext(), "NombreBD", null, 1);
        SQLiteDatabase db = GestorBD.getWritableDatabase();

        String consultaRP = "select puntos from Puntuaciones Where nombre='" + textstr + "' order by puntos desc";
        Cursor c = db.rawQuery(consultaRP, null);
        if(c.getCount()>0) {
            if (c.moveToFirst()) {
                do {
                    // Passing values
                    int punto = c.getInt(0);
                    // Do something Here with values
                    puntuaciones.add(String.valueOf(punto));
                } while (c.moveToNext());
            }
        }
        c.close();
        db.close();
        adapter.notifyDataSetChanged();
    }
}