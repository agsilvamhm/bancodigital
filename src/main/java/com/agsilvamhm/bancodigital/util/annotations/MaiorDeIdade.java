package com.agsilvamhm.bancodigital.util.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaiorDeIdadeValidator.class)
public @interface MaiorDeIdade {
    String message() default "O cliente deve ser maior de 18 anos.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
