package com.cms.chatbot.dao;

import com.cms.chatbot.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationDao extends JpaRepository<Conversation, Integer> {
}
