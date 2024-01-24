package com.example.renewrecycle_projeto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Create_User extends AppCompatActivity {

    Button User;
    Button Company;
    EditText Nome;
    EditText Email;
    EditText Celular;
    EditText Endereco;
    EditText Senha;
    Button Salvar;
    private TextView Voltar;
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);

        User = findViewById(R.id.btnUser);
        Company = findViewById(R.id.btnCompany);
        Nome = findViewById(R.id.NomeUser);
        Email = findViewById(R.id.EmailUser);
        Celular = findViewById(R.id.CelularUser);
        Endereco = findViewById(R.id.AddressUser);
        Senha = findViewById(R.id.SenhaUser);
        Salvar = findViewById(R.id.btnSalvar);
        Voltar = findViewById(R.id.btnVoltar);

        Company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Create_User.this, Create_Emp.class));
                finish();
            }
        });

        Salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = Nome.getText().toString();
                String email = Email.getText().toString();
                String celular = Celular.getText().toString();
                String endereco = Endereco.getText().toString();
                String senha = Senha.getText().toString();

                if(nome.isEmpty() || email.isEmpty() || celular.isEmpty() || endereco.isEmpty() || senha.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else{
                    cadastrarUser(v);
                }
            }
        });

        Voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Create_User.this, MainActivity.class));
                finish();
            }
        });
    }

    private void cadastrarUser(View v){
        String email = Email.getText().toString();
        String senha = Senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SalvarDadosUser();
                    bdHistorico();
                    //SALVO NO BANCO DE DADOS
                    Toast.makeText(getApplicationContext(), "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Create_User.this, InicioUser.class));
                    finish();
                }else{
                    String erro;
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erro = "Senha tem que ter no mínimo 6 caracteres";
                    }catch (FirebaseAuthUserCollisionException e){
                        erro = "Conta já cadastrada";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "E-mail inválido";
                    }catch (Exception e){
                        erro = "Erro ao cadastrar usuário";
                    }

                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    private void SalvarDadosUser(){
        String nome = Nome.getText().toString();
        String celular = Celular.getText().toString();
        String endereco = Endereco.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> users = new HashMap<>();
        users.put("nome", nome);
        users.put("celular", celular);
        users.put("endereco", endereco);
        users.put("pontos", "0");                        //TESTE

        UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRefer = db.collection("Usuarios").document(UserID);
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

    private void bdHistorico(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> users = new HashMap<>();
        users.put("vazio", "Nada reciclado ainda");                       //TESTE

        UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRefer = db.collection("HistoricoUsuarios").document(UserID);
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