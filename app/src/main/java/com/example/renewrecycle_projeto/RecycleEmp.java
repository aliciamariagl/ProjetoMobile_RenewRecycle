package com.example.renewrecycle_projeto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class RecycleEmp extends AppCompatActivity {

    TextView Empresa;
    TextView Endereco;
    TextView Contato;
    EditText Horario;
    EditText Pesagem;
    Button Salvar;
    private ImageView Voltar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String CNPJ;
    String empID;
    String EmpID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reciclagem_emp);

        Empresa = findViewById(R.id.EmpInf);
        Endereco = findViewById(R.id.EnderecoInf);
        Contato = findViewById(R.id.ContatoInf);
        Horario = findViewById(R.id.HorarioInf);
        Pesagem = findViewById(R.id.PesagemInf);
        Salvar = findViewById(R.id.btnSalvar);
        Voltar = findViewById(R.id.btnVoltar);

        Salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalvarDadosUser();
                Toast.makeText(getApplicationContext(), "Alterações Salvas", Toast.LENGTH_SHORT).show();
                // Colocar no banco de dados as alterações
            }
        });

        Voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecycleEmp.this, InicioEmp.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        empID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference DocRefer = db.collection("Empresas").document(empID);
        DocRefer.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){
                    Empresa.setText(documentSnapshot.getString("nome"));
                    Contato.setText(documentSnapshot.getString("celular"));
                    Endereco.setText(documentSnapshot.getString("endereco"));
                    CNPJ = documentSnapshot.getString("cnpj");
                    Horario.setText(documentSnapshot.getString("horario"));
                    Pesagem.setText(documentSnapshot.getString("peso"));
                }
            }
        });
    }

    private void SalvarDadosUser(){
        String horario = Horario.getText().toString();
        String peso = Pesagem.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> users = new HashMap<>();
        users.put("nome", Empresa.getText().toString()); // Obter o texto da TextView
        users.put("celular", Contato.getText().toString()); // Obter o texto da TextView
        users.put("endereco", Endereco.getText().toString()); // Obter o texto da TextView
        users.put("cnpj", CNPJ);
        users.put("horario", horario);
        users.put("peso", peso);

        EmpID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRefer = db.collection("Empresas").document(EmpID);
        docRefer.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("db","Sucesso ao salvar os dados");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_error","Erro ao salvar os dados" + e.toString());
                    }
                });
    }
}