package xyz.nyist.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author : fucong
 * @Date: 2021-03-14 10:33
 * @Description :
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "`artifact`")
public class ArtifactEntity extends BaseEntity {

    @Column(name = "`path`", nullable = false)
    private String path;

    @Column(name = "`file_name`", nullable = false)
    private String fileName;

    @Column(name = "is_use", columnDefinition = "TINYINT(1) default 0", nullable = false)
    private boolean isUse;
}
