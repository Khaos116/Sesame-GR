package io.github.lazyimmortal.sesame.hook.ext;

import android.content.Context;
import android.content.pm.PackageInfo;

import org.json.JSONObject;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import io.github.lazyimmortal.sesame.entity.AlipayVersion;
import io.github.lazyimmortal.sesame.util.*;

/**
 * 版本伪装Hook - 拦截 PackageManager.getPackageInfo() 实现版本伪装
 *
 * 设计思路：
 * 1. 使用独立的配置文件 version_config.json（位于 sesame 主目录）
 * 2. 通过 ensureVersionConfig() 初始化默认配置
 * 3. 通过 loadVersionConfig() 加载用户配置
 * 4. 通过 initVersionHook() 注册 Hook
 * 5. 提供与 ApplicationHook 中现有版本伪装逻辑兼容的接口
 */
public class VersionHook {

    private static String  sCachedVersionName = "";
    private static long    sCachedVersionCode = 0;
    private static boolean sEnableVersionHook = false;
    private static boolean isVersionHookRegistered = false;

    private static AlipayVersion sAlipayVersion;

    // ==================== 配置初始化 ====================

    /**
     * 确保版本配置文件存在（创建默认配置）
     */
    public static void ensureVersionConfig(Context context) {
        try {
            File configDir = FileUtil.MAIN_DIRECTORY_FILE;
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            File configFile = new File(configDir, "version_config.json");
            if (!configFile.exists()) {
                JSONObject defaultConfig = new JSONObject();
                defaultConfig.put("enableVersionHook", false);
                defaultConfig.put("versionName", "");
                defaultConfig.put("versionCode", 0);
                FileUtil.write2File(defaultConfig.toString(4), configFile);
                Log.record("✨ 已创建版本配置文件");
            }
        } catch (Throwable t) {
            Log.record("❌ 创建版本配置失败: " + t.getMessage());
        }
    }

    /**
     * 加载版本配置
     */
    public static void loadVersionConfig() {
        try {
            File configFile = new File(FileUtil.MAIN_DIRECTORY_FILE, "version_config.json");
            if (!configFile.exists()) {
                return;
            }
            String content = FileUtil.readFromFile(configFile);
            if (content == null || content.isEmpty()) {
                return;
            }
            JSONObject config = new JSONObject(content);

            sEnableVersionHook = config.optBoolean("enableVersionHook", false);
            sCachedVersionName  = config.optString("versionName", "");
            sCachedVersionCode  = config.optLong("versionCode", 0);

            Log.i("VersionHook", "配置加载完成: enabled=" + sEnableVersionHook
                    + ", name=" + sCachedVersionName + ", code=" + sCachedVersionCode);
        } catch (Throwable t) {
            Log.i("VersionHook", "加载配置失败: " + t.getMessage());
        }
    }

    /**
     * 保存版本配置
     */
    public static void saveVersionConfig() {
        try {
            File configFile = new File(FileUtil.MAIN_DIRECTORY_FILE, "version_config.json");
            JSONObject config = new JSONObject();
            config.put("enableVersionHook", sEnableVersionHook);
            config.put("versionName", sCachedVersionName);
            config.put("versionCode", sCachedVersionCode);
            FileUtil.write2File(config.toString(4), configFile);
        } catch (Throwable t) {
            Log.record("❌ 保存版本配置失败: " + t.getMessage());
        }
    }

    // ========== Getter/Setter ==========

    public static void setAlipayVersion(AlipayVersion version) {
        sAlipayVersion = version;
    }

    public static String getCachedVersionName() {
        return sCachedVersionName;
    }

    public static long getCachedVersionCode() {
        return sCachedVersionCode;
    }

    public static boolean isVersionHookEnabled() {
        return sEnableVersionHook;
    }

    public static void setEnableVersionHook(boolean enable) {
        sEnableVersionHook = enable;
    }

    public static void setVersionName(String name) {
        sCachedVersionName = (name != null) ? name : "";
    }

    public static void setVersionCode(long code) {
        sCachedVersionCode = code;
    }

    /**
     * 获取伪装后的版本名（若未启用则返回空字符串）
     */
    public static String getFakeVersionName() {
        if (!sEnableVersionHook) {
            return "";
        }
        return (sCachedVersionName != null && !sCachedVersionName.isEmpty())
                ? sCachedVersionName : "10.6.58.8000";
    }

    /**
     * 获取伪装后的版本号（若未启用则返回 0）
     */
    public static long getFakeVersionCode() {
        if (!sEnableVersionHook) {
            return 0;
        }
        return sCachedVersionCode > 0 ? sCachedVersionCode : 1881L;
    }

    // ==================== 版本伪装 Hook ====================

    /**
     * 注册 PackageManager.getPackageInfo() Hook
     * 拦截所有对支付宝包名的版本查询，返回伪装版本
     */
    public static void initVersionHook() {
        if (isVersionHookRegistered) {
            Log.i("VersionHook", "Hook 已注册，跳过重复注册");
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(
                "android.app.ApplicationPackageManager",
                null,
                "getPackageInfo",
                String.class,
                int.class,
                new XC_MethodHook() {
                    private boolean logged = false;

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        if (!sEnableVersionHook) {
                            return;
                        }

                        Object info = param.getResult();
                        if (info == null) {
                            return;
                        }

                        String pkg = (String) XposedHelpers.getObjectField(info, "packageName");
                        if (!ClassUtil.PACKAGE_NAME.equals(pkg)) {
                            return;
                        }

                        // 替换版本名
                        if (sCachedVersionName != null && !sCachedVersionName.isEmpty()) {
                            XposedHelpers.setObjectField(info, "versionName", sCachedVersionName);
                        }

                        // 替换版本号
                        if (sCachedVersionCode > 0) {
                            try {
                                PackageInfo.class.getMethod("setLongVersionCode", long.class)
                                        .invoke(info, sCachedVersionCode);
                            } catch (Throwable ignored) {
                                XposedHelpers.setIntField(info, "versionCode", (int) sCachedVersionCode);
                            }
                        }

                        // 更新返回结果
                        param.setResult(info);

                        if (!logged) {
                            Log.record("✨ 版本伪装已生效: " + sCachedVersionName
                                    + " (code=" + sCachedVersionCode + ")");
                            logged = true;
                        }
                    }
                }
            );

            isVersionHookRegistered = true;
            Log.i("VersionHook", "✅ getPackageInfo Hook 注册成功");
        } catch (Throwable t) {
            Log.i("VersionHook", "❌ 版本Hook注册失败: " + t.getMessage());
            Log.printStackTrace("VersionHook", t);
        }
    }

    /**
     * 获取用于展示的版本号（伪装开启时返回伪装版本，否则返回真实版本）
     */
    public static String getDisplayVersion() {
        if (sEnableVersionHook && sCachedVersionName != null && !sCachedVersionName.isEmpty()) {
            return sCachedVersionName;
        }
        if (sAlipayVersion != null) {
            return sAlipayVersion.getVersionString();
        }
        return "";
    }
}
