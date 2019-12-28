package com.leyou.sms.util;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.pojo.Msg;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtils {

    @Autowired
    private SmsProperties prop;

    static final Logger logger = LoggerFactory.getLogger(SmsUtils.class);
    public Msg sendMsg(String phone, String code) {
        Msg msg = new Msg();
        msg.setCode(1001);
        try {
            String[] params = {code};//数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
            SmsSingleSender ssender = new SmsSingleSender(prop.getAppid(), prop.getAppkey());
            SmsSingleSenderResult result = ssender.sendWithParam("86", phone,
                    prop.getTemplateId(), params, prop.getSmsSign(), "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            //System.out.println(result);

            JSONObject jsonObject = new JSONObject(result.toString());
            int recode = jsonObject.getInt("result");
            switch (recode) {
                case 0:
                    msg.setCode(1000);
                    msg.setMsg("短信验证码发送成功！");
                    break;
                case 1016:
                    msg.setCode(1016);
                    msg.setMsg("手机号格式错误！");
                    break;
                case 1023:
                    msg.setCode(1023);
                    msg.setMsg("请在60s后重试！");//30s内短信已达上限！
                    break;
                case 1024:
                    msg.setCode(1024);
                    msg.setMsg("请在1小时后重试！");//1小时内短信已达上限
                    break;
                default:
                    msg.setCode(1001);
                    msg.setMsg("错误码" + recode + ",请带上您的错误码反馈给客服");
            }

            // msg.setMsg(result.toString());
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
            msg.setMsg("HTTP响应码错误");
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
            msg.setMsg("json解析错误！");
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
            msg.setMsg("网络IO错误！");
        }
        logger.info("发送短信状态：{}", msg.getCode());
        logger.info("发送短信消息：{}", msg.getMsg());
        return msg;
    }



}