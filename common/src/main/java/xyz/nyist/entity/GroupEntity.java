package xyz.nyist.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author : fucong
 * @Date: 2021-03-12 12:48
 * @Description :
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"friendOnes", "friendTwos"}, callSuper = false)
@ToString(exclude = {"friendOnes", "friendTwos"})
@Table(name = "`group`", indexes = @Index(columnList = "`name`"))
public class GroupEntity extends BaseEntity {

    @Column(name = "`name`", length = 25, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = UserEntity.class)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groupOne", targetEntity = FriendEntity.class, cascade = CascadeType.ALL)
    private Set<FriendEntity> friendOnes = new HashSet<>(0);

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groupTwo", targetEntity = FriendEntity.class, cascade = CascadeType.ALL)
    private Set<FriendEntity> friendTwos = new HashSet<>(0);

    public Set<FriendEntity> getFriends() {
        return Stream.concat(friendOnes.stream(), friendTwos.stream())
                .collect(Collectors.toSet());
    }
}
