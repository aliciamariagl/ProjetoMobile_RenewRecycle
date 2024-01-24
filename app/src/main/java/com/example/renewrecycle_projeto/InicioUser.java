package com.example.renewrecycle_projeto;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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

public class InicioUser extends AppCompatActivity {

    private TextView Nome, Email, Pontos;
    ImageButton home;
    ImageButton edit;
    ImageButton exit;
    ImageButton map;
    ImageButton history;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_user);

        Nome = findViewById(R.id.NomeUser);
        Email = findViewById(R.id.EmailUser);
        Pontos = findViewById(R.id.Pontos);
        home = findViewById(R.id.home);
        edit = findViewById(R.id.edit);
        exit = findViewById(R.id.exit);
        map = findViewById(R.id.maps);
        history = findViewById(R.id.history);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InicioUser.this, EditUser.class));
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
                startActivity(new Intent(InicioUser.this, MapUser.class));
                finish();
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InicioUser.this, HistoricoUser.class));
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
                startActivity(new Intent(InicioUser.this, MainActivity.class));
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permissões não concedidas, solicite-as
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS},
                    33);
        }

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference DocRefer = db.collection("Usuarios").document(userID);
        DocRefer.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){
                    Nome.setText(documentSnapshot.getString("nome"));
                    Pontos.setText(documentSnapshot.getString("pontos"));
                    Email.setText(email);
                }
            }
        });
    }
}