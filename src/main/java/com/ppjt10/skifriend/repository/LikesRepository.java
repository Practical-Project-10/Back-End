package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserIdAndFreePostId(Long userId, Long boardId);
}
