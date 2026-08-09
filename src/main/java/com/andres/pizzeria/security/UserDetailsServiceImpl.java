package com.andres.pizzeria.security;

import com.andres.pizzeria.persistence.entity.Role;
import com.andres.pizzeria.persistence.entity.UserEntity;
import com.andres.pizzeria.persistence.entity.UserRoleEntity;
import com.andres.pizzeria.persistence.repository.UserRepository;
import com.andres.pizzeria.persistence.repository.UserRoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (UserRoleEntity userRoleEntity : userRoleRepository.findByUsername(username)) {
            Role role = userRoleEntity.getRole();

            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

            for (String extra : getExtraAuthorities(role)) {
                authorities.add(new SimpleGrantedAuthority(extra));
            }
        }

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)

                .accountLocked(user.getLocked())
                .disabled(user.getDisabled())
                .build();
    }

    private String[] getExtraAuthorities(Role role){
      if (role == Role.ADMIN || role == Role.EMPLOYEE){
          return new String[]{"RANDOM_ORDER"};
      }
      return new String[]{};
    }
}
