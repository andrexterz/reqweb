/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;


/**
 *
 * @author andre
 */
@Controller
public class ReqWebController {

    @RequestMapping(value = "/home")
    public String home() {
        return "home";
    }

    @RequestMapping(value = "/error")
    public String error() {
        return "error";
    }

    @RequestMapping(value = "/redir")
    public String redir() {
        System.out.println("redirecionando...");
        return "redirect:login";
    }

    @RequestMapping(value = "/login")
    public String login() {

        return "login";

    }
    
    @RequestMapping(value = "/autenticar", method= RequestMethod.POST)
    public String autenticar() {
        String txt = "meu texto do modelo";
        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        ctx.getServletContext().setAttribute("umTexto", txt);
        return "home";
    }
}
