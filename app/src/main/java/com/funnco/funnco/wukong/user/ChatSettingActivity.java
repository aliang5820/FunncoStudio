package com.funnco.funnco.wukong.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.auth.AuthService;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Member;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageBuilder;
import com.alibaba.wukong.im.MessageContent;
import com.alibaba.wukong.im.User;
import com.alibaba.wukong.im.UserService;
import com.alibaba.wukong.im.utils.Utils;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.activity.team.TeamMemberChooseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.TeamMember;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.view.gridview.WrapperGridView;
import com.funnco.funnco.wukong.base.BaseFragmentActivity;
import com.funnco.funnco.wukong.impl.AvatarMagicianImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天设置
 * @author shawn
 * @time 2015年11月17日10:43:25
 */
public class ChatSettingActivity extends BaseFragmentActivity implements View.OnClickListener{
    private static final String TAG = ChatSettingActivity.class.getSimpleName();
    private List<User> mUserList;
    private List<Member> mMemberList;
    private Member member;
    private WrapperGridView mAvatarGridView;
    private AvatarAdapter mAvatarAdapter;

    private ConversationService mConversationService;
    private Conversation mConversation;
    private MessageBuilder mMessageBuilder;
    private static final int REQUEST_CODE_MEMBER_ADD = 0xf100;
    private static final int RESULT_CODE_MEMBERCHOOSE = 0xf16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_setting);

        super.initActionBar(null);

        mConversation = (Conversation)getIntent().getSerializableExtra(Constants.KEY_CONVERSATION);

        mConversationService = IMEngine.getIMService(ConversationService.class);
        mAvatarGridView = (WrapperGridView)findViewById(R.id.avatar_grid);
        findViewById(R.id.id_lview).setOnClickListener(this);
        findViewById(R.id.id_rview).setOnClickListener(this);
        ((TextView)findViewById(R.id.id_mview)).setText(R.string.menu_chat_setting);
        mAvatarAdapter = new AvatarAdapter(this);
        mAvatarGridView.setAdapter(mAvatarAdapter);
        mMessageBuilder = IMEngine.getIMService(MessageBuilder.class);
        loadData();

        findViewById(R.id.quit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatSettingActivity.this);
                builder.setTitle(getString(R.string.quit_confirm))
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Message message = buildSystemMessage(MessageContent.MessageTemplate.QUIT_CONVERSATION);
                                FunncoUtils.showProgressDialog(ChatSettingActivity.this, getString(R.string.quiting));
                                mConversation.quit(message,
                                        new Callback<Void>() {
                                            @Override
                                            public void onSuccess(Void data) {
                                                LogUtils.e(TAG, "[onSuccess] quit");
                                                FunncoUtils.dismissProgressDialog();
                                                startActivity(new Intent(ChatSettingActivity.this, MainActivity.class));
                                                finish();
                                            }

                                            @Override
                                            public void onException(String code, String reason) {
                                                FunncoUtils.dismissProgressDialog();
                                                LogUtils.e(TAG,
                                                        "[onException] quit; code: " + code + "reason: "
                                                                + reason);
                                            }

                                            @Override
                                            public void onProgress(Void data, int progress) {

                                            }
                                        });

                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            }
        });
        mAvatarGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position < mUserList.size()-1){
                    if (mMemberList != null && mMemberList.size() > position) {
                        if (mMemberList.get(position).roleType() != Member.RoleType.MASTER){
                            FunncoUtils.showToast(ChatSettingActivity.this, "不是群主，没有操作权限！");
                            return true;
                        }
                    }else{
                        return true;
                    }
                    FunncoUtils.showAlertDialog(ChatSettingActivity.this, R.string.str_chat_member_breadout, new FunncoUtils.DialogCallback() {
                        @Override
                        public void onPositive() {//群聊踢人

                            //删除成员成功后,推送的系统消息
                            Message message = IMEngine.getIMService(MessageBuilder.class)
                                    .buildTextMessage(mUserList.get(position).openId() + "" + mUserList.get(position).nickname()+"被踢出群");
                            mConversationService.removeMembers(new Callback<List<Long>>() {
                                @Override
                                public void onSuccess(List<Long> longs) {

                                }

                                @Override
                                public void onException(String s, String s1) {

                                }

                                @Override
                                public void onProgress(List<Long> longs, int i) {

                                }
                            },mConversation.conversationId(),message,new Long[]{mUserList.get(position).openId()});

                        }
                    });
                }
                return false;
            }
        });
    }

    private void loadData() {
        if (mConversation.type() == Conversation.ConversationType.GROUP) {
            mConversationService.listMembers(new Callback<List<Member>>() {
                @Override
                public void onSuccess(List<Member> data) {
                    mMemberList = data;
                    mUserList = resolveMemberImageUrl(data);
                    LogUtils.e(TAG, "refreshData onSuccess, mUserList: " + mUserList);
                    mAvatarAdapter.notifyDataSetChanged();

                }

                @Override
                public void onException(String code, String reason) {
                    LogUtils.e(TAG, "refreshData onException, code: " + code + "reason: " + reason);
                }

                @Override
                public void onProgress(List<Member> data, int progress) {
                }

            }, mConversation.conversationId(), 0, 200);
        }

        if (mConversation.type() == Conversation.ConversationType.CHAT) {
            List<Long> uids = new ArrayList<Long>();
            uids.add(mConversation.getOtherOpenId());
            uids.add(AuthService.getInstance().latestAuthInfo().getOpenId());
            IMEngine.getIMService(UserService.class).listUsers(new Callback<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    mUserList = users;
                    mAvatarAdapter.notifyDataSetChanged();
                }

                @Override
                public void onException(String s, String s1) {

                }

                @Override
                public void onProgress(List<User> users, int i) {

                }
            }, uids);

            findViewById(R.id.quit_btn).setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_lview:
                finish();
                break;
            case R.id.id_rview:
                startActivityForResult(new Intent(ChatSettingActivity.this, TeamMemberChooseActivity.class)
                        .putExtra("chooseMode", false)
                        .putExtra("isChat", false), REQUEST_CODE_MEMBER_ADD);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_MEMBER_ADD && resultCode == RESULT_CODE_MEMBERCHOOSE){
            if (data != null){
                String key = data.getStringExtra("key");
                if (!TextUtils.isNull(key)){
                    List<TeamMember> ls = (List<TeamMember>) BaseApplication.getInstance().getT(key);
                    if (ls != null){
                        Long[] openIds = new Long[ls.size()];
                        String names = "";
                        for (int i = 0; i < ls.size(); i ++){
                            openIds[i] = Long.valueOf(ls.get(i).getMember_uid());
                            names += ls.get(i).getNickname()+"、";
                        }
                        addMember(openIds, names.length() > 0 ? names.substring(0, names.length() - 1) : "");
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addMember(Long[] openIds, String names){
        Message message = IMEngine.getIMService(MessageBuilder.class).buildTextMessage(member != null ? member.user().nickname():"" + "邀请了"+names+"加入群");

        mConversationService.addMembers(new Callback<List<Long>>() {
            @Override
            public void onProgress(List<Long> data, int progress) {
                //Nothing to do
            }

            @Override
            public void onSuccess(List<Long> data) {
                //ToDo 添加成功后的处理。data为添加成功的成员id
                FunncoUtils.showAlertDialog(ChatSettingActivity.this, R.string.success, new FunncoUtils.DialogCallback() {
                    @Override
                    public void onPositive() {
                        ChatSettingActivity.this.finish();
                    }
                });
            }

            @Override
            public void onException(String code, String reason) {
                FunncoUtils.showAlertDialog(ChatSettingActivity.this, R.string.failue, new FunncoUtils.DialogCallback() {
                    @Override
                    public void onPositive() {
                        ChatSettingActivity.this.finish();
                    }
                });
                //异常处理
                LogUtils.e("funnco", "code="+ code +" reason=" + reason);
            }
        }, mConversation.conversationId(), message, openIds);
    }

    public class AvatarAdapter extends BaseAdapter {
        private Context mContext;

        public AvatarAdapter(Context ctx) {
            mContext = ctx;
        }

        @Override
        public int getCount() {
            if(mUserList == null) {
                return 0;
            }
            return mUserList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView avatarImg;
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_avatar_item, null);
            avatarImg = (ImageView) convertView.findViewById(R.id.avatar_iv);
            TextView nameTv = (TextView)convertView.findViewById(R.id.name_tv);

            if (position == mUserList.size()) {
                avatarImg.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.chat_setting_plus));
                nameTv.setVisibility(View.GONE);
                avatarImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(ChatSettingActivity.this);
                        alert.setTitle(getString(R.string.add_new_member_tips));
                        final EditText input = new EditText(ChatSettingActivity.this);
                        alert.setView(input);
                        alert.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String openId = input.getText().toString();
                                if(TextUtils.isNull(openId)){
                                    FunncoUtils.showToast(ChatSettingActivity.this, getString(R.string.chat_create_invalid_openId));
                                    return;
                                }
                                if (mConversation.type() == Conversation.ConversationType.GROUP) {
                                    addMember(Utils.toLong(openId));
                                }else {
                                    createConversation(Utils.toLong(openId));
                                }
                            }
                        }).setNegativeButton(getString(R.string.cancel), null);
                        alert.show();
                    }
                });
            } else {
                User u = mUserList.get(position);
                if (u!=null){
                    if (!TextUtils.isNull(u.avatar())){
                        AvatarMagicianImpl.getInstance().setUserAvatar(avatarImg, u.openId(), mAvatarGridView);
                    }
                    if (!TextUtils.isNull(u.nickname())) {
                        nameTv.setText(u.nickname());
                    }
                }
                nameTv.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    private void addMember(long uid) {
        final Long[] uids = new Long[1];
        uids[0] = uid;
        Message message = buildSystemMessage(MessageContent.MessageTemplate.ADD_MEMBER);
        mConversationService.addMembers(new Callback<List<Long>>() {
            @Override
            public void onSuccess(List<Long> longs) {
                loadData();
            }

            @Override
            public void onException(String s, String s1) {
                FunncoUtils.showToast(ChatSettingActivity.this, getString(R.string.add_new_member_error));
            }
            @Override
            public void onProgress(List<Long> longs, int i) {
            }
        }, mConversation.conversationId(), message, uids);
    }

    private void createConversation(long uid) {
        Long[] uids = new Long[3];
        uids[0] = uid;

        StringBuilder title = new StringBuilder();
        StringBuilder icon = new StringBuilder();
        icon.append(uid);
        title.append(uid);

        int count = 1;
        for(User u : mUserList){
            icon.append(":").append(u.openId());
            title.append(",").append(u.openId());
            uids[count] = u.openId();
            if(count >= 2)
                break;
            count ++;
        }
        String sysMsg = getString(R.string.chat_create_sysmsg, FunncoUtils.currentNickname());
        Message message = IMEngine.getIMService(MessageBuilder.class).buildTextMessage(sysMsg); //系统消息
        mConversationService.createConversation(new Callback<Conversation>() {
            @Override
            public void onSuccess(Conversation co) {
                Intent intent = new Intent(ChatSettingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onException(String s, String s1) {
                FunncoUtils.showToast(ChatSettingActivity.this, getString(R.string.add_new_member_error));
            }

            @Override
            public void onProgress(Conversation co, int i) {

            }
        },  title.toString(), icon.toString(), message, Conversation.ConversationType.GROUP, uids);
    }

    private Message buildSystemMessage(String text){
        return mMessageBuilder.buildTextMessage(text);
    }

    public ArrayList<User> resolveMemberImageUrl(List<Member> data) {
        ArrayList<User> stringArrayList = new ArrayList<>();
        for (Member member : data) {
            if (member.user().openId() == Long.valueOf(BaseApplication.getInstance().getUser().getId())){
                this.member = member;
            }
            stringArrayList.add((member.user()));
        }
        return stringArrayList;

    }
}
