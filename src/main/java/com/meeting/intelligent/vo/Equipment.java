package com.meeting.intelligent.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Equipment {
    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;
    /**
     * 设备数量
     */
    @NotNull(message = "设备数量不能为空")
    private String equipmentNumber;
}
