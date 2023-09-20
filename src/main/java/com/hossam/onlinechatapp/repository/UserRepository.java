package com.hossam.onlinechatapp.repository;

import com.hossam.onlinechatapp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findUserByEmail(String email);

}
