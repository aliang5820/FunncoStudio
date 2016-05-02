package com.funnco.funnco.utils.url;

/**
 * Created by user on 2015/5/17.
 *
 * @author Shawn
 */
public class FunncoUrls {

    /**
     * @return 接口地址
     */
    public static String getBaseUrl() {
//        return "http://api.funnco.com/";
        return "http://api.test.funnco.com/";
    }

    public static String getWapBaseUrl() {
//        return "http://wap.funnco.com/";
        return "http://wap.test.funnco.com/";
    }

    //二维码登录地址
    public static String getQRBaseUrl() {
        //return "http://gannt.funnco.com/test/funnco-php-front/funnco-b-admin/badmin/Home/IBase/erwlogin";
        return "http://www.funnco.com/funnco-b-admin/badmin/home/IBase/erwlogin";
    }

    public static String getTeamMemberList() {

        return getBaseUrl() + "?m=schedule_team_member";
    }

    public static String getTimeList() {

        return getBaseUrl() + "m=customer/schedule_list";
    }

    /**
     * 后边直接接着需要分享人或团队的id即可
     *
     * @return 分享接口
     */
    public static String getBaseUrlShare() {
        return getWapBaseUrl() + "?uid=";
    }

    /**
     * 帮助h5
     *
     * @return
     */
    public static String getHelpUrl() {
        return getWapBaseUrl() + "help.html";
    }

    /**
     * 参数名称：mobile手机号
     *
     * @return 获取手机验证码
     */
    public static String getAuthorCode() {
        return getBaseUrl() + "?m=vcode";
    }

    /**
     * 参数名称 mobile手机号/nickname昵称/pwd密码/vcode验证码/career_id行业类别/file1头像/device_token设备标识
     *
     * @return 注册接口
     */
    public static String getRegisterUrl() {
        return getBaseUrl() + "?m=reg";
    }

    /**
     * 参数名称：mobile/pwd/device_token设备唯一标识
     *
     * @return 登录接口
     */
    public static String getLoginUrl() {
        return getBaseUrl() + "?m=login";
    }

    /**
     * 参数名称：token授权令牌/uid用户ID/device_token设备唯一标识
     *
     * @return 用户默认自动登录
     */
    public static String getAutoLoginUrl() {
        return getBaseUrl() + "?m=login_auto";
    }

    /**
     * 参数名称：mobile手机号码
     *
     * @return 找回密码
     */
    public static String getFindPasswordUrl() {
        return getBaseUrl() + "?m=findpwd";
    }

    /**
     * 参数名称：oldpwd原密码/newpwd新密码
     *
     * @return 修改密码
     */
    public static String getUpdatePassworddUrl() {
        return getBaseUrl() + "?m=update_pwd";
    }

    /**
     * @return 行业类别
     */
    public static String getCareerTypeListUrl() {
        return getBaseUrl() + "?m=career_list";
    }

    /**
     * @return 获取国际省份基础数据
     */
    public static String getProvinceListUrl() {
        return getBaseUrl() + "?m=province";
    }

    /**
     * 参数名称：province_id省份/address详细地址
     *
     * @return 修改我的地址
     */
    public static String getUpdateAddressUrl() {
        return getBaseUrl() + "?m=update_address";
    }

    /**
     * 参数名称：
     * nickname昵称/career_id职业ID/intro简介/headpic老头像url/file1新头像/sex性别 (1男 2女)/work_phone工作电话/address地址
     *
     * @return 修改资料
     */
    public static String getUpdateInfoUrl() {
        return getBaseUrl() + "?m=update_info";
    }

    /**
     * 参数名称：sort 排列方式(time按时间排序 letter按字母排序)  type(时间排序用1历史，2未来)
     *
     * @return 我的预约客户
     */
    public static String getMyCustomersUrl() {
        return getBaseUrl() + "?m=my_customers";
    }

    /**
     * 2.0 新增
     * 参数说明：sort 排列方式(time)/type(1:未来 2：历史)/page 页码/pagesize 页容量
     *
     * @return 我的预约 未来&历史
     */
    public static String getMyConventionUrl() {
        return getBaseUrl() + "?m=my_schedule";
    }

