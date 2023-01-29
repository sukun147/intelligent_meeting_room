package com.meeting.intelligent.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignInVo {
    /**
     * 会议id
     */
    @NotNull(message = "会议id不能为空")
    private Long meetingId;
    /**
     * 人脸照片（base64）
     */
    @NotBlank(message = "人脸照片不能为空")
    private String facePhoto;
}
