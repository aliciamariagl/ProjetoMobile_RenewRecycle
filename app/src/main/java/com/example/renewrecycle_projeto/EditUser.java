package com.example.renewrecycle_projeto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class EditUser extends AppCompatActivity {

    EditText Nome;
    EditText Email;
    EditText Celular;
    EditText Endereco;
    EditText Senha;
    Button Salvar, Deletar;
    private TextView Voltar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID;
    String UserID;
    String Pontos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user);

        Nome = findViewById(R.id.NomeUser);
        Email = findViewById(R.id.EmailUser);
        Celular = findViewById(R.id.CelularUser);
        Endereco = findViewById(R.id.AddressUser);
        Senha = findViewById(R.id.SenhaUser);
        Salvar = findViewById(R.id.btnSalvar);
        Deletar = findViewById(R.id.btnDeletar);
        Voltar = findViewById(R.id.btnVoltar);

        Salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SALVAR NO BANCO DE DADOS
                SalvarDadosUser();
                String novaSenha = Senha.getText().toString().trim();
                if (!novaSenha.isEmpty() && novaSenha.length() >= 6) {
                    resetPassword(novaSenha);
                }
                Toast.makeText(getApplicationContext(), "Alterações Salvas", Toast.LENGTH_SHORT).show();
               // startActivity(new Intent(EditUser.this, InicioUser.class));
            }
        });

        Voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditUser.this, InicioUser.class));
                finish();
            }
        });

        Deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoDeConfirmacao();
            }
        });
    }

    private void mostrarDialogoDeConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tem certeza que deseja deletar sua conta?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ação a ser executada se o usuário confirmar a exclusão da conta
                // Obtém a instância do usuário atual
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Deleta permanentemente a conta do usuário
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("DeleteAccount", "Conta deletada com sucesso!");
                                        // Aqui você pode adicionar qualquer ação adicional após a exclusão da conta
                                        startActivity(new Intent(EditUser.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Log.e("DeleteAccount", "Falha ao deletar conta", task.getException());
                                        // Tratar falha na exclusão da conta
                                    }
                                }
                            });
                }

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
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference DocRefer = db.collection("Usuarios").document(userID);
        DocRefer.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){
                    Nome.setText(documentSnapshot.getString("nome"));
                    Email.setText(email);
                    Celular.setText(documentSnapshot.getString("celular"));
                    Endereco.setText(documentSnapshot.getString("endereco"));
                    Pontos = documentSnapshot.getString("pontos");
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
        users.put("pontos", Pontos);

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

    private void resetPassword(String novaSenha) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updatePassword(novaSenha)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditUser.this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show();
                                //finish(); // Fecha a atividade após a alteração da senha
                            } else {
                                Toast.makeText(EditUser.this, "Falha ao alterar a senha", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}