    /**
     * 参数名称：token授权令牌/uid用户ID
     *
     * @return 注销登录
     */
    public static String getLoginOutUrl() {
        return getBaseUrl() + "?m=logout";
    }

    /**
     * 2.1新修
     * 新增参数 team_id 团队Id(添加团队作品用)
     * 参数名称：title1标题/description1描述/file1图片文件/title2标题描述文件。。。。。
     *
     * @return 添加作品
     */
    public static String getAddWorkUrl() {
        return getBaseUrl() + "?m=work_add";
    }

    /**
     * 2.1新修
     * 新增参数 team_id 团队Id(团队作品列表)
     * 参数名称：page页码/pagesize每页数据数量
     *
     * @return 获得作品列表
     */
    public static String getWorkListUrl() {
        return getBaseUrl() + "?m=work_list";
    }

    /**
     * 2.1新修
     * 新增参数 team_id 团队Id(用于团队作品修改)
     * 参数名称：id作品ID/title标题/description描述
     *
     * @return 编辑作品
     */
    public static String getEditWorkUrl() {
        return getBaseUrl() + "?m=work_edit";
    }

    /**
     * 参数名称：id作品ID
     *
     * @return 删除作品
     */
    public static String getDeleteWorkUrl() {
        return getBaseUrl() + "?m=work_del";
    }

    /**
     * 2.1 新修 (团队服务)
     * 参数名称 team_id 团队Id
     * 2.1 修改（参数添加）
     * 参数名称：service_name服务名称/duration服务时长(单位分钟)/numbers人数/price价格/description服务描述/repeat_type循环模式 0:不重复 1:重复
     * weeks 一周内的周次/startdate 开始日期/enddate 结束日期/starttime 开始时间(分钟)/endtime结束时间/relation 可同时进行的服务
     * relation 可同时进行的服务
     * 新增参数：（适应于课程）team_id 团队id/team_uid同时进行的老师/service_type服务类型 0表示服务，1表示课程/remind_time 提醒时间
     * min_numbers 最少开课人数
     *
     * @return 添加服务
     */
    public static String getAddServiceUrl() {
        return getBaseUrl() + "?m=service_add";
    }

    /**
     * 2.1 修改（由无参变为有参）
     * 参数名称：service_type 服务类型 0表示服务，1表示课程(不上传该参数服务课程都返回)/team_id 团队id/team_uid  --- 课程列表需要
     *
     * @return 获得服务列表
     */
    public static String getServiceListUrl() {
        return getBaseUrl() + "?m=service_list";
    }

    /**
     * 参数名称：service_name服务名称/duration服务时长/numbers人数/price价格/description描述/id服务ID
     *
     * @return 编辑服务
     */
    public static String getEditServiceUrl() {
        return getBaseUrl() + "?m=service_edit";
    }

    /**
     * 参数名称：id服务ID
     *
     * @return 删除服务
     */
    public static String getDeleteServicdeUrl() {
        return getBaseUrl() + "?m=service_del";
    }

    /**
     * 参数名称：contents事件内容/dates日期/starttime开始时间/endtime结束时间/repeat_type重复类型
     * 2.1 新增参数 team_uid 团队中成员Id n/team_id 团队Id n
     *
     * @return 发布日程
     */
    public static String getAddShceduleUrl() {
        return getBaseUrl() + "?m=schedule_add";
    }

    /**
     * 2.1新修
     * 新增参数 team_id 团队id /team_uid 团队uid
     * 参数名称：dates日期
     *
     * @return 日程列表
     */
    public static String getSchedeleListUrl() {
        return getBaseUrl() + "?m=schedule_list";
    }

    /**
     * 参数名称：contents事件内容/dates日期/starttime开始时间/endtime结束时间
     *
     * @return 编辑日程事件
     */
    public static String getEditScheduleUrl() {
        return getBaseUrl() + "?m=schedule_edit";
    }

