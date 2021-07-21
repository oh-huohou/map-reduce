package com.canaan.data;

import lombok.Data;

@Data
public class Result {

    /**
     * 电源状态参数: 0 正常，1 异常
     */
    private String pows;

    /**
     * 算力板参数: 0 正常，1 异常
     */
    private String hashs;

    /**
     * 矿池网络参数: 0 正常，1 异常
     */
    private String pools;

    /**
     * 矿机固件版本号
     */
    private String ver;

    /**
     * 主版本号
     */
    private String majorv;

    /**
     * 工作状态
     */
    private String workstatus;

    /**
     * 算力板数量
     */
    private String hashbord;

    /**
     * 风扇转速
     */
    private String fan;

    /**
     * 在每个频率下的core数
     */
    private String pll;

    /**
     * 算力板频点配置状态
     */
    private String sf;

    /**
     * 算力板所有芯片温度列表
     */
    private String pvt_t;

    /**
     * 算力板所有芯片电压列表
     */
    private String pvt_v;

    /**
     * 芯片算出的总的 nonce 值
     */
    private String mwn;

    /**
     * 频率
     */
    private String atabd;
}
