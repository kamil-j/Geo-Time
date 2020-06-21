package pl.edu.agh.geotime.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {

    static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    static final String ERR_VALIDATION = "error.validation";

    private static final String PROBLEM_BASE_URL = "http://geotime.agh.edu.pl/problem";
    static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    static final URI PARAMETERIZED_TYPE = URI.create(PROBLEM_BASE_URL + "/parameterized");
    static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    static final URI EMAIL_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/email-not-found");

    private ErrorConstants() {
    }
}
