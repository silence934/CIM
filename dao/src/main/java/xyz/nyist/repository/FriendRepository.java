package xyz.nyist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.nyist.entity.FriendEntity;

/**
 * @Author : fucong
 * @Date: 2021-03-12 13:39
 * @Description :
 */
public interface FriendRepository extends JpaRepository<FriendEntity, Integer>, JpaSpecificationExecutor<FriendEntity> {
}
