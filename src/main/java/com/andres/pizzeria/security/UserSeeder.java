package com.andres.pizzeria.security;

import com.andres.pizzeria.persistence.entity.Role;
import com.andres.pizzeria.persistence.entity.UserEntity;
import com.andres.pizzeria.persistence.entity.UserRoleEntity;
import com.andres.pizzeria.persistence.repository.UserRepository;
import com.andres.pizzeria.persistence.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUserIfMissing("admin", "admin123", Role.ADMIN);
        seedUserIfMissing("employee", "employee123", Role.EMPLOYEE);
        seedUserIfMissing("customer", "customer123", Role.CUSTOMER);
    }

    private void seedUserIfMissing(String username, String rawPassword, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setLocked(false);
        user.setDisabled(false);
        userRepository.save(user);

        userRoleRepository.save(new UserRoleEntity(username, role));
    }
}
