package com.agsilvamhm.bancodigital.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
    @GetMapping
    public String welcome(){
        return "Bem vindo ao projeto em Spring para a trilha de aprendizado Java - EDUC360 <br>" +
                "Java RESTFul API criada para a simulação de um banco digital ";
    }
}
