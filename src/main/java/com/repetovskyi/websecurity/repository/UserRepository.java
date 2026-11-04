package com.repetovskyi.websecurity.repository;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  UserRepository
  @version  1.0.0 
  @since 11/1/2026 - 08.41
*/

import com.repetovskyi.websecurity.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
}
