package pardeev.feedback;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Validated
@RequestMapping(value = "/feedback")
@Api(value = "Feedback API", description = "Operations to manage feedback.", tags = "feedback")
@Controller
public class FeedbackController {

    public final static Long DEFAULT_SKIP = 0L;

    @Autowired
    private FeedbackRepository repository;

    @ApiOperation(
            value = "List of all feedback",
            notes = "By passing in the appropriate options, you can search for available feedback in the system."
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "Bad input parameter", response = ValidationErrors.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "Search by name of author",
                    paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "offset", value = "Number of records to skip for pagination",
                    paramType = "query", dataType = "long", allowableValues = "range[0, infinity]"),
            @ApiImplicitParam(name = "limit", value = "Maximum number of records to return",
                    paramType = "query", dataType = "long", allowableValues = "range[1, infinity]"),
    })
    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<Feedback> find(@RequestParam("name") Optional<String> name,
                               @Min(0) @RequestParam("offset") Optional<Long> offset,
                               @Min(1) @RequestParam("limit") Optional<Long> limit) {
        Stream<Feedback> stream = name
                .map(repository::findByName)
                .orElse(repository.findAll())
                .skip(offset.orElse(DEFAULT_SKIP));
        stream = limit.map(stream::limit).orElse(stream);
        return stream.collect(Collectors.toList());
    }

    @ApiOperation(
            value = "Find feedback by ID",
            notes = "This method return a single feedback based on the provided ID."
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "Invalid ID supplied", response = ValidationErrors.class),
            @ApiResponse(code = 404, message = "Feedback not found"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Feedback ID", paramType = "path", dataType = "uuid", required = true)
    })
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Feedback findOne(@PathVariable(value = "id") UUID id) {
        return repository.findOne(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @ApiOperation(value = "Add a new feedback", code = 201)
    @ApiResponses({
            @ApiResponse(code = 400, message = "Invalid input, object invalid", response = ValidationErrors.class)
    })
    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Feedback create(@ApiParam(value = "Feedback object that needs to be added.", required = true) @Valid @RequestBody Feedback feedback) {
        Feedback updatedFeedback = feedback.toBuilder()
                .id(null)
                .createdAt(new Date())
                .build();
        return repository.save(updatedFeedback);
    }
}
