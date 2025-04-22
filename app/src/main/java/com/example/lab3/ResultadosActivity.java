package com.example.lab3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ResultadosActivity extends AppCompatActivity {


    //----------------------------------------------



    // La app usa hilos internamente gracias a Volley.

    // Además, se usa CountDownTimer, que también funciona en segundo plano con callbacks en el hilo principal.



    //----------------------------------------------
    private TextView txtCorrectas, txtIncorrectas, txtNoRespondidas;
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        txtCorrectas = findViewById(R.id.txtCorrectas);
        txtIncorrectas = findViewById(R.id.txtIncorrectas);
        txtNoRespondidas = findViewById(R.id.txtNoRespondidas);
        btnVolver = findViewById(R.id.btnVolver);

        // Obtener datos del intent
        int correctas = getIntent().getIntExtra("correctas", 0);
        int incorrectas = getIntent().getIntExtra("incorrectas", 0);
        int noRespondidas = getIntent().getIntExtra("no_respondidas", 0);

        txtCorrectas.setText(String.valueOf(correctas));
        txtIncorrectas.setText(String.valueOf(incorrectas));
        txtNoRespondidas.setText(String.valueOf(noRespondidas));

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(ResultadosActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
