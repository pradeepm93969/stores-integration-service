package com.app.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

	public static final String ACCESS_DENIED = "Access denied!";
	public static final String INVALID_REQUEST = "Invalid request";
	public static final String ERROR_MESSAGE_TEMPLATE = "message: %s %n requested uri: %s";
	public static final String LIST_JOIN_DELIMITER = ",";
	public static final String FIELD_ERROR_SEPARATOR = ": ";
	private static final String ERRORS_FOR_PATH = "errors {} for path {}";
	private static final String PATH = "path";
	private static final String ERRORS = "error";
	private static final String STATUS = "status";
	private static final String MESSAGE = "message";
	private static final String TIMESTAMP = "timestamp";
	private static final String TYPE = "type";
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> validationErrors = new ArrayList<String>();
		for (ObjectError error : exception.getBindingResult().getAllErrors()) {
			if (error instanceof FieldError) {
				validationErrors.add(((FieldError) error).getField() + FIELD_ERROR_SEPARATOR + error.getDefaultMessage());
			} else {
				validationErrors.add(error.getObjectName() + FIELD_ERROR_SEPARATOR + error.getDefaultMessage());
			}
		}
		return getExceptionResponseEntity(exception, HttpStatus.BAD_REQUEST, request, validationErrors);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return getExceptionResponseEntity(exception, status, request,
				Collections.singletonList(exception.getLocalizedMessage()));
	}

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception,
			WebRequest request) {
		final List<String> validationErrors = exception.getConstraintViolations().stream()
				.map(violation -> violation.getPropertyPath() + FIELD_ERROR_SEPARATOR + violation.getMessage())
				.collect(Collectors.toList());
		return getExceptionResponseEntity(exception, HttpStatus.BAD_REQUEST, request, validationErrors);
	}
	
	/**
	 * A general handler for all uncaught exceptions
	 */
	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<Object> handleAccessDeniedException(Exception exception, WebRequest request) {
		final String localizedMessage = ((AccessDeniedException) exception).getMessage();
		final String path = request.getDescription(false);
		String message = (StringUtils.isNotEmpty(localizedMessage) ? localizedMessage : ((AccessDeniedException) exception).getMessage());
		logger.error(String.format(ERROR_MESSAGE_TEMPLATE, message, path), exception);
		return getExceptionResponseEntity(exception, HttpStatus.FORBIDDEN, request, Collections.singletonList(message));
	}
	
	/**
	 * A general handler for ResponseStatusException
	 */
	@ExceptionHandler({ ResponseStatusException.class })
	public ResponseEntity<Object> handleResponseStatusExceptions(Exception exception, WebRequest request) {
		final String localizedMessage = ((ResponseStatusException) exception).getReason();
		final String path = request.getDescription(false);
		String message = (StringUtils.isNotEmpty(localizedMessage) ? localizedMessage : ((ResponseStatusException) exception).getStatus().getReasonPhrase());
		logger.error(String.format(ERROR_MESSAGE_TEMPLATE, message, path), exception);
		return getExceptionResponseEntity(exception, ((ResponseStatusException) exception).getStatus(), request, Collections.singletonList(message));
	}

	/**
	 * A general handler for CustomApplicationException
	 */
	@ExceptionHandler({ CustomApplicationException.class })
	public ResponseEntity<Object> handleCustomApplicationExceptions(CustomApplicationException exception, WebRequest request) {
		
		final Map<String, Object> body = new LinkedHashMap<>();
		final List<ErrorDetail> errorDetailsList = new ArrayList<>();
		errorDetailsList.add(new ErrorDetail(exception.getErrorCode(), exception.getErrorDescription()));
		final String path = request.getDescription(false);
		body.put(TIMESTAMP, Instant.now());
		body.put(STATUS, exception.getHttpStatus());
		body.put(ERRORS, errorDetailsList);
		body.put(TYPE, exception.getClass().getSimpleName());
		body.put(PATH, path);
		body.put(MESSAGE, getMessageForStatus(exception.getHttpStatus()));
		return new ResponseEntity<>(body, exception.getHttpStatus());
	}

	
	/**
	 * A general handler for all uncaught exceptions
	 */
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAllExceptions(Exception exception, WebRequest request) {
		ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);
		final HttpStatus status = responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
		final String localizedMessage = exception.getLocalizedMessage();
		final String path = request.getDescription(false);
		String message = (StringUtils.isNotEmpty(localizedMessage) ? localizedMessage : status.getReasonPhrase());
		logger.error(String.format(ERROR_MESSAGE_TEMPLATE, message, path), exception);
		return getExceptionResponseEntity(exception, status, request, Collections.singletonList(message));
	}

	/**
	 * Build a detailed information about the exception in the response
	 */
	private ResponseEntity<Object> getExceptionResponseEntity(final Exception exception, final HttpStatus status,
			final WebRequest request, final List<String> errors) {
		final Map<String, Object> body = new LinkedHashMap<>();
		final List<ErrorDetail> errorDetailsList = new ArrayList<>();
		final String path = request.getDescription(false);
		body.put(TIMESTAMP, Instant.now());
		body.put(STATUS, status.value());
		for (String e : errors) {
			if (StringUtils.isNotBlank(e)) {
				if (e.contains(": ")) {
					errorDetailsList.add(new ErrorDetail(e.split(": ")[0],e.split(": ")[1]));
				} else if (e.contains("|")) {
					errorDetailsList.add(new ErrorDetail(e.split("|")[0],e.split("|")[1]));
				} else {
					errorDetailsList.add(new ErrorDetail("",e));
				}
			} 
		}
		body.put(ERRORS, errorDetailsList);
		body.put(TYPE, exception.getClass().getSimpleName());
		body.put(PATH, path);
		body.put(MESSAGE, getMessageForStatus(status));
		final String errorsMessage = CollectionUtils.isEmpty(errors)
				? status.getReasonPhrase() : errors.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(LIST_JOIN_DELIMITER));
		log.error(ERRORS_FOR_PATH, errorsMessage, path);
		return new ResponseEntity<>(body, status);
	}

	private String getMessageForStatus(HttpStatus status) {
		switch (status) {
		case UNAUTHORIZED:
			return ACCESS_DENIED;
		case BAD_REQUEST:
			return INVALID_REQUEST;
		default:
			return status.getReasonPhrase();
		}
	}
}