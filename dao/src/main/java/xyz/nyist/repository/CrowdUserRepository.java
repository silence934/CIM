package xyz.nyist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.nyist.entity.CrowdEntity;
import xyz.nyist.entity.CrowdUserEntity;
import xyz.nyist.entity.UserEntity;

import java.util.Optional;

/**
 * @Author : fucong
 * @Date: 2021-03-21 22:06
 * @Description :
 */
public interface CrowdUserRepository extends JpaRepository<CrowdUserEntity, Integer>, JpaSpecificationExecutor<CrowdUserEntity> {


    Optional<CrowdUserEntity> findByCrowdAndUser(CrowdEntity crowd, UserEntity user);

}
