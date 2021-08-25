package nl.bsoft.roo.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.bsoft.roo.service.api.UsersApi;
import nl.bsoft.roo.service.api.model.User;
import nl.bsoft.roo.storage.model.UserDao;
import nl.bsoft.roo.storage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@NoArgsConstructor
@Slf4j
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
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

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