    /**
     * 参数名称：id事件id
     *
     * @return 删除日程事件
     */
    public static String getDeleteScheduleUrl() {
        return getBaseUrl() + "?m=schedule_del";
    }

    /**
     * 参数名称：contents内容
     *
     * @return 意见反馈
     */
    public static String getFeedBackUrl() {
        return getBaseUrl() + "?m=feedback";
    }

    /**
     * @return 关于我们 html
     */
    public static String getAboutUrl() {
        return getBaseUrl() + "?m=about";
    }

    /**
     * @return 服务条款和隐私政策 html
     */
    public static String getPrivacyUrl() {
        return getBaseUrl() + "?m=privacy";
    }

    /**
     * 参数名称 token从微信获取的token/openid用户openid/device_token设备唯一标识
     *
     * @return 微信登录接口
     */
    public static String getWeiCatLoginUrl() {
        return getBaseUrl() + "?m=login_wx";
    }

    /**
     * 参数名称 token从微信获取的token/openid用户openid/type固定值，取值login/mobile手机号
     *
     * @return 微信绑定接口
     */
    public static String getBindWeCharUrl() {
        return getBaseUrl() + "?m=reg_bind";
    }

    /**
     * 分享的链接
     *
     * @param uid 用户id
     * @return 名片分享、名片预览
     */
    public static String getShareScheduleUrl(String uid) {
        return getBaseUrlShare() + uid;
    }

    /**
     * 分享的链接
     *
     * @param uid 用户id
     * @return 名片分享、名片预览
     */
    public static String getShareTeamScheduleUrl(String uid) {
        return getBaseUrlShare() + uid + "&type=1";
    }

    /**
     * 服务取消的原因列表
     *
     * @return 取消预约原因
     */
    public static String getScheduleCancleReason() {
        return getBaseUrl() + "?m=schedule_cancel_reason";
    }

    /**
     * 2.0 新增
     * 参数说明 c_uid 客户uid
     *
     * @return 预约客户详细信息
     */
    public static String getCustomerInfo() {
        return getBaseUrl() + "?m=customers_info";
    }

    /**
     * 2.0 新增
     * 参数说明 uid 用户Id team_id 所在的团队Id
     *
     * @return 返回用户信息
     */
    public static String getUserInfoUrl() {
        return getBaseUrl() + "?m=user_info";
    }

    /**
     * 2.0 新增
     * 参数说明 c_uid 客户uid pagesize 页容量 page 页码
     *
     * @return 我的客户预约列表
     */
    public static String getCustomerScheduleList() {
        return getBaseUrl() + "?m=customers_schedule_list";
    }

    /**
     * 2.0 新增
     * 参数说明 id 预约信息Id price 价格 remark 备注
     *
     * @return 我的客户预约记录编辑
     */
    public static String getCustomerConventationEdit() {
        return getBaseUrl() + "?m=customers_schedule_edit";
    }

    /**
     * 2.0 新增
     * 参数说明 id 用户id  remark_name 备注名称 birthday 生日 description 用户描述
     *
     * @return 编辑客户备注信息
     */
    public static String getCustomerEdit() {
        return getBaseUrl() + "?m=customers_edit";
    }

    /**
     * 参数说明 id已约客户id
     *
     * @return 删除已预约客户
     */
    public static String getCustomerDelete() {
        return getBaseUrl() + "?m=customers_del";
    }

    /**
     * 参数说明：ids 多人预约用，隔开 reasion_id 取消原因 reason_contents 取消其他内容
     *
     * @return 取消预约客户接口
     */
    public static String getScheduleCancleUrl() {
        return getBaseUrl() + "?m=schedule_cancel";
    }

    /**
     * 参数说明 work_phone 我的电话  多个用逗号隔开
     *
     * @return 修改我的电话
     */
    public static String getAddMobile() {
        return getBaseUrl() + "?m=update_workphone";
    }

    /**
     * 参数说明 dates 当前日期
     *
     * @return 获取当月有预约de日期数据(2.1 废弃)
     */
    public static String getScheduleDateStat() {
        return getBaseUrl() + "?m=schedule_date_stat";
    }

