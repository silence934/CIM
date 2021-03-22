package xyz.nyist.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author : fucong
 * @Date: 2021-03-15 20:41
 * @Description :
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"crowdUsers"}, callSuper = false)
@ToString(exclude = {"crowdUsers"})
@Table(name = "crow")
public class CrowdEntity extends BaseEntity {

    @Column(name = "`name`")
    private String name;

    @Column(name = "avatar")
    private String avatar;

    /**
     * 群主id
     */
    @Column(name = "`lord_id`")
    private Integer lordId;

    /**
     * 公告
     */
    @Column(name = "`announcement`", columnDefinition = "TEXT")
    private String announcement;


    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "crowd", targetEntity = CrowdUserEntity.class, cascade = CascadeType.ALL)
    private List<CrowdUserEntity> crowdUsers = new ArrayList<>(0);


    public List<UserEntity> getUsers() {
        return crowdUsers.stream().map(CrowdUserEntity::getUser).collect(Collectors.toList());
    }
}
