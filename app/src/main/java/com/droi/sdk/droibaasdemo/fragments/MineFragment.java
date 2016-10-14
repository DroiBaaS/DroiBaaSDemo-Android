package com.droi.sdk.droibaasdemo.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiPermission;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.activities.DownloadActivity;
import com.droi.sdk.droibaasdemo.activities.LoginActivity;
import com.droi.sdk.droibaasdemo.activities.ProfileActivity;
import com.droi.sdk.droibaasdemo.models.AppInfo;
import com.droi.sdk.droibaasdemo.models.AppType;
import com.droi.sdk.droibaasdemo.models.Banner;
import com.droi.sdk.droibaasdemo.models.MyUser;
import com.droi.sdk.droibaasdemo.views.RoundImageView;
import com.droi.sdk.feedback.DroiFeedback;
import com.droi.sdk.selfupdate.DroiUpdate;

import java.io.File;

/**
 * Created by chenpei on 2016/5/12.
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "MineFragment";
    private Context mContext;
    private RoundImageView titleImg;
    private TextView nameTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
        DroiAnalytics.onFragmentStart(getActivity(), "MineFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "MineFragment");
    }

    /**
     * 当登录成功或者登出时刷新View
     */
    private void refreshView() {
        MyUser user = DroiUser.getCurrentUser(MyUser.class);
        if (user != null && user.isAuthorized() && !user.isAnonymous()) {
            nameTextView.setText(user.getUserId());
            if (user.getHeadIcon() != null) {
                user.getHeadIcon().getInBackground(new DroiCallback<byte[]>() {
                    @Override
                    public void result(byte[] bytes, DroiError error) {
                        if (error.isOk()) {
                            if (bytes == null) {
                                Log.i(TAG, "bytes == null");
                            } else {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                titleImg.setImageBitmap(bitmap);
                            }
                        }
                    }
                }, null);
            }
        } else {
            titleImg.setImageResource(R.drawable.profile_default_icon);
            nameTextView.setText(R.string.fragment_mine_login);
        }
    }

    private void initUI(View view) {
        view.findViewById(R.id.mine_frag_account).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_update).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_feedback).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_download).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_upload).setOnClickListener(this);
        view.findViewById(R.id.head_icon).setOnClickListener(this);
        titleImg = (RoundImageView) view.findViewById(R.id.head_icon);
        nameTextView = (TextView) view.findViewById(R.id.user_name);
    }

    @Override
    public void onClick(View v) {
        MyUser user = DroiUser.getCurrentUser(MyUser.class);
        switch (v.getId()) {
            case R.id.head_icon:
                if (user != null && user.isAuthorized() && !user.isAnonymous()) {
                    toProfile();
                } else {
                    toLogin();
                }
                break;
            case R.id.mine_frag_account:
                if (user != null && user.isAuthorized() && !user.isAnonymous()) {
                    toProfile();
                } else {
                    toLogin();
                }
                break;
            case R.id.mine_frag_update:
                //手动更新
                DroiUpdate.manualUpdate(mContext);
                break;
            case R.id.mine_frag_feedback:
                //如果使用自己的账户系统
                //DroiFeedback.setUserId("userId");
                //自定义部分颜色
                //DroiFeedback.setTitleBarColor(Color.GREEN);
                //DroiFeedback.setSendButtonColor(Color.GREEN,Color.GREEN);
                //打开反馈页面
                DroiFeedback.callFeedback(mContext);
                break;
            case R.id.mine_frag_download:
                toDownload();
                break;
            case R.id.mine_frag_upload:
                Log.i("TEST", "mine_frag_upload");
                //uploadBanner();
                // uploadAppInfo();
                // uploadAppType();
                AppType appType1 = new AppType();
                appType1.setName("test");
                appType1.setMainType("test");
                appType1.setIcon(new DroiFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1.apk")));
                appType1.saveInBackground(new DroiCallback<Boolean>() {
                    @Override
                    public void result(Boolean aBoolean, DroiError droiError) {
                        Log.i("test", droiError.toString());
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 转到下载管理页面
     */
    private void toDownload() {
        Intent downloadIntent = new Intent(mContext, DownloadActivity.class);
        startActivity(downloadIntent);
    }

    /**
     * 转到登录页面
     */
    private void toLogin() {
        Intent loginIntent = new Intent(mContext, LoginActivity.class);
        startActivity(loginIntent);
    }

    /**
     * 转到个人信息页面
     */
    private void toProfile() {
        Intent profileIntent = new Intent(mContext, ProfileActivity.class);
        startActivity(profileIntent);
    }

    private void uploadAppType() {
        DroiPermission permission = new DroiPermission();
        permission.setPublicReadPermission(true);
        permission.setPublicWritePermission(false);

        AppType appType1 = new AppType();
        appType1.setName("社交");
        appType1.setMainType("app");
        appType1.setIcon(new DroiFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1.png")));
        appType1.setPermission(permission);
        appType1.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                //
            }
        });

        AppType appType2 = new AppType();
        appType2.setName("交友");
        appType2.setMainType("app");
        appType2.setIcon(new DroiFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "2.png")));
        appType2.setPermission(permission);
        appType2.saveInBackground(null);

        AppType appType3 = new AppType();
        appType3.setName("动作");
        appType3.setMainType("game");
        appType3.setIcon(new DroiFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "3.png")));
        appType3.setPermission(permission);
        appType3.saveInBackground(null);

        AppType appType4 = new AppType();
        appType4.setName("RPG网游");
        appType4.setMainType("game");
        appType4.setIcon(new DroiFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "4.png")));
        appType4.setPermission(permission);
        appType4.saveInBackground(null);
    }

    private void uploadAppInfo() {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("001");
        appInfo.setIcon("http://newmarket1.oo523.com:8080/mfiles/channels/360/icons/2015/12/01/1448927818782/t01698f9ff66377a4ee.png");
        appInfo.setName("找对象");
        appInfo.setRating(4);
        appInfo.setCount(1116449);
        appInfo.setSize(3861467);
        appInfo.setBrief("找对象-同城交友聊天约会");
        appInfo.setIntro("找对象，用手机找对象。婚恋交友约会相亲。\r\n\r\n产品介绍：\r\n找对象是国内唯一一款专为单身男性打造的女多男少的婚恋社交产品。随时随地发现同城的单身男女 \r\n，一键打招呼功能，方便快捷；高级搜索同城、老乡、职业、学历，快速精确找到最适合你的缘分 \r\n\r\n当你因为忙于工作还是单身的时候，找对象帮你随时随地发现身边适合你的人 \r\n当你长夜长夜漫漫无心睡眠的时候，找对象这里有跟你一样寂寞的人陪你聊天 \r\n当你逢年过节被亲人问婚姻的时候，找对象可以快速帮你3天找到生命的有缘人 \r\n\r\n【温馨提示】 \r\n1.拒绝色情聊天,拒绝一夜情、约炮,违反规定禁用账号 \r\n2.拒绝传播淫秽不雅图片或照片 \r\n3.跟陌生人线下约会时,请注意安全,尽量选择公共场所 \r\n\r\n相关应用关键词：婚恋,异性,帅哥,美女,聊天,浪漫,邂逅,宅男,宅女,私聊,微信,微博,QQ恋爱,相亲, \r\n有缘,同城,约会,交友,单身,缘分,相爱,寂寞,伴侣 \r\n\r\n【联系我们】 \r\n公司名：北京艾瑞斯科技有限责任公司 \r\n客服电话：010-56144014 \r\n官方网站：http://t.xianglianai.cn \r\n官方微博：http://www.weibo.com/xlacn \r\n官方微信：想恋爱");
        appInfo.setVersion("4.1.0");
        appInfo.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/03/30/my8whegh9h/ssf_v410_noad-NNA20075.apk&hot=1&company_type=1");
        appInfo.setType("社交");
        appInfo.setMainType("app");
        appInfo.setImageUrl("http://newmarket1.oo523.com:8080/mfiles/channels/360/imgs/2015/12/01/1448927818753/t015254f18533fddbb4.jpg");
        appInfo.saveInBackground(null);

        AppInfo appInfo2 = new AppInfo();
        appInfo2.setAppId("002");
        appInfo2.setIcon("http://newmarket1.oo523.com:8080/mfiles/channels/360/icons/2016/01/19/1453150807861/t01caec7013aaf7a7bc.png");
        appInfo2.setName("陌陌");
        appInfo2.setRating(5);
        appInfo2.setCount(4721274);
        appInfo2.setSize(35765982);
        appInfo2.setBrief("进入陌陌的世界，去结识那些陌生而有趣的");
        appInfo2.setIntro("全球150多国家1.8亿人使用的社交产品，中国领先的移动社交应用。\r\n陌陌全新6.0版本，现在你不只可以发现附近的人，还能发现他们在关注什么话题，做什么有趣的事情。\r\n附近的动态/人/群组：多样展现自己，随时分享生活，找到身边有交集的人，加入周围感兴趣的群组，让社交更有个性。\r\n个人资料：填写家乡、职业、学校和生活、工作地点，更快看到同乡、同行、同校、同兴趣的人的动态，让社交更有内容。\r\n聊天 室：随时加入，想聊就聊，还可以邀请好友，让社交更有活力。\r\n话题：创建你自己的最热话题，发现身边的人在关注什么。\r\n附近活动：丰富的身边精彩活动，等你和小伙伴们一起加入。\r\n礼物商城/表情商城/游戏中心：礼物、表情、游戏，让社交更有温度有格调。\r\n信用体系：星级评定隔绝不良用户，社交更放心");
        appInfo2.setVersion("6.8");
        appInfo2.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/05/10/rgrtkvdkk5/momo_6.8_c48.apk&hot=1&company_type=1");
        appInfo2.setType("社交");
        appInfo2.setMainType("app");
        appInfo2.setImageUrl("http://newmarket1.oo523.com:8080/mfiles/channels/360/imgs/2016/01/19/1453150807201/t01d0b8c882f1ed82cd.jpg");
        appInfo2.saveInBackground(null);

        AppInfo appInfo3 = new AppInfo();
        appInfo3.setAppId("003");
        appInfo3.setIcon("http://newmarket.oo523.com:8080/180/img/2015/12/31/gnn5wo11e4/162x162.png");
        appInfo3.setName("微博");
        appInfo3.setRating(5);
        appInfo3.setCount(4699125);
        appInfo3.setSize(53424675);
        appInfo3.setBrief("人人都会下载的应用！");
        appInfo3.setIntro("随时随地发现新鲜事！微博带你欣赏世界上每一个精彩瞬间，了解每一个幕后故事。分享你想表达的，让全世界都能听到你的心声。\r\n在微博：\r\n-你能看到最新，最全面的资讯内容。无论是官方发布的新闻还是草根爆料的八卦。\r\n-第一时间了解关注人的最新动态，无论是体育、电影、财经、还是吃喝玩乐。\r\n-想说点啥？发送文字、照片、视频，随心所欲地创作，轻松自在地表达。\r\n \r\n首页信息流：\r\n查看大图、GIF动画、视频、音频，查看好友微博及分组微博；\r\n消息：\r\n及时收到@、评论和私信，还可发送语音、表情、图片及位置信息，私密分享也精彩；\r\n发现广场：\r\n在这里可以查看热门内容，还有购物、电影、听歌、运动，旅游等频道。你的生活，也许因为微博而不同；\r\n微博运动：\r\n绑定指定运动设备，查看运动信息，健康数据。与别人一拼高低\r\n微博雷达：\r\n探索附近的好友和优惠，参与电视节目的互动好玩又实惠。\r\n微博会员：\r\n设置卡片背景，主题皮肤、封面，背景音乐和微博来源。让你个性十足，与众不同。\r\n微博支付：\r\n买东西，发红包，生活缴费。支持各种支付场景，让你生活，购物更轻松");
        appInfo3.setVersion("6.5.1");
        appInfo3.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/05/25/8jsii7edaj/5743c275d70bb.apk&hot=1&company_type=1");
        appInfo3.setType("社交");
        appInfo3.setMainType("app");
        appInfo3.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/02/19/f8z64bnc6q/498x692.jpg");
        appInfo3.saveInBackground(null);

        AppInfo appInfo4 = new AppInfo();
        appInfo4.setAppId("004");
        appInfo4.setIcon("http://newmarket1.oo523.com:8080/mfiles/channels/360/icons/2015/12/01/1448917452407/t01e5b7bf105e7a4651.png");
        appInfo4.setName("有缘网");
        appInfo4.setRating(4);
        appInfo4.setCount(2427960);
        appInfo4.setSize(3861467);
        appInfo4.setBrief("找对象，上有缘网！");
        appInfo4.setIntro("找对象最重要的是啥？缘分~~\r\n        缘分哪里找？当然是有缘呀！\r\n        1亿9836万单身男女都在上面找对象，缘分多多，机会多多，还怕找不到？\r\n        免费下载、快速注册、自定义搜索条件、贴心异性推荐……想要的缘分，这里都有！\r\n        找对象，上有缘网！");
        appInfo4.setVersion("5.3.1");
        appInfo4.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/01/29/vi1yn20no3/YouYuan_45685.apk&hot=1&company_type=1");
        appInfo4.setType("交友");
        appInfo4.setMainType("app");
        appInfo4.setImageUrl("http://newmarket1.oo523.com:8080/mfiles/channels/360/imgs/2015/12/01/1448917452497/t01677eb236c7d338d6.jpg");
        appInfo4.saveInBackground(null);

        AppInfo appInfo5 = new AppInfo();
        appInfo5.setAppId("002");
        appInfo5.setIcon("http://newmarket.oo523.com:8080/180/img/2015/11/24/6o00ng144j/162x162.png");
        appInfo5.setName("恋爱神器");
        appInfo5.setRating(4);
        appInfo5.setCount(1794918);
        appInfo5.setSize(5213423);
        appInfo5.setBrief("婚恋交友神器，快到我的碗里来");
        appInfo5.setIntro("“恋爱神器”是新一代的婚恋交友软件，依托1亿1亿9836万用户的专业平台，创新式的交友方式：简单、快捷、靠谱；随时随地听妹子在线告白。\r\n\r\n【精准推荐】：根据您的条件，推荐与你最匹配的优质异性！\r\n【安全可靠】：严格身份核查，上传真实照片、真实资料?！\r\n【丰富多彩】：不同类型的TA随你挑选，交友资料、照片畅爽浏览！\r\n【同城约会】：随时邂逅身边的缘分，不让缘分擦肩而过！\r\n【恋爱真人秀】：看爱情是怎样炼成的，分享最朴实的爱情告白！");
        appInfo5.setVersion("5.3.6");
        appInfo5.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/01/29/ac43hkm9ya/YouYuan_60049.apk&hot=1&company_type=1");
        appInfo5.setType("交友");
        appInfo5.setMainType("app");
        appInfo5.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/04/09/4xaii86j0c/498x692.jpg");
        appInfo5.saveInBackground(null);

        AppInfo appInfo6 = new AppInfo();
        appInfo6.setAppId("006");
        appInfo6.setIcon("http://newmarket.oo523.com:8080/180/img/2015/05/23/b4m0rym0ky/162x162.png");
        appInfo6.setName("in");
        appInfo6.setRating(4);
        appInfo6.setCount(1335613);
        appInfo6.setSize(34240332);
        appInfo6.setBrief("***年轻人超爱的图片社交软件***");
        appInfo6.setIntro("年度最受好评的美图神器，人气最旺的美图社区！\r\n独家大头、海量贴纸，轻松一步让你的萌值爆表！\r\n");
        appInfo6.setVersion("2.8.2");
        appInfo6.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/05/16/pwadvix90n/in_v2.8.2_in_04.apk&hot=1&company_type=1");
        appInfo6.setType("交友");
        appInfo6.setMainType("app");
        appInfo6.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/05/23/thtuq3wp9v/498x692.png");
        appInfo6.saveInBackground(null);

        AppInfo appInfo7 = new AppInfo();
        appInfo7.setAppId("007");
        appInfo7.setIcon("http://newmarket.oo523.com:8080/180/img/2015/04/03/n0q1u74epl/162x162.png");
        appInfo7.setName("求勾搭");
        appInfo7.setRating(4);
        appInfo7.setCount(1322585);
        appInfo7.setSize(2758226);
        appInfo7.setBrief("求勾搭——每天涨姿势~升内涵~");
        appInfo7.setIntro("@你能从附近的人找到你的朋友。享受陌生交友的心跳。\r\n@你无聊想解闷时，可以随手拍拍，分享生活点滴。\r\n@你挤公交地铁的间隙，围观一下奇葩网友的趣事。\r\n青菜萝卜各有所爱，帅哥美女众口难调，\r\n这个色彩缤纷的社区是你每天涨姿势升内涵的好帮手。\r\n\r\n每天都有上千美女帅哥加入其中！\r\n");
        appInfo7.setVersion("1.5.3");
        appInfo7.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2015/04/03/90ts3q4f8u/qiugouda.apk&hot=1&company_type=1");
        appInfo7.setType("交友");
        appInfo7.setMainType("app");
        appInfo7.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/04/03/0d8uqlrvme/498x692.jpg");
        appInfo7.saveInBackground(null);

        AppInfo appInfo8 = new AppInfo();
        appInfo8.setAppId("008");
        appInfo8.setIcon("http://newmarket.oo523.com:8080/180/img/2015/08/11/w0huk2nj87/162x162.png");
        appInfo8.setName("同城约会吧");
        appInfo8.setRating(4);
        appInfo8.setCount(1303664);
        appInfo8.setSize(3861467);
        appInfo8.setBrief("同城单身男女约会神器");
        appInfo8.setIntro("同城约会找对象，不再寂寞单身一人\r\n\r\n同城约会吧是服务于大众的大型婚恋交友综合平台，用户遍及全国各地，不光可以通过情书表达爱意，还可以通过各种新颖的方式吸引对方，成就美满的爱情故事。\r\n\r\n真实、便捷、并充满着浪漫的色彩的交友、约会、相亲不再难\r\n身份证认证和手机认证，诚信认证，非常勿扰\r\n马上注册，与同城的Ta开启一段美好的恋爱幸福时光\r\n你可以在轻松、快乐的时间里体验美妙的爱情\r\n你可以在温馨、浪漫的环境中找到心灵的归属\r\n赶快结束单身，找个情人去约会吧");
        appInfo8.setVersion("4.7.0");
        appInfo8.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/05/10/uoutknu3lj/4.7.0_020051.apk&hot=1&company_type=1");
        appInfo8.setType("交友");
        appInfo8.setMainType("app");
        appInfo8.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/01/15/gwmd6r94g2/498x692.jpg");
        appInfo8.saveInBackground(null);

        AppInfo appInfo9 = new AppInfo();
        appInfo9.setAppId("009");
        appInfo9.setIcon("http://newmarket1.oo523.com:8080/mfiles/channels/360/icons/2016/03/01/1456779744836/t011ef1115a91a57570.png");
        appInfo9.setName("触宝电话");
        appInfo9.setRating(4);
        appInfo9.setCount(1132986);
        appInfo9.setSize(13042659);
        appInfo9.setBrief("触宝电话，电话免费打");
        appInfo9.setIntro("触宝电话，电话免费打！真正无需充值的免费电话，超过2亿用户的共同选择，省钱省心！注册即送500分钟免费通话时长，每日签到及活动，惊喜连连！三星、华为、中兴、微软、谷歌等官方合作伙伴，中国电信创新大赛特等奖应用。\r\n\r\n【免费打全球】告别长途漫游，免费拨打全球座机手机，对方不装不联网也能打！\r\n【骚扰拦截】 360度精准识别1.11亿陌生电话，超过35个国家号码归属地显示\r\n【通讯录管理】智能分组，首字母、部分拼音、号码、联系人信息便捷查找\r\n【通话录音】重要电话一键录音\r\n【隐私通讯录】加密重要联系人和通话记录，支持话单无痕打电话，账单无记录\r\n【双卡支持】完美支持三星、中兴、华为、联想、酷派等10000多款热门双卡机型\r\n【微信集成】联系人详情发微信\r\n【全局搜索】常用号码一键搜索\r\n【手势拨号】联系人划一笔拨号\r\n【发现生活】一站式生活服务平台\r\n覆盖电影、美食、外卖、打车、租房、快递、招聘、游戏、金融等多种品类，提供话费充值、违章代缴、水电煤缴费、医疗挂号、查航班、查招聘、汽车维修保养、家政月嫂、买房租房、微信支付、支付宝支付等服务，接入大众点评、淘点点、饿了么、赶集网、58同城、携程网、百度糯米以提供最全的商家信息和真实的消费者评价评分，想您所想，做您所需。\r\n\r\n联系我们：\r\n微博/微信/百度贴吧：@触宝电话\r\n\r\n小贴士：\r\n免费电话在WIFI下不耗任何流量；3/4G下通话每分钟消耗约300K；回拨模式（流量消耗约30K），通话中是否收费与运营商套餐接听是否免费相关。3/4G流量费和手机月租费由运营商收取。\r\n免费电话系统内核要求高于3.0，2.3系统用户可移步触宝官网www.chubao.cn下载老版本v5.4.3.2。");
        appInfo9.setVersion("5.7.8.0");
        appInfo9.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/04/27/9yb9hs3se0/571f226e019af.apk&hot=1&company_type=1");
        appInfo9.setType("通话");
        appInfo9.setMainType("app");
        appInfo9.setImageUrl("http://newmarket1.oo523.com:8080/mfiles/channels/360/imgs/2016/03/01/1456779744119/t01f864e82d1f0133d7.jpg");
        appInfo9.saveInBackground(null);

        AppInfo appInfo10 = new AppInfo();
        appInfo10.setAppId("010");
        appInfo10.setIcon("http://newmarket1.oo523.com:8080/mfiles/channels/360/icons/2016/04/01/1459458342816/t0158762fb459b6135a.png");
        appInfo10.setName("百度贴吧");
        appInfo10.setRating(4);
        appInfo10.setCount(909766);
        appInfo10.setSize(38745022);
        appInfo10.setBrief("上贴吧，找组织！");
        appInfo10.setIntro("百度贴吧手机客户端 --- 上贴吧，找组织！由于系统版本的问题，部分Android2.3系统的用户无法安装贴吧，请升级系统至更高版本后安装。\r\n百度贴吧是以兴趣主题聚合志同道合者的互动平台。从2003年至今，百度贴吧拥有10亿注册用户，1000万贴吧。在这里，每天都在诞生神贴，这里是当今网络新文化的发源地。每天都有数千万用户在贴吧找到自己所属的组织。\r\n贴吧手机客户端，更方便找到属于你的组织，选择你喜欢的星球，找到精彩兴趣吧，轻松定制专属你的首页内容。\r\n如果你喜爱旅行，旅行吧、驴友吧、背包客吧、穷游吧等1700多个吧等你一起出发；\r\n如果你爱好休闲活动，摄影吧、钓鱼吧、推理题吧、骑行吧等400多个吧会让你结识很多伙伴；\r\n如果你是收藏达人，手工吧、翡翠吧、手办吧、文玩吧、模型吧等700多个吧等你来晒晒珍藏；\r\n如果你是明星粉丝，体育追星狂，动漫爱好者，游戏玩家，美食达人，文艺青年，贴吧会满足你所有的兴趣，同样会带你认识有共同爱好的朋友。\r\n欢迎访问“贴吧客户端反馈吧”提出您的宝贵意见或建议。");
        appInfo10.setVersion("7.4.5");
        appInfo10.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/05/20/d6gjfrdryh/baidutieba_AndroidPhone_v7.4.5(7.4.5)_1013943a.apk&hot=1&company_type=1");
        appInfo10.setType("社交");
        appInfo10.setMainType("app");
        appInfo10.setImageUrl("http://newmarket1.oo523.com:8080/mfiles/channels/360/imgs/2016/04/01/1459458342791/t017cc27b1683646ebf.jpg");
        appInfo10.saveInBackground(null);

        AppInfo appInfo11 = new AppInfo();
        appInfo11.setAppId("011");
        appInfo11.setIcon("http://newmarket.oo523.com:8080/180/img/2016/02/17/dufe1nsu0z/162x162.png");
        appInfo11.setName("世纪佳缘");
        appInfo11.setRating(5);
        appInfo11.setCount(201857);
        appInfo11.setSize(14668146);
        appInfo11.setBrief("青春不常在，抓紧谈恋爱！");
        appInfo11.setIntro("青春不常在，抓紧谈恋爱！\r\n\r\n最新开通缘分圈，马上分享你的生活，彰显你的个人风采，让你在人海中闪亮绽放。\r\n\r\n恋爱就用世纪佳缘手机交友客户端，高效时尚，位置交友、距离搜索、随时随地助你交友觅缘！世纪佳缘是严肃的婚恋交友网站，拥有1.6亿单身用户，拥有完善的客服和认证体系，是中国在美国纳斯达克上市的婚恋交友品牌，迄今为止已帮助数百万人成功觅缘。\r\n\r\n世纪佳缘去年在全国105个城市举办了近千场线下大规模集体相亲交友活动，以帮助单身男女高效觅缘。世纪佳缘和数十家电视相亲节目紧密合作，向其推荐优质单身嘉宾。\r\n\r\n特色功能世纪佳缘手机平台让单身男女可以随时随地利用自己的碎片时间，在任何有手机信号的地方，都可以随时系心仪异性，并且可查看近在身边的异性。每天晚上8点还会将24小时之内与你擦肩而过的异性资料推送给你，让你的缘分来得更容易。缘分不用再等待，马上下载，幸福就在你“掌握”之中。");
        appInfo11.setVersion("5.9");
        appInfo11.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/05/09/vvdzb3rh65/jiayuan_android_2_1_093_5_90(1).apk&hot=1&company_type=1");
        appInfo11.setType("交友");
        appInfo11.setMainType("app");
        appInfo11.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/02/18/ww41dx1ah0/498x692.jpg");
        appInfo11.saveInBackground(null);

        AppInfo appInfo12 = new AppInfo();
        appInfo12.setAppId("012");
        appInfo12.setIcon("http://newmarket1.oo523.com:8080/mfiles/channels/360/icons/2015/12/01/1448929472716/t01bfa3675370e784d1.png");
        appInfo12.setName("掌上宝电话");
        appInfo12.setRating(4);
        appInfo12.setCount(207456);
        appInfo12.setSize(5783741);
        appInfo12.setBrief("掌上宝，超过5000万人信赖的手机省钱电话专家");
        appInfo12.setIntro("1、掌上宝，超过5000万人信赖的手机省钱电话专家\r\n2、免费注册加送70分钟免费通话，绿色安全，高清音质，畅聊优选\r\n3、国内长途低至4分5，节省高达85%的话费\r\n4、0月租，0漫游，0功能费，随时随地，手机互拨接通快\r\n5、回拨直拨智能选择，超低资费个性匹配，专人专享\r\n6、新老用户充值优惠个性定制，会员专享免费显号，夜间免费和专属语音专线\r\n7、一键备份通讯录，支持网络安全快速检测，安全，便捷，贴心\r\n8、签到，抽奖，分享赚话费，时效热点图文推送，好用更好玩\r\n9、注重用户体验，电话,微信，QQ客服为用户提供专业的咨询服务\r\n新用户专享特权\r\n1、免费注册加送70分钟免费通话\r\n2、首充100送500加送30天去电显号\r\n\r\n更多优惠详情请咨询\r\n客服热线：0746-5211002\r\n客服QQ:800015945");
        appInfo12.setVersion("8.3.1.1");
        appInfo12.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2015/12/01/np5b0s9w0v/Handbb_v83110_n00005127.apk&hot=1&company_type=1");
        appInfo12.setType("通话");
        appInfo12.setMainType("app");
        appInfo12.setImageUrl("http://newmarket1.oo523.com:8080/mfiles/channels/360/imgs/2015/12/01/1448929472908/t01f9584fd8f86c8147.jpg");
        appInfo12.saveInBackground(null);

        AppInfo appInfo13 = new AppInfo();
        appInfo13.setAppId("013");
        appInfo13.setIcon("http://newmarket.oo523.com:8080/180/img/2015/03/09/qypytsyxqx/162x162.png");
        appInfo13.setName("缘分吧");
        appInfo13.setRating(4);
        appInfo13.setCount(214198);
        appInfo13.setSize(4019158);
        appInfo13.setBrief("在这个白色情人节，想找一份纯纯的缘分，就靠你了");
        appInfo13.setIntro("应用介绍：“缘分吧”是在线同城交友、找对象、恋爱、婚恋、约会必备社交神器，缘分吧支持即时通讯功能，可以和帅哥、\r\n美女随时聊天。通过附近的人功能发现更多陌生人朋友，也是一款陌生人交友利器。“缘分吧”提供有缘广场功能，\r\n根据您的条件筛选帅哥美女，发现心仪的对象约会聊天、甚至恋爱、结婚哦。聊天交友、单身交友、相亲找对象、\r\n征婚恋爱、快来下载缘分吧！");
        appInfo13.setVersion("1.4.033");
        appInfo13.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2015/09/28/262zjojyu1/fate_it_1347_1000_10100074_1.4.033.apk&hot=1&company_type=1");
        appInfo13.setType("交友");
        appInfo13.setMainType("app");
        appInfo13.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/03/28/94ucgtun6k/498x692.png");
        appInfo13.saveInBackground(null);

        AppInfo appInfo14 = new AppInfo();
        appInfo14.setAppId("014");
        appInfo14.setIcon("http://newmarket.oo523.com:8080/180/img/2016/03/11/6a8mlpoct4/162x162.png");
        appInfo14.setName("我秀娱乐直播");
        appInfo14.setRating(4);
        appInfo14.setCount(221641);
        appInfo14.setSize(14918133);
        appInfo14.setBrief("萌妹女神小鲜肉等你嗨");
        appInfo14.setIntro("美女如云，风情万种，劲歌热舞，视频交友，尽在我秀娱乐直播。\r\n--国内首创真人视频、互动直播应用\r\n--聚集各路美女帅哥，7*24小时不间断视频真人秀直播\r\n--与明星主播实时互动、视频交友，网络选秀、LOL/DOTA游戏赛事直播、午夜 \r\n 情感电台……更多精彩等着你");
        appInfo14.setVersion("4.1.6");
        appInfo14.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/04/21/ctmi2z4az5/5717552975b3e.apk&hot=1&company_type=1");
        appInfo14.setType("直播");
        appInfo14.setMainType("app");
        appInfo14.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/01/12/1bxzjhoa0d/498x692.png");
        appInfo14.saveInBackground(null);

        AppInfo appInfo15 = new AppInfo();
        appInfo15.setAppId("015");
        appInfo15.setIcon("http://newmarket.oo523.com:8080/market/img/2014/12/30/a9qzjspjnm/162x162.png");
        appInfo15.setName("热恋");
        appInfo15.setRating(4);
        appInfo15.setCount(238900);
        appInfo15.setSize(5427960);
        appInfo15.setBrief("想跟谁约会就跟谁约会");
        appInfo15.setIntro("长夜漫漫，空虚寂寥？热恋这里搜同城、搜美女、搜帅哥、搜附近。想跟谁约会就跟谁约会。最快的脱单神器，最真实的交友相亲平台。还等什么？赶快进来寻找你的缘分伴侣吧！\r\n\r\n【产品特色】\r\n1.魅力榜单：每周的魅力人气之星，是否就是你等待已久的缘分呢？快速锁定让我们帮你找到她吧\r\n2.谁看过我：也许你的爱情就在默默的关注着你，我们不再让你有缘无分\r\n3.红娘牵线：这里的红娘经过专业培训，比百合网，世纪佳缘，珍爱网更加贴心\r\n4.尊贵会员：没时间去婚介征婚？成为会员后每天为您推荐优质对象，私人照片任你挑选\r\n\r\n=============================================\r\n北京艾瑞斯科技有限责任公司 版权所有\r\n客户电话：010-56144014\r\n");
        appInfo15.setVersion("5.6.0");
        appInfo15.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2015/12/04/9w0fsgsc2o/relian-560-noad-MNA30013.apk&hot=1&company_type=1");
        appInfo15.setType("交友");
        appInfo15.setMainType("app");
        appInfo15.setImageUrl("http://newmarket.oo523.com:8080/market/img/2015/02/05/zn72mdpkkf/498x692.jpg");
        appInfo15.saveInBackground(null);

        AppInfo appInfo16 = new AppInfo();
        appInfo16.setAppId("016");
        appInfo16.setIcon("http://newmarket1.oo523.com:8080/mfiles/channels/360/icons/2016/01/05/1451941279155/t0104db1bb2f8a39612.png\",\"apkimgurls");
        appInfo16.setName("繁星直播");
        appInfo16.setRating(4);
        appInfo16.setCount(443028);
        appInfo16.setSize(20107803);
        appInfo16.setBrief("移动直播，换种姿势看女神");
        appInfo16.setIntro("1. 繁星直播在线视频互动演艺平台，每天超过6000多位艺人、歌手、网络好声音，不间断直播。\r\n2. 同城美女在线视频互动交友，7x24小时不间断视频直播！\r\n3. 女神萌妹、劲爆好声音、热辣才艺比拼、各色网络选秀、视频互动交友、情感电台…你想看的，这里都有！\r\n4. 没她QQ？没他微信？不用怕！\r\n加入美女艺人个人群组，随时随地与她亲密互动。就算不在直播间，也能与她畅聊！\r\n5. 高质量的视频画面，Live现场音效感知，千万音乐爱好者在线狂欢，与艺人零距离互动，让你随时随地享受演唱会超high气氛。还有大咖艺人的直播互动类访谈节目——星乐坊，丰富多彩的直播互动访谈模式，让百万粉丝通过互联网，近距离接触访谈现场。\r\n【特色功能】\r\n劲爆活动：《蒙面歌王》官方独家听审团招募平台\r\n移动LIVE：随时随地，欣赏艺人直播。\r\n超全曲库：海量曲库，随时点歌，您点她唱，她唱你听。\r\n礼物商城：最全的礼物商城，喜欢就送她，与她进行更深的互动。\r\n听歌识曲：喜爱的音乐，不再悄然而逝。\r\n明星访谈：每周大咖艺人直播访谈，与偶像零距离互动。");
        appInfo16.setVersion("2.9.6.2");
        appInfo16.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/06/08/85zr3k781r/fanxing_6568_2.9.6.2_289.apk&hot=1&company_type=1");
        appInfo16.setType("直播");
        appInfo16.setMainType("app");
        appInfo16.setImageUrl("http://newmarket1.oo523.com:8080/mfiles/channels/360/imgs/2016/01/05/1451941279198/t01f4fb4c8c51a34149.jpg");
        appInfo16.saveInBackground(null);

        AppInfo appInfo17 = new AppInfo();
        appInfo17.setAppId("017");
        appInfo17.setIcon("http://newmarket.oo523.com:8080/market/img/2014/10/10/0w2f12f0r2/162x162.png");
        appInfo17.setName("想恋爱");
        appInfo17.setRating(4);
        appInfo17.setCount(428464);
        appInfo17.setSize(4550647);
        appInfo17.setBrief("想恋爱——线上聊天，线下牵线，3天成功约会。");
        appInfo17.setIntro("★真诚交友平台，拒绝约炮 \r\n★超高人气，24小时在线聊 \r\n★哪款是你的菜，私人定制\r\n★严格认证，保护用户权益\r\n★同城约会，快速见面牵手\r\n★魅力大比拼，看谁是女神 \r\n想恋爱时下全国唯一一款女多男少的婚恋交友平台。最安全的“脱光神器”\r\n人工审核照片，会员资料信息。保证用户真实性，提供安全可靠的相亲平台。\r\n一键登录，同城速配，智能精准推荐，恋爱约会相亲，互动方式简单且高效。\r\n24小时专业客服为在线您服务，及时解答各类问题。为您解决所有后顾之忧。");
        appInfo17.setVersion("4.6.0");
        appInfo17.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/02/02/8t0nev8csf/xla_v460_noad-MNA10013.apk&hot=1&company_type=1");
        appInfo17.setType("交友");
        appInfo17.setMainType("app");
        appInfo17.setImageUrl("http://newmarket.oo523.com:8080/market/img/2015/01/23/ye2ami8msm/498x692.jpg");
        appInfo17.saveInBackground(null);

        AppInfo appInfo18 = new AppInfo();
        appInfo18.setAppId("018");
        appInfo18.setIcon("http://newmarket.oo523.com:8080/market/img/2014/03/18/xch967mrjs/162x162.png");
        appInfo18.setName("微视");
        appInfo18.setRating(4);
        appInfo18.setCount(369924);
        appInfo18.setSize(31828952);
        appInfo18.setBrief("这是一个很有意思的短视频分享社区");
        appInfo18.setIntro("这是一个很有意思的短视频分享社区\r\n明星名人在玩，你的QQ好友、微博好友也在玩\r\n各种用户原创的美女、创意、搞笑和自拍视频，应有尽有，令你目不暇接\r\n你也可以用分段拍摄轻松拍出好玩的视频哦\r\n产品主要功能：\r\n8秒短视频，讲述你我的故事\r\n分段拍摄，操作极简，让创意无限\r\n视频流量和图片一样小，观看流畅\r\n众多明星名人欢乐自拍，等你来关注\r\n支持QQ号、腾讯微博帐号登录，与小伙伴们一起互动\r\n支持转发，评论和赞三种互动形式；业内首创转发和原创分离，打造最纯净的社区浏览体验\r\n支持将视频分享到腾讯微博、微信好友、朋友圈，无缝触达腾讯好友关系圈\r\n支持未登录浏览精选内容，闲着没事就来刷视频吧！");
        appInfo18.setVersion("3.0.1");
        appInfo18.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/market/apk/2015/02/13/qc3tp9woi9/weishi_3.0.1_zhuoyishichang.apk&hot=1&company_type=1");
        appInfo18.setType("交友");
        appInfo18.setMainType("app");
        appInfo18.setImageUrl("http://newmarket.oo523.com:8080/market/img/2014/11/29/bybj9ar03r/498x692.jpg");
        appInfo18.saveInBackground(null);

        AppInfo appInfo19 = new AppInfo();
        appInfo19.setAppId("019");
        appInfo19.setIcon("http://newmarket1.oo523.com:8080/mfiles/channels/360/icons/2016/01/09/1452287004926/t01fdee1e6397e7e1cf.png");
        appInfo19.setName("易信");
        appInfo19.setRating(5);
        appInfo19.setCount(496568);
        appInfo19.setSize(46949516);
        appInfo19.setBrief("易信真的可以有！");
        appInfo19.setIntro("真正不耗流量的免费电话？易信3.0，真的可以有！ 在社交聊天应用一窝蜂的今天，你可曾想过让它帮你节省电话费？ 易信3.0，或许是迄今为止地球上通话功能最强大的社交app。 打字贴图也聊不够？试试更高清的语音聊天，让感情升温，让朋友更真。 普通电话还不够聊？送你首月260分钟的免费电话，无回声不延迟，你不挂我不挂。 试试新版易信，你会发现好朋友们一直就在身边。 【易信主要特点】 ◆ 大杀器！新增易信电话，免费*直拨国内任意手机固话，通话过程不耗流量！通话质量秒杀同类产品！现在登录就送260分钟通话时间**！ ◆ 更个性！偶遇新增昵称设置并可将朋友圈、晒一晒展示在我的偶遇名片中； ◆ 更安全！新增帐号保护功能，新设备登录易信需要验证手机号码！ ◆ 更方便！iOS8中可在通知中心添加易信小插件，可快速启动易信电话、拍照等功能 【意见反馈】 ◆ 软件内进入【设置->意见反馈】 *免费拨打国内电话，部分用户可能因手机本身套餐类型，由运营商收取接听或漫游费用。 *赠送的260分钟通话时间，包括100分钟双人通话、100分钟多人通话");
        appInfo19.setVersion("4.4.1");
        appInfo19.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/05/16/fwfn0pwn4k/yixin_4.4.1.229_161080_20160509_c694.apk&hot=1&company_type=1");
        appInfo19.setType("电话");
        appInfo19.setMainType("app");
        appInfo19.setImageUrl("http://newmarket1.oo523.com:8080/mfiles/channels/360/imgs/2016/01/09/1452287004193/t016f30508394eb9554.png");
        appInfo19.saveInBackground(null);

        AppInfo appInfo20 = new AppInfo();
        appInfo20.setAppId("020");
        appInfo20.setIcon("http://newmarket.oo523.com:8080/market/img/2014/05/13/vjckccph0a/162x162.png");
        appInfo20.setName("4G网络电话");
        appInfo20.setRating(4);
        appInfo20.setCount(532204);
        appInfo20.setSize(4190862);
        appInfo20.setBrief("4G时代省钱必选神器!");
        appInfo20.setIntro("4G网络电话—最省话费的打电话软件，在全球500多个国家和地区内低资费甚至免费打电话，是拨打长途电话和国际电话的最佳选择。 【功能介绍】 音质清晰：WIFI直拨最流畅，直拨回拨随意换 资费最低：0月租0漫游0通信费，长途5分国际包月无压力 轻松连线：电脑手机，任意下载；绿色软件，安装即用 活动多多：一键注册送话费；推荐好友双赠送；夜间亲情免费打 【选择3G网络电话的理由】 有了4G，不插卡也能打电话，手机打到爆也不害怕欠费停机； 有了4G，直拨回拨任意切换，再也不用担心通话音质不给力； 有了4G，去电显号贴心拥有，从此告别不断解释和未知号码； 有了4G，声音即时诠释思念，联系亲友随时随地畅聊无极限。");
        appInfo20.setVersion("2.1.0");
        appInfo20.setApkUrl("\":\"http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/01/27/i07sflbuid/56a6d6a303587.apk&hot=1&company_type=1");
        appInfo20.setType("电话");
        appInfo20.setMainType("app");
        appInfo20.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/12/05/rk0315ew7d/498x692.png,http://newmarket.oo523.com:8080/180/img/2015/12/05/e14c94euvw/498x692.png");
        appInfo20.saveInBackground(null);

        AppInfo appInfo21 = new AppInfo();
        appInfo21.setAppId("021");
        appInfo21.setIcon("http://newmarket.oo523.com:8080/180/img/2016/02/01/eqh2k8i1oj/162x162.png");
        appInfo21.setName("王者荣耀");
        appInfo21.setRating(4);
        appInfo21.setCount(10591968);
        appInfo21.setSize(280721076);
        appInfo21.setBrief("全球首款5V5公平对战手游");
        appInfo21.setIntro("《王者荣耀》是全球首款5V5英雄公平对战手游，腾讯最新MOBA手游大作！5V5王者峡谷、5V5深渊大乱斗、以及3V3、1V1等多样模式一键体验，热血竞技尽享快感！海量英雄随心选择，精妙配合默契作战！10秒实时跨区匹配，与好友组队登顶最强王者！操作简单易上手，一血、五杀、超神，极致还原经典体验！实力操作公平对战，回归MOBA初心！\r\n赶快加入《王者荣耀》，随时开启你的激情团战！ \r\n \r\n\r\n【游戏特色】\r\n1、5V5！越塔强杀！超神！\r\n5V5经典地图，三路推塔，呈现最原汁原味的对战体验。英雄策略搭配，组建最强阵容，默契配合极限666！\r\n\r\n2、深渊大乱斗！随机英雄一路团战！\r\n5V5大乱斗，即刻激情团战！随机盲选英雄，全团杀中路，冲突一触即发！一条路，全神装，血战到底！\r\n\r\n3、随时开团！10分钟爽一把！\r\n最适合手机的MOBA游戏，10分钟享受极致竞技体验。迂回作战，手脑配合，一战到底！人多，速来！\r\n\r\n4、公平竞技！好玩不坑拼实力！\r\n凭实力carry全场，靠技术决定胜负。不做英雄养成，不设体力，还你最初的游戏乐趣！\r\n\r\n5、指尖超神！精妙走位秀操作！\r\n微操改变战局！手速流？意识流？看我精妙走位，力压群雄，打出钻石操作！收割，连杀超神！");
        appInfo21.setVersion("1.12.1.6");
        appInfo21.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/05/16/2icbt780dm/10003411_SGame_App_Android_1.12.1.6_r102073p.apk&hot=1&company_type=1");
        appInfo21.setType("RPG网游");
        appInfo21.setMainType("game");
        appInfo21.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/03/25/nt6m6humwf/498x692.jpg");
        appInfo21.saveInBackground(null);

        AppInfo appInfo22 = new AppInfo();
        appInfo22.setAppId("022");
        appInfo22.setIcon("http://newmarket.oo523.com:8080/180/img/2016/01/22/eyet7f22h4/162x162.png");
        appInfo22.setName("穿越火线：枪战王者");
        appInfo22.setRating(5);
        appInfo22.setCount(5668649);
        appInfo22.setSize(331664870);
        appInfo22.setBrief("CF正版FPS手游");
        appInfo22.setIntro("《穿越火线：枪战王者》是一款由韩国Smilegate研发商及腾讯游戏三年的倾力打造的CF正版FPS手游，完美传承了PC端的品质和玩法，同时还针对手机端的操作特点，进行了针对枪战玩家习惯的定制化适配与优化，让玩家能够在手机上真正享受枪战游戏带来的乐趣和快感，将三亿鼠标的枪战梦想延续到了手机上。");
        appInfo22.setVersion("1.0.7.60");
        appInfo22.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/04/29/4t1akxvgsy/10003411_cf-v60-v1_0_7_0-84-2016-04-26_11-44-01_p.apk&hot=1&company_type=1");
        appInfo22.setType("动作");
        appInfo22.setMainType("game");
        appInfo22.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/12/03/co07pqo3f0/498x692.png");
        appInfo22.saveInBackground(null);

        AppInfo appInfo23 = new AppInfo();
        appInfo23.setAppId("023");
        appInfo23.setIcon("http://newmarket.oo523.com:8080/180/img/2016/01/21/szmcf5gapa/162x162.png");
        appInfo23.setName("梦幻西游");
        appInfo23.setRating(5);
        appInfo23.setCount(2034414);
        appInfo23.setSize(321963862);
        appInfo23.setBrief("网易游戏出品，2015国民手游");
        appInfo23.setIntro("2015国民手游，2015年唯一包揽App Store四榜大满贯的网易史诗级巨作！\r\n《梦幻西游手游》新主角神天兵飞燕女，新宠物幽冥书生，震撼登场。\r\n阳光魅力榜，票选你的TA！魅力人气，无法抵挡。\r\n全新梦幻，经典再续！拥有3000万注册玩家，在线人数屡破纪录，上线仅两天即取得100万玩家同时在线的空前成绩，随后更是引爆了204万玩家的在线狂欢。\r\n\r\n《梦幻西游手游》手游是网易公司对中华传统文化的献礼，梦幻西游金牌团队倾力打造，2015年首选MMORPG手游！\r\nQ版造型的可爱人物，浓郁古典风的精美场景，原汁原味的经典玩法，尽在手游重现，随时随地领略梦幻西游的精彩与快乐！\r\n\r\n【游戏特色】\r\n-三界主角 各领风骚\r\n大唐官府、普陀山、阴曹地府……三界门派英雄优雅冷峻，调和阴阳，洞察众生，予你另类经典体验。\r\n-西游题材 再续经典\r\n《梦幻西游手游》经典玩法爱不释手，继承端游经典人设、地图设计，对其进行再诠释，感受梦幻角色的全新风貌，西游手游巅峰之作在精心雕琢中诞生。\r\n-技能绚丽 特效醉人\r\n精益求精的梦幻团队孕育出精彩绚丽的3D技能特效，在回合制畅游中感受迷醉的游戏风情。\r\n-造型Q萌，场景精美\r\n秉承梦幻西游端游的Q版风格，人物角色可爱迷人，游戏场景仙气缭绕，美轮美奂，神话感十足。\r\n\r\n【玩家好评】\r\n- 10年来的风雨同舟，用《梦幻西游手游》来重温那一段梦幻西游的青春记忆。\r\n- 游戏上手很容易，画面灰常精致，更有超神武器，2D页面3D效果，不愧是网易重金打造的MMORPG游戏，值得一玩。\r\n- 网易最强IP重出江湖，英雄神武，经典PVP，单挑BOSS，回合特效非常精彩，玩起来！");
        appInfo23.setVersion("1.58.0");
        appInfo23.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/01/16/755vxogmw4/g18_netease_zhuoyi2_dev_1.58.0.apk&hot=1&company_type=1");
        appInfo23.setType("RPG网游");
        appInfo23.setMainType("game");
        appInfo23.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/12/22/nn103i43cr/498x692.jpg");
        appInfo23.saveInBackground(null);

        AppInfo appInfo24 = new AppInfo();
        appInfo24.setAppId("024");
        appInfo24.setIcon("http://newmarket.oo523.com:8080/180/img/2016/01/29/qe59r9e3ny/162x162.png");
        appInfo24.setName("全民枪战");
        appInfo24.setRating(4);
        appInfo24.setCount(2645510);
        appInfo24.setSize(284238784);
        appInfo24.setBrief("加入就有机会获得相应积分，领取游戏奖励哦！");
        appInfo24.setIntro("《全民枪战》是全球首款FPS竞技手机游戏，最刺激的竞技游戏，最公平的游戏环境，最完美的操作手感，最高端的次世代游戏显示技术，在游戏内你可以自由跳跃射击，体验最真实的极致3D游戏世界！\r\n《全民枪战》数十种游戏地图和游戏模式任你选择，爆破、歼灭、团战、单挑、夺旗、抢物等特色玩法，成就掌上枪王不是梦，随时随地开战，体验移动战场美妙感觉！\r\n在《全民枪战》游戏内，上万性格迥异，能力超强的对手等你来战，强化、养成、升级技能等等多种玩法不会让你寂寞。冒险、闯关、对战，和战队朋友一起经历枪林弹雨。把超强的反动派一枪爆头，玩法多多您说了算，只为招募到最强伙伴！\r\n《全民枪战》游戏支持低端机型，只要你是触屏手机，就可展开激情战役。游戏支持WIFI和3G两种网络环境，即使出门在外也可以随时随时进入游戏。更有单机剧情模式，高速快捷的战斗，任何时候想玩都可以。公平合理的积分设计，玩家即使在无网络的环境下，也可以和机器人酣战一番，还会获得相应积分，领取游戏奖励哦！\r\n");
        appInfo24.setVersion("2.4");
        appInfo24.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/01/29/19gkd4xgrg/P30224A.apk&hot=1&company_type=1");
        appInfo24.setType("动作");
        appInfo24.setMainType("game");
        appInfo24.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/01/29/zx8uz76xdk/498x692.jpg");
        appInfo24.saveInBackground(null);

        AppInfo appInfo25 = new AppInfo();
        appInfo25.setAppId("025");
        appInfo25.setIcon("http://newmarket.oo523.com:8080/180/img/2016/04/01/8qvo8tnunb/162x162.png");
        appInfo25.setName("花千骨（糖宝代言）");
        appInfo25.setRating(5);
        appInfo25.setCount(2008765);
        appInfo25.setSize(274901999);
        appInfo25.setBrief("赵丽颖力荐-同名小说电视剧正版授权");
        appInfo25.setIntro("※  亿万粉丝力荐 同名电视剧剧情同步\r\n※  国民甜心赵丽颖  首次代言游戏！\r\n※  《花千骨》电视剧各大主创&演员联手入驻\r\n《花千骨》手游是根据暑期档热播电视剧《花千骨》及其小说原著改编而成的正版授权3D动作手游。\r\n《花千骨》斩获新浪微博话题总榜三连冠，双网收视第一，百度指数将破400万、单日网络播放量破3亿的傲人纪录。");
        appInfo25.setVersion("2.0.0");
        appInfo25.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/04/01/8m00at058p/huaqiangu.apk&hot=1&company_type=1");
        appInfo25.setType("RPG网游");
        appInfo25.setMainType("game");
        appInfo25.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/12/16/8z14qbg7wp/498x692.jpg");
        appInfo25.saveInBackground(null);

        AppInfo appInfo26 = new AppInfo();
        appInfo26.setAppId("026");
        appInfo26.setIcon("http://newmarket.oo523.com:8080/180/img/2016/01/28/6dy09rn9e9/162x162.png");
        appInfo26.setName("天天枪战");
        appInfo26.setRating(4);
        appInfo26.setCount(1706256);
        appInfo26.setSize(112389463);
        appInfo26.setBrief("一起来拯救世界吧！");
        appInfo26.setIntro("****血色萝莉扛枪扫射！****\r\n****《天天枪战》末日危机大逃亡拉开序幕****\r\n百余种酷炫枪械随心搭配，末日枪王之王由你打造！\r\n—————————————————————————————\r\n什么，DOTA太烧脑？\r\nCF嫌头大费眼？\r\n那还是沉静在软妹子的怀抱中，一起来拯救世界吧！\r\n一篇精彩绝伦的末日画卷，正在徐徐展开！一场史无前例的生存之战，正向你娓娓道来！《天天枪战》区别于市面上一些重度游戏，在讲究技巧和策略的同时，却能让你轻松上手。\r\n—————————————————————————————\r\n游戏特色：\r\n\r\n【特色一】霸气萝莉，颜值爆表！\r\nQ版人设，各色萝莉御姐NPC独树一帜。酷炫军团长高冷迷人，精妙小萝莉扛枪扫射，霸气御姐高雅迷人，人物特征鲜明过目不忘。不管你是萝莉控还是御姐控，这里一定有你的菜。\r\n\r\n【特色二】精准操控，以技取胜！\r\n触屏操控，血虐BOSS，痛快手感！走位，卡点，放大招“轰炸风暴”、“扭曲黑洞”、“激光巨炮”，酣畅打击，用操作点燃你的战斗激情，告别枯燥的数值比拼，用实力说话。\r\n\r\n【特色三】点碎屏幕，全程高能！\r\n完全区别于其他依靠单纯定位，不断点击操作的枪战游戏。《天天枪战》采用双摇杆似类街机操作，从风骚走位到技能搭配，战斗策略的制定无时无刻不在考验着玩家的才智。\r\n\r\n【特色四】独创基因，个性时装！\r\n“天才大脑”、“异变小腿”、“改造手臂”，改变它们从基因系统开始。“绿、蓝、紫、橙”四种基因材料，收集基因材料合成，提升角色属性战斗力。你的内在由你打造！有钱，任性。个性时装“进击的巨人”“凌波丽”，跟随喜好量身定制。\r\n\r\n【特色五】淋漓酣战，枪械为王！\r\n火、雷、冰三种属性衍生的百余种神级枪械，“土豪金——雷霆审判、血腥之牙、紫电风暴、毁灭公爵，”神枪傍手，助你百发百中。副本装备超高掉落，开启崭新的国民级射击微操盛宴。\r\n\r\n【用户评价】\r\n·玩了这么多的ARPG，这款游戏的画风和人设是我最满意的。果然小萝莉神马的才是大爱！\r\n·身临其境的战斗氛围，高度仿真的战斗危机，我擦，一不小心就被BOSS虐死了。再来！\r\n·一进入游戏，瞬间就被那个萌软小红猪迷住了！我的角色居然是个大波妹子，鼻血~~~\r\n·丧尸生化题材，就是喜欢重口味。\r\n\r\n天天枪战群：385330353，\r\n客服qq：800086778\r\n服务热线：4000889936");
        appInfo26.setVersion("1.14.0");
        appInfo26.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/05/31/kdcu5m33q6/release_X10035A_ttqzh_newgame_com.apk&hot=1&company_type=1");
        appInfo26.setType("RPG网游");
        appInfo26.setMainType("game");
        appInfo26.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/01/28/vjo4is04u5/498x692.jpg");
        appInfo26.saveInBackground(null);

        AppInfo appInfo27 = new AppInfo();
        appInfo27.setAppId("027");
        appInfo27.setIcon("http://newmarket.oo523.com:8080/180/img/2016/03/07/jggrsd1x4r/162x162.png");
        appInfo27.setName("大话西游");
        appInfo27.setRating(5);
        appInfo27.setCount(1616467);
        appInfo27.setSize(341396648);
        appInfo27.setBrief("网易游戏出品-大话西游同名手游");
        appInfo27.setIntro("大话西游手游是由网易大话2金牌团队倾力打造，中国回合经典MMORPG网络游戏大话西游同名手游。国风古韵，清新人物，轻盈笔触勾勒出情义江湖；经典玩法手游重现，再续江湖豪情！畅爽激情的快节奏战斗，处处惊喜的几率玩法，网易游戏史诗级力作，大话西游手游来了！");
        appInfo27.setVersion("1.1.27");
        appInfo27.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/03/07/urn79ddti8/g17_netease_zhuoyi2_dev_1.1.27.apk&hot=1&company_type=1");
        appInfo27.setType("RPG网游");
        appInfo27.setMainType("game");
        appInfo27.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/11/10/znd9fozuva/498x692.jpg");
        appInfo27.saveInBackground(null);

        AppInfo appInfo28 = new AppInfo();
        appInfo28.setAppId("028");
        appInfo28.setIcon("http://newmarket.oo523.com:8080/180/img/2016/03/31/ke7j83nl6b/162x162.png");
        appInfo28.setName("天天炫舞");
        appInfo28.setRating(4);
        appInfo28.setCount(1564778);
        appInfo28.setSize(169105807);
        appInfo28.setBrief("全新版本“我们结婚啦”甜蜜上线！");
        appInfo28.setIntro("全新版本“我们结婚啦”甜蜜上线！\r\n情侣对战，2V2斗舞模式重磅登场！\r\n浪漫婚礼，伴郎伴娘亲友团见证您的恋情！\r\n\r\n《天天炫舞》由英雄互娱发行，劲舞团原班人马倾力打造的超人气3D音舞手游，在这里无论您是什么身份都可以找到属于您自己的友情爱情，快来一起开启指尖恋爱全民时代！\r\n\r\n游戏荣耀\r\n2015GMGC手游画面大奖，2015年金翎奖“原创移动游戏大奖”,英雄联赛电竞比赛指定游戏！");
        appInfo28.setVersion("2.9");
        appInfo28.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/03/31/ysv7lc0mr8/56f8ccd23b537.apk&hot=1&company_type=1");
        appInfo28.setType("动作");
        appInfo28.setMainType("game");
        appInfo28.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/03/31/u7io7z27k8/498x692.jpg");
        appInfo28.saveInBackground(null);

        AppInfo appInfo29 = new AppInfo();
        appInfo29.setAppId("029");
        appInfo29.setIcon("http://newmarket.oo523.com:8080/180/img/2016/04/01/plazw1tk0d/162x162.png");
        appInfo29.setName("神魔");
        appInfo29.setRating(4);
        appInfo29.setCount(1540228);
        appInfo29.setSize(193578252);
        appInfo29.setBrief("神魔2.0-下载抢红包");
        appInfo29.setIntro("青春神魔，新春贺岁天天抢红包！即日起下载游戏就有机会领取红包，还有春节版iwatch、猴年金条、充值卡等你来赢！\r\n颠覆经典西游题材手游，超过1.5亿次下载，进军海外手游市场top10，5000万玩家同场竞技！ \r\n年末重磅阵营混战新玩法，神魔人三界纷争，巅峰对决火力全开，挑战指尖的斗战风云！坐骑法宝助力降魔，华丽技能无缝连招，力破千军的战斗体验，让你玩转动作手游经典！");
        appInfo29.setVersion("3.2.35");
        appInfo29.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/06/07/l98t8jofo4/shenmo_egg_3.2.35_357.apk&hot=1&company_type=1");
        appInfo29.setType("RPG网游");
        appInfo29.setMainType("game");
        appInfo29.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/01/25/bxm00s13xe/498x692.jpg");
        appInfo29.saveInBackground(null);

        AppInfo appInfo30 = new AppInfo();
        appInfo30.setAppId("030");
        appInfo30.setIcon("http://newmarket.oo523.com:8080/180/img/2015/12/15/eufua4459x/162x162.png");
        appInfo30.setName("全民超神");
        appInfo30.setRating(5);
        appInfo30.setCount(1424947);
        appInfo30.setSize(357419989);
        appInfo30.setBrief("多人实时电竞手游！");
        appInfo30.setIntro("《全民超神》是全球首款5V5实时MOBA电竞手游。游戏将经典的MOBA玩法落地于“3V3大乱斗”、“5V5 MOBA”等模式；还在等什么？来《全民超神》手机开个黑！");
        appInfo30.setVersion("1.14.0");
        appInfo30.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/06/20/90map1ty3x/10003411_Moba_1.14.0.114262_android_Release.apk.protect.apk&hot=1&company_type=1");
        appInfo30.setType("动作");
        appInfo30.setMainType("game");
        appInfo30.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/12/17/5pyzg0end0/498x692.jpg");
        appInfo30.saveInBackground(null);

        AppInfo appInfo31 = new AppInfo();
        appInfo31.setAppId("031");
        appInfo31.setIcon("http://newmarket.oo523.com:8080/180/img/2016/03/09/l391mcoj4r/162x162.png");
        appInfo31.setName("去吧皮卡丘");
        appInfo31.setRating(4);
        appInfo31.setCount(1336324);
        appInfo31.setSize(114739922);
        appInfo31.setBrief("精灵卡牌对战手游千万玩家力荐");
        appInfo31.setIntro("《去吧皮卡丘》独特的模拟精灵养成玩法，百种美萌精灵助你战场取胜！组队刷怪，巨龙遗迹等你来战，击杀BOSS，怒刷碎片！装备附魔，成就战场之王！\r\n\r\n【游戏特色】\r\n※巨龙遗迹 太极战场\r\n全新版本的《去吧皮卡丘》新增巨龙遗迹刷怪战场，体现属性系的时候到了,和好友组队一起刷更过瘾，畅享团队副本战斗激情！狂杀BOSS，征战不休，我的大刀已经饥渴难耐！\r\n\r\n※装备附魔 全新战斗\r\n游戏新增装备附魔，给装备赋予魔性，吸取来自宠物的属性，作为自身强化的对象！增加装备属性系加成，回馈给精灵，增强精灵属性输出！助力战场，叠加越多，加成越多！全新体验，等你来战！\r\n\r\n※新版火箭队 助力全场\r\n新版本新增火箭神宠，征服巨龙，脚踏竞技场，备战卡洛斯！异军凸起领军人物！谁更能维护世界的和平？谁才是最强精灵王？让我们期待新版本发布吧！时刻准备着！\r\n");
        appInfo31.setVersion("2.6.0");
        appInfo31.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/06/02/yltaub2n6p/pikaqiu_X10266A_PM_SMA_260.apk&hot=1&company_type=1");
        appInfo31.setType("卡牌");
        appInfo31.setMainType("game");
        appInfo31.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/03/09/6agg8nwc7x/498x692.jpg");
        appInfo31.saveInBackground(null);

        AppInfo appInfo32 = new AppInfo();
        appInfo32.setAppId("002");
        appInfo32.setIcon("http://newmarket.oo523.com:8080/180/img/2015/12/14/v8fq0ecpx7/162x162.png");
        appInfo32.setName("全民突击-腾讯首款真人实时对战FPS枪战手游");
        appInfo32.setRating(5);
        appInfo32.setCount(1303641);
        appInfo32.setSize(373985957);
        appInfo32.setBrief("腾讯首款3D枪战手游！");
        appInfo32.setIntro("《全民突击》全新枪神赛版本震撼来袭，快来和好友一起来参加“枪神排位赛”吧！《全民突击》是腾讯首款3D枪战手游。 全3D绚丽画面，打造电影级视觉盛宴；经典名枪、炫酷战车，构建军迷博物馆；最强佣兵团养成系统，晋升全能指挥官；闯关模式、无尽模式，再现真实战场；真人实时对战，不再一个人寂寞打枪！ 快加入《全民突击》，体验枪战快感，激爽战斗！\r\n重要问题周知：\r\n1、请关注”百度贴吧“——”全民突击“吧；\r\n2、请关注QQ“兴趣部落”——“全民突击”部落，了解更多资讯；\r\n3、全民突击微信公众号\\\"qmtj2015\\\"了解更多资讯。");
        appInfo32.setVersion("2.4.0");
        appInfo32.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/05/26/yfh8fxwuki/10003411_WeFire_2_4_0_4435p_Release.apk&hot=1&company_type=1");
        appInfo32.setType("动作");
        appInfo32.setMainType("game");
        appInfo32.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/12/14/tr10jkhjpx/498x692.jpg");
        appInfo32.saveInBackground(null);

        AppInfo appInfo33 = new AppInfo();
        appInfo33.setAppId("033");
        appInfo33.setIcon("http://newmarket.oo523.com:8080/180/img/2016/01/22/as0t9zv65w/162x162.png");
        appInfo33.setName("少年三国志");
        appInfo33.setRating(4);
        appInfo33.setCount(1151457);
        appInfo33.setSize(149302360);
        appInfo33.setBrief("“跑男”陈赫代言，七天送赵云！");
        appInfo33.setIntro("19岁的诸葛亮，师从水镜，寄傲琴书，南阳苦读，27岁，草庐三顾，博望坡火烧夏侯终成名；\r\n15岁的刘备，初识儒术，游侠涿县，织席贩履，23岁，大破黄巾，安喜县怒鞭督邮逞豪杰！\r\n每位英雄都有那么一段年少青葱、热血无畏，也有那么一阵积累蛰伏、韬光养晦；\r\n游族网络《少年三国志》以英雄们少年时的视角，重走那段正统的三国旅程，这里没有是非成败转头空的寂寞，只有天下英雄独使君的霸气！\r\n【好礼不断】\r\n◆送顶级橙将！登录7天，赵云、张辽、太史慈、张角任你选！\r\n◆元宝天天送！不愁囊中空涩，有钱任性！\r\n◆史上炫周边！高仿真小乔，26个隐藏关节，仿真皮肤，姿势任摆！\r\n◆最时尚礼品！最轻平板iPad air2，智能手环Jawbone up2…等你来取！\r\n◆最贴心礼包！免费流量礼包，情人节滴滴红包，春节话费红包……好礼不断！");
        appInfo33.setVersion("2.4.20");
        appInfo33.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/03/23/2r5e0ds1iw/94_10266_2106_2.4.20.apk&hot=1&company_type=1");
        appInfo33.setType("卡牌");
        appInfo33.setMainType("game");
        appInfo33.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/07/13/lg9jbh87ls/498x692.jpg");
        appInfo33.saveInBackground(null);

        AppInfo appInfo34 = new AppInfo();
        appInfo34.setAppId("034");
        appInfo34.setIcon("http://newmarket.oo523.com:8080/180/img/2016/01/08/ble1u7r4yo/162x162.png");
        appInfo34.setName("火影忍者");
        appInfo34.setRating(4);
        appInfo34.setCount(1305944);
        appInfo34.setSize(437850445);
        appInfo34.setBrief("火之意志，格斗重燃！");
        appInfo34.setIntro("全新忍者来袭，静音、药师兜带你横扫忍界；\r\n全新玩法革新，任务集会所超多玩法超好玩；\r\n副本再次升级，修行之路再添120关爽翻天；\r\n全新对战模式，PVP赛季狂欢开启等你来战；");
        appInfo34.setVersion("1.10.12.13");
        appInfo34.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/05/26/uzt204z9gy/10003411_Kihan_PUBLISH_1.10.12.13.apk&hot=1&company_type=1");
        appInfo34.setType("动作");
        appInfo34.setMainType("game");
        appInfo34.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/01/08/c1qffo84kb/498x692.jpg");
        appInfo34.saveInBackground(null);

        AppInfo appInfo35 = new AppInfo();
        appInfo35.setAppId("035");
        appInfo35.setIcon("http://newmarket.oo523.com:8080/market/img/2015/03/05/2ruhejl68z/162x162.png");
        appInfo35.setName("月光宝盒(电影正版)");
        appInfo35.setRating(4);
        appInfo35.setCount(1027648);
        appInfo35.setSize(163507280);
        appInfo35.setBrief("星爷经典电影大作《月光宝盒（电影正版）》独家正版授权");
        appInfo35.setIntro("星爷经典电影大作大话西游独家正版授权——梦幻级动作回合制手游《月光宝盒（电影正版）》正式开放！\r\n痴情执着的紫霞仙子，纠结难舍的至尊宝，原配妻子白晶晶，舍生取义的唐僧等熟悉的经典角色；大圣娶亲夜的经典桥段、夕阳武士城门荣誉之战等无厘头的爆笑剧情。致敬经典，让感动再一次弥漫心间。还星爷一个正版手游，还亿万粉丝一个心愿！\r\n\r\n●正版授权，还原经典●\r\n《大话西游》电影独家正版授权，最大程度的电影剧情还原，每时每刻都能感受到曾经熟悉的回忆。\r\n\r\n●画面唯美，身临其境●\r\n1080P高清画质，栩栩如生；唯美场景，一花一草带你感受制作团队的专业。\r\n\r\n●拉帮结派，缔结姻缘●\r\n是否想体验万人之上的感觉，不如拉上你的生死好友开山立派，从此万人流传。花前月下，耳鬓厮磨，偶然的相遇促成甜蜜的婚姻。\r\n\r\n●坐骑翅膀，光武法宝●\r\n华丽的坐骑，带你上天入地。炫酷拉风的翅膀光武，让你在万众之中成为瞩目的焦点。\r\n\r\n●诸天神佛，唯你号令●\r\n诸天神佛皆可收入麾下，助力你西行之路；特有的缘分系统组合搭配千变万化；战场瞬息万变，打造你的最强战阵。\r\n\r\n客服电话：0551-65417668");
        appInfo35.setVersion("1.03");
        appInfo35.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/market/apk/2015/03/05/dkpqp9n6ys/月光宝盒（电影正版）_天奕达_0001262.apk&hot=1&company_type=1");
        appInfo35.setType("策略网游");
        appInfo35.setMainType("game");
        appInfo35.setImageUrl("http://newmarket.oo523.com:8080/market/img/2015/03/05/2aerc0lz65/498x692.jpg");
        appInfo35.saveInBackground(null);

        AppInfo appInfo36 = new AppInfo();
        appInfo36.setAppId("036");
        appInfo36.setIcon("http://newmarket.oo523.com:8080/market/img/2014/09/26/t9ub0t5gfu/162x162.png");
        appInfo36.setName("英雄之剑");
        appInfo36.setRating(4);
        appInfo36.setCount(999366);
        appInfo36.setSize(68358749);
        appInfo36.setBrief("4000万玩家的格斗梦想，蓝港年度格斗手游独家首发！以你之名铸英雄之剑！百度携英雄之剑万元豪礼限时放送！！");
        appInfo36.setIntro("★关注《英雄之剑》贴吧享百度移动游戏好礼，十万话费独家福利七天连送\r\n年度最受欢迎格斗手游！次世代ARPG再掀惊世狂潮！金牌团队全新力作——百度独家首发《英雄之剑》王者君临！浮空连击秒杀全场，经典操作随心所欲，百变时装华丽转身，公平交易自由买卖，副本闯关绝世装备萝莉女仆应有尽有，4000万玩家都在玩，以你之名铸英雄之剑！\r\n★50M超小安装包，随时随地想下就下！\r\n★物品交易全面开放，人人平等绝对公平！\r\n★50套外装随你搭配，尽显时尚张扬本色！\r\n★觉醒技能无消耗释放，全屏秒杀炫爆战场！\r\n★200+多样副本，虐杀BOSS征服大陆！\r\n★1080P高清画面，精致美感指尖展现！\r\n【游戏特色】\r\n★★★50M超小安装包★★★\r\n50M超小安装包，粉碎流量问题，体感战斗畅爽随行！\r\n★★★公平交易人人平等★★★\r\n开放交易人人平等，前所未有绝对公平，免费也能玩的爽！\r\n★★★绚丽外装自由展现★★★\r\n50套时装随你搭配，尽显时尚张扬本色，青春就要与众不同！\r\n★★★无双觉醒征服与碾压★★★\r\n独创无双觉醒AOE，全屏制霸，给你前所未有的战斗体验\r\n★★★1080P高清画面★★★\r\n1080P高清画面，电影般的视觉体验！\r\n★★★自由操纵全屏制霸★★★\r\n无消耗技能释放，精准操作只为实现你的荣耀，酣畅战斗超乎想象！\r\n★★★浮空连击秒杀全场★★★\r\n自由组合COMBO连击，狂猛连击尽在指尖，碾压对手畅爽体验！！\r\n【九大活动喜迎来袭】\r\n活动一：来玩就送橙武，三件更过瘾\r\n活动二：冲级送时装，战力就是高\r\n活动三：蓝钻、红钻天天送，我才是大富豪\r\n活动四：海量电话卡，英雄速来拿\r\n活动五：共建骑士团，再送电话卡\r\n活动六：关注官方微信，精灵公主来献礼\r\n活动七：新英雄七日奖励\r\n活动八：升级就送大礼包\r\n活动九：累计签到送不停");
        appInfo36.setVersion("1.0");
        appInfo36.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2015/05/28/4txxp8bt8x/6000000784.apk&hot=1&company_type=1");
        appInfo36.setType("动作");
        appInfo36.setMainType("game");
        appInfo36.setImageUrl("http://newmarket.oo523.com:8080/market/img/2014/09/26/nr5psr0ryv/498x692.jpg");
        appInfo36.saveInBackground(null);

        AppInfo appInfo37 = new AppInfo();
        appInfo37.setAppId("037");
        appInfo37.setIcon("http://newmarket.oo523.com:8080/180/img/2015/09/09/snofok8m98/162x162.png");
        appInfo37.setName("格斗冒险岛");
        appInfo37.setRating(4);
        appInfo37.setCount(943559);
        appInfo37.setSize(156889270);
        appInfo37.setBrief("《格斗冒险岛》是2015年最好玩的游戏，没有之一！");
        appInfo37.setIntro("《格斗冒险岛》是全球首款动作版冒险岛手游！将动作、冒险与解谜、经典单机游戏元素完美融合！你只有在《格斗冒险岛》，才能体会到思考和探索的乐趣！你只有在《格斗冒险岛》，才能享受爽快流畅的战斗打击感！你只有在《格斗冒险岛》，才能体验到原汁原味的冒险，回味小霸王的经典！《格斗冒险岛》是2015年最好玩的游戏，没有之一！");
        appInfo37.setVersion("1.5.1");
        appInfo37.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/03/02/ygf6htcm35/gdmxd-1.5.1-b-n-1860.apk&hot=1&company_type=1");
        appInfo37.setType("动作");
        appInfo37.setMainType("game");
        appInfo37.setImageUrl("http://newmarket.oo523.com:8080/market/img/2015/02/04/xfnllkxxt4/498x692.jpg");
        appInfo37.saveInBackground(null);

        AppInfo appInfo38 = new AppInfo();
        appInfo38.setAppId("038");
        appInfo38.setIcon("http://newmarket.oo523.com:8080/180/img/2016/03/18/qbnxyvjggq/162x162.png");
        appInfo38.setName("太极熊猫");
        appInfo38.setRating(4);
        appInfo38.setCount(797705);
        appInfo38.setSize(276159271);
        appInfo38.setBrief("蜗牛首款动作RPG手游《太极熊猫》");
        appInfo38.setIntro("蜗牛首款动作RPG手游《太极熊猫》携浓厚美式漫画风格来袭！想体验3D端游引擎和真人动作捕捉技术强强打造出的手游最爽快打击感么？想体验独一无二的浮空连招格斗带来畅爽体验嘛？《太极熊猫》庞大的伊瓦兰斯幻之大陆等你来挑战！三大职业，百种武神，千种装备最强的动作角色等你来养成！全新开放实时PVP竞技场、组队BOSS战让你成为真正的强者");
        appInfo38.setVersion("1.1.19");
        appInfo38.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/06/04/a31kxzj2aj/product_officialnew(1.1.19)5-30Ad1614.apk&hot=1&company_type=1");
        appInfo38.setType("RPG网游");
        appInfo38.setMainType("game");
        appInfo38.setImageUrl("http://newmarket.oo523.com:8080/180/img/2015/12/16/jwsotsvbaa/498x692.jpg");
        appInfo38.saveInBackground(null);

        AppInfo appInfo39 = new AppInfo();
        appInfo39.setAppId("039");
        appInfo39.setIcon("http://newmarket.oo523.com:8080/180/img/2016/04/21/ymwyymuh3b/162x162.png");
        appInfo39.setName("新花千骨");
        appInfo39.setRating(4);
        appInfo39.setCount(791156);
        appInfo39.setSize(178078262);
        appInfo39.setBrief("大型Q版创新回合制MMORPG仙侠手游大作");
        appInfo39.setIntro("《新花千骨》是一款大型Q版创新回合制MMORPG仙侠手游大作，由Fresh果果同名小说正版授权，完美还原小说剧情。\r\n游戏以一人多宠、激情PK为核心玩法，玩家可带领多名宠物怒闯蛮荒之地，在帮派守卫战中享受畅爽PK战！运镖、护送神兽、收集十方神器，还能钓鱼、答题、猜拳等能获取丰厚收益，更有语音聊天、组队抓鬼等精彩玩法，让你玩转回合、驰骋天地！");
        appInfo39.setVersion("4.1.0");
        appInfo39.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/06/04/5rnxm1qiht/574f9cc54f4ee.apk&hot=1&company_type=1");
        appInfo39.setType("RPG网游");
        appInfo39.setMainType("game");
        appInfo39.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/03/16/x8nguodvoi/498x692.jpg");
        appInfo39.saveInBackground(null);

        AppInfo appInfo40 = new AppInfo();
        appInfo40.setAppId("040");
        appInfo40.setIcon("http://newmarket.oo523.com:8080/180/img/2016/03/04/0kky2ty8wq/162x162.png");
        appInfo40.setName("赛尔号超级英雄");
        appInfo40.setRating(4);
        appInfo40.setCount(760181);
        appInfo40.setSize(218252524);
        appInfo40.setBrief("《赛尔号》原版官方手游，探险宇宙拯救地球");
        appInfo40.setIntro("球能源告急，赛尔英雄的新使命已经开始！\r\n\r\n宇宙新冒险现已开启，所有赛尔英雄即将接受新的挑战。在《赛尔号》超级英雄中，每一位赛尔英雄均可组建属于自己的战队，与万千赛尔精灵一起，打造专属战神联盟，寻找拯救地球的新能源！\r\n\r\n【游戏福利】\r\n玩《赛尔号超级英雄》送游戏战神联盟手办\r\n\r\n【特色玩法】\r\n\r\n---精灵乱斗\r\n报告船长，前方有敌情！赛尔号正在遭受袭击，赛尔精灵准备全军出击！不对，我方精灵怎么跑到敌方阵营去了，并且我们的队伍里竟然也有敌方精灵？是反间计还是新科技，快来《赛尔号》超级英雄精灵大乱斗里一探究竟。\r\n\r\n---战神联盟\r\n 令敌人闻风丧胆的战神联盟出现了！《赛尔号》超级英雄的战神联盟里，有你的赛尔精灵吗？赛尔号战神联盟代表高战力的存在，这可是宇宙星际荣耀，加入战神联盟，成为金闪闪的一员！\r\n\r\n---星际远征\r\n浩瀚宇宙，赛尔号在探索。新能源就在前方，可恶竟然有实力强大的对手阻止赛尔精灵的前进。没办法，只能击败它们了。赛尔号精灵们请做好战斗的准备吧，探索更广阔的空间获取更多的资源，为了地球前进！");
        appInfo40.setVersion("1.6.0");
        appInfo40.setApkUrl("http://apk.tt286.com:8005/api/charge?token=cyl&from=000001&blink=http://newmarket.oo523.com:8080/180/apk/2016/06/03/xz8ej6wqam/574cfc944cd52.apk&hot=1&company_type=1");
        appInfo40.setType("RPG网游");
        appInfo40.setMainType("game");
        appInfo40.setImageUrl("http://newmarket.oo523.com:8080/180/img/2016/03/31/hbfcdkovx0/498x692.jpg");
        appInfo40.saveInBackground(null);
    }

    private void uploadBanner() {
        Banner banner = new Banner();
        banner.setAppId("001");
        banner.setImgUrl("http://newmarket.oo523.com:8080/market/img/2013/11/27/jrfbaj5pxo/1080x300.jpg");
        banner.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                Log.i("Test", "result1" + aBoolean);
            }
        });

        Banner banner2 = new Banner();
        banner2.setAppId("002");
        banner2.setImgUrl("http://newmarket.oo523.com:8080/market/img/2013/11/27/5zshqvupyw/1080x300.png");
        banner2.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                Log.i("Test", "result2" + aBoolean);
            }
        });
        Banner banner3 = new Banner();
        banner3.setAppId("003");
        banner3.setImgUrl("http://newmarket.oo523.com:8080/market/img/2013/11/27/gjcdeo1up3/1080x300.png");
        banner3.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                Log.i("Test", "result3" + aBoolean);
            }
        });
        Banner banner4 = new Banner();
        banner4.setAppId("004");
        banner4.setImgUrl("http://newmarket.oo523.com:8080/market/img/2013/11/27/0xmc6y9hxe/1080x300.png");
        banner4.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                Log.i("Test", "result4" + aBoolean);
            }
        });
        Banner banner5 = new Banner();
        banner5.setAppId("005");
        banner5.setImgUrl("http://newmarket.oo523.com:8080/market/img/2013/11/27/kosftkb7nv/1080x300.png");
        banner5.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                Log.i("Test", "result5" + aBoolean);
            }
        });
    }

}
