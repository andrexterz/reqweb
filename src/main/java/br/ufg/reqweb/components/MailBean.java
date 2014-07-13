/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.components;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author André
 */

@Component
@Scope(value = "singleton")
public class MailBean {
    
    private int counter = 0;
    
    @Scheduled(cron = "${mail.mailScheduler}")
    public void runTimer() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss - dd/MM/Y");
        System.out.format("scheduder%d executed at: %s\n", ++counter, dateFormat.format(Calendar.getInstance().getTime()));
    }
}
