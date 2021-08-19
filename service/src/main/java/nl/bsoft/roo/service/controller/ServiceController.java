package nl.bsoft.roo.service.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.bsoft.roo.model.bom.User;
import nl.bsoft.roo.storage.model.UserDao;
import nl.bsoft.roo.storage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@NoArgsConstructor
@Slf4j
@OpenAPIDefinition(
        info = @Info(
                version = "1.0",
                description = "The ServiceController enables user operations",
                title = "User api",
                license = @License(
                        url = "https://opensource.org/licenses/MIT",
                        name = "MIT"
                )
        ),
        tags = {
                @Tag(name = "Users")
        })
public class ServiceController {

    private UserRepository userRepository;

    @Autowired
    public ServiceController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @Operation(summary = "Get all known users", tags = {"Users"})
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

        log.info("Found {} entries", userDaos.size());

        User[] users = new User[userDaos.size()];
        int userIndex = 0;
        for (UserDao userDao : userDaos) {
            User user = convertUserDaoToUser(userDao);
            users[userIndex] = user;
            userIndex++;
        }

        result = ResponseEntity.ok(users);

        return result;
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @Operation(summary = "Register new user", tags = {"Users"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New user is registered",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Bad parameters",
                    content = @Content)})
    public ResponseEntity<User> addUser(final @RequestBody User user) {
        ResponseEntity<User> userResponse = null;
        UserDao userDao = convertUserToToUserDao(user);
        UserDao savedUserDao = null;
        Optional<UserDao> optionalUserDao = null;

        try {
            optionalUserDao = userRepository.findById(user.getId());
            if (!optionalUserDao.isPresent()) {
                savedUserDao = userRepository.save(userDao);
                User savedUser = convertUserDaoToUser(savedUserDao);

                userResponse = ResponseEntity.ok(savedUser);

                return userResponse;
            } else {
                log.error("User with id: {} already existed", user.getId());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("User not saved: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    @Operation(summary = "Update a user", tags = {"Users"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "401", description = "User not found",
                    content = @Content)})
    public ResponseEntity<User> updateUser(final @RequestBody User user) {
        ResponseEntity<User> userResponse = null;

        Optional<UserDao> savedUserDao = userRepository.findById(user.getId());
        if (savedUserDao.isPresent()) {
            UserDao updatedUser = updateFoundUser(savedUserDao.get(), user);
            User savedUser = convertUserDaoToUser(updatedUser);

            userResponse = ResponseEntity.ok(savedUser);

            return userResponse;
        } else {
            log.error("User with id {} not found", user.getId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.DELETE)
    @Operation(summary = "Delete a user", tags = {"Users"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not found",
                    content = @Content)})
    public ResponseEntity<User> deleteUser(final @RequestBody User user) {

        try {
            userRepository.deleteById(user.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("User not deleted: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private UserDao updateFoundUser(final UserDao userDao, final User user) {
        if ((userDao != null) && (user != null)) {
            userDao.setFirstName(user.getFirstName());
            userDao.setLastName(user.getLastName());
            userDao.setBirthDate(user.getBirthDate());
        }
        return userDao;
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
        userDao.setId(user.getId());
        userDao.setFirstName(user.getFirstName());
        userDao.setLastName(user.getLastName());
        userDao.setBirthDate(user.getBirthDate());
        return userDao;
    }
}
