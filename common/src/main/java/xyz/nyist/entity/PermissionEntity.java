package xyz.nyist.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
@EqualsAndHashCode(exclude = {"parentPermission", "roles", "subPermissions"}, callSuper = false)
@ToString(exclude = {"parentPermission", "roles", "subPermissions"})
@Table(name = "permission", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class PermissionEntity extends BaseEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "display_name", length = 50)
    private String displayName;


    @Column(name = "url", length = 255)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = PermissionEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private PermissionEntity parentPermission;


    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentPermission", targetEntity = PermissionEntity.class)
    private Set<PermissionEntity> subPermissions = new HashSet<>(0);


    @ManyToMany(fetch = FetchType.LAZY, targetEntity = RoleEntity.class)
    @JoinTable(name = "role_permission",
            joinColumns = {@JoinColumn(name = "permission_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "role_id", nullable = false)}
    )
    @Builder.Default
    private Set<RoleEntity> roles = new HashSet<>(0);

    @Override
    public String getAuthority() {
        return name;
    }
}
