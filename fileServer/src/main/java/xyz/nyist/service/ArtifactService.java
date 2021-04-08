package xyz.nyist.service;

import cn.hutool.core.io.file.FileNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.nyist.entity.ArtifactEntity;
import xyz.nyist.exception.CimException;
import xyz.nyist.repository.ArtifactRepository;
import xyz.nyist.result.ResultCode;

import java.util.Optional;

/**
 * @Author : fucong
 * @Date: 2021-03-14 10:39
 * @Description :
 */
@Slf4j
@Service
public class ArtifactService {
    @Autowired
    private ArtifactRepository artifactRepository;

    public ArtifactEntity create(String path) {
        return artifactRepository.saveAndFlush(ArtifactEntity.builder()
                .fileName(FileNameUtil.getName(path))
                .path(path)
                .build());
    }

    public ArtifactEntity getById(Integer id) {
        return artifactRepository.findById(id).orElseThrow(() -> new CimException(ResultCode.SYSTEM_RESOURCE_ERROR, "资源[%d]不存在", id));
    }

    public ArtifactEntity getByPath(String path) {
        return artifactRepository.getByPath(path).orElseThrow(() -> new CimException(ResultCode.SYSTEM_RESOURCE_ERROR, "文件不存在", path));
    }

    public void deleteById(Integer id) {
        ArtifactEntity entity = getById(id);
        artifactRepository.delete(entity);
    }

    public void deleteByPath(String path) {
        Optional<ArtifactEntity> optional = artifactRepository.getByPath(path);
        optional.ifPresent(artifactEntity -> artifactRepository.delete(artifactEntity));
    }

    public void update(ArtifactEntity artifact) {
        artifactRepository.saveAndFlush(artifact);
    }
}
