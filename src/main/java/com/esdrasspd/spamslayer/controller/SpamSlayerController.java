package com.esdrasspd.spamslayer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.esdrasspd.spamslayer.model.ArbolDecision;
import com.esdrasspd.spamslayer.model.Email;
import com.esdrasspd.spamslayer.repository.EmailRepository;

@Controller
public class SpamSlayerController {

    private final EmailRepository emailRepository;
    private final ArbolDecision arbolDecision;
    
    @Autowired
    public SpamSlayerController(EmailRepository emailRepository, ArbolDecision arbolDecision) {
        this.emailRepository = emailRepository;
        this.arbolDecision = arbolDecision;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    
    @RequestMapping("/formulario")
    public String formulario(Model model) throws Exception {
        model.addAttribute("email", new Email());
        arbolDecision.construirArbolDecision();
        return "formulario";
    }

    @PostMapping("/predecir")
    public String predecirSpam(Email email, Model model) {
        try{
            arbolDecision.construirArbolDecision();
            //Realizamos la prediccion
            boolean esSpam = arbolDecision.predecir(email);
            email.setEs_spam(esSpam);
            //Guardamos el email en la base de datos
            emailRepository.save(email);

            //Agregamos el email al modelo
            model.addAttribute("resultado", esSpam ? "Es Spam" : "No es SPAM");
        }catch(Exception e){
            e.printStackTrace();
            model.addAttribute("resultado", "Error al predecir el correo electrónico");
        }
        return "resultado";
    }
}
