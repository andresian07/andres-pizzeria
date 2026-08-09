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
        if (userRepository.count() == 0) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            UserRoleEntity adminRole = new UserRoleEntity("admin", Role.ADMIN);
            admin.setLocked(false);
            admin.setDisabled(false);

            UserEntity employee = new UserEntity();
            employee.setUsername("employee");
            employee.setPassword(passwordEncoder.encode("employee123"));
            UserRoleEntity employeeRole = new UserRoleEntity("employee", Role.EMPLOYEE);
            employee.setLocked(false);
            employee.setDisabled(false);

            userRepository.save(admin);
            userRoleRepository.save(employeeRole);
            userRoleRepository.save(adminRole);
            userRepository.save(employee);
        }
    }
}
