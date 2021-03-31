package xyz.nyist.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @Author : fucong
 * @Date: 2021-03-29 15:59
 * @Description :
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user"}, callSuper = false)
@ToString(exclude = {"user"})
@Table(name = "user_security")
public class UserSecurityEntity extends BaseEntity {

    @Column(name = "question")
    private String question;

    @Column(name = "answer")
    private String answer;


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = UserEntity.class)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
