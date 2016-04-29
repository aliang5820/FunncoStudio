package com.funnco.funnco.application;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;

import com.alibaba.wukong.AuthConstants;
import com.alibaba.wukong.Callback;
import com.alibaba.wukong.WKConstants;
import com.alibaba.wukong.WKManager;
import com.alibaba.wukong.auth.AuthInfo;
import com.alibaba.wukong.auth.AuthService;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageListener;
import com.alibaba.wukong.im.MessageService;
import com.funnco.funnco.activity.chat.impl.MessageSenderImpl;
import com.funnco.funnco.bean.ImageBucket;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.bean.WorkItem;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.DebugUtil;
import com.funnco.funnco.utils.exception.HandyExceptionUtils;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.wukong.avatar.AvatarMagician;
import com.funnco.funnco.wukong.controller.ChatWindowManager;
import com.funnco.funnco.wukong.impl.AvatarMagicianImpl;
import com.funnco.funnco.wukong.receiver.AuthReceiver;
import com.funnco.funnco.wukong.route.RouteRegister;
import com.lidroid.xutils.DbUtils;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.client.CookieStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author thrman
 */
public class BaseApplication<T> extends Application {
    private static final String TAG = BaseApplication.class.getSimpleName();
    private static BaseApplication instance = null;
    //登录用户对象
    private UserLoginInfo user = null;
    //用于传递值
    private Map<String, T> map = new HashMap<>();
    //数据库操作的类
    private DbUtils dbUtils = null;
    private static final String DB_NAME = "funnco.db";

