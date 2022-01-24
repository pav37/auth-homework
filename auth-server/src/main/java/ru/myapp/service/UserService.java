package ru.myapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.myapp.entity.User;
import ru.myapp.exception.UserNotFoundException;
import ru.myapp.mapper.UserMapper;
import ru.myapp.model.UserDto;
import ru.myapp.model.UserRegistrationDto;
import ru.myapp.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(u -> userMapper.toDto(u))
                .collect(Collectors.toList());
    }

    public UserDto registerUser(UserRegistrationDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setEnabled(true);
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto updateUser(User updatedUser, Long id) {
        return userRepository.findById(id)
            .map(user -> {
                validatePermissionForUser(user);
                user.setFirstName(updatedUser.getFirstName());
                user.setLastName(updatedUser.getLastName());
                return userMapper.toDto(userRepository.save(user));
            })
            .orElseThrow(() -> new UserNotFoundException("User not found id: " + id));
    }

    public UserDto getUser(Long id) {
        return userRepository.findById(id)
            .map(user -> {
                validatePermissionForUser(user);
                return userMapper.toDto(user);
            }).orElseThrow(() -> new UserNotFoundException("User not found id: " + id));
    }

    private void validatePermissionForUser(User user) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (!currentUser.getName().equalsIgnoreCase(user.getUsername())) {
            throw new AccessDeniedException("Access denied for user " + user.getUsername());
        }
    }

    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            validatePermissionForUser(user.get());
            userRepository.delete(user.get());
        }
    }
}
