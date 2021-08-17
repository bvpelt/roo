package nl.bsoft.roo.service.controller;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.bsoft.roo.model.bom.User;
import nl.bsoft.roo.storage.model.UserDao;
import nl.bsoft.roo.storage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
    public ResponseEntity<User []> getUsers() {
        ResponseEntity<User[]> result = null;

        List<UserDao> userDaos =  null;
        userDaos = userRepository.findAll();

        User[] users = new User[userDaos.size()];
        int userIndex = 0;
        for (UserDao userDao: userDaos) {
            User user = convertUserDaoToUser(userDao);
            users[userIndex] = user;
        }

        result = ResponseEntity.ok(users);

        return result;
    }

    private User convertUserDaoToUser(final UserDao userDao) {
        User user = new User();
        user.setId(userDao.getId());
        user.setFirstName(userDao.getFirstName());
        user.setLastName(userDao.getLastName());
        user.setBirthDate(userDao.getBirthDate());
        return user;
    }
}
