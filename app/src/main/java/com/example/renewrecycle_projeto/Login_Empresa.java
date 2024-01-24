package com.example.renewrecycle_projeto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class Login_Empresa extends AppCompatActivity{

    EditText Email;
    EditText Senha;
    Button Entrar;
    private TextView Pessoal;
    private TextView Cadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_empresa);

        Email = findViewById(R.id.edtEmail);
        Senha = findViewById(R.id.edtSenha);
        Entrar = findViewById(R.id.btnEntrar);
        Pessoal = findViewById(R.id.ContaEmp);
        Cadastro = findViewById(R.id.Cadastrar);

        Entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString();
                String senha = Senha.getText().toString();

                if(email.isEmpty() || senha.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else{
                    autenticarEmp(v);
                }
            }
        });

        Pessoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Empresa.this, MainActivity.class));
                finish();
            }
        });

        Cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Empresa.this, Create_Emp.class));
                finish();
            }
        });

    }

    private void autenticarEmp(View v){
        String email = Email.getText().toString();
        String senha = Senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(Login_Empresa.this, InicioEmp.class));
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
        if (atualUser != null){
            startActivity(new Intent(Login_Empresa.this, InicioEmp.class));
            finish();
        }
    }
}
