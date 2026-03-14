package com.group2.navigation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Cross-field validation for RouteRequest.
 * Ensures at least one of (valid coordinates) or (non-blank address)
 * is provided for both the start and end locations.
 */
@Documented
@Constraint(validatedBy = RouteRequestValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRouteRequest {
    String message() default "Both start and end locations must be specified via coordinates or address";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
