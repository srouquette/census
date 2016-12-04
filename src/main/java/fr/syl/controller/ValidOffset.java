package fr.syl.controller;

import javax.validation.constraints.Pattern;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@Pattern(regexp = "^[0-9]+$", message = "invalid offset")
public @interface ValidOffset {
}