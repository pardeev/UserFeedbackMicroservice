package pardeev.feedback;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonFormat
@ApiModel(description = "Response of validation problems")
public class ValidationErrors {

    public ValidationErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    @ApiModelProperty(value = "List of constraint violations")
    private Map<String, String> errors = new HashMap<>();

    public Map<String, String> getErrors() {
        return errors;
    }
}
