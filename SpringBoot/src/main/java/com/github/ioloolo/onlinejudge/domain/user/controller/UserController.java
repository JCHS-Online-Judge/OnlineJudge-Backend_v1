package com.github.ioloolo.onlinejudge.domain.user.controller;

import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.user.controller.payload.request.*;
import com.github.ioloolo.onlinejudge.domain.user.controller.payload.response.TokenResponse;
import com.github.ioloolo.onlinejudge.domain.user.controller.payload.response.UserInfoResponse;
import com.github.ioloolo.onlinejudge.domain.user.controller.payload.response.UserListResponse;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import com.github.ioloolo.onlinejudge.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    @PutMapping("/credential")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<TokenResponse> register(
            @Validated(OrderChecks.class) @RequestBody RegisterRequest request
    ) throws Exception {

        String username = request.getUsername();
        String password = request.getPassword();
        String name = request.getName();

        service.register(username, password, name);
        String jwtToken = service.login(username, password);

        return ResponseEntity.ok(new TokenResponse(jwtToken));
    }

    @PostMapping("/credential")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<TokenResponse> login(@Validated(OrderChecks.class) @RequestBody LoginRequest request) {

        String username = request.getUsername();
        String password = request.getPassword();

        String jwtToken = service.login(username, password);

        return ResponseEntity.ok(new TokenResponse(jwtToken));
    }

    @PostMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserListResponse> getUsers() {

        List<User> users = service.getUsers();

        return ResponseEntity.ok(new UserListResponse(users));
    }

    @PostMapping
    public ResponseEntity<UserInfoResponse> getUserInfo(
            @Validated(OrderChecks.class) @RequestBody UserInfoRequest request
    ) throws Exception {

        String userId = request.getUserId();

        User userInfo = service.getUserInfo(userId);

        return ResponseEntity.ok(new UserInfoResponse(userInfo));
    }

    @PatchMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateUser(
            @Validated(OrderChecks.class) @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String userId = request.getUserId();
        String name = request.getName();
        String password = request.getPassword();

        service.updateUser(userId, name, password, userDetails.toUser());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @Validated(OrderChecks.class) @RequestBody DeleteUserRequest request
    ) throws Exception {

        String userId = request.getUserId();

        service.deleteUser(userId);

        return ResponseEntity.ok().build();
    }
}
