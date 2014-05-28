/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model.validators;

import java.util.Calendar;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author andre
 */
public class YearValidator implements ConstraintValidator<ValidYear, Integer> {

    @Override
    public void initialize(ValidYear constraintAnnotation) {
        //blank method
    }

    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext constraintValidatorContext) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return (year >= currentYear);
    }

    
}
