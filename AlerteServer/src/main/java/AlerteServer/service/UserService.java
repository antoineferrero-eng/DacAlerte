package AlerteServer.service;

import AlerteServer.dto.UserDTO;
import AlerteServer.entity.User;
import AlerteServer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO getOrCreateUserFromJwt(Jwt jwt) {
        String mail = jwt.getClaimAsString("email");

        User user = userRepository.findByMail(mail).orElseGet(() -> {
            User newUser = new User();
            newUser.setMail(mail);
            newUser.setFirstName(jwt.getClaimAsString("given_name"));
            newUser.setLastName(jwt.getClaimAsString("family_name"));
            newUser.setLevel(0);
            return userRepository.save(newUser);
        });

        String googleFirstName = jwt.getClaimAsString("given_name");
        String googleLastName = jwt.getClaimAsString("family_name");
        if ((googleFirstName != null && !googleFirstName.equals(user.getFirstName())) ||
                (googleLastName != null && !googleLastName.equals(user.getLastName()))) {
            user.setFirstName(googleFirstName);
            user.setLastName(googleLastName);
            user = userRepository.save(user);
        }

        return new UserDTO(
                user.getId(),
                user.getMail(),
                user.getFirstName(),
                user.getLastName(),
                user.getLevel(),
                user.getRegion());
    }

    public List<UserDTO> getAllUsers(Jwt jwt) {
        User requester = userRepository.findByMail(jwt.getClaimAsString("email")).orElse(null);
        if (requester == null || requester.getLevel() < 2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return userRepository.findAll().stream()
                .map(u -> new UserDTO(u.getId(), u.getMail(), u.getFirstName(), u.getLastName(), u.getLevel(), u.getRegion()))
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(Long id, Integer newLevel, String newRegion, Jwt jwt) {
        User requester = userRepository.findByMail(jwt.getClaimAsString("email")).orElse(null);
        if (requester == null || requester.getLevel() < 2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
                
        if (user.getLevel() > 2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify users above level 2");
        }

        if (newLevel != null && newLevel > 2 && requester.getLevel() == 2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot promote a user above level 2");
        }
                
        user.setLevel(newLevel);
        if (newLevel != null && newLevel == 1) {
            user.setRegion(newRegion);
        } else {
            user.setRegion(null);
        }
        
        user = userRepository.save(user);
        return new UserDTO(user.getId(), user.getMail(), user.getFirstName(), user.getLastName(), user.getLevel(), user.getRegion());
    }
}
