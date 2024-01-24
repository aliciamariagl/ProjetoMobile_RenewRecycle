package com.example.renewrecycle_projeto;

public class User {
    private String nome;
    private String celular;
    private String endereco;
    private String horario;
    private String peso;
    private String idEmpresa;

    private String latitude;
    private String longitude;

    public User() {
        // Necessário para o Firebase
    }

    public User(String nome) { this.nome = nome; }
    public String getNome() {
        return nome;
    }
    public String getCelular() {
        return celular;
    }
    public String getHorario() {
        return horario;
    }
    public String getEndereco() { return endereco; }
    public String getPeso() { return peso; }
    public String getId() { return idEmpresa; }
    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }


}
