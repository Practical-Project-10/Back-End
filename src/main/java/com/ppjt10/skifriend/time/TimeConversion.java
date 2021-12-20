package com.ppjt10.skifriend.time;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeConversion {
    public static String timeConversion(LocalDateTime createdAt) {
        LocalDateTime currentTime = LocalDateTime.now();
        Long timeDiff = Duration.between(createdAt, currentTime).getSeconds();
        String resultConversion = "";

        // 몇 개월 전 만들기

        if ((timeDiff / 86400) > 0) { // 일
            resultConversion = timeDiff / 86400 + "일 전";
        } else if ((timeDiff / 3600) > 0) { // 시간
            resultConversion = timeDiff / 3600 + "시간 전";
        } else if ((timeDiff / 60) > 0) { // 분
            resultConversion = timeDiff / 60 + "분 전";
        } else {
            resultConversion = timeDiff + "초 전";
        }

        return resultConversion;
    }
}
