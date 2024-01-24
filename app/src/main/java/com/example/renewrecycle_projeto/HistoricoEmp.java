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
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HistoricoEmp extends AppCompatActivity {

    TextView historico;
    ImageButton home;
    ImageButton edit;
    ImageButton exit;
    ImageButton map;
    ImageButton history;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String histEmpID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historico_emp);

        historico = findViewById(R.id.Historico);
        home = findViewById(R.id.home);
        edit = findViewById(R.id.edit);
        exit = findViewById(R.id.exit);
        map = findViewById(R.id.maps);
        history = findViewById(R.id.history);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoricoEmp.this, EditEmp.class));
                finish();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoDeConfirmacao();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoricoEmp.this, MapEmp.class));
                finish();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoricoEmp.this, InicioEmp.class));
                finish();
            }
        });
    }

    private void mostrarDialogoDeConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tem certeza que deseja sair?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ação a ser executada se o usuário confirmar a saída
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HistoricoEmp.this, Login_Empresa.class));
                finish();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ação a ser executada se o usuário optar por não sair
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        histEmpID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference historicoRef = db.collection("HistoricoEmpresas").document(histEmpID);
        historicoRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    historicoRef.collection("Transacoes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                StringBuilder historicoText = new StringBuilder();

                                for (QueryDocumentSnapshot transacao : task.getResult()) {
                                    // Processar os documentos da subcoleção
                                    String data = transacao.getString("data");
                                    String pesoReciclado = transacao.getString("pesoReciclado");
                                    String pontosRecebidos = transacao.getString("pontosDados");
                                    String nomeEmpresa = transacao.getString("usuario");

                                    historicoText.append("Data: ").append(data).append("\n");
                                    historicoText.append("Peso Reciclado: ").append(pesoReciclado).append(" kg\n");
                                    historicoText.append("Pontos Dados: ").append(pontosRecebidos).append("\n");
                                    historicoText.append("Usuário: ").append(nomeEmpresa).append("\n\n");
                                }

                                // Exibir o histórico na TextView ou em qualquer componente que você esteja usando
                                historico.setText(historicoText.toString());
                            } else {
                                Log.e("db_error", "Erro ao obter documentos da subcoleção: " + task.getException());
                            }
                        }
                    });
                } else {
                    Log.e("db_error", "Erro ao obter documento do histórico: " + error);
                }
            }
        });
    }
}