package africa.semicolon.ecommerce.services.appUser;

import africa.semicolon.ecommerce.data.dtos.UserRequestDto;
import africa.semicolon.ecommerce.data.dtos.UserResponseDto;
import africa.semicolon.ecommerce.web.exceptions.UserAlreadyExists;
import africa.semicolon.ecommerce.web.exceptions.UserNotFoundException;

public interface AppUserService {
    UserResponseDto createUser(UserRequestDto request) throws UserAlreadyExists;
    UserResponseDto findUserByEmail(String email) throws UserNotFoundException;
}
