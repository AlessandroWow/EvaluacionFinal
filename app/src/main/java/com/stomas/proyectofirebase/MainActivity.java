package com.stomas.proyectofirebase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText txtCodigo, txtNombre, txtPrecio , txtCapitulo;
    private ListView lista;
    private Spinner spCosmetico;
    private FirebaseFirestore db;
    String [] TiposRarezas = {"Comun", "Raro", "Epico", "Legendario"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CargarListaFirestore();

        db=FirebaseFirestore.getInstance();

        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        txtPrecio = findViewById(R.id.txtPrecio);
        txtCapitulo = findViewById(R.id.txtCapitulo);
        spCosmetico = findViewById(R.id.spCosmetico);
        lista = findViewById(R.id.lista);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TiposRarezas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCosmetico.setAdapter(adapter);

    }

    public void enviarDatosFirestore(View view){
        String codigo = txtCodigo.getText().toString();
        String nombre = txtNombre.getText().toString();
        String precio = txtPrecio.getText().toString();
        String capitulo = txtCapitulo.getText().toString();
        String tipoRareza = spCosmetico.getSelectedItem().toString();

        Map<String, Object> cosmetico = new HashMap<>();
        cosmetico.put("codigo", codigo);
        cosmetico.put("nombre", nombre);
        cosmetico.put("precio", precio);
        cosmetico.put("Capitulo", capitulo);
        cosmetico.put("tipoRareza", tipoRareza);

        db.collection("cosmeticos")
                .document(codigo)
                .set(cosmetico)
                .addOnSuccessListener(aVoid ->{
                    Toast.makeText(MainActivity.this,"Datos enviados a Firestore correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(MainActivity.this, "Error al enviar datos a Firestore: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                });
    }

    public void  CargarLista(View view){
        CargarListaFirestore();
    }

    public void CargarListaFirestore(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cosmeticos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<String> listaCosmeticos = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String linea = "|| " + document.getString("codigo") + "||" +
                                        document.getString("nombre") + "||" +
                                        document.getString("precio") + "||" +
                                        document.getString("capitulo");
                                listaCosmeticos.add(linea);
                            }

                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                                    MainActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    listaCosmeticos
                            );
                            lista.setAdapter(adaptador);
                        }else{
                            Log.e("TAG", "Error al obtener datos de Firestore", task.getException());
                        }
                    }
                });

    }
}