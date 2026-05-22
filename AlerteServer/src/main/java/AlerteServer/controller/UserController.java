package AlerteServer.controller;

import AlerteServer.dto.UserDTO;
import AlerteServer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public UserDTO getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return userService.getOrCreateUserFromJwt(jwt);
    }

    @GetMapping
    public java.util.List<UserDTO> getAllUsers(@AuthenticationPrincipal Jwt jwt) {
        return userService.getAllUsers(jwt);
    }

    @org.springframework.web.bind.annotation.PatchMapping("/{id}")
    public UserDTO updateUser(@org.springframework.web.bind.annotation.PathVariable Long id,
                              @org.springframework.web.bind.annotation.RequestBody UpdateUserRequest request,
                              @AuthenticationPrincipal Jwt jwt) {
        return userService.updateUser(id, request.level(), request.region(), jwt);
    }
}

record UpdateUserRequest(Integer level, String region) {}
