package com.example.gaizkaalabort_higherlower;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

//Clase de las base de datos locales del programa
public class BD extends SQLiteOpenHelper {

    public BD(@Nullable Context context, @Nullable String name,
              @Nullable SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Tabla de usuarios de la aplicacion.
        sqLiteDatabase.execSQL("CREATE TABLE Usuarios ('Nombre' VARCHAR(50) PRIMARY KEY NOT NULL,'Contraseña' VARCHAR(50) NOT NULL)");
        //Tabla de puntuaciones
        sqLiteDatabase.execSQL("CREATE TABLE Puntuaciones ('Nombre' VARCHAR(50) NOT NULL,'Puntos' NUMBER NOT NULL, PRIMARY KEY ('Nombre','Puntos'))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Usuarios");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Puntuaciones");
        onCreate(sqLiteDatabase);
    }
}
