package com.esdrasspd.spamslayer.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esdrasspd.spamslayer.model.Email;
import com.esdrasspd.spamslayer.repository.EmailRepository;



@Service
public class EmailService {
    
    @Autowired
    private EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public List<Email> obtenerDatosEntrenamiento() {
        return (List<Email>) emailRepository.findAll();
    }
}
