package nl.bsoft.roo.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.bsoft.roo.model.bom.User;
import nl.bsoft.roo.storage.model.UserDao;
import nl.bsoft.roo.storage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@NoArgsConstructor
@Slf4j
public class ServiceController {

    private UserRepository userRepository;

    @Autowired
    public ServiceController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @Operation(summary = "Get all known users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found users",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))}),
            @ApiResponse(responseCode = "404", description = "No users found",
                    content = @Content)})
    public ResponseEntity<User[]> getUsers() {
        ResponseEntity<User[]> result = null;

        List<UserDao> userDaos = null;
        userDaos = userRepository.findAll();

        User[] users = new User[userDaos.size()];
        int userIndex = 0;
        for (UserDao userDao : userDaos) {
            User user = convertUserDaoToUser(userDao);
            users[userIndex] = user;
        }

        result = ResponseEntity.ok(users);

        return result;
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @Operation(summary = "Register new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New user is registered",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Bad parameters",
                    content = @Content)})
    public ResponseEntity<User> addUser(final @RequestBody User user) {
        ResponseEntity<User> userResponse = null;
        UserDao userDao = convertUserToToUserDao(user);
        UserDao savedUserDao = userRepository.save(userDao);
        User savedUser = convertUserDaoToUser(savedUserDao);

        userResponse = ResponseEntity.ok(savedUser);

        return userResponse;
    }

    private User convertUserDaoToUser(final UserDao userDao) {
        User user = new User();
        user.setId(userDao.getId());
        user.setFirstName(userDao.getFirstName());
        user.setLastName(userDao.getLastName());
        user.setBirthDate(userDao.getBirthDate());
        return user;
    }

    private UserDao convertUserToToUserDao(final User user) {
        UserDao userDao = new UserDao();
        userDao.setFirstName(user.getFirstName());
        userDao.setLastName(user.getLastName());
        userDao.setBirthDate(user.getBirthDate());
        return userDao;
    }
}
