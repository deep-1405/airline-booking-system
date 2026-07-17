package com.deep.service.impl;

import com.deep.Repository.UserRepository;
import com.deep.mapper.UserMapper;
import com.deep.model.User;
import com.deep.payload.dto.UserDTO;
import com.deep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDTO getUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new Exception("User with " + email + " not found!");
        }
        return UserMapper.toDTO(user);
    }

    @Override
    public UserDTO getUserById(Long id) throws Exception {
//        User user = userRepository.findUserById(id);
//        if(user == null) {
//            throw new Exception("User with " + id + " not found!");
//        }
//        return user;

        // here we can call the orElseThrow because we have not defined the method expression inside the repository interface,
        // So this is being provided by the JPARepository, with the optional return type
        User user = userRepository.findById(id).orElseThrow(
                ()->new Exception("User not found with id " + id + " id")
        );

        return UserMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toDTOList(users);
    }
}
