package io.github.lazyimmortal.sesame.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import org.json.JSONObject;

import io.github.lazyimmortal.sesame.BuildConfig;
import io.github.lazyimmortal.sesame.hook.ApplicationHook;

public class LibraryUtil {
    private static final String TAG = LibraryUtil.class.getSimpleName();
    private static final String VERSION = "1.8.2302070202.46";

    public static String getLibSesamePath(Context context) {
        String libSesamePath = null;
        try {
            libSesamePath = context.getPackageManager()
                                    .getApplicationInfo(BuildConfig.APPLICATION_ID, 0)
                                    .nativeLibraryDir + "/" + System.mapLibraryName("sesame");
        } catch (PackageManager.NameNotFoundException e) {
            ToastUtil.show(context, "请授予支付宝读取芝麻粒的权限");
            Log.record("请授予支付宝读取芝麻粒的权限");
        }
        return libSesamePath;
    }

    public static Boolean loadLibrary(String libraryName) {
        try {
            System.loadLibrary(libraryName);
            return true;
        } catch (UnsatisfiedLinkError e) {
            return false;
        }
    }

    /**
     * 执行庄园任务（Java RPC实现，替代native SO调用）
     * 参考：https://github.com/Fansirsqi/Sesame-TK/blob/main/app/src/main/java/fansirsqi/xposed/sesame/task/antFarm/AntFarm.java
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static Boolean doFarmTask(JSONObject jo) {
        try {
            String title = jo.optString("title", "");
            String bizKey = jo.optString("bizKey", "");
            String taskId = jo.optString("taskId", "");
            String taskMode = jo.optString("taskMode", "");
            String desc = jo.optString("desc", "");
            boolean canDoTask = TextUtils.equals("VIEW", taskMode);
            if (!canDoTask) canDoTask = TextUtils.equals("COUNT_DOWN", taskMode);
            if (!canDoTask) canDoTask = !TextUtils.isEmpty(taskId) && TextUtils.equals("TRIGGER", taskMode) && TextUtils.equals(taskId, bizKey);
            if (TextUtils.equals("ONLINE_PAY", bizKey) || TextUtils.equals("OFFLINE_PAY", bizKey)) {
                canDoTask = false;
            } else if (title.contains("付款") || title.contains("买") || desc.contains("付款") || (desc.contains("付") && desc.contains("元"))) {
                canDoTask = false;
            } else if (bizKey.startsWith("HEART_DONAT")) {
                canDoTask = false;
            } else if (bizKey.equals("BABAFARM_TB")) {
                canDoTask = false;
            } else if ((desc.contains("捐") && desc.contains("元")) || (desc.contains("捐") && desc.contains("金额"))) {
                canDoTask = false;
            } else if (bizKey.toLowerCase().contains("xiadan") && !bizKey.equals("LSHS_xiadan_202509")) {
                canDoTask = false;
            }
            if (canDoTask) {
                jo = new JSONObject(doFarmTaskRpc(bizKey));
                if ("SUCCESS".equals(jo.optString("memo"))) {
                    int awardCount = jo.optInt("awardCount");
                    if (TextUtils.equals("HEART_DONATION_ADVANCED_FOOD_V2", bizKey)) {
                        Log.farm("KT-庄园任务♥️[" + title + "]#获得爱心美食*" + awardCount);
                    } else {
                        Log.farm("KT-庄园任务🧾[" + title + "]#获得饲料" + jo.optString("awardCount", "0") + "g");
                    }
                    return true;
                } else {
                    Log.record(jo.optString("memo"));
                    Log.i(jo.toString());
                    return false;
                }
            } else {
                Log.farm("KT-庄园任务🈲[" + title + "]，taskMode=" + taskMode + ",bizKey=" + bizKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.printStackTrace(TAG, e);
        }
        return false;
    }

    /**
     * 执行抽抽乐任务（Java RPC实现，替代native SO调用）
     * 参考：https://github.com/Fansirsqi/Sesame-TK/blob/main/app/src/main/java/fansirsqi/xposed/sesame/task/antFarm/AntFarm.java
     */
    public static Boolean doFarmDrawTimesTask(JSONObject job) {
        String title = job.optString("title");
        String bizKey = job.optString("bizKey");
        int rightsTimes = job.optInt("rightsTimes", 0);
        int rightsTimesLimit = job.optInt("rightsTimesLimit", 0);
        int times = rightsTimesLimit - rightsTimes;
        int sucCount = 0;
        try {
            for (int i = 0; i < times; i++) {
                String s = chouchouleDoFarmTask(bizKey);
                JSONObject jo = new JSONObject(s);
                if (jo.optBoolean("success", false)) {
                    sucCount++;
                    Log.farm("KT-庄园小鸡🧾️[完成:抽抽乐" + title + "]*" + sucCount);
                }
            }
            return sucCount == times;
        } catch (Exception e) {
            Log.i(TAG, "chouchouleDoFarmTask err:");
            Log.printStackTrace(TAG, e);
            return false;
        }
    }

    public static boolean isDrawTimesNotEnough(JSONObject drawMachine) {
        if (drawMachine == null) return false;
        String resultCode = drawMachine.optString("resultCode");
        String memo = drawMachine.optString("memo");
        boolean success = drawMachine.optBoolean("success");
        if (!success && TextUtils.equals("DRAW_MACHINE01", resultCode)) {
            Log.record("IP抽抽乐抽奖失败1:" + memo);
            return true;
        } else if (!success) {
            Log.record("IP抽抽乐抽奖失败2:" + memo);
        }
        return false;
    }

    // https://github.com/Fansirsqi/Sesame-TK/blob/main/app/src/main/java/fansirsqi/xposed/sesame/task/antFarm/AntFarmRpcCall.java
    private static String doFarmTaskRpc(String bizKey) {
        return ApplicationHook.requestString("com.alipay.antfarm.doFarmTask",
                "[{\"bizKey\":\"" + bizKey
                        + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                        + VERSION + "\"}]");
    }

    // https://github.com/Fansirsqi/Sesame-TK/blob/main/app/src/main/java/fansirsqi/xposed/sesame/task/antFarm/AntFarmRpcCall.java
    private static String chouchouleDoFarmTask(String bizKey) {
        return ApplicationHook.requestString("com.alipay.antfarm.doFarmTask",
                "[{\"bizKey\":\"" + bizKey + "\",\"requestType\":\"RPC\",\"sceneCode\":\"ANTFARM\",\"source\":\"chouchoule\",\"taskSceneCode\":\"ANTFARM_DRAW_TIMES_TASK\"}]");
    }
}
