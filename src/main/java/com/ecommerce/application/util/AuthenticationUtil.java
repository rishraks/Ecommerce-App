package com.ecommerce.application.util;


import com.ecommerce.application.model.User;
import com.ecommerce.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class AuthenticationUtil {

    @Autowired
    private UserRepository userRepository;

    public String loggedInEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return user.getEmail();
    }

    public Long loggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return user.getUserId();
    }

    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return user;
    }
}
