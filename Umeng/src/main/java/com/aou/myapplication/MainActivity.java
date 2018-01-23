package com.aou.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {


    private Button shareButton;

/*    // 友盟分享的服务
    private UMSocialService controller = null;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shareButton = (Button) findViewById(R.id.btn_share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    controller.openShare(MainActivity.this, false);
            }
        });
/*
        initShareConfig();
        setShareContent();*/
    }
/*
    *//**
     * 初始化与友盟分享SDK的信息
     *//*
    private void initShareConfig() {
        // context = this;
        controller = UMServiceFactory.getUMSocialService("com.umeng.share");

        // // 设置分享的内容
        // controller
        // .setShareContent("在这里设置要分享的内容");
        //
        // // 设置分享的图片
        // String imageUrl =
        // "http://img3.imgtn.bdimg.com/it/u=67026920,1094670043&fm=21&gp=0.jpg";
        // controller.setShareMedia(new UMImage(context, imageUrl));

        // SocializeConfig config = controller.getConfig();
        // 开通短信分享
        // config.setShareSms(true);
        // 开通Email分享
        // config.setShareMail(true);

        // config.setPlatforms(SHARE_MEDIA.QZONE, SHARE_MEDIA.TENCENT);

        // 微信平台的appID，要进行申请，跟eclipse的签名有关
        String appID = "wx79ee8a2e6fdf7ae0";
        String appSecret = "7a2e921d4129d6d4c3098f738da4ede5";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(this, appID, appSecret);
        wxHandler.addToSocialSDK();
        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(this, appID, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();



        // 添加QQ分享平台
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "1105764120",
                "AkmXgiVWyyUmYupY");
        qqSsoHandler.addToSocialSDK();

        // 添加QQ空间分享平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this,
                "1105764120", "AkmXgiVWyyUmYupY");
        qZoneSsoHandler.addToSocialSDK();

        // 新浪微博需要去申请授权后才能使用
        // 设置新浪SSO handler
        controller.getConfig().setSsoHandler(new SinaSsoHandler(this));

        // 设置腾讯微博SSO handler
        controller.getConfig().setSsoHandler(new TencentWBSsoHandler());
    }

    *//**
     * 根据不同的平台设置不同的分享内容</br>
     *//*
    private void setShareContent() {

        // 图片分享
        UMImage localImage = new UMImage(this, R.drawable.icon);
        UMImage urlImage = new UMImage(this,
                "http://inavy.cn:4888/icon.png");
        // UMImage resImage = new UMImage(LoginActivity.this, R.drawable.icon);

        // 视频分享
        UMVideo video = new UMVideo(
                "http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
        // vedio.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
        video.setTitle("友盟社会化组件视频");
        video.setThumb(urlImage);
        video.setThumb(new UMImage(this, BitmapFactory.decodeResource(
                getResources(), R.drawable.icon)));

        // 音乐分享
        UMusic uMusic = new UMusic(
                "http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
        uMusic.setAuthor("umeng");
        uMusic.setTitle("天籁之音");
        uMusic.setThumb(urlImage);
        // uMusic.setThumb("http://www.umeng.com/images/pic/social/chart_1.png");

        // 分享到微信好友的内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent("更多详情尽在车吧，微信");
        weixinContent.setTitle("微信分享");
        weixinContent.setTargetUrl("http://www.anou.net.cn:4888/CarbarFileServer/app/icarba.apk");
        weixinContent.setShareMedia(urlImage);
        controller.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent("更多详情尽在车吧，朋友圈");
        circleMedia.setTitle("朋友圈");
        circleMedia.setShareImage(urlImage);
        // circleMedia.setShareMedia(uMusic);
        // circleMedia.setShareMedia(video);
        circleMedia.setTargetUrl("http://www.anou.net.cn:4888/CarbarFileServer/app/icarba.apk");
        controller.setShareMedia(circleMedia);

        UMImage qzoneImage = new UMImage(this,
                "http://inavy.cn:4888/icon.png");
        qzoneImage
                .setTargetUrl("http://inavy.cn:4888/icon.png");

        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent("这里是分享的内容");
        qzone.setTargetUrl("http://www.anou.net.cn:4888/CarbarFileServer/app/icarba.apk");
        qzone.setTitle("这里是分享的标题");
        qzone.setShareImage(urlImage);
        controller.setShareMedia(qzone);

        // 设置QQ 分享内容
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent("更多详情尽在车吧 -- QQ");
        qqShareContent.setTitle("hello, title");
        qqShareContent.setShareImage(urlImage);
        // qqShareContent.setShareMusic(uMusic);
        // qqShareContent.setShareVideo(video);
        qqShareContent.setTargetUrl("http://www.anou.net.cn:4888/CarbarFileServer/app/icarba.apk");
        controller.setShareMedia(qqShareContent);

        // 视频分享
        UMVideo umVideo = new UMVideo(
                "http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
        umVideo.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
        umVideo.setTitle("友盟社会化组件视频");

        // 腾讯微博分享的内容
        TencentWbShareContent tencent = new TencentWbShareContent();
        tencent.setShareContent("更多详情尽在车吧，腾讯微博");
        // 设置tencent分享内容
        controller.setShareMedia(tencent);

        // // 设置邮件分享内容， 如果需要分享图片则只支持本地图片
        // MailShareContent mail = new MailShareContent(localImage);
        // mail.setTitle("share form umeng social sdk");
        // mail.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能，email");
        // // 设置mail分享内容
        // controller.setShareMedia(mail);

        // // 设置短信分享内容
        // SmsShareContent sms = new SmsShareContent();
        // sms.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能，短信");
        // sms.setShareImage(urlImage);
        // controller.setShareMedia(sms);

        // 新浪微博分享的内容
        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setShareContent("更多详情尽在车吧，新浪微博");
        // sinaContent.setShareImage(new UMImage( this,
        // R.drawable.actionbar_back_indicator));

        sinaContent.setTargetUrl("http://www.anou.net.cn:4888/CarbarFileServer/app/icarba.apk");
        sinaContent.setTitle("这里是分享的标题");
     //   sinaContent.setShareImage(urlImage);

        controller.setShareMedia(sinaContent);

    }*/

}
