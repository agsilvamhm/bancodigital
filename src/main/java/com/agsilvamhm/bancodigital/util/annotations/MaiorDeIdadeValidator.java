package com.agsilvamhm.bancodigital.util.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class MaiorDeIdadeValidator implements ConstraintValidator<MaiorDeIdade, LocalDate> {
    @Override
    public boolean isValid(LocalDate dataNascimento, ConstraintValidatorContext context) {
        if (dataNascimento == null) {
            return true;
        }
        Period idade = Period.between(dataNascimento, LocalDate.now());
        return idade.getYears() >= 18;
    }
}