    private static boolean isFirstUser = true;
    private static boolean updateDB = false;
    private boolean hasTeam = false;
    public static boolean needRegresh = false;
    //是否是第一次添加服务  默认是第一次发布
    private boolean isFirstLaunchService = true;
    //用于第一次登陆和自动登录返回的CookieStore
    private CookieStore cookieStore = null;
    //用于传值
    public static ArrayList<WorkItem> list = new ArrayList<>();
    //用于选择头像是传值
    public static ImageBucket imageBucket;
    //本机语言环境  默认是英语
    private String lan = "en";
    private AuthReceiver mAuthReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
        }
        //捕捉全局异常信息
        /*HandyExceptionUtils handyExceptionUtils = HandyExceptionUtils.getInstance();
        handyExceptionUtils.init(this);*/
        DebugUtil.setUncaughtExceptionHandler();
        //本机语言环境
        Locale locale = getResources().getConfiguration().locale;
        String strLan = locale.getLanguage();
        lan = strLan.equals("zh") ? "cn" : strLan;
        LogUtils.e("----", "本机语言环境是：" + lan);
        dbUtils = DbUtils.create(instance, DB_NAME, 4, new DbUtils.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbUtils dbUtils, int oldVersion, int newVersion) {
                if (oldVersion < newVersion) {//更新个个表，第一个版本暂时不做处理，因为没有表需要更新
                    updateDB = true;
                }
            }
        });
        //数据发送策略
        MobclickAgent.updateOnlineConfig(this);

        initWukongIM();
        initIMUtil();
        registerMessageListener();
        registerAuthReceiver();
        RouteRegister.bootwrapped();
    }

    /**
     * 初始化 Wukong IM
     */
    private void initWukongIM(){
        // 只有用户profile（如：nickname、gender、mobile等）信息放在悟空时，才需要设置成true，默认false
        IMEngine.setUserAvailable(true);
        // 设置为上海沙箱环境
//        IMEngine.setEnvironment(WKConstants.Environment.ONLINE);
        WKManager.setEnvironment(WKConstants.Environment.SANDBOX);
        //初始化IMEngine
        IMEngine.launch(this);
        //自动登录上一次的账户
        AuthInfo authInfo = AuthService.getInstance().latestAuthInfo();
        LogUtils.e("funnco","初始化获得的 author 数据是："+ authInfo);
        if(authInfo != null && authInfo.getOpenId() != 0){
            LogUtils.e("funnco","authinfo-----openId:"+authInfo.getOpenId());
            LogUtils.e("funnco","authinfo-----status:"+authInfo.getStatus());
            LogUtils.e("funnco","authinfo-----domain:"+authInfo.getDomain());
            LogUtils.e("funnco","authinfo-----mobile:"+authInfo.getMobile());
            LogUtils.e("funnco","authinfo-----nickname:"+authInfo.getNickname());
            AuthService.getInstance().autoLogin(authInfo.getOpenId());
        }
    }
    /**
     * 初始化IM工具包
     */
    private void initIMUtil(){
        MessageSenderImpl.getInstance().init(this);
        AvatarMagicianImpl.getInstance().init(this);
        AvatarMagicianImpl.getInstance().setAvatarShape(AvatarMagician.CIRCLE_AVATAR_SHAPE);
    }
    /**
     * 注册接收消息监听器，用于更改消息未读数
     * 放在这里的原因:杀掉进程重启的时候未进入MainActivity就接收到消息了，
     * 所以如果放在主页处理会出现未读消息数异常
     */
    private void registerMessageListener(){
        IMEngine.getIMService(MessageService.class).addMessageListener(new MessageListener() {
            @Override
            public void onAdded(List<Message> list, DataType dataType) {//会话新增
                String currentChatCid = ChatWindowManager.getInstance().getCurrentChatCid();
                for (Message msg : list) {
                    if (msg.senderId() == FunncoUtils.currentOpenId()) {
                        continue;   //发送人是自己的时候，未读数不增加
                    }

                    Conversation conversation = msg.conversation();
                    if (conversation == null) {
                        continue;
                    }

                    //如果消息不属于当前会话则将累加未读数
                    if (currentChatCid == null || !currentChatCid.equals(conversation.conversationId())) {
                        msg.conversation().addUnreadCount(1);
                        newMessageNotify();
                        if (msg.isAt()) {
                            conversation.updateAtMeStatus(true);
                        }
                    }
                }
            }

            @Override
            public void onRemoved(List<Message> list) {//会话被移除
            }

            @Override
            public void onChanged(List<Message> list) {//会话修改
            }
        });
    }
    /**
     * 注册账号异常的广播监听
     */
    public void registerAuthReceiver() {
        if (mAuthReceiver == null) {
            mAuthReceiver = new AuthReceiver();
        }
        IntentFilter accountFilter = new IntentFilter();
        accountFilter.addAction(AuthConstants.Event.EVENT_AUTH_LOGOUT);
        accountFilter.addAction(AuthConstants.Event.EVENT_AUTH_KICKOUT);
        accountFilter.addAction(AuthConstants.Event.EVENT_AUTH_LOGIN);
        LocalBroadcastManager.getInstance(this).registerReceiver(mAuthReceiver, accountFilter);
    }
    private void newMessageNotify(){
        if(FunncoUtils.isAppForeground(BaseApplication.getInstance())) {
            return;
        }

        IMEngine.getIMService(ConversationService.class).getTotalUnreadCount(
                new Callback<Integer>() {
                    @Override
                    public void onSuccess(Integer unReadCount) {
                        FunncoUtils.sendNotification(unReadCount);
                    }

                    @Override
                    public void onException(String code, String reason) {

                    }

                    @Override
                    public void onProgress(Integer data, int progress) {

                    }
                }, false);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * @return Application
     */
    public static synchronized BaseApplication getInstance() {
        return instance;
    }

    /**
     * 内存低时调用该方法
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
        LogUtils.e(TAG, "LowMemory");
    }

    public void setUser(UserLoginInfo user) {
        this.user = user;
    }

    /**
     * 得到用户对象
     *
     * @return
     */
    public UserLoginInfo getUser() {
        if (user == null){
            String id = SharedPreferencesUtils.getValue(instance,"uid");
            this.user = (UserLoginInfo) new SQliteAsynchTask().selectT(dbUtils,UserLoginInfo.class,id);
        }
        return user;
    }


    public boolean isFirstLaunchService() {
        return isFirstLaunchService;
    }

    public void setIsFirstLaunchService(boolean isFirstLaunchService) {
        this.isFirstLaunchService = isFirstLaunchService;
    }

    public void setT(String key, T t) {
        map.put(key, t);
    }

    public T getT(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }

    /**
     * 移除用于传递数据的所有数据
     */
    public void removeAll() {
        if (map != null) {
            map.clear();
        }
    }

    /**
     * 移除指定的对象
     *
     * @param key
     */
    public void removeT(String key) {
        if (map != null && map.containsKey(key)) {
            map.remove(key);
        }
    }

    /**
     * 返回全局的Cookie
     *
     * @return
     */
    public CookieStore getCookieStore(Context context) {
        return cookieStore;
    }
    public CookieStore getCookieStore(){
        return getCookieStore(getInstance());
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    /**
     * 返回当前语言环境
     *
     * @return
     */
    public String getLan() {
        return lan;
    }

    /**
     * 数据库操作的类
     *
     * @return
     */
    public DbUtils getDbUtils() {
        dbUtils.configAllowTransaction(true); //标示开启事务，这样多个线程操作数据库时就不会出现问题了
        return dbUtils;
    }

    /**
     * 是否是第一次使用APP
     *
     * @return
     */
    public static boolean isFirstUser() {
        return isFirstUser;
    }

    public static void setIsFirstUser(boolean isFirstUser) {
        BaseApplication.isFirstUser = isFirstUser;
    }

    /**
     * 是否有团队
     * @return
     */
    public boolean isHasTeam() {
        if (!hasTeam){
            String has = SharedPreferencesUtils.getValue(this,"hasTeam");
            if (!TextUtils.isNull(has) && TextUtils.equals("1",has)){
                hasTeam = true;
            }else{
                hasTeam = false;
            }
        }
        return hasTeam;
    }
    @Override
    public void onTerminate() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAuthReceiver);
        super.onTerminate();
    }
    public void setHasTeam(boolean hasTeam) {
        this.hasTeam = hasTeam;
    }

    public static boolean isUpdateDB() {
        return updateDB;
    }

    public static void setUpdateDB(boolean updateDB) {
        BaseApplication.updateDB = updateDB;
    }
}

