package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.domain.UserStatus;
import com.debjitpal.jobportal.dto.response.UserResponse;
import com.debjitpal.jobportal.user_service.dto.UpdateUserRequest;
import com.debjitpal.jobportal.user_service.mapper.UserMapper;
import com.debjitpal.jobportal.user_service.model.User;
import com.debjitpal.jobportal.user_service.repository.UserRepository;
import com.debjitpal.jobportal.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImple implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return user;
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserResponse updateProfile(String email, UpdateUserRequest request) {
        User user = getUserByEmail(email);

        if (request.getName()!= null) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }
        return UserMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse suspendUser(UUID id) {
        User user = getUserById(id);
        user.setStatus(UserStatus.SUSPENDED);
        user.setSuspendedAt(LocalDateTime.now());
        return UserMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse activateUser(UUID id) {
        User user = getUserById(id);
        user.setStatus(UserStatus.ACTIVE);
        user.setSuspendedAt(null);
        return UserMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse deleteUser(UUID id) {
        User user = getUserById(id);
        user.setStatus(UserStatus.DELETED);
        user.setDeletedAt(LocalDateTime.now());
        return UserMapper.toUserResponse(userRepository.save(user));
    }
}
