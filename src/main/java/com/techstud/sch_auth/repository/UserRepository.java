package com.techstud.sch_auth.repository;

import com.techstud.sch_auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Long, User> {

    @Query(value = """
    SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u \s
    WHERE LOWER(u.username) = LOWER(:username)\s
          OR LOWER(u.email) = LOWER(:email)\s
          OR LOWER(u.phoneNumber) = LOWER(:phoneNumber)
   \s""")
    boolean existsByUniqueFields(@Param("username") String username,
                                 @Param("email") String email,
                                 @Param("phoneNumber") String phoneNumber);


}
