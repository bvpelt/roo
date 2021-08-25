package nl.bsoft.roo.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import nl.bsoft.roo.service.api.UsersApi;
import nl.bsoft.roo.service.api.model.User;
import nl.bsoft.roo.storage.model.UserDao;
import nl.bsoft.roo.storage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
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
public class ServiceController implements UsersApi {

    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    private HttpServletRequest request;

    @Autowired
    public ServiceController(ObjectMapper objectMapper, HttpServletRequest request, final UserRepository userRepository) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @Operation(summary = "Register new user", tags = {"Users"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New user is registered",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Bad parameters",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)})
    public ResponseEntity<User> addUser(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody User user) {
        ResponseEntity<User> userResponse;
        UserDao userDao = convertUserToToUserDao(user);
        UserDao savedUserDao;
        Optional<UserDao> optionalUserDao;

        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                // If user has id, check if user already known
                boolean userExists = false;

                if (user.getId() != null) {
                    optionalUserDao = userRepository.findById(user.getId());
                    if (optionalUserDao.isPresent()) {
                        userExists = true;
                    }
                }
                if (!userExists) {
                    savedUserDao = userRepository.save(userDao);
                    User savedUser = convertUserDaoToUser(savedUserDao);

                    userResponse = ResponseEntity.ok(savedUser);
                    log.debug("User with id: {} saved", savedUserDao.getId());

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

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    @Operation(summary = "Delete a user", tags = {"Users"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    public ResponseEntity<Void> deleteUser(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id") Long id) {

        Optional<UserDao> optionalUserDao;
        boolean userExists = false;

        optionalUserDao = userRepository.findById(id);
        if (optionalUserDao.isPresent()) {
            userExists = true;
        }

        if (userExists) {
            userRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.error("User with id {} doesnot exist ", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    @Operation(summary = "Get a users by id", tags = {"Users"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found users",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "No user found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)})
    public ResponseEntity<User> getUser(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id") Long id) {
        ResponseEntity<User> result;

        Optional<UserDao> userDao;
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            userDao = userRepository.findById(id);

            if (userDao.isPresent()) {
                User user = convertUserDaoToUser(userDao.get());
                result = ResponseEntity.ok(user);
                return result;
            } else {
                log.error("User with id {} not found", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @Operation(summary = "Get all known users", tags = {"Users"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found users",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))}),
            @ApiResponse(responseCode = "404", description = "No users found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)})
    public ResponseEntity<List<User>> getUsers() {
        ResponseEntity<List<User>> result;

        List<UserDao> userDaos;
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            userDaos = userRepository.findAll();

            log.info("Found {} entries", userDaos.size());

            List<User> users = new ArrayList<>();

            for (UserDao userDao : userDaos) {
                User user = convertUserDaoToUser(userDao);
                users.add(user);
            }

            result = ResponseEntity.ok(users);

            return result;
        }
        log.error("Accept header not valid, value: {}", accept);
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    @Operation(summary = "Update a user", tags = {"Users"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)})
    public ResponseEntity<User> updateUser(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id") Long id, @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody User user) {
        ResponseEntity<User> userResponse;
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {


            Optional<UserDao> savedUserDao = userRepository.findById(id);
            if (savedUserDao.isPresent()) {
                if ((user.getId() == null) || ((user.getId() != null) && (id == user.getId()))) {
                    UserDao updatedUser = updateFoundUser(savedUserDao.get(), user);
                    User savedUser = convertUserDaoToUser(updatedUser);
                    updatedUser = convertUserToToUserDao(savedUser);

                    UserDao userDao = userRepository.save(updatedUser);
                    savedUser = convertUserDaoToUser(userDao);

                    userResponse = ResponseEntity.ok(savedUser);

                    return userResponse;
                } else {
                    log.error("User id {} does not match parameter id {}}", user.getId(), id);
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                log.error("User with id {} not found", user.getId());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
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
