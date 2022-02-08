package pardeev.feedback;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    public ValidationErrors handle(MethodArgumentNotValidException exception) {
        final BindingResult result = exception.getBindingResult();
        return processFieldErrors(result.getFieldErrors());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    public ValidationErrors handle(ConstraintViolationException exception) {
        return processConstraintViolations(exception.getConstraintViolations());
    }

    private ValidationErrors processFieldErrors(final List<FieldError> fieldErrors) {
        return new ValidationErrors(fieldErrors.stream()
                .collect(Collectors.toMap(fieldErrorToKey(), FieldError::getDefaultMessage)));
    }

    private ValidationErrors processConstraintViolations(Set<ConstraintViolation<?>> constraintViolations) {
        return new ValidationErrors(constraintViolations.stream()
                .collect(Collectors.toMap(constraintViolationToKey(), ConstraintViolation::getMessage)));
    }

    private Function<FieldError, String> fieldErrorToKey() {
        return f -> f.getObjectName() + "." + f.getField();
    }

    private Function<ConstraintViolation, String> constraintViolationToKey() {
        return f -> f.getPropertyPath().toString();
    }
}
