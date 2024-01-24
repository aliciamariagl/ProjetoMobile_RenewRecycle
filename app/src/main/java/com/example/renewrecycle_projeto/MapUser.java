package com.example.renewrecycle_projeto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.renewrecycle_projeto.databinding.MapUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapUser extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "MapUser";
    private FirebaseFirestore db;

    private List<User> empresas;

    private LocationManager locationManager;
    ArrayList<LatLng> arrayList = new ArrayList<LatLng>();
    LatLng PEstacao = new LatLng(-4.968881710636509, -39.01200902191491);
    LatLng PLeao = new LatLng(-4.970026377927016, -39.01580743546501);
    LatLng Chale = new LatLng(-4.971265309694488, -39.0141853371518);
    LatLng UFC = new LatLng(-4.978576549268029, -39.056417461597704);
    LatLng Atacarejo = new LatLng(-4.977951095045926, -39.01856733992456);
    LatLng Pinheiro = new LatLng(-4.967740470461075, -39.023812587580004);
    LatLng Feclesc = new LatLng(-4.968412171796951, -39.02454342705745);
    LatLng Unicatolica = new LatLng(-4.973299815092198, -39.0136080809032);
    LatLng QuintalLagoa = new LatLng(-4.972352958593746, -39.021932988050516);
    LatLng OssosBar = new LatLng(-4.971441498016794, -39.01178653721612);

    public ArrayList<Integer> Num = new ArrayList<>();


    ImageButton home;
    ImageButton edit;
    ImageButton exit;
    ImageButton map;
    ImageButton history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_user);

        db = FirebaseFirestore.getInstance();
        empresas = new ArrayList<>(); // Inicializa a lista

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


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
                startActivity(new Intent(MapUser.this, EditUser.class));
                finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapUser.this, InicioUser.class));
                finish();
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapUser.this, HistoricoUser.class));
                finish();
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        arrayList.add(PEstacao);
        arrayList.add(PLeao);
        arrayList.add(Chale);
        arrayList.add(UFC);
        arrayList.add(Atacarejo);
        arrayList.add(Pinheiro);
        arrayList.add(Feclesc);
        arrayList.add(Unicatolica);
        arrayList.add(QuintalLagoa);
        arrayList.add(OssosBar);


        for (int i = 0; i < arrayList.size(); i++) {
            Num.add(i);
        }
    }

    private void mostrarDialogoDeConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tem certeza que deseja sair?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ação a ser executada se o usuário confirmar a saída
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MapUser.this, MainActivity.class));
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
    public void onMapReady(GoogleMap map) {
        map.setTrafficEnabled(true);
        db.collection("Empresas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    empresas = new ArrayList<>(); // Inicializa a lista

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User empresa = document.toObject(User.class);
                        empresas.add(empresa); // Adiciona o User à lista
                    }

                    for (int i = 0; i < arrayList.size(); i++) {
                        if (i < Num.size()) {
                            LatLng location = arrayList.get(i);
                            Integer idMarker = i;

                            map.addMarker(new MarkerOptions().position(location).title(String.valueOf(idMarker)));
                        }
                    }
                    map.getUiSettings().setZoomControlsEnabled(true);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(PLeao, 14));

                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {

                            User empresaSelecionada = empresas.get(Integer.parseInt(marker.getTitle()));

                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(Integer.parseInt(marker.getTitle()));

                            // Obtém o ID do documento da empresa
                            String idDaEmpresa = documentSnapshot.getId();
                            Log.d("db", "ID da empresa: " + idDaEmpresa);


                            Intent intent = new Intent(MapUser.this, RecycleUser.class);
                            intent.putExtra("nome", empresaSelecionada.getNome());
                            intent.putExtra("endereco", empresaSelecionada.getEndereco());
                            intent.putExtra("celular", empresaSelecionada.getCelular());
                            intent.putExtra("horario", empresaSelecionada.getHorario());
                            intent.putExtra("peso", empresaSelecionada.getPeso());
                            intent.putExtra("idEmpresa", idDaEmpresa);
                            startActivity(intent);
                            finish();

                            return false;
                        }
                    });
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10f, (LocationListener) this
                );
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}