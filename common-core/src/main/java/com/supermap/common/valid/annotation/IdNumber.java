package com.supermap.common.valid.annotation;

import com.supermap.common.valid.validator.IdNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 身份证号校验注解
 *
 * @author gzw
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = IdNumberValidator.class)
public @interface IdNumber {

    String message() default "身份证号码不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
