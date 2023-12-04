package com.github.ioloolo.onlinejudge.domain.user.service;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.common.security.util.JwtUtil;
import com.github.ioloolo.onlinejudge.common.util.OptionalParser;
import com.github.ioloolo.onlinejudge.domain.user.data.Role;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import com.github.ioloolo.onlinejudge.domain.user.repository.RoleRepository;
import com.github.ioloolo.onlinejudge.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    @PostConstruct
    public void init() {

        EnumSet<Role.Roles> enumSet = EnumSet.allOf(Role.Roles.class);

        for (Role.Roles role : enumSet) {
            if (roleRepository.findByRole(role).isEmpty()) {
                roleRepository.save(Role.builder().role(role).build());
            }
        }
    }

    public void register(String username, String password, String name) throws Exception {

        if (repository.existsByUsername(username)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_EXIST_USERNAME);
        }

        if (repository.existsByName(name)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_EXIST_NAME);
        }

        Role defaultRole = roleRepository.findByRole(Role.Roles.ROLE_USER).orElseThrow();

        User user = User.builder()
                .username(username)
                .password(encoder.encode(password))
                .name(name)
                .role(defaultRole)
                .build();

        repository.save(user);
    }

    public String login(String username, String password) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password
        );
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        return jwtUtil.from(authentication);
    }

    public List<User> getUsers() {

        return repository.findAll();
    }

    public User getUserInfo(String userId) throws Exception {

        User user = OptionalParser.parse(repository.findById(userId), ExceptionFactory.Type.USER_NOT_FOUND);

        user.setId(null);
        user.setPassword(null);

        return user;
    }

    public void updateUser(String userId, String name, String password, User user) throws Exception {

        User user1 = OptionalParser.parse(repository.findById(userId), ExceptionFactory.Type.USER_NOT_FOUND);

        if (!user.isAdmin() && !user1.equals(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
        }

        user1.setName(name);

        if (password != null && !password.isEmpty()) {
            user1.setPassword(encoder.encode(password));
        }

        repository.save(user1);
    }

    public void deleteUser(String userId) throws Exception {

        User user = OptionalParser.parse(repository.findById(userId), ExceptionFactory.Type.USER_NOT_FOUND);

        repository.delete(user);
    }
}
