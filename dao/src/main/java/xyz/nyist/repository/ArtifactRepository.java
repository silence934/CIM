package xyz.nyist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.nyist.entity.ArtifactEntity;

import java.util.Optional;

/**
 * @Author : fucong
 * @Date: 2021-03-14 10:37
 * @Description :
 */
public interface ArtifactRepository extends JpaRepository<ArtifactEntity, Integer>, JpaSpecificationExecutor<ArtifactEntity> {

    Optional<ArtifactEntity> getByPath(String path);

}
