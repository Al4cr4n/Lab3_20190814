package com.example.lab3;


//----------------------------------------------



// La app usa hilos internamente gracias a Volley.

// Además, se usa CountDownTimer, que también funciona en segundo plano con callbacks en el hilo principal.



//----------------------------------------------



import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerCategoria, spinnerDificultad;
    EditText editCantidad;
    Button btnComprobar, btnComenzar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerDificultad = findViewById(R.id.spinnerDificultad);
        editCantidad = findViewById(R.id.editCantidad);
        btnComprobar = findViewById(R.id.btnComprobar);
        btnComenzar = findViewById(R.id.btnComenzar);
        btnComenzar.setEnabled(false);

        String[] categorias = {"Cultura General", "Libros", "Películas", "Música", "Computación", "Matemática", "Deportes", "Historia"};
        String[] dificultades = {"Fácil", "Medio", "Difícil"};

        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        ArrayAdapter<String> adapterDificultad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dificultades);

        spinnerCategoria.setAdapter(adapterCategorias);
        spinnerDificultad.setAdapter(adapterDificultad);

        // Botón Comprobar Conexión
        btnComprobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cantidadStr = editCantidad.getText().toString().trim();

                if (cantidadStr.isEmpty() || Integer.parseInt(cantidadStr) <= 0) {
                    Toast.makeText(MainActivity.this, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (spinnerCategoria.getSelectedItem() == null || spinnerDificultad.getSelectedItem() == null) {
                    Toast.makeText(MainActivity.this, "Seleccione categoría y dificultad", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (hayInternet()) {
                    Toast.makeText(MainActivity.this, "Conexión exitosa", Toast.LENGTH_SHORT).show();
                    btnComenzar.setEnabled(true); // habilitar botón Comenzar
                } else {
                    Toast.makeText(MainActivity.this, "Error: Sin conexión a Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Botón Comenzar
        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cantidadStr = editCantidad.getText().toString().trim();
                String categoriaNombre = spinnerCategoria.getSelectedItem().toString();
                String dificultadTexto = spinnerDificultad.getSelectedItem().toString().toLowerCase();

                // Traducir dificultad al formato de la API
                if (dificultadTexto.equals("fácil")) dificultadTexto = "easy";
                else if (dificultadTexto.equals("medio")) dificultadTexto = "medium";
                else if (dificultadTexto.equals("difícil")) dificultadTexto = "hard";

                int cantidadPreguntas = Integer.parseInt(cantidadStr);
                Map<String, String> categorias = obtenerCategorias();
                String categoriaCodigo = categorias.get(categoriaNombre);

                Intent intent = new Intent(MainActivity.this, TriviaActivity.class);
                intent.putExtra("categoria", categoriaCodigo);
                intent.putExtra("categoriaNombre", categoriaNombre);
                intent.putExtra("dificultad", dificultadTexto);
                intent.putExtra("cantidad", cantidadPreguntas);
                startActivity(intent);
            }
        });
    }

    private boolean hayInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    private Map<String, String> obtenerCategorias() {
        Map<String, String> categorias = new HashMap<>();
        categorias.put("Cultura General", "9");
        categorias.put("Libros", "10");
        categorias.put("Películas", "11");
        categorias.put("Música", "12");
        categorias.put("Computación", "18");
        categorias.put("Matemática", "19");
        categorias.put("Deportes", "21");
        categorias.put("Historia", "23");
        return categorias;
    }
}
