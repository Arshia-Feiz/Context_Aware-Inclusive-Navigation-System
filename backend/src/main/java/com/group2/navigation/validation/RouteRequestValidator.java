package com.group2.navigation.validation;

import com.group2.navigation.model.RouteRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that a RouteRequest has either valid Toronto-area coordinates
 * or a non-blank address for both start and end points.
 */
public class RouteRequestValidator implements ConstraintValidator<ValidRouteRequest, RouteRequest> {

    // Approximate Toronto bounding box
    private static final double MIN_LAT = 43.58;
    private static final double MAX_LAT = 43.86;
    private static final double MIN_LNG = -79.65;
    private static final double MAX_LNG = -79.10;

    @Override
    public boolean isValid(RouteRequest req, ConstraintValidatorContext ctx) {
        if (req == null) return true;

        boolean startValid = hasValidCoordinates(req.getStartLat(), req.getStartLng())
                || isNonBlank(req.getStartAddress());
        boolean endValid = hasValidCoordinates(req.getEndLat(), req.getEndLng())
                || isNonBlank(req.getEndAddress());

        if (startValid && endValid) return true;

        ctx.disableDefaultConstraintViolation();
        if (!startValid) {
            ctx.buildConstraintViolationWithTemplate(
                    "Start location must be specified via Toronto-area coordinates or a non-blank address")
                    .addPropertyNode("startAddress")
                    .addConstraintViolation();
        }
        if (!endValid) {
            ctx.buildConstraintViolationWithTemplate(
                    "End location must be specified via Toronto-area coordinates or a non-blank address")
                    .addPropertyNode("endAddress")
                    .addConstraintViolation();
        }
        return false;
    }

    private boolean hasValidCoordinates(double lat, double lng) {
        return lat >= MIN_LAT && lat <= MAX_LAT && lng >= MIN_LNG && lng <= MAX_LNG;
    }

    private boolean isNonBlank(String s) {
        return s != null && !s.isBlank();
    }
}
