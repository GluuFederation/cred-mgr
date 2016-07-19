package org.gluu.credmgr.web.rest.errors;

import org.gluu.credmgr.service.error.OPException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 */
@ControllerAdvice
public class ExceptionTranslator {

    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO processConcurencyError(ConcurrencyFailureException ex) {
        return new ErrorDTO(ErrorConstants.ERR_CONCURRENCY_FAILURE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        return processFieldErrors(fieldErrors);
    }

    @ExceptionHandler(CustomParameterizedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ParameterizedErrorDTO processParameterizedValidationError(CustomParameterizedException ex) {
        return ex.getErrorDTO();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDTO processAccessDeniedExcpetion(AccessDeniedException e) {
        return new ErrorDTO(ErrorConstants.ERR_ACCESS_DENIED, e.getMessage());
    }

    private ErrorDTO processFieldErrors(List<FieldError> fieldErrors) {
        ErrorDTO dto = new ErrorDTO(ErrorConstants.ERR_VALIDATION);

        for (FieldError fieldError : fieldErrors) {
            dto.add(fieldError.getObjectName(), fieldError.getField(), fieldError.getCode());
        }

        return dto;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorDTO processMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return new ErrorDTO(ErrorConstants.ERR_METHOD_NOT_SUPPORTED, exception.getMessage());
    }

    @ExceptionHandler(OPException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO processOPException(OPException exception) {
        BodyBuilder builder;
        switch (exception.getMessage()) {

            case OPException.ERROR_RETRIEVE_OP_CONFIG:
            case OPException.ERROR_CREATE_SCIM_USER:
            case OPException.ERROR_FIND_SCIM_USER:

            case OPException.ERROR_EMAIL_OR_LOGIN_ALREADY_EXISTS:
            case OPException.ERROR_PASSWORD_CHANGE:
                builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
                break;
            case OPException.ERROR_UPDATE_SCIM_USER:
            case OPException.ERROR_DELETE_SCIM_USER:
            case OPException.ERROR_RETRIEVE_LOGOUT_URI:
            case OPException.ERROR_RETRIEVE_OPEN_ID_CONFIGURATION:
            case OPException.ERROR_RETRIEVE_CLIENT_INFO:
            case OPException.ERROR_REGISTER_CLIENT:
            case OPException.ERROR_RETRIEVE_TOKEN:
            case OPException.ERROR_RETRIEVE_USER_INFO:
            case OPException.ERROR_ACTIVATE_OP_ADMIN:
            case OPException.ERROR_LOGIN:
                builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
                break;
            default:
                builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
                break;

        }
        return new ErrorDTO(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> processRuntimeException(Exception ex) throws Exception {
        BodyBuilder builder;
        ErrorDTO errorDTO;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            builder = ResponseEntity.status(responseStatus.value());
            errorDTO = new ErrorDTO("error." + responseStatus.value().value(), responseStatus.reason());
        } else {
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            errorDTO = new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER_ERROR, "Internal server error");
        }
        return builder.body(errorDTO);
    }
}
