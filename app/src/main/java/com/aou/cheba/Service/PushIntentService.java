package com.aou.cheba.Service;

import android.content.Context;
import android.content.Intent;

import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/12/23.
 */
public class PushIntentService extends UmengBaseIntentService {

// 如果需要打开Activity，请调用Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)；否则无法打开Activity。

    @Override
    protected void onMessage(Context context, Intent intent) {
        // 需要调用父类的函数，否则无法统计到消息送达
        super.onMessage(context, intent);
//        String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        UMessage msg = null;
        try {
            msg = new UMessage(new JSONObject(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}