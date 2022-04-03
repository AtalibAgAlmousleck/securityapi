package com.codinglevel.controller;

import com.codinglevel.common.ApplicationConstant;
import com.codinglevel.entities.User;
import com.codinglevel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping(value = "/register")
    public String joinGroup(@RequestBody User user) {
        user.setRoles(ApplicationConstant.DEFAULT_ROLE);
        String encoded_password = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded_password);
        userRepository.save(user);
        return "Hello " + user.getUserName() + " Welcome To Java Backend Developers";
    }

    //If Loggedin user is ADMIN -> ADMIN OR MODERATOR
    //If Loggedin user is MODERATOR -> MODERATOR

    @GetMapping(value = "/access/{id}/{userRole}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String giveAccessToUser(@PathVariable Long id, @PathVariable String userRole,
                                   Principal principal) {
        User user = userRepository.findById(id).get();
        List<String> activeRoles = getRolesByLoggedInUser(principal);
        String newRole = "";
        if(activeRoles.contains(userRole)) {
            newRole = user.getRoles() + "," + userRole;
            user.setRoles(newRole);
        }
        userRepository.save(user);
        return "Hello " + user.getUserName() + " New Role assign to you by " + principal.getName();
    }

    @GetMapping(value = "/all")
    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> loadUsers() {
        return userRepository.findAll();
    }

    @GetMapping(value = "/test")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String testAccess() {
        return "User can only access this !";
    }

    private List<String> getRolesByLoggedInUser(Principal principal) {
        String roles = getLoggedInUser(principal).getRoles();
        List<String> assignRoles = Arrays.stream(roles.split(","))
                .collect(Collectors.toList());
        if(assignRoles.contains("ROLE_ADMIN")) {
            return Arrays.stream(ApplicationConstant.ADMIN_ACCESS).collect(Collectors.toList());
        }
        if(assignRoles.contains("ROLE_MODERATOR")) {
            return Arrays.stream(ApplicationConstant.MODERATOR_ACCESS).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private User getLoggedInUser(Principal principal) {
        return userRepository.findByUserName(principal.getName()).get() ;
    }
}
