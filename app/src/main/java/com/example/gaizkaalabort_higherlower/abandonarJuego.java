package com.example.gaizkaalabort_higherlower;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

//Dialogo para la hora de salir de la actividad del juego, poder cancelar salida
public class abandonarJuego extends DialogFragment {

    public interface abandonarListener {
        //Metodo que se implementa en usuario (El cual hace la llamada al dialogo)
        public void salir(DialogFragment dialog);
    }

    @Override
    //Se crea el dialogo
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        //Se crea el dialogo donde se debera elegir si se quiere salir o no
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.abandono);

        builder.setPositiveButton(R.string.positivo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                abandonarListener listener = (abandonarJuego.abandonarListener) getContext();
                listener.salir(abandonarJuego.this);
            }
        });//Boton para salir

        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });//Boton para cancelar salida y mantenerse en partida

        return builder.create();
    }
}