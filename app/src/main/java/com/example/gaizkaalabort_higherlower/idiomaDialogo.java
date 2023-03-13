package com.example.gaizkaalabort_higherlower;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

//Dialogo para elegir idioma de la aplicacion
public class idiomaDialogo extends DialogFragment {
    public String seleccion;

    public interface idiomaListener {
        //Metodo que de cambio cambio de idioma
        public void seleccionarIdioma(DialogFragment dialog);
    }
    idiomaListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        //Se crea el dialogo donde se debera elegir entre Español e Ingles
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.selIdioma));

        //Lista de idiomas de la aplicacion, junto a su forma de seleccion
        final CharSequence[] opciones = {"Español", "English"};
        builder.setSingleChoiceItems(opciones, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                seleccion = opciones[i].toString();
            }
        });

        //Boton de confirmacion de eleccion
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener = (idiomaListener) getContext();
                //Comprobacion de eleccion de idioma
                if(seleccion== null){
                    int tiempo = Toast.LENGTH_LONG;
                    Toast error = Toast.makeText(getContext(),getString(R.string.errorIdioma),tiempo);
                    error.show();
                    dismiss();
                } else {
                    listener.seleccionarIdioma(idiomaDialogo.this);
                }
            }
        });

        return builder.create();
    }
}
