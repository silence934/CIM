package xyz.nyist.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @Author : fucong
 * @Date: 2021-03-12 13:02
 * @Description :
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "friend")
public class FriendEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = UserEntity.class)
    @JoinColumn(name = "user_one_id")
    private UserEntity userOne;

    @Column(name = "remark_one")
    private String remarkOne;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = GroupEntity.class)
    @JoinColumn(name = "group_one_id")
    private GroupEntity groupOne;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = UserEntity.class)
    @JoinColumn(name = "user_two_id")
    private UserEntity userTwo;

    @Column(name = "remark_two")
    private String remarkTwo;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = GroupEntity.class)
    @JoinColumn(name = "group_two_id")
    private GroupEntity groupTwo;


}
