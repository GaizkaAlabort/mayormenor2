package com.example.gaizkaalabort_higherlower;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class RankingPersonal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_personal);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Ranking_personal_fragment second=(Ranking_personal_fragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);
            second.setmethod(extras.getString("contenido"));
        }
    }

    public void salir(View view){
        finish();
    }
}