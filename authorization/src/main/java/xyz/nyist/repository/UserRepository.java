package xyz.nyist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.nyist.entity.UserEntity;

import java.util.Optional;

/**
 * @Author : fucong
 * @Date: 2021-02-03 13:25
 * @Description :
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {


    /**
     * 根据 username 查询
     *
     * @param username username
     * @return UserEntity
     */
    Optional<UserEntity> findByUsername(String username);

}
