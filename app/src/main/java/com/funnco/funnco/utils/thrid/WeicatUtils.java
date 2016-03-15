package com.funnco.funnco.utils.thrid;

import android.content.Context;
import android.graphics.Bitmap;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 微信分享工具类
 * Created by user on 2015/7/3.
 */
public class WeicatUtils {

    /**
     * @功能描述 : 添加微信平台分享
     * @return
     */
    public static void addWXPlatform(Context context,String appId,String appSecret) {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context, appId,
                appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(context,
                appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    public static void setShareContent(Context context,UMSocialService mController,UserLoginInfo user,int imageId) {
        setShareContent(context,mController,user,imageId,null);
    }

    /**
     * 设置分享内容，图片来自网络图片  对Bitmap进行分享
     * @param context
     * @param mController
     * @param user
     * @param imageId
     * @param bitmap
     */
    public static void setShareContent(Context context,UMSocialService mController,UserLoginInfo user,int imageId,Bitmap bitmap){
        UMImage urlImage = null;
        if (bitmap != null) {
            urlImage = new UMImage(context,bitmap);
        }else{
            urlImage = new UMImage(context,imageId);
        }
        String title1 = context.getString(R.string.weicat_title_1);
        String title2 = context.getString(R.string.weicat_title_2);
        String title3 = context.getString(R.string.weicat_title_3);
        String title4 = context.getString(R.string.weicat_title_4);

        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(title4);
        weixinContent.setTitle(user.getNickname() + title3);
        weixinContent.setTargetUrl(FunncoUrls.getShareScheduleUrl(user.getId()));
        weixinContent.setShareMedia(urlImage);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(title4);
        circleMedia.setTitle(title1 + user.getNickname() + title2);
        circleMedia.setShareImage(urlImage);
        circleMedia.setTargetUrl(FunncoUrls.getShareScheduleUrl(user.getId()));
        mController.setShareMedia(circleMedia);
    }
}
