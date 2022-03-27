package africa.semicolon.ecommerce.services.appUser;

import africa.semicolon.ecommerce.data.dtos.UserRequestDto;
import africa.semicolon.ecommerce.data.dtos.UserResponseDto;
import africa.semicolon.ecommerce.data.models.AppUser;
import africa.semicolon.ecommerce.data.repostories.AppUSerRepository;
import africa.semicolon.ecommerce.web.exceptions.UserAlreadyExists;
import africa.semicolon.ecommerce.web.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService{
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    AppUSerRepository appUSerRepository;

    @Override
    public UserResponseDto createUser(UserRequestDto request) throws UserAlreadyExists {
        //check if request is empty
        if(request == null) throw new IllegalArgumentException("Invalid request parameter!!");

        //check if user already exists in database
        AppUser queryResult = appUSerRepository.findByEmail(request.getEmail()).orElse(null);
        if(queryResult != null) throw new UserAlreadyExists("User Already Exists!!");

        //create new user
        AppUser appUser = new AppUser();
        appUser.setFirstName(request.getFirstName());
        appUser.setEmail(request.getEmail());
        appUser.setLastName(request.getLastName());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUser.setAddress(request.getAddress());
        appUser.setAuthorities(request.getAuthority());

        //save new user to database
        AppUser savedUser = appUSerRepository.save(appUser);

        //return responseDto
        return buildAppUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto findUserByEmail(String email) throws UserNotFoundException {
        //check that request is not empty
        if(email == null) throw new IllegalArgumentException("Invalid request parameter!!");

        //check that user exits in database
        Optional<AppUser> queryResult = appUSerRepository.findByEmail(email);
        if(queryResult.isEmpty()) throw new UserNotFoundException("User Does not exist!!");

        //return user
        return buildAppUserResponseDto(queryResult.get());
    }

    private UserResponseDto buildAppUserResponseDto(AppUser user){
        return UserResponseDto.builder()
                .email(user.getEmail())
                .lastName(user.getLastName())
                .address(user.getAddress())
                .firstName(user.getFirstName())
                .build();
    }
}
