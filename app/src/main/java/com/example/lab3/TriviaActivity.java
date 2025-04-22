package com.example.lab3;

//----------------------------------------------



// La app usa hilos internamente gracias a Volley.

// Además, se usa CountDownTimer, que también funciona en segundo plano con callbacks en el hilo principal.



//----------------------------------------------
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

import java.util.*;

public class TriviaActivity extends AppCompatActivity {

    private TextView txtCategoria, txtTiempo, txtPregunta, txtContador;
    private RadioGroup opcionesGroup;
    private RadioButton opcion1, opcion2, opcion3, opcion4;
    private Button btnSiguiente;

    private List<Question> preguntas = new ArrayList<>();
    private int indexPregunta = 0;
    private int correctas = 0;
    private int incorrectas = 0;
    private int noRespondidas = 0;

    private CountDownTimer timer;
    private long tiempoTotal = 0;
    private long tiempoRestante = 0;
    private int tiempoPorPregunta = 5000;

    private String categoria, dificultad;
    private int cantidad;
    private String categoriaNombre;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        txtCategoria = findViewById(R.id.txtCategoria);
        txtTiempo = findViewById(R.id.txtTiempo);
        txtPregunta = findViewById(R.id.txtPregunta);
        txtContador = findViewById(R.id.txtContador);
        opcionesGroup = findViewById(R.id.opcionesGroup);
        opcion1 = findViewById(R.id.opcion1);
        opcion2 = findViewById(R.id.opcion2);
        opcion3 = findViewById(R.id.opcion3);
        opcion4 = findViewById(R.id.opcion4);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        // Obtener datos del Intent
        categoria = getIntent().getStringExtra("categoria");
        categoriaNombre = getIntent().getStringExtra("categoriaNombre");
        dificultad = getIntent().getStringExtra("dificultad");
        cantidad = getIntent().getIntExtra("cantidad", 3);

        txtCategoria.setText(categoriaNombre);

        // Duración por dificultad
        if (dificultad.equalsIgnoreCase("easy")) tiempoPorPregunta = 5000;
        else if (dificultad.equalsIgnoreCase("medium")) tiempoPorPregunta = 7000;
        else if (dificultad.equalsIgnoreCase("hard")) tiempoPorPregunta = 10000;

        tiempoTotal = tiempoPorPregunta * cantidad;
        iniciarTemporizador();

        cargarPreguntas();

        btnSiguiente.setOnClickListener(v -> {
            evaluarRespuesta();
            indexPregunta++;
            if (indexPregunta < preguntas.size()) {
                mostrarPregunta();
            } else {
                irAResultados();
            }
        });
    }

    private void iniciarTemporizador() {
        timer = new CountDownTimer(tiempoTotal, 1000) {
            public void onTick(long millisUntilFinished) {
                tiempoRestante = millisUntilFinished;
                txtTiempo.setText("00:" + String.format("%02d", millisUntilFinished / 1000));
            }

            public void onFinish() {
                noRespondidas += preguntas.size() - indexPregunta;
                irAResultados();
            }
        };
        timer.start();
    }

    private void cargarPreguntas() {
        String url = "https://opentdb.com/api.php?amount=" + cantidad +
                "&category=" + categoria +
                "&difficulty=" + dificultad +
                "&type=multiple";

        Log.d("TRIVIA_URL", url); //  Mostrar URL completa

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("TRIVIA_RESPONSE", response.toString()); // Logear respuesta

                        int code = response.getInt("response_code");
                        if (code != 0) {
                            Toast.makeText(this, "No se encontraron preguntas. Cambia los parámetros.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONArray results = response.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject obj = results.getJSONObject(i);
                            String pregunta = android.text.Html.fromHtml(obj.getString("question")).toString();
                            String correcta = obj.getString("correct_answer");
                            JSONArray incorrectasArray = obj.getJSONArray("incorrect_answers");

                            List<String> opciones = new ArrayList<>();
                            for (int j = 0; j < incorrectasArray.length(); j++) {
                                opciones.add(incorrectasArray.getString(j));
                            }
                            opciones.add(correcta);
                            Collections.shuffle(opciones);

                            preguntas.add(new Question(pregunta, correcta, opciones));
                        }

                        mostrarPregunta();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar preguntas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("TRIVIA_ERROR", "Volley error: " + error.getMessage());
                    Toast.makeText(this, "Error al obtener preguntas", Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    }

    private void mostrarPregunta() {
        opcionesGroup.clearCheck();
        Question q = preguntas.get(indexPregunta);
        txtPregunta.setText(q.getTexto());
        txtContador.setText("Pregunta " + (indexPregunta + 1) + "/" + preguntas.size());

        List<String> opts = q.getOpciones();
        opcion1.setText(opts.get(0));
        opcion2.setText(opts.get(1));
        opcion3.setText(opts.get(2));
        opcion4.setText(opts.get(3));
    }

    private void evaluarRespuesta() {
        int checkedId = opcionesGroup.getCheckedRadioButtonId();
        if (checkedId == -1) {
            noRespondidas++;
            return;
        }

        RadioButton seleccionada = findViewById(checkedId);
        String respuesta = seleccionada.getText().toString();
        String correcta = preguntas.get(indexPregunta).getCorrecta();

        if (respuesta.equals(correcta)) correctas++;
        else incorrectas++;
    }

    private void irAResultados() {
        timer.cancel();
        Intent intent = new Intent(this, ResultadosActivity.class);
        intent.putExtra("correctas", correctas);
        intent.putExtra("incorrectas", incorrectas);
        intent.putExtra("no_respondidas", noRespondidas);
        startActivity(intent);
        finish();
    }
}
