package com.example.renewrecycle_projeto;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class InicioEmp extends AppCompatActivity {

    private TextView Nome, Email;
    ImageButton home;
    ImageButton edit;
    ImageButton exit;
    ImageButton map;
    ImageButton history;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String empID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_emp);

        Nome = findViewById(R.id.NomeEmp);
        Email = findViewById(R.id.EmailEmp);
        home = findViewById(R.id.home);
        edit = findViewById(R.id.edit);
        exit = findViewById(R.id.exit);
        map = findViewById(R.id.maps);
        history = findViewById(R.id.history);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  startActivity(new Intent(InicioEmp.this, EditEmp.class));
                  finish();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(InicioUser.this, MainActivity.class));
                mostrarDialogoDeConfirmacao();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   startActivity(new Intent(InicioEmp.this, MapEmp.class)) ;
                   finish();
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(InicioEmp.this, HistoricoEmp.class));
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
                startActivity(new Intent(InicioEmp.this, Login_Empresa.class));
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

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        empID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference DocRefer = db.collection("Empresas").document(empID);
        DocRefer.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){
                    Nome.setText(documentSnapshot.getString("nome"));
                    Email.setText(email);
                }
            }
        });
    }
}