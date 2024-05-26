package com.nure.apz.fatianov.daniil.AuthService.user;

import com.nure.apz.fatianov.daniil.AuthService.auth.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserModel> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserModel> userModels = new ArrayList<>();
        for (User user : users) {
            userModels.add(UserModel.toModel(user));
        }

        return userModels;
    }

    public UserModel getById(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        return UserModel.toModel(user);
    }
}
