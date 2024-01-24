package com.example.renewrecycle_projeto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class RecycleUser extends AppCompatActivity {

    Button Reciclar;
    private ImageView Voltar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText kg;
    String nomeEmpresa, idEmpresa;
    String userID;
    String UserID;
    String Nome, Endereco, Celular, pontosString, pesoEmpresa, PontosBD, pontosAdc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reciclagem_user);

        Reciclar = findViewById(R.id.btnRecycle);
        Voltar = findViewById(R.id.btnVoltar);
        // Recupera os dados do Intent
        Intent intent = getIntent();
        nomeEmpresa = intent.getStringExtra("nome");
        idEmpresa = intent.getStringExtra("idEmpresa");
        String enderecoEmpresa = intent.getStringExtra("endereco");
        String celularEmpresa = intent.getStringExtra("celular");
        String horarioEmpresa = intent.getStringExtra("horario");
        pesoEmpresa = intent.getStringExtra("peso");

        // Agora, você pode usar 'nomeEmpresa' e 'enderecoEmpresa' conforme necessário na sua InfEmpresas Activity
        // Por exemplo, você pode exibir esses valores em TextViews ou qualquer outro componente da interface do usuário.

        // Exemplo de exibição em TextViews:
        TextView textViewNome = findViewById(R.id.EmpInf);
        TextView textViewEndereco = findViewById(R.id.EnderecoInf);
        TextView textViewContato = findViewById(R.id.ContatoInf);
        TextView textViewHorario = findViewById(R.id.HorarioInf);
        TextView textViewPeso = findViewById(R.id.PesagemInf);

        textViewNome.setText(nomeEmpresa);
        textViewEndereco.setText(enderecoEmpresa);
        textViewContato.setText(celularEmpresa);
        textViewHorario.setText(horarioEmpresa);
        textViewPeso.setText(pesoEmpresa);

        Reciclar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        Voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(RecycleUser.this, MapUser.class));
              finish();
            }
        });
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
                    Nome = documentSnapshot.getString("nome");
                    Celular = documentSnapshot.getString("celular");
                    Endereco = documentSnapshot.getString("endereco");
                    PontosBD = documentSnapshot.getString("pontos");
                }
            }
        });
    }

    private void showDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.modal_recycle);

        TextView pontuacao = dialog.findViewById(R.id.pont);
        kg = dialog.findViewById(R.id.kg);
        Button ok = dialog.findViewById(R.id.btnOK);
        ImageView voltar = dialog.findViewById(R.id.btnVoltar);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Atualizar pontuação
                // pesoEmp    100 pontos
                // pesoUser   x pontos
                // x * pesoEmp = 100 * pesoUser  =>  x = 100*pesoUser/pesoEmp
                EditText kgEditText = dialog.findViewById(R.id.kg);

                // Obter o texto digitado no EditText
                String pesoDigitado = kgEditText.getText().toString();

                // Verificar se o campo não está vazio antes de tentar converter para inteiro
                if (!pesoDigitado.isEmpty()) {
                    try {
                        // Converter o texto para um número inteiro
                        int pesoUsuario = Integer.parseInt(pesoDigitado);
                        int pesoEmp = Integer.parseInt(pesoEmpresa);
                        int pontosBD = Integer.parseInt(PontosBD);
                        // Realizar cálculos ou qualquer outra lógica com o valor obtido
                        int pontosCalc = (100 * pesoUsuario) / pesoEmp;
                        int pontos = pontosCalc + pontosBD;
                        // Transformar pontos em String
                        pontosAdc = String.valueOf(pontosCalc);
                        pontuacao.setText(pontosAdc);               //TESTAR
                        pontosString = String.valueOf(pontos);
                        // Colocar pontosString no banco de dados
                        SalvarDadosUser();
                        SalvarHistoricoUser();
                        SalvarHistoricoEmp();
                        Toast.makeText(getApplicationContext(), "Pontos adicionados: " + pontosCalc, Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        // Tratar caso o texto não possa ser convertido para inteiro
                        Toast.makeText(getApplicationContext(), "Digite um valor válido em kg.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Tratar o caso em que o campo está vazio
                    Toast.makeText(getApplicationContext(), "Digite um valor em kg.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT) );
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void SalvarDadosUser(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> users = new HashMap<>();
        users.put("nome", Nome);
        users.put("celular", Celular);
        users.put("endereco", Endereco);
        users.put("pontos", pontosString);

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

    private void SalvarHistoricoUser(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obter a data atual
        String dataAtual = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

        // Criar um novo documento na coleção "HistoricoUsuarios"
        Map<String, Object> historico = new HashMap<>();
        historico.put("data", dataAtual);
        historico.put("pesoReciclado", kg.getText().toString());
        historico.put("pontosRecebidos", pontosAdc);
        historico.put("empresa", nomeEmpresa); // Substitua pelo nome real da empresa

        // Obter a referência da coleção "HistoricoUsuarios" para o usuário atual
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference historicoRef = db.collection("HistoricoUsuarios").document(userID);

        // Adicionar o novo documento na coleção "HistoricoUsuarios"
        historicoRef.collection("Transacoes")
                .add(historico)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("db", "Sucesso ao salvar o histórico do usuário");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_error", "Erro ao salvar o histórico do usuário: " + e.toString());
                    }
                });
    }

    private void SalvarHistoricoEmp(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obter a data atual
        String dataAtual = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

        // Criar um novo documento na coleção "HistoricoEmpresas"
        Map<String, Object> historico = new HashMap<>();
        historico.put("data", dataAtual);
        historico.put("pesoReciclado", kg.getText().toString());
        historico.put("pontosDados", pontosAdc);
        historico.put("usuario", Nome); // Substitua pelo nome real do usuario

        // Obter a referência da coleção "HistoricoEmpresas" para o usuário atual
        DocumentReference historicoRef = db.collection("HistoricoEmpresas").document(idEmpresa);

        // Adicionar o novo documento na coleção "HistoricoEmpresas"
        historicoRef.collection("Transacoes")
                .add(historico)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("db", "Sucesso ao salvar o histórico da empresa");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_error", "Erro ao salvar o histórico da empresa: " + e.toString());
                    }
                });
    }
}