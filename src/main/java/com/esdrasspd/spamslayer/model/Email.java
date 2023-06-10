package com.esdrasspd.spamslayer.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String remitente;
    private String mensaje;
    private boolean es_spam;

    public Email(Long id, String remitente, String mensaje, boolean es_spam) {
        this.id = id;
        this.remitente = remitente;
        this.mensaje = mensaje;
        this.es_spam = es_spam;
    }

    public Email() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente; 
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje; 
    }

    public boolean getEs_spam() {
        return es_spam;
    }

    public void setEs_spam(boolean esSpam) {
        this.es_spam = esSpam; 
    }


}
