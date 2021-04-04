package xyz.nyist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.nyist.entity.CrowdEntity;

/**
 * @Author : fucong
 * @Date: 2021-03-15 20:57
 * @Description :
 */
public interface CrowdRepository extends JpaRepository<CrowdEntity, Integer>, JpaSpecificationExecutor<CrowdEntity> {


    CrowdEntity findByName(String name);

}
