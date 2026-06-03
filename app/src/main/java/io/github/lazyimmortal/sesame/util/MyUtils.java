package io.github.lazyimmortal.sesame.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import io.github.lazyimmortal.sesame.data.ViewAppInfo;

/*
 *  ⭐为了保障您的操作安全，请进行验证后继续：
 *    com.alipay.sportshealth.biz.rpc.SportsHealthCoinTaskRpc.queryCoinTaskPanel
 *    com.alipay.sportshealth.biz.rpc.sportsHealthHomeRpc.queryEnergyBubbleModule
 *    com.alipay.sportshealth.biz.rpc.SportsHealthCoinTaskRpc.completeTask
 *    com.alipay.antfarm.doFarmTask
 *  ⭐访问被拒绝：
 *    method: alipay.mobile.ipsponsorprod.consume.gold.task.signin.calendar
 *  ⭐人气太旺啦，请稍后再试：
 *    alipay.antmember.biz.rpc.membertask.h5.signPageTaskList
 *  ⭐反射了不存在的方法
 *    ERROR: AntSports, java.lang.NoSuchMethodError: com.alibaba.health.pedometer.intergation.rpc.RpcManager#a()
 *  ⭐当前网络不可用，请稍后重试
 *    alipay.antforest.forest.h5.queryTaskList
 *  ⭐广告请求错误 this is a cheating traffic   作弊流量
 *    com.alipay.adexchange.ad.facade.xlightPlugin
 */

public class MyUtils {

    // 用户id、昵称对应表
    public static final HashMap<String, String> mUidMap = new HashMap<>();
    private static SharedPreferences mSP = null;
    // 修改参数，不让小鸡自动睡觉
    public static final String NO_SLEEP = "canSleepXXX";

    public static final boolean _关闭人气太旺 = true;
    public static final boolean _关闭不存在的方法调用 = true;
    public static final boolean _关闭首页弹窗 = true;
    public static final boolean _关闭必弹验证1 = true;
    public static final boolean _关闭必弹验证2 = true;
    public static final boolean _关闭必弹验证3 = true;
    public static final boolean _关闭必弹验证4 = true;
    public static final boolean _关闭作弊广告流量 = true;
    public static final boolean _关闭不支持RPC1 = true;
    public static final boolean _关闭不支持RPC2 = true;
    public static final String _访问被拒绝1 = "alipay.mrchservbase.mrchbusiness.sign.transcode.check_1";
    public static final String _访问被拒绝2 = "alipay.mobile.ipsponsorprod.consume.gold.task.signin.calendar_2";
    public static final String _访问被拒绝3 = "alipay.mobile.ipsponsorprod.consume.gold.task.signin.calendar_3";
    public static final String _系统出错正在排查1 = "alipay.mrchservbase.zcj.taskList.query.v2_1";
    // 任务[0.1元起租会员攒粒]id:2026012058541320399
    // 任务[去雇佣芝麻大表鸽]id:2026012058542045915
    // 任务[坚持看直播领福利]id:2026012058542176083
    // 任务[完成旧衣回收得现金]id:2026012058543269012
    // 任务[去玩小游戏]id:2026012058542511985
    public static final List<String> _不是有效的入参 = new ArrayList<>(Arrays.asList(
            "2026010358596942583",
            "2026012058541320399",
            "2026012058542045915",
            "2026012058542176083",
            "2026012058543269012",
            "2026012058542511985"
    ));

    // 功能异常key加版本后缀，避免新版本继承旧版本的错误标记
    private static String get功能异常Key(@NonNull String key) {
        String version = ViewAppInfo.getAppVersion();
        if (version != null && !version.isEmpty()) {
            return key + "_" + version;
        }
        return key;
    }

    public static boolean getSp功能异常(@NonNull String key) {
        SharedPreferences sp = getMySp();
        if (sp == null) return true;
        return sp.getBoolean(get功能异常Key(key), false);
    }

    public static void setSp功能异常(@NonNull String key, org.json.JSONObject jo) {
        if (jo == null) return;
        // {"error":1009,"errorMessage":"访问被拒绝","errorNo":3,"errorTip":"1009"}
        // {"error":3000,"errorMessage":"系统出错，正在排查","errorNo":3,"errorTip":"3000"}
        String errorMessage = jo.optString("errorMessage", "");
        boolean isError = errorMessage.contains("访问被拒绝") || errorMessage.contains("系统出错");
        if (isError) {
            SharedPreferences sp = getMySp();
            if (sp != null) {
                sp.edit()
                        .putBoolean(get功能异常Key(key), true)
                        .apply();
            }
        }
    }

    // 是否关闭验证（拼手速、派遣动物、能量雨、赠送道具、部分蚂蚁积分任务、消费金签到 -> 目前发现这些操作会触发验证）
    public static boolean closeVerification() {
        return true;
    }

    // 关闭可能异常的功能：
    // 黄金票 com.alipay.wealthgoldtwa.goldbill.v2.index.collect  系统出错，正在排查
    // 文体中心走路 alipay.tiyubiz.wenti.walk.participate 系统出错，正在排查
    public static boolean closeErrorFunction() {
        return true;
    }

    // 关闭"不支持rpc完成的任务"
    public static boolean closeUnRpc() {
        return true;
    }

    private static @Nullable SharedPreferences getMySp() {
        Context context = ViewAppInfo.getContext();
        if (context == null) return null;
        if (mSP == null) mSP = context.getSharedPreferences("XQE_UID", Context.MODE_PRIVATE);
        return mSP;
    }

    // 打印用户切换
    public static String recordUserName(@Nullable String uid) {
        SharedPreferences sp = getMySp();
        if (sp == null) return "";
        if (TextUtils.isEmpty(uid)) return "";
        String name = mUidMap.get(uid);
        if (!TextUtils.isEmpty(name)) {
            sp.edit().putString(uid, name).apply();
            return ":" + name;
        } else {
            String spName = sp.getString(uid, "");
            if (TextUtils.isEmpty(spName)) {
                return ":" + uid;
            } else {
                mUidMap.put(uid, spName);
                return ":" + spName;
            }
        }
    }

    // 首页显示全部记录，菜单显示其他记录
    public static boolean showHomeAllLog() {
        return true;
    }

    public static final String _OPT_TASKLIST = "taskList";
    public static final String _OPT_USER_EXCHANGE_RECORDS = "userExchangeRecords";
    public static final String _OPT_ANIMALS = "animals";
    public static final String _OPT_NOW = "now";
    public static final String _OPT_SLEEP_NOTIFY_INFO = "sleepNotifyInfo";
    public static final String _NO_SUPPORT_ANTFARM_CHOUCHOULECHOUKUAN = "_chouchoulechoukuan";
    public static final String _NO_SUPPORT_ANTFARM_TAO_GOLDEN_V2 = "TAO_GOLDEN_V2";
    public static final String _NO_SUPPORT_ANTMEMBER_NGFE_TAG__PTR3O4ERIU = "ngfe_tag__ptr3o4eriu";

    // 加密（直通，如有需要可接入AESUtil）
    public static String encryptData(String data) {
        return data;
    }

    // 解密（直通，如有需要可接入AESUtil）
    public static String decryptData(String data) {
        return data;
    }

    // APP名称后缀
    public static String getAppTitleExt(@Nullable Context context) {
        if (context == null) return "";
        if (context.getPackageName().startsWith("kt")) {
            return "GR";
        }
        return "";
    }

    public static Calendar getInstance() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
    }
}
