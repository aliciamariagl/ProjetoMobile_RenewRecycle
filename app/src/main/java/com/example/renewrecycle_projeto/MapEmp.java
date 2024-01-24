package com.example.renewrecycle_projeto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MapEmp extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;

    String empID;
    String EmpID;
    String Nome;

    String Email;

    String Celular;

    String Endereco;

    String CNPJ;

    String Horario;

    String Peso;

    String latitude;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageButton home;
    ImageButton edit;
    ImageButton exit;
    ImageButton map;
    ImageButton history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_user);

        home = findViewById(R.id.home);
        edit = findViewById(R.id.edit);
        exit = findViewById(R.id.exit);
        map = findViewById(R.id.maps);
        history = findViewById(R.id.history);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoDeConfirmacao();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapEmp.this, EditEmp.class));
                finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapEmp.this, InicioEmp.class));
                finish();
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapEmp.this, HistoricoEmp.class));
                finish();
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void mostrarDialogoDeConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tem certeza que deseja sair?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ação a ser executada se o usuário confirmar a saída
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MapEmp.this, Login_Empresa.class));
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                addMarker(latLng);


            }
        });

        LatLng m1 = new LatLng(-4.9680809373350385, -39.016403012924805);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(m1, 14));

    }

    private void addMarker(LatLng latLng) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Novo marcador"));
        marker.showInfoWindow();


        marker.setTag(latLng);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng clickedLatLng = (LatLng) marker.getTag();
                if (clickedLatLng != null) {

                    addLatLngToFirebase(marker);
                    startActivity(new Intent(MapEmp.this, RecycleEmp.class)) ;
                }
                return false;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        empID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference DocRefer = db.collection("Empresas").document(empID);
        DocRefer.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    Nome = documentSnapshot.getString("nome");
                    Celular = documentSnapshot.getString("celular");
                    Endereco = documentSnapshot.getString("endereco");
                    CNPJ = documentSnapshot.getString("cnpj");
                    Horario = documentSnapshot.getString("horario");
                    Peso = documentSnapshot.getString("peso");
                }
            }
        });
    }


    private void addLatLngToFirebase(Marker marker) {

        LatLng MarkerEmp = (LatLng) marker.getTag();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> users = new HashMap<>();
        users.put("nome", Nome);
        users.put("celular", Celular);
        users.put("endereco", Endereco);
        users.put("cnpj", CNPJ);
        users.put("horario", Horario);
        users.put("peso", Peso);
        users.put("localizacao", new GeoPoint(MarkerEmp.latitude, MarkerEmp.longitude));

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
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}