package com.example.flightapp.auth.api;

import com.example.flightapp.auth.model.CurrentUserResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CurrentUserController {

    @GetMapping("/me")
    public CurrentUserResponse me(Principal principal) {
        Authentication authentication = (Authentication) principal;
        List<String> roles = new ArrayList<String>();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            roles.add(authority.getAuthority());
        }
        return new CurrentUserResponse(authentication.getName(), roles);
    }
}
