package com.supermap.common.valid.validator;

import com.supermap.common.valid.annotation.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * 电话号码校验处理类
 *
 * @author gzw
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    public static final String pattern = "(?:\\(\\d{3}\\)\\d{8}(?:-\\d{0,6})?|\\d{11})(?:\\s+(?:\\(\\d{3}\\)\\d{8}(?:-\\d{0,6})?|\\d{11}))*";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value))
            return true;

        return value.matches(pattern);
    }

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

}
