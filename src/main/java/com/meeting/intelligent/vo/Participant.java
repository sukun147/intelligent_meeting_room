package com.meeting.intelligent.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Participant {

    /**
     * 参会人员id
     */
    @NotNull(message = "参会人员id不能为空")
    private Long userId;
    /**
     * 参会人员签到状态（0为未签到，1为已签到）
     */
    @NotNull(message = "参会人员签到状态不能为空")
    private Integer signStatus;
}