package xyz.nyist.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import xyz.nyist.constant.MessageType;
import xyz.nyist.entity.MessageEntity;

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
            "and t.type not in :notIn "
    )
    Page<MessageEntity> findAllByPage(Integer from, Integer to, Collection<MessageType> notIn, Pageable page);

    @Query(value = "select t from MessageEntity t where t.to=:userId and t.status='UN_READ' ")
    List<MessageEntity> getUnReadMessage(Integer userId);
}
