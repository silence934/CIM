package xyz.nyist.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author : fucong
 * @Date: 2021-02-04 15:02
 * @Description :
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"parentRole", "subRoles", "users", "permissions"}, callSuper = false)
@ToString(exclude = {"parentRole", "subRoles", "users", "permissions"})
@Table(name = "role", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class RoleEntity extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "display_name", length = 50)
    private String displayName;


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = RoleEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private RoleEntity parentRole;


    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentRole", targetEntity = RoleEntity.class)
    private List<RoleEntity> subRoles = new ArrayList<>(0);


    @ManyToMany(fetch = FetchType.LAZY, targetEntity = UserEntity.class)
    @JoinTable(name = "user_role", joinColumns = {
            @JoinColumn(name = "role_id", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "user_id", nullable = false)})
    @Builder.Default
    private List<UserEntity> users = new ArrayList<>(0);


    @ManyToMany(fetch = FetchType.LAZY, targetEntity = PermissionEntity.class)
    @JoinTable(name = "role_permission",
            joinColumns = {@JoinColumn(name = "role_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "permission_id", nullable = false)}
    )
    @Builder.Default
    private List<PermissionEntity> permissions = new ArrayList<>();
}
