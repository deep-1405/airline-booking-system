package com.deep.Repository;

import com.deep.enums.UserRole;
import com.deep.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    User findByRole(UserRole role);
//    User findUserById(Long id);
    // no functional expression of getAllUsers is done here
//    List<User> findAllUser();
}
