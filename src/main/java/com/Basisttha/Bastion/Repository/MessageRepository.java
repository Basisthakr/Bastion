package com.Basisttha.Bastion.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Basisttha.Bastion.Model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID>{
    @Query("SELECT m FROM Message m WHERE "+
        "(m.sender.id =:userA AND m.recipient.id = :userB) OR "+
        "(m.sender.id = :userB) AND (m.recipient.id = :userA) "+
        "ORDER BY m.createdAt ASC")
    List<Message> findMConversationBetween(@Param("userA") UUID userA, @Param("userB") UUID userB);
}
