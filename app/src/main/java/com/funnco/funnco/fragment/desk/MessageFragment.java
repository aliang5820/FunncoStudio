package com.funnco.funnco.fragment.desk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.wukong.AuthConstants;
import com.alibaba.wukong.Callback;
import com.alibaba.wukong.auth.AuthService;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Member;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageBuilder;
import com.alibaba.wukong.im.MessageContent;
import com.alibaba.wukong.im.MessageListener;
import com.alibaba.wukong.im.MessageService;
import com.alibaba.wukong.im.User;
import com.alibaba.wukong.im.UserService;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.activity.login.LoginActivity;
import com.funnco.funnco.activity.notification.NotificationRemindActivity;
import com.funnco.funnco.activity.notification.NotificationSystemActivity;
import com.funnco.funnco.activity.notification.NotificationTeamInviteActivity;
import com.funnco.funnco.activity.team.TeamMemberChooseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.NotificationLastRemindAndSystem;
import com.funnco.funnco.bean.TeamInvite;
import com.funnco.funnco.bean.TeamMember;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.fragment.BaseFragment;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.date.TimeUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.listview.MessageListHeaderView;
import com.funnco.funnco.view.listview.XListView;
import com.funnco.funnco.view.textview.DesignTextView;
import com.funnco.funnco.wukong.model.GroupSession;
import com.funnco.funnco.wukong.model.Session;
import com.funnco.funnco.wukong.user.GroupChatActivity;
import com.funnco.funnco.wukong.user.SingleChatActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2.1添加
 * 消息 聊天
 * Created by user on 2015/10/12.
 */
