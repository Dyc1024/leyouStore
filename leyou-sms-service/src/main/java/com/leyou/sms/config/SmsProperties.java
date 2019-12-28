package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "leyou.sms")
@Data
public class SmsProperties {
    int appid;

    String appkey;

    int templateId;

    String smsSign;

}
