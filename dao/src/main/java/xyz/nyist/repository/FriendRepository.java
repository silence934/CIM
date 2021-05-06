package xyz.nyist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import xyz.nyist.entity.FriendEntity;
import xyz.nyist.entity.UserEntity;

/**
 * @Author : fucong
 * @Date: 2021-03-12 13:39
 * @Description :
 */
public interface FriendRepository extends JpaRepository<FriendEntity, Integer>, JpaSpecificationExecutor<FriendEntity> {

    @Query(value = "select t from FriendEntity t where " +
            "((t.userOne = :user1  and t.userTwo=:user2) or (t.userOne = :user2  and t.userTwo=:user1))")
    FriendEntity getByUser(UserEntity user1,UserEntity user2);

}
