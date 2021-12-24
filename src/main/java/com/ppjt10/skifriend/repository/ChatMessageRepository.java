package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByRoomIdOrderByCreateAtDesc(String roomId);
}
