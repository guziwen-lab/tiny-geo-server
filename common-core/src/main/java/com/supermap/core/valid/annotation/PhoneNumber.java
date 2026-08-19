package com.supermap.core.valid.annotation;

import com.supermap.core.valid.validator.PhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 电话号码校验注解
 *
 * @author gzw
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {

    String message() default "电话号码不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
