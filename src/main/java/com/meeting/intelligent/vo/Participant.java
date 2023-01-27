package com.meeting.intelligent.vo;

import lombok.Data;

@Data
public class Participant {

    /**
     * 参会人员id
     */
    private Long userId;
    /**
     * 参会人员签到状态（0为未签到，1为已签到）
     */
    private Integer signStatus;
}