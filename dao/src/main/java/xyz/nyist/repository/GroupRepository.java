package xyz.nyist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.nyist.entity.GroupEntity;

/**
 * @Author : fucong
 * @Date: 2021-03-12 13:39
 * @Description :
 */
public interface GroupRepository extends JpaRepository<GroupEntity, Integer>, JpaSpecificationExecutor<GroupEntity> {
}