    /**
     * 2.0 新增
     * 参数说明 dates 当前日期
     *
     * @return 获取日期所在月有预约的日期集合以及有事件的日期集合
     */
    public static String getScheduleDateStat2() {
        return getBaseUrl() + "?m=schedule_date_stat2";
    }

    /**
     * @return 获取新预约的日期列表
     */
    public static String getScheduleNew() {
        return getBaseUrl() + "?m=schedule_new";
    }

    /**
     * @return 版本更新接口
     */
    public static String getUpdateVersion() {
        return getBaseUrl() + "?m=soft_info";
    }

    /**
     * 2.1新增
     *
     * @return 我的团队成员列表
     */
    public static String getTeamMemberUrl() {
        return getBaseUrl() + "?m=schedule_team_member";
    }

    /**
     * 2.1新增
     * 参数说明
     *
     * @return 预约邀请
     */
    public static String getInvitationUrl() {
        return getBaseUrl() + "?m=schedule_invite";
    }

    /**
     * 2.1新增
     * 参数说明 dates 服务日期(2015-09-24 y) booktime 预约时间段(12:30 y) service_id 服务Id
     * truename 顾客姓名 y mobile 顾客电话 y remark 备注 team_id 团队Id y team_uid 团队成员Id y (规则：多个逗号","隔开)
     *
     * @return 添加新预约
     */
    public static String getConventionNewUrl() {
        return getBaseUrl() + "?m=schedule_booking";
    }

    /**
     * 2.1新增
     * 参数说明 team_uid 团队成员的Id dates 日期(格式yyyy-MM-dd) service_id 服务Id
     *
     * @return 已被预约的时间
     */
    public static String getScheduletimeUrl() {
        return getBaseUrl() + "?m=schedule_time";
    }

    /**
     * 2.1新增
     * 参数说明 keyword 搜索的关键字
     *
     * @return 成员搜索添加
     */
    public static String getTeamSearchMemberUrl() {
        return getBaseUrl() + "?m=user_search";
    }

    /**
     * 2.1新增
     * 参数说明 keyword 搜索的关键字(按照名字模糊查询)
     *
     * @return 团队搜索
     */
    public static String getTeamSearchUrl() {
        return getBaseUrl() + "?m=team_search";
    }

    /**
     * 2.1新增
     * 字段说明 role表示角色，0是创建者，1管理员，2普通团员
     *
     * @return 我的团队列表
     */
    public static String getTeamListUrl() {
        return getBaseUrl() + "?m=team_list";
    }

    /**
     * 2.1新增
     * 参数说明 team_name 团队名称 allow_search 是否可以被搜索(0 false 1 true) phone 团队电话
     * intro 团队描述 address 团队地址 team_uid 邀请的团队成员(多个用逗号隔开) cover_pic 封面
     *
     * @return 创建团队
     */
    public static String getTeamCreateUrl() {
        return getBaseUrl() + "?m=team_add";
    }

    /**
     * 2.1新增
     * 参数说明 team_id 团队Id
     * 字段说明 role表示角色，0是创建者，1管理员，2普通成员
     *
     * @return 我的团队成员
     */
    public static String getTeamMemberListUrl() {
        return getBaseUrl() + "?m=team_member_list";
    }

    /**
     * 2.1新增
     * 参数说明 team_id 团队的Id team_uid 该成员的Id role 角色标示（0创建者一般没有这一项，1 管理员，2普通成员）
     *
     * @return 修改团队成员角色
     */
    public static String getTeamMemberSetRoleUrl() {
        return getBaseUrl() + "?m=team_member_set";
    }

    /**
     * 2.1新增
     * 参数说明 team_id 团队Id team_uid 该成员的Id(自己退出团队时 可以不上传该参数)
     *
     * @return 移除团队成员/退出团队
     */
    public static String getTeamMemberRemoveUrl() {
        return getBaseUrl() + "?m=team_member_del";
    }

    /**
     * 2.1新增
     * 参数说明 team_id 团队Id team_uid 好友Id
     *
     * @return 邀请好友加入
     */
    public static String getTeamInviteUrl() {
        return getBaseUrl() + "?m=team_invite";
    }

