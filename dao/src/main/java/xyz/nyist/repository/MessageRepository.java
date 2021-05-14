package xyz.nyist.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import xyz.nyist.constant.MessageType;
import xyz.nyist.entity.MessageEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @Author : fucong
 * @Date: 2021-03-14 13:54
 * @Description :
 */
public interface MessageRepository extends JpaRepository<MessageEntity, Integer>, JpaSpecificationExecutor<MessageEntity> {

    @Query(value = "select t from MessageEntity t where " +
            "((t.from = :from  and t.to=:to) or (t.from = :to  and t.to=:from)) " +
            "and t.type not in :notIn ")
    Page<MessageEntity> findAllByPageAndTypeNotIn(Integer from, Integer to, Collection<MessageType> notIn, Pageable page);

    @Query(value = "select t from MessageEntity t where " +
            "((t.from = :from  and t.to=:to) or (t.from = :to  and t.to=:from)) " +
            "and t.type  in :in ")
    Page<MessageEntity> findAllByPageAndTypeIn(Integer from, Integer to, Collection<MessageType> in, Pageable page);


    @Query(value = "select t from MessageEntity t where  t.to=:from and t.type  in :in ")
    Page<MessageEntity> findAllByPageAndTypeIn(Integer from, Collection<MessageType> in, Pageable page);

    @Query(value = "select t from MessageEntity t where " +
            "((t.from = :from  and t.to=:to) or (t.from = :to  and t.to=:from)) and t.isRead=false and t.time<=:time ")
    List<MessageEntity> findAllByTime(Integer from, Integer to, LocalDateTime time);

    @Query(value = "select t from MessageEntity t where " +
            "t.to = :to and t.type='ADD_FRIEND' and t.isRead=false and t.time<=:time ")
    List<MessageEntity> findVerifyMessageByTime(Integer to, LocalDateTime time);

    @Query(value = "select t from MessageEntity t where t.to=:to and t.type not in :notIn ")
    Page<MessageEntity> findAllByTo(Integer to, Collection<MessageType> notIn, Pageable page);


    @Query(value = "select t from MessageEntity t where t.to=:crowdId and t.time>:time ")
    List<MessageEntity> getUnReadMessageWithCrowd(Integer crowdId, LocalDateTime time);

    @Query(value = "select t from MessageEntity t where t.to=:userId  and t.isRead=false ")
    List<MessageEntity> getUnReadMessageWithFriend(Integer userId);
}
