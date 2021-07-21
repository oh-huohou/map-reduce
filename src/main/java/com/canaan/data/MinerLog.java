package com.canaan.data;

import com.canaan.lib.core.ObjectMapperWrapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class MinerLog implements Serializable {

    private static final long serialVersionUID = 6600253865619639317L;

    /**
     * 状态码 200 成功，否则有errorMessage
     */
    private Integer statusCode;

    /**
     * 功能码 0 心跳 1 登陆 2客户端关闭  3发送消息 4设置nuc扫描IP 5设置机位扫描ip 6nuc扫描 7机位扫描
     */
    private Integer functionCode;

    private String userId;

    /**
     * 矿场编号
     */
    private String farmCode;

    /**
     * nuc编号
     */
    private String nucCode;

    /**
     * 内容长度
     */
    private Long messageLength;

    /**
     * 消息
     */
    private String message;

    /**
     * 消息体
     * IP:value
     */
    private String content;
    private Map<String, Object> mapContent;
    private Map<String, NucInfo> objContent;

    public void setContent(String content) {
        this.content = content;
        if (StringUtils.isNotBlank(content)) {
            String s = content.replaceAll("\\\\", "");
            this.setMapContent(ObjectMapperWrapper.toObject(s, Map.class));
        }
    }

    public void setMapContent(Map<String, Object> mapContent) {
        this.mapContent = mapContent;
        this.objContent = new HashMap<>();
        mapContent.forEach((k, v) -> {
            NucInfo nucInfo = ObjectMapperWrapper.toObject(ObjectMapperWrapper.toJsonString(v), NucInfo.class);
            objContent.put(k, nucInfo);
        });
    }


}