    /**
     * 2.1新增
     * 参数说明 team_id 所申请的团队Id
     *
     * @return 申请加入团队
     */
    public static String getTeamApplyUrl() {
        return getBaseUrl() + "?m=team_apply";
    }

    /**
     * 2.1新增
     * 参数说明 id 邀请的Id status 接受/拒绝(1接受 2拒绝)
     *
     * @return 对邀请处理
     */
    public static String getTeamInviteReplyUrl() {
        return getBaseUrl() + "?m=team_reply";
    }

    /**
     * 2.1新增
     * 参数说明 team_id 团队Id
     *
     * @return 解散团队
     */
    public static String getTeamBreakUpUrl() {
        return getBaseUrl() + "?m=team_del";
    }

    /**
     * 2.1新增
     * 参数说明 team_id 团队Id team_name 团队名称 allow_search 是否可以被搜索(0 false 1 true) phone 团队电话
     * intro 团队描述 address 团队地址 封面
     *
     * @return 团队设置
     */
    public static String getTeamSettingUrl() {
        return getBaseUrl() + "?m=team_edit";
    }

    /**
     * 2.1新增
     * 参数说明 team_id 团队Id team_uid 组员Id service_ids 所选服务id(多个用逗号隔开)
     *
     * @return 团队服务分配
     */
    public static String getTeamServiceDistributeUrl() {
        return getBaseUrl() + "?m=team_service_distrib";
    }

    /**
     * 2.1 新增
     * 参数说明 lan client
     *
     * @return 获得当前用户的signature 等信息
     */
    public static String getSignatureUrl() {
        return getBaseUrl() + "?m=wukong_auth";
    }

    /**
     * 2.2 新增
     * 参数说明
     * title是产品名字 content是产品描述 amount是产品价格 channel是支付方式
     *
     * @return 获得订单信息信息
     */
    public static String getOrderUrl() {
        return getBaseUrl() + "?m=order";
    }

    /**
     * 2.2 新增
     * 参数说明
     *
     * @return 获得账户商业信息
     */
    public static String getBusinessStatusUrl() {
        return getBaseUrl() + "?m=business_status";
    }

    /**
     * 2.2 新增
     * 参数说明
     *
     * @return 获得账户收支信息
     */
    public static String getAccountsUrl() {
        return getBaseUrl() + "?m=accounts";
    }

    /**
     * 2.2 新增
     * 参数说明
     *
     * @return 获得预约时间格
     */
    public static String getCustomerScheduleTimesUrl() {
        return getBaseUrl() + "?m=customer/schedule_list";
    }

    /**
     * 2.2 新增
     * 参数说明 lan client
     *
     * @return 二维码登录 等信息
     */
    public static String getQRCodeUrl() {
        return getQRBaseUrl() + "?";
    }

    /**
     * 2.2 新增
     * 参数说明 lan client
     *
     * @return 消息首页（提醒，系统）最新信息
     */
    public static String getMessageUrl() {
        return getBaseUrl() + "?m=message";
    }

    /**
     * 2.2 新增
     * 参数说明 lan client
     *
     * @return 系统通知信息
     */
    public static String getMessageSystemUrl() {
        return getBaseUrl() + "?m=sysmessagelist";
    }

    /**
     * 2.2 新增
     * 参数说明 lan client
     *
     * @return 提醒通知信息
     */
    public static String getMessageRemindUrl() {
        return getBaseUrl() + "?m=remindmessagelist";
    }

    /**
     * 2.2 新增
     * 参数说明 lan client
     * status:0:申请 1：通过，2拒绝
     * types：1:团队邀请 2：申请加入
     * @return 团队通知信息
     */
    public static String getMessageTeamUrl() {
        return getBaseUrl() + "?m=team_invite_list";
    }

    /**
     * 2.2新增
     * 第三方推送码更新
     */
    public static String getBaiduPushCode() {
        return getBaseUrl() + "?m=baidu_push_code";
    }
}
