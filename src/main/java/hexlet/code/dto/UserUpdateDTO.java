package hexlet.code.dto;

import org.openapitools.jackson.nullable.JsonNullable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

    @Email
    private JsonNullable<String> email;

    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;

    @Size(min = 3, max = 100)
    private JsonNullable<String> password;
}
