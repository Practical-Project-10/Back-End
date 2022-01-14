package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.certification.MessageService;
import com.ppjt10.skifriend.dto.chatroomdto.ChatRoomCarpoolInfoDto;
import com.ppjt10.skifriend.dto.chatroomdto.ChatRoomListResponseDto;
import com.ppjt10.skifriend.dto.chatroomdto.ChatRoomResponseDto;
import com.ppjt10.skifriend.entity.*;
import com.ppjt10.skifriend.repository.*;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final RedisRepository redisRepository;
    private final CarpoolRepository carpoolRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatUserInfoRepository chatUserInfoRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;

    //내가 참여한 모든 채팅방 목록 조회 메소드
    public List<ChatRoomListResponseDto> getAllRooms(User user) {
        Long userId = user.getId();

        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByUserId(userId);
        List<ChatRoomListResponseDto> chatRoomListResponseDtoList = new ArrayList<>();
        for (ChatUserInfo chatUserInfo : chatUserInfoList) {
            ChatRoom chatRoom = chatUserInfo.getChatRoom();
            List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomId(chatRoom.getId());
            int chatMessageSize = chatMessages.size();
            ChatMessage chatMessage = chatMessages.get(chatMessageSize - 1);
            String otherNick;
            String otherProfileImg;
            try {
                User other = userRepository.findById(chatUserInfo.getOtherId()).orElseThrow(
                        () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다")
                );
                otherNick = other.getNickname();
                otherProfileImg = other.getProfileImg();
            } catch (Exception err) {
                otherNick = "알 수 없음";
                otherProfileImg = "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/defalt+user+frofile.png";
            }

            chatRoomListResponseDtoList.add(generateChatRoomListResponseDto(chatRoom, chatMessage, chatMessageSize, otherNick, otherProfileImg, user));
        }

        chatRoomListResponseDtoList.sort(ChatRoomListResponseDto::compareTo);
        return chatRoomListResponseDtoList;
    }


    // 유저가 참여한 특정 채팅방 조회 메소드
    public ChatRoomResponseDto getRoom(Long roomId, User user) {
        Long userId = user.getId();
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다")
        );

        // 채팅방에 있는 모든 유저 정보 가져오기
        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByChatRoomId(chatRoom.getId());

        if (!chatUserInfoList.get(0).getUserId().equals(userId) && !chatUserInfoList.get(1).getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 채팅방에 참여중이 아닙니다");
        }

        Long opponentId;
        if (chatUserInfoList.get(0).getUserId().equals(userId)) {
            opponentId = chatUserInfoList.get(1).getUserId();
        } else {
            opponentId = chatUserInfoList.get(0).getUserId();
        }
        User opponent = userRepository.findById(opponentId).orElseThrow(
                () -> new IllegalArgumentException("유저가 없어용")
        );
        return generateChatRoomResponseDto(chatRoom, opponent.getNickname());
    }


    // 채팅방 생성 메소드
    @Transactional
    public ChatRoomResponseDto createChatRoom(Long carpoolId, User sender) {

        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 카풀 게시물은 존재하지 않습니다")
        );

        if (sender.getCareer() == null) {
            throw new IllegalArgumentException("프로필 작성을 진행하셔야 채팅방에 입장하실 수 있습니다");
        }

        Long writerId = carpool.getUserId();
        Long senderId = sender.getId();
        if (writerId.equals(senderId)) {
            throw new IllegalArgumentException("채팅은 다른 유저와만 가능합니다");
        }

        User writer = userRepository.findById(writerId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 유저가 없습니다")
        );

        String writerNickname = writer.getNickname();
        String writerUsername = writer.getUsername();
        String writerPhone = writer.getPhoneNum();

        ChatUserInfo chatUserInfo = chatUserInfoRepository.findByUserIdAndOtherIdAndChatRoomCarpoolId(writerId, senderId, carpoolId);

        //채팅방이 존재한다면
        if (chatUserInfo != null) {
            ChatRoom existedChatRoom = chatUserInfo.getChatRoom();
            return generateChatRoomResponseDto(existedChatRoom, writerNickname);
        } else {    //존재하지 않는다면 방을 만들어준다.
            // 방 생성 알림 메세지 글 작성자한테 전송하기
            String msg = carpool.getTitle() + "게시글에 대한 채팅이 왔습니다! 확인하세요 :)";
            messageService.createChatRoomAlert(writerPhone, msg);

            ChatRoom chatRoom = new ChatRoom(carpoolId);
            chatRoomRepository.save(chatRoom);

            //방 생성시 첫 메시지 강제전송
            ChatMessage initMsg = new ChatMessage(ChatMessage.MessageType.ENTER, chatRoom, sender.getId(), ":)");
            chatMessageRepository.save(initMsg);

            // 작성자가 안 읽은 메시지 수를 저장
            redisRepository.setLastReadMsgCnt(chatRoom.getId(), writerUsername, 1);

            //sender 정보
            ChatUserInfo chatUserInfoSender = new ChatUserInfo(sender.getId(), writer.getId(), chatRoom);
            chatUserInfoRepository.save(chatUserInfoSender);

            //카풀 작성자 정보
            ChatUserInfo chatUserInfoWriter = new ChatUserInfo(writer.getId(), sender.getId(), chatRoom);
            chatUserInfoRepository.save(chatUserInfoWriter);

            return generateChatRoomResponseDto(chatRoom, writerNickname);
        }
    }

    // 해당 채팅방에서 카풀 게시물 정보 조회 메소드
    public ChatRoomCarpoolInfoDto getCarpoolInChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다")
        );
        Carpool carpool = carpoolRepository.findById(chatRoom.getCarpoolId()).orElseThrow(
                () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다")
        );
        return generateChatRoomCarpoolInfoDto(carpool);
    }

    // 채팅방 생성
    private ChatRoomResponseDto generateChatRoomResponseDto(ChatRoom chatRoom, String nickName) {
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getId())
                .roomName(nickName)
                .longRoomId(chatRoom.getId())
                .build();
    }

    // 채팅방 목록 조회
    private ChatRoomListResponseDto generateChatRoomListResponseDto(
            ChatRoom chatRoom,
            ChatMessage chatMessage,
            int chatMessageSize,
            String otherNick,
            String otherProfileImg,
            User user
    ) {
        Long roomId = chatRoom.getId();
//        int presentChatMsgCnt = chatMessageRepository.findAllByChatRoomRoomId(roomId).size();
        int pastMsgCnt = redisRepository.getLastReadMsgCnt(roomId, user.getUsername());
        int notVerifiedMsgCnt = chatMessageSize - pastMsgCnt;

        return ChatRoomListResponseDto.builder()
                .roomId(roomId)
                .longRoomId(chatRoom.getId())
                .roomName(otherNick)
                .lastMsg(chatMessage.getMessage())
                .lastMsgTime(TimeConversion.timeChatConversion(chatMessage.getCreateAt()))
                .notVerifiedMsgCnt(notVerifiedMsgCnt)
                .userProfile(otherProfileImg)
                .build();
    }

    // 해당 채팅방에서 게시물 정보 조회
    private ChatRoomCarpoolInfoDto generateChatRoomCarpoolInfoDto(Carpool carpool) {
        return ChatRoomCarpoolInfoDto.builder()
                .title(carpool.getTitle())
                .startLocation(carpool.getStartLocation())
                .endLocation(carpool.getEndLocation())
                .date(carpool.getDate())
                .time(carpool.getTime())
                .memberNum(carpool.getMemberNum())
                .price(carpool.getPrice())
                .notice(carpool.getNotice())
                .build();

    }

}
