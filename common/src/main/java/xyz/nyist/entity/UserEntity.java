package xyz.nyist.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author : fucong
 * @Date: 2021-02-02 19:57
 * @Description :
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"roles", "groups"}, callSuper = false)
@ToString(exclude = {"roles", "groups"})
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
public class UserEntity extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", length = 25, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "phone")
    private String phone;

    @Column(name = "mail")
    private String mail;

    @Column(name = "sex")
    private String sex;

    @Column(name = "enabled", columnDefinition = "TINYINT(1) default 1", nullable = false)
    private boolean enabled;

    @Column(name = "client_id")
    private String clientId;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = RoleEntity.class)
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "user_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "role_id", nullable = false)})
    @Builder.Default
    private Set<RoleEntity> roles = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", targetEntity = GroupEntity.class, cascade = CascadeType.ALL)
    private Set<GroupEntity> groups = new HashSet<>(0);

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<PermissionEntity> permissions = Stream.concat(roles.stream(), roles.stream().flatMap(r -> r.getSubRoles().stream()))
                .flatMap(r -> r.getPermissions().stream())
                .collect(Collectors.toList());
        return Stream.concat(permissions.stream(), permissions.stream().flatMap(p -> p.getSubPermissions().stream()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    public String getAvatar() {
        return avatar == null ? username : avatar;
    }
}
