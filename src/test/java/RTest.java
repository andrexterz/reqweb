/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andre
 */
public class RTest {
    
    public static void main(String[] args) {
        int i;
        for (i=0; i < 101; i++) {
            if (i == 50) {
                break;
            }
            System.out.println("number: " +  i);
        }
        System.out.println("last value: " + i);
    }
}