public class MessageFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_CODE_CHATTING = 0xf100;
    private static final int REQUEST_CODE_CHATTING_CREATE = 0xf120;
    private static final int RESULT_CODE_CHATTING_CREATE = 0xf121;
    private static final int REQUEST_CODE_NOTIFICATION = 0xf110;
    private static final int RESULT_CODE_NOTIFICATION = 0xf111;

    private XListView xListView;
    private CommonAdapter adapter;
    private List<Conversation> list = new ArrayList<>();
    private Map<Long, TeamMember> memberMap = new HashMap<>();
    private UserLoginInfo user;
    private UserService userService;
    private static int AVATAR_MAX_NUM = 4;
    private LocalBroadcastManager localBroadcastManager;
    private int conversationListSize = 200;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageButton imageButton;
    private PopupWindow popupWindow;
    private View vCreateConversation;
    private MessageService messageService;
    private MessageListHeaderView remindView;
    private MessageListHeaderView systemView;
    private MessageListHeaderView teamMsgView;
    private boolean hasSingleCon = false;
    private boolean hasGroupCon = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (xListView != null) {
                    xListView.stopRefresh();
                    xListView.stopLoadMore();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.layout_fragment_message, container, false);
        init();
        initViews();
        initEvents();
        return parentView;
    }

    @Override
    protected void initViews() {
        user = BaseApplication.getInstance().getUser();

        userService = IMEngine.getIMService(UserService.class);
        imageLoader = ((MainActivity) mActivity).getImageLoader();
        options = ((MainActivity) mActivity).getOptions();
        imageButton = (ImageButton) findViewById(R.id.id_rview);
        findViewById(R.id.id_lview).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.id_mview)).setText(R.string.str_message_my);
        xListView = (XListView) findViewById(R.id.id_listView);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
        xListView.setHeaderVisibleState(true);
        xListView.setFooterVisibleState(false);

        vCreateConversation = mActivity.getLayoutInflater().inflate(R.layout.layout_popupwindow_chatcreate, null);


        adapter = new CommonAdapter<Conversation>(mContext, list, R.layout.layout_item_message) {
            @Override
            public void convert(ViewHolder helper, final Conversation item, int position) {
                long peerId = item.getPeerId();//单聊对象的openId 群聊则为0
                long lastModify = item.getLastModify();//最后发送的时间
                String icon = item.icon();
                String conversationId = item.conversationId();
                int unreadCount = item.unreadMessageCount();
                String nickName = item.title();
                int type = item.type();
                String lastMessage = "";
                if (item.latestMessage() != null && null != item.latestMessage().messageContent()) {
                    LogUtils.d("消息", item.latestMessage().messageContent().toString());
                    int lastetMessageType = item.latestMessage().messageContent().type();
                    switch (lastetMessageType) {
                        case MessageContent.MessageContentType.AUDIO:
                            lastMessage = "[语音]";
                            break;
                        case MessageContent.MessageContentType.FILE:
                            lastMessage = "[文件]";
                            break;
                        case MessageContent.MessageContentType.IMAGE:
                            lastMessage = "[图片]";
                            break;
                        case MessageContent.MessageContentType.LINKED:
                            lastMessage = "[链接]";
                            break;
                        case MessageContent.MessageContentType.TEXT:
                            lastMessage = JsonUtils.getStringByKey4JOb(item.latestMessage().messageContent().toString(), "txt");
                            break;
                        case MessageContent.MessageContentType.UNKNOWN:
                            lastMessage = "[未知]";
                            break;
                        default:
                            lastMessage = "";
                            break;
                    }
                }
                helper.setText(R.id.id_title_5, lastMessage + "");
                CircleImageView circleImageView = helper.getView(R.id.id_imageview);
                if (type == Conversation.ConversationType.CHAT) {
                    if (memberMap != null && memberMap.containsKey(peerId)) {
                        helper.setText(R.id.id_title_4, memberMap.get(peerId).getNickname());
                        imageLoader.displayImage(memberMap.get(peerId).getHeadpic(), circleImageView);
                    } else {
                        helper.setText(R.id.id_title_4, nickName);
                        circleImageView.setImageResource(R.mipmap.my_kehu);
                    }
                } else if (type == Conversation.ConversationType.GROUP) {
                    helper.setText(R.id.id_title_4, nickName + "");
                    helper.setImageResource(R.id.id_imageview, R.mipmap.my_group);
                }

                DesignTextView unreadCountTv = helper.getView(R.id.id_title_3);
                unreadCountTv.invalidate();
                unreadCountTv.setText(unreadCount);

//                helper.setText(R.id.id_title_3, unreadCount);
                helper.getView(R.id.tip).setVisibility(View.GONE);
                helper.setText(R.id.id_title_6, DateUtils.getDate(lastModify, DateUtils.isCurrentDay(lastModify) ? "HH:mm" : "MM-dd"));//最近一条信息的时间

                helper.setCommonListener(R.id.id_title_0, new Post() {//置顶
                    @Override
                    public void post(int... position) {
                        item.stayOnTop(true, new Callback<Long>() {
                            @Override
                            public void onSuccess(Long aLong) {
                                getConversationList(conversationListSize);//单聊 或者是群聊
                            }

                            @Override
                            public void onException(String s, String s1) {
                            }

                            @Override
                            public void onProgress(Long aLong, int i) {
                            }
                        });
                    }
                });
                helper.setCommonListener(R.id.id_title_1, new Post() {//标为未读
                    @Override
                    public void post(int... position) {
                        item.addUnreadCount(1);
                        getConversationList(conversationListSize);//单聊 或者是群聊
                    }
                });
                helper.setCommonListener(R.id.id_title_2, new Post() {//删除
                    @Override
                    public void post(int... position) {
                        item.remove();
                        getConversationList(conversationListSize);//单聊 或者是群聊
                    }
                });
            }
        };
        adapter.isTag(true, new int[]{R.id.id_title_0, R.id.id_title_1, R.id.id_title_2});
        //设置3个系统级消息
        //服务提醒
        remindView = new MessageListHeaderView(mContext);
        remindView.setIcon(R.mipmap.msg_service);
        remindView.setTitle("服务提醒");
        xListView.addHeaderView(remindView);
        //系统通知
        systemView = new MessageListHeaderView(mContext);
        systemView.setIcon(R.mipmap.msg_sys);
        systemView.setTitle("系统通知");
        xListView.addHeaderView(systemView);
        //团队通知
        teamMsgView = new MessageListHeaderView(mContext);
        teamMsgView.setIcon(R.mipmap.msg_team);
        teamMsgView.setTitle("团队通知");
        xListView.addHeaderView(teamMsgView);

        xListView.setAdapter(adapter);
        initLocalBroadcastManager();
        initMessageListener();
        getConversationList(conversationListSize);//单聊 或者是群聊
    }

    private void initMessageListener() {
        messageService = IMEngine.getIMService(MessageService.class);
        messageService.addMessageListener(new MessageListener() {
            @Override
            public void onAdded(List<Message> list, DataType dataType) {
                getConversationList(conversationListSize);
            }

            @Override
            public void onRemoved(List<Message> list) {
                getConversationList(conversationListSize);
            }

            @Override
            public void onChanged(List<Message> list) {
                getConversationList(conversationListSize);
            }
        });
    }

    private void getConversationList(int size) {
        if (!AuthService.getInstance().isLogin()) {
            FunncoUtils.showAlertDialog(mContext, R.string.str_login_wukong_no, new FunncoUtils.DialogCallback() {
                @Override
                public void onPositive() {
                    startActivity(LoginActivity.class);
                    mActivity.finish();
                }
            });
            return;
        }
        /**
         * callback Callback<List<Conversation>>()
         * size 拉取数量
         * type 群聊类型，单聊或者群聊
         */
        IMEngine.getIMService(ConversationService.class).listConversations(new Callback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations == null || conversations.size() <= 0) {
                    return;
                }
                list.clear();
                list.addAll(conversations);
                xListView.setRefreshTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
                xListView.stopLoadMore();
                xListView.stopRefresh();

                for (Conversation conversation : conversations) {
                    long peerId = conversation.getPeerId();
                    if (conversation.type() == Conversation.ConversationType.CHAT && !memberMap.containsKey(peerId)) {
                        TeamMember tm = (TeamMember) SQliteAsynchTask.selectT(dbUtils, TeamMember.class, peerId + "");
                        if (tm != null) {
                            memberMap.put(peerId, tm);
                        }

                    }

                    LogUtils.e("funnco------", "" + conversation + "\n getPeerId:" + conversation.getPeerId() + "\n getGroupLevel:"
                            + conversation.getGroupLevel() + "\n getLastModify:" + conversation.getLastModify()
                            + "\n getMemberLimit:" + conversation.getMemberLimit() + "\n getOnlyOwnerModifiable:"
                            + conversation.getOnlyOwnerModifiable() + "\n conversationId:" + conversation.conversationId()
                            + "\n getTop:" + conversation.getTop() + "\n "
                            + "\n icon:" + conversation.icon() + "\n unreadMessageCount:" + conversation.unreadMessageCount()
                            + "\n title:" + conversation.title() + "\n createdAt:" + conversation.createdAt() + "\n latestMessage:" + conversation.latestMessage()
                            + "\n totalMembers:" + conversation.totalMembers() + "\n type:" + conversation.type());
                    userService.getUser(new Callback<User>() {
                        @Override
                        public void onSuccess(User user) {
                            LogUtils.e("funnco", " avatar : " + user.avatar()
                                    + "\n nickname : " + user.nickname()
                                    + "\n alias : " + user.alias()
                                    + "\n remark : " + user.remark());
                        }

                        @Override
                        public void onException(String s, String s1) {
                            LogUtils.e("funnco", "s : " + s + "\n s1 : " + s1);
                        }

                        @Override
                        public void onProgress(User user, int i) {

                        }
                    }, conversation.getPeerId());
                    conversation.getMembers(new Callback<List<Member>>() {
                        @Override
                        public void onSuccess(List<Member> members) {
                            for (Member mb : members) {
                                LogUtils.e("funnco------", "获得的聊天成员是：roletype:" + mb.roleType() + " \n nickname:" +
                                        mb.user().nickname() + " \navatar:" + mb.user().avatar());
                            }
                        }

                        @Override
                        public void onException(String s, String s1) {

                        }

                        @Override
                        public void onProgress(List<Member> members, int i) {

                        }
                    });

                }
                adapter.setmDatas(list);
            }

            @Override
            public void onException(String s, String s1) {
            }

            @Override
            public void onProgress(List<Conversation> conversations, int i) {
            }
        }, size, Conversation.ConversationType.CHAT | Conversation.ConversationType.GROUP);
    }

    private void initLocalBroadcastManager() {
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(AuthConstants.Event.EVENT_AUTH_LOGIN);
        intentFilter.addAction(AuthConstants.Event.EVENT_AUTH_LOGOUT);
        intentFilter.addAction(AuthConstants.Event.EVENT_AUTH_KICKOUT);

        localBroadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (AuthConstants.Event.EVENT_AUTH_LOGIN.equals(action)) {
                    //登录成功
                    LogUtils.e("funnco---MessageFragment", "登录成功！！！");

                } else if (AuthConstants.Event.EVENT_AUTH_LOGOUT.equals(action)) {
                    //退出登录
                    LogUtils.e("funnco---MessageFragment", "退出登录！！！");

                } else if (AuthConstants.Event.EVENT_AUTH_KICKOUT.equals(action)) {
                    //被踢断线
                    LogUtils.e("funnco---MessageFragment", "被踢下线！！！");
                    FunncoUtils.showAlertDialog(mContext, R.string.str_chat_kicked_offline, new FunncoUtils.DialogCallback() {
                        @Override
                        public void onPositive() {
                            startActivity(LoginActivity.class);
                            mActivity.finish();
                        }
                    });
                }
            }
        }, intentFilter);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.id_rview).setOnClickListener(this);
        vCreateConversation.findViewById(R.id.id_title_0).setOnClickListener(this);
        vCreateConversation.findViewById(R.id.id_title_1).setOnClickListener(this);
        if (xListView != null) {
            xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //要抛开头部的3个系统消息
                    if(position == 1){
                        //提醒通知
                        Intent intent = new Intent().setClass(mContext, NotificationRemindActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_NOTIFICATION);
                    } else if(position == 2){
                        //系统通知
                        Intent intent = new Intent().setClass(mContext, NotificationSystemActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_NOTIFICATION);
                    } else if(position == 3){
                        //团队通知
                        Intent intent = new Intent().setClass(mContext, NotificationTeamInviteActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_NOTIFICATION);
                    } else {
                        Conversation conversation = list.get(position - 4);
                        conversation.resetUnreadCount();
                        adapter.notifyDataSetChanged();
                        Intent intent = new Intent(mContext, conversation.type() == Conversation.ConversationType.CHAT ? SingleChatActivity.class : GroupChatActivity.class);
                        TeamMember tm = memberMap.get(conversation.getPeerId());
                        if (tm != null) {
                            intent.putExtra("title", tm.getNickname());
                        }
                        intent.putExtra(Session.SESSION_INTENT_KEY, new GroupSession(conversation));
                        startActivity(intent);
                    }
                }
            });
            xListView.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    clearAsyncTask();
                    handler.sendEmptyMessageDelayed(0, 3000);
                    getConversationList(conversationListSize);//单聊 或者是群聊
                    getLastSystemMessage();
                }

                @Override
                public void onLoadMore() {
                    clearAsyncTask();
                    handler.sendEmptyMessageDelayed(0, 3000);
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLastSystemMessage();
    }

    @Override
    protected void init() {

    }

    @Override
    public void onMainAction(String data) {
    }

    @Override
    public void onMainData(List<?>... list) {
    }

    private void showPopupwindow(View view) {
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setClippingEnabled(true);
        popupWindow.setFocusable(true);
        popupWindow.setSplitTouchEnabled(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(imageButton, 0, 0);
    }

    private boolean dismissPopupwindow() {
        boolean flag = false;
        for (PopupWindow pw : new PopupWindow[]{popupWindow}) {
            if (pw != null && pw.isShowing()) {
                pw.dismiss();
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_rview://右上角 发起聊天
                showPopupwindow(vCreateConversation);
                break;
            case R.id.id_title_0://单聊
                isSinglechatting = true;
                Intent intent1 = new Intent();
                intent1.putExtra("isChat", true);
                intent1.putExtra("chooseMode", true);
                intent1.setClass(mContext, TeamMemberChooseActivity.class);
                dismissPopupwindow();
                startActivityForResult(intent1, REQUEST_CODE_CHATTING_CREATE);
                break;
            case R.id.id_title_1://群聊
                isSinglechatting = false;
                Intent intent2 = new Intent();
                intent2.putExtra("isChat", true);
                intent2.putExtra("chooseMode", false);
                intent2.setClass(mContext, TeamMemberChooseActivity.class);
                dismissPopupwindow();
                startActivityForResult(intent2, REQUEST_CODE_CHATTING_CREATE);
                break;
        }
    }

    private static final int RESULT_CODE_MEMBERCHOOSE = 0xf16;
    private boolean isSinglechatting = true;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHATTING_CREATE && resultCode == RESULT_CODE_MEMBERCHOOSE) {
            if (data != null) {
                String title = data.getStringExtra("title");
                String icon = data.getStringExtra("headpic");
                String key = data.getStringExtra("key");
                if (!TextUtils.isNull(key)) {
                    List<TeamMember> ls = (List<TeamMember>) BaseApplication.getInstance().getT(key);
                    BaseApplication.getInstance().removeT(key);
                    if (ls != null && ls.size() > 0) {
                        String sysMsg = getString(R.string.chat_create_sysmsg, FunncoUtils.currentNickname());
                        Message message = IMEngine.getIMService(MessageBuilder.class).buildTextMessage(sysMsg); //系统消息
                        FunncoUtils.showProgressDialog(mContext, getString(R.string.chat_create_doing));
                        Long[] uids = new Long[ls.size()];
                        int i = 0;
                        for (TeamMember tm : ls) {
                            uids[i] = Long.valueOf(tm.getMember_uid());
                            i++;
                        }
                        IMEngine.getIMService(ConversationService.class)
                                .createConversation(new Callback<Conversation>() {
                                                        @Override
                                                        public void onSuccess(Conversation conversation) {
                                                            FunncoUtils.dismissProgressDialog();
                                                            Intent intent = new Intent(mContext, isSinglechatting ? SingleChatActivity.class :
                                                                    GroupChatActivity.class);
                                                            intent.putExtra(Session.SESSION_INTENT_KEY, new GroupSession(conversation));
                                                            startActivity(intent);
                                                        }

                                                        @Override
                                                        public void onException(String s, String s1) {
                                                        }

                                                        @Override
                                                        public void onProgress(Conversation conversation, int i) {
                                                        }
                                                    }, title, icon, message,
                                        ls.size() == 1 ? Conversation.ConversationType.CHAT : Conversation.ConversationType.GROUP, uids);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取最新的系统消息
     */
    private void getLastSystemMessage() {
        postData2(null,FunncoUrls.getMessageUrl(), true);//获取系统通知和预约提醒
        postData2(null,FunncoUrls.getMessageTeamUrl(), true);//获取团队通知
    }

    @Override
    protected void dataPostBack(String result, String url) {
        dismissLoading();
        if(TextUtils.equals(url, FunncoUrls.getMessageUrl())) {
            //获取系统通知和预约提醒
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            if (paramsJSONObject != null) {
                JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "remmsg");
                if (listJSONArray != null) {
                    List<NotificationLastRemindAndSystem> ls = JsonUtils.getObjectArray(listJSONArray.toString(), NotificationLastRemindAndSystem.class);
                    if(ls != null && !ls.isEmpty()) {
                        NotificationLastRemindAndSystem remindAndSystem = ls.get(0);
                        remindView.setMessage(remindAndSystem.getContent());
                        remindView.setTime(remindAndSystem.getCreate_time());
                    }
                }
                listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "sysmsg");
                if (listJSONArray != null) {
                    List<NotificationLastRemindAndSystem> ls = JsonUtils.getObjectArray(listJSONArray.toString(), NotificationLastRemindAndSystem.class);
                    if(ls != null && !ls.isEmpty()) {
                        NotificationLastRemindAndSystem remindAndSystem = ls.get(0);
                        systemView.setMessage(remindAndSystem.getSummary());
                        systemView.setTime(remindAndSystem.getCreate_time());
                    }
                }
            }
        } else if(TextUtils.equals(url, FunncoUrls.getMessageTeamUrl())){
            //获取团队通知
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            if (paramsJSONObject != null) {
                JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                if (listJSONArray != null) {
                    List<TeamInvite> ls = JsonUtils.getObjectArray(listJSONArray.toString(), TeamInvite.class);
                    if(ls != null && !ls.isEmpty()) {
                        TeamInvite teamInvite = ls.get(0);
                        String msg;
                        if(TextUtils.equals(teamInvite.getTypes(), "1")) {
                            //邀请
                            msg = teamInvite.getInviter_nickname() + "邀请您加入" + teamInvite.getTeam_name() + "团队";
                        } else {
                            //申请
                            msg = teamInvite.getInviter_nickname() + "申请加入" + teamInvite.getTeam_name() + "团队";
                        }
                        teamMsgView.setMessage(msg);
                        teamMsgView.setTime("");
                    }
                }
            }
        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
    }
}
