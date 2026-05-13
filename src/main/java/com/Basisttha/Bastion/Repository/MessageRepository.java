package com.Basisttha.Bastion.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.Basisttha.Bastion.Model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m WHERE "
            + "(m.sender.id =:userA AND m.recipient.id = :userB) OR "
            + "(m.sender.id = :userB) AND (m.recipient.id = :userA) "
            + "ORDER BY m.createdAt ASC")
    List<Message> findMConversationBetween(@Param("userA") UUID userA, @Param("userB") UUID userB);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.deliveryStatus = 'READ' WHERE m.recipient.id = :userId AND m.sender.id = :contactId AND m.deliveryStatus != 'READ'")
    void markMessagesAsRead(@Param("userId") UUID userId, @Param("contactId") UUID contactId);
}
