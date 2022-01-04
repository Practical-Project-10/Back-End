package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.chatroomdto.ChatRoomListResponseDto;
import com.ppjt10.skifriend.dto.chatroomdto.ChatRoomResponseDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 내가 참여한 모든 채팅방 목록 조회
    @GetMapping("/chat/rooms")
    public ResponseEntity<List<ChatRoomListResponseDto>> getAllRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(chatRoomService.getAllRooms(user));
    }

    // 채팅방 생성
    @PostMapping("/chat/room/{carpoolId}")
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(@PathVariable Long carpoolId,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(chatRoomService.createChatRoom(carpoolId, user));
    }

    // 특정 채팅방 조회
    @GetMapping("/chat/room/{roomId}")
    public ResponseEntity<ChatRoomResponseDto> getRoom(@PathVariable String roomId,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(chatRoomService.getRoom(roomId, user));
    }

}