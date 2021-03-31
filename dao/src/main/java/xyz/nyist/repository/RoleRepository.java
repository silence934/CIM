package xyz.nyist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.nyist.entity.RoleEntity;

import java.util.List;

/**
 * @Author : fucong
 * @Date: 2021-02-03 13:25
 * @Description :
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {


    /**
     * 根据 displayName 查询
     *
     * @param displayName displayName
     * @return List<RoleEntity>
     */
    List<RoleEntity> findAllByDisplayName(String displayName);


}
