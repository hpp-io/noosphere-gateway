package io.hpp.noosphere.gw.service.security;

import io.hpp.noosphere.gw.domain.User;
import io.hpp.noosphere.gw.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserRepository userRepository;

    public AuthenticationSuccessEventListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();

        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            Map<String, Object> claims = jwt.getClaims();

            String login = (String) claims.get("preferred_username");
            String userId = jwt.getSubject();

            userRepository.findById(userId).or(() -> {
                User newUser = new User();
                newUser.setId(userId);
                newUser.setLogin(login);
                newUser.setFirstName((String) claims.get("given_name"));
                newUser.setLastName((String) claims.get("family_name"));
                newUser.setEmail((String) claims.get("email"));
                userRepository.save(newUser);
                return userRepository.findById(userId);
            });
        }
    }
}
