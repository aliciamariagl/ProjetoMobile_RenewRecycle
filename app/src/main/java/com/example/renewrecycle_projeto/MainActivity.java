package com.example.renewrecycle_projeto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    EditText Email;
    EditText Senha;
    Button Entrar;
    private TextView Emp;
    private TextView Cadastro;
    int aux = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Email = findViewById(R.id.edtEmail);
        Senha = findViewById(R.id.edtSenha);
        Entrar = findViewById(R.id.btnEntrar);
        Emp = findViewById(R.id.ContaEmp);
        Cadastro = findViewById(R.id.Cadastrar);

        Entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString();
                String senha = Senha.getText().toString();

                if(email.isEmpty() || senha.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else{
                    autenticarUser(v);
                }
            }
        });

        Emp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login_Empresa.class));
                finish();
            }
        });

        Cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Create_User.class));
                finish();
            }
        });
    }

    private void autenticarUser(View v){
        String email = Email.getText().toString();
        String senha = Senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this, InicioUser.class));
                    finish();
                }else{
                    String erro;
                    try{
                        throw task.getException();
                    }catch (Exception e){
                        erro = "Erro ao logar o usuário";
                    }

                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser atualUser = FirebaseAuth.getInstance().getCurrentUser();

        if (atualUser != null) {
            String uid = atualUser.getUid();

            // Obter referência ao Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Verificar se o usuário está na coleção "usuarios"
            db.collection("Usuarios")
                    .document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                            // Usuário está na coleção "Usuarios"
                            startActivity(new Intent(MainActivity.this, InicioUser.class));
                            finish();
                        } else {
                            startActivity(new Intent(MainActivity.this, InicioEmp.class));
                            finish();
                        }
                    });
        }
    }

}