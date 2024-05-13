package com.nure.apz.fatianov.daniil.AuthService.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-service/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserModel>> getAll(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/id")
    public ResponseEntity<UserModel> getById(@RequestParam("id") Integer id){
        return ResponseEntity.ok(userService.getById(id));
    }
}
