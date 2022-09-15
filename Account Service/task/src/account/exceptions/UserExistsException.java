package account.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
    If you don't know how to use exceptions in Spring Boot, please, take a look:

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Some error message")
    public class UserExistException extends RuntimeException { }
*/

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "User exist!")
public class UserExistsException extends RuntimeException {

}

