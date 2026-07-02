package io.github.lazyimmortal.sesame.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.github.lazyimmortal.sesame.R;
import io.github.lazyimmortal.sesame.data.AppConfig;
import io.github.lazyimmortal.sesame.data.TokenConfig;
import io.github.lazyimmortal.sesame.hook.ext.VersionHook;
import io.github.lazyimmortal.sesame.util.ToastUtil;

public class ExtensionsActivity extends BaseActivity {

  Button btnGetWateredItems,btnGetWateringItems;
  Button btnGetTreeItems, btnGetNewTreeItems;
  Button btnQueryAreaTrees, btnGetUnlockTreeItems;
  Button btnClearDishImage, btnWriteDishImage;
  Button btnSetCustomWalkPathId, btnSetCustomWalkPathIdQueue;
  Button btnDeveloperMode, btnViewRpcDebugLogs;
  Button btnFillWateredFriendList;
  Button btnFakeVersionSlider;
  EditText etFakeVersionName, etFakeVersionCode;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.activity_extend);
    setBaseTitle(getString(R.string.extensions));
    btnGetWateredItems = findViewById(R.id.btn_get_watered_items);
    btnGetWateringItems = findViewById(R.id.btn_get_watering_items);
    btnGetTreeItems = findViewById(R.id.btn_get_tree_items);
    btnGetNewTreeItems = findViewById(R.id.btn_get_newTree_items);
    btnQueryAreaTrees = findViewById(R.id.btn_query_area_trees);
    btnGetUnlockTreeItems = findViewById(R.id.btn_get_unlock_treeItems);
    btnWriteDishImage = findViewById(R.id.btn_save_dish_image);
    btnClearDishImage = findViewById(R.id.btn_clear_dish_image);
    btnSetCustomWalkPathId = findViewById(R.id.btn_set_custom_walk_path_id_list);
    btnSetCustomWalkPathIdQueue = findViewById(R.id.btn_set_custom_walk_path_id_queue);
    btnDeveloperMode = findViewById(R.id.btn_developer_mode);
    btnViewRpcDebugLogs = findViewById(R.id.btn_view_rpc_debug_logs);
    btnFillWateredFriendList = findViewById(R.id.btn_fill_watered_friend_list);
    btnFakeVersionSlider = findViewById(R.id.btn_fake_version_slider);
    etFakeVersionName = findViewById(R.id.et_fake_version_name);
    etFakeVersionCode = findViewById(R.id.et_fake_version_code);

    // 初始化输入框的当前值（优先从 VersionHook 读取，兼容旧 AppConfig）
    String currentVersionName = VersionHook.getCachedVersionName();
    if (currentVersionName == null || currentVersionName.isEmpty()) {
      currentVersionName = AppConfig.INSTANCE.getFakeVersionName();
    }
    etFakeVersionName.setText(currentVersionName != null && !currentVersionName.isEmpty() ? currentVersionName : "10.6.58.8000");

    long currentVersionCode = VersionHook.getCachedVersionCode();
    if (currentVersionCode <= 0) {
      Long appCode = AppConfig.INSTANCE.getFakeVersionCode();
      currentVersionCode = (appCode != null && appCode > 0) ? appCode : 1881L;
    }
    etFakeVersionCode.setText(String.valueOf(currentVersionCode));

    btnGetWateredItems.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendItemsBroadcast("antForest", "getWateredItems", null);
        ToastUtil.show(ExtensionsActivity.this, "已发送查询请求，请在森林日志查看结果！");
      }
    });

    btnGetWateringItems.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendItemsBroadcast("antForest", "getWateringItems", null);
        ToastUtil.show(ExtensionsActivity.this, "已发送查询请求，请在森林日志查看结果！");
      }
    });

    btnGetTreeItems.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendItemsBroadcast("antForest", "getTreeItems", null);
        ToastUtil.show(ExtensionsActivity.this, "已发送查询请求，请在森林日志查看结果！");
      }
    });

    btnGetNewTreeItems.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendItemsBroadcast("antForest", "getNewTreeItems", null);
        ToastUtil.show(ExtensionsActivity.this, "已发送查询请求，请在森林日志查看结果！");
      }
    });

    btnQueryAreaTrees.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendItemsBroadcast("antForest", "queryAreaTrees", null);
        ToastUtil.show(ExtensionsActivity.this, "已发送查询请求，请在森林日志查看结果！");
      }
    });

    btnGetUnlockTreeItems.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendItemsBroadcast("antForest", "getUnlockTreeItems", null);
        ToastUtil.show(ExtensionsActivity.this, "已发送查询请求，请在森林日志查看结果！");
      }
    });

    btnClearDishImage.setOnClickListener(v -> {
      Context context = ExtensionsActivity.this;
      new AlertDialog.Builder(context)
          .setTitle(R.string.clear_dish_image)
          .setMessage("确认清空" + TokenConfig.getDishImageCount() + "组光盘行动图片？")
          .setPositiveButton(R.string.ok, (dialog, which) -> {
            if (TokenConfig.clearDishImage()) {
              ToastUtil.show(context, "光盘行动图片清空成功");
            } else {
              ToastUtil.show(context, "光盘行动图片清空失败");
            }
          })
          .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
          .show();
    });

    btnWriteDishImage.setOnClickListener(v -> {
      Context context = ExtensionsActivity.this;
      android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
      layout.setOrientation(android.widget.LinearLayout.VERTICAL);
      layout.setPadding(50, 20, 50, 20);

      EditText etBeforeMeals = new EditText(context);
      etBeforeMeals.setHint("请输入餐前照片ID（留空则自动生成）");
      etBeforeMeals.setMaxLines(1);
      android.widget.LinearLayout.LayoutParams params1 = new android.widget.LinearLayout.LayoutParams(
          android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
          android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
      );
      params1.bottomMargin = 20;
      etBeforeMeals.setLayoutParams(params1);

      EditText etAfterMeals = new EditText(context);
      etAfterMeals.setHint("请输入餐后照片ID（留空则自动生成）");
      etAfterMeals.setMaxLines(1);
      etAfterMeals.setLayoutParams(params1);

      layout.addView(etBeforeMeals);
      layout.addView(etAfterMeals);

      new AlertDialog.Builder(context)
          .setTitle(R.string.save_dish_image)
          .setView(layout)
          .setPositiveButton(R.string.ok, (dialog, which) -> {
            String beforeMealsId = etBeforeMeals.getText().toString().trim();
            String afterMealsId = etAfterMeals.getText().toString().trim();

            boolean success;
            if (beforeMealsId.isEmpty() || afterMealsId.isEmpty()) {
              success = TokenConfig.writeDishImageWithRandomIds();
              if (success) {
                ToastUtil.show(context, "光盘图片写入成功（随机ID）");
              } else {
                ToastUtil.show(context, "光盘图片写入失败");
              }
            } else {
              success = TokenConfig.writeDishImage(beforeMealsId, afterMealsId);
              if (success) {
                ToastUtil.show(context, "光盘图片写入成功");
              } else {
                ToastUtil.show(context, "光盘图片写入失败");
              }
            }
          })
          .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
          .show();
    });

    btnSetCustomWalkPathId.setOnClickListener(v -> {
      Context context = ExtensionsActivity.this;
      EditText input = new EditText(context);
      input.setHint(R.string.msg_input_custom_walk_path_id);

      new AlertDialog.Builder(context)
          .setTitle(R.string.set_custom_walk_path_id_list)
          .setView(input)
          .setPositiveButton(R.string.btn_add_custom_walk_path_id, (dialog, which) -> {
            String text = input.getText().toString().trim();
            sendItemsBroadcast("setCustomWalkPathIdList", "addCustomWalkPathId", text);
          }).show();
    });
    btnSetCustomWalkPathIdQueue.setOnClickListener(v -> {
      Context context = ExtensionsActivity.this;
      EditText input = new EditText(context);
      input.setHint(R.string.msg_input_custom_walk_path_id);

      new AlertDialog.Builder(context)
          .setTitle(R.string.set_custom_walk_path_id_queue)
          .setView(input)
          .setPositiveButton(R.string.btn_add_custom_walk_path_id, (dialog, which) -> {
            String text = input.getText().toString().trim();
            sendItemsBroadcast("setCustomWalkPathIdQueue", "addCustomWalkPathIdQueue", text);
          }).setNegativeButton(getString(R.string.btn_clear_custom_walk_path_id_queue), (dialog, which) -> {
            sendItemsBroadcast("setCustomWalkPathIdQueue", "clearCustomWalkPathIdQueue", null);
          }).show();
    });

    btnDeveloperMode.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        try {
          startActivity(new Intent(ExtensionsActivity.this, Class.forName("io.github.lazyimmortal.sesame.ui.AlphaActivity")));
        } catch (Exception e) {
          ToastUtil.show(ExtensionsActivity.this, "不符合开启资格！");
        }
      }
    });

    btnViewRpcDebugLogs.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        try {
          // 读取RPC抓包日志文件
          java.io.File debugLogFile = io.github.lazyimmortal.sesame.util.FileUtil.getDebugLogFile();
          if (!debugLogFile.exists()) {
            ToastUtil.show(ExtensionsActivity.this, "未找到RPC抓包日志文件");
            return;
          }

          String logContent = io.github.lazyimmortal.sesame.util.FileUtil.readFromFile(debugLogFile);
          if (logContent == null || logContent.isEmpty()) {
            ToastUtil.show(ExtensionsActivity.this, "RPC抓包日志为空");
            return;
          }

          // 使用Map去重：key=接口名+参数，value=返回数据
          java.util.Map<String, String> uniqueRecords = new java.util.LinkedHashMap<>();
          java.util.Map<String, String> methodParamsMap = new java.util.LinkedHashMap<>();

          String[] lines = logContent.split("\\n");
          String currentMethod = null;
          String currentParams = null;
          String currentData = null;
          int duplicateCount = 0;

          for (String line : lines) {
            String trimmedLine = line.trim();

            // 检测到新记录开始（支持两种格式）
            if (trimmedLine.equals("记录") || trimmedLine.equals("DEBUG: 记录")) {
              // 保存当前记录
              if (currentMethod != null && !currentMethod.isEmpty()) {
                String key = currentMethod + "|||" + (currentParams != null ? currentParams : "");

                if (uniqueRecords.containsKey(key)) {
                  // 重复记录，计数+1
                  duplicateCount++;
                } else {
                  // 新记录，保存
                  uniqueRecords.put(key, currentData != null ? currentData : "");
                  methodParamsMap.put(key, currentParams != null ? currentParams : "");
                }
              }

              // 重置
              currentMethod = null;
              currentParams = null;
              currentData = null;

            } else if (trimmedLine.startsWith("方法:")) {
              String method = trimmedLine.substring(3).trim();
              if (!method.isEmpty() && !method.equals("null")) {
                currentMethod = method;
              }

            } else if (trimmedLine.startsWith("参数:")) {
              String params = trimmedLine.substring(3).trim();
              if (!params.isEmpty() && !params.equals("null")) {
                currentParams = params;
              }

            } else if (trimmedLine.startsWith("数据:")) {
              String data = trimmedLine.substring(3).trim();
              if (!data.isEmpty() && !data.equals("null")) {
                currentData = data;
              }
            }
          }

          // 保存最后一条记录
          if (currentMethod != null && !currentMethod.isEmpty()) {
            String key = currentMethod + "|||" + (currentParams != null ? currentParams : "");
            if (!uniqueRecords.containsKey(key)) {
              uniqueRecords.put(key, currentData != null ? currentData : "");
              methodParamsMap.put(key, currentParams != null ? currentParams : "");
            } else {
              duplicateCount++;
            }
          }

          // 构建输出结果（只显示唯一的记录）
          StringBuilder resultBuilder = new StringBuilder();
          int recordIndex = 0;

          for (java.util.Map.Entry<String, String> entry : uniqueRecords.entrySet()) {
            recordIndex++;
            String key = entry.getKey();
            String[] parts = key.split("\\|\\|\\|");
            String method = parts[0];
            String params = methodParamsMap.get(key);
            String data = entry.getValue();

            resultBuilder.append("\n━━━━━━━━━━ RPC #").append(recordIndex).append(" ━━━━━━━━━━\n");
            resultBuilder.append("📡 接口: ").append(method).append("\n");

            if (params != null && !params.isEmpty()) {
              resultBuilder.append("📥 参数: ").append(params).append("\n");
            }

            if (data != null && !data.isEmpty()) {
              // 如果是querySubplotsActivity接口，简化DYNAMIC_LIST显示
              if (method.contains("querySubplotsActivity") && data.contains("DYNAMIC_LIST")) {
                try {
                  org.json.JSONObject jsonObj = new org.json.JSONObject(data);
                  if (jsonObj.has("subplotsActivityList")) {
                    org.json.JSONArray activityList = jsonObj.getJSONArray("subplotsActivityList");
                    // 简化动态列表，只保留前3条
                    for (int i = 0; i < activityList.length(); i++) {
                      org.json.JSONObject activity = activityList.getJSONObject(i);
                      if ("DYNAMIC_LIST".equals(activity.optString("activityType"))) {
                        String extend = activity.optString("extend", "[]");
                        org.json.JSONArray dynamicList = new org.json.JSONArray(extend);
                        if (dynamicList.length() > 3) {
                          // 只保留前3条记录
                          org.json.JSONArray simplified = new org.json.JSONArray();
                          for (int j = 0; j < Math.min(3, dynamicList.length()); j++) {
                            simplified.put(dynamicList.getJSONObject(j));
                          }
                          simplified.put(new org.json.JSONObject().put("note", "... 共" + dynamicList.length() + "条记录，已省略"));
                          activity.put("extend", simplified.toString());
                          activityList.put(i, activity);
                          break;
                        }
                      }
                    }
                    data = jsonObj.toString();
                  }
                } catch (Exception e) {
                  // JSON解析失败，使用原始数据
                }
              }
              resultBuilder.append("📤 返回: ").append(data).append("\n");
            }
          }

          String result = resultBuilder.toString();

          // 统计信息
          int uniqueCount = uniqueRecords.size();
          if (uniqueCount == 0) {
            ToastUtil.show(ExtensionsActivity.this, "未找到有效的RPC记录");
          } else {
            // 保存到临时文件
            java.io.File tempFile = new java.io.File(
                io.github.lazyimmortal.sesame.util.FileUtil.MAIN_DIRECTORY_FILE,
                "rpc_debug_" + System.currentTimeMillis() + ".txt"
            );
            java.io.FileWriter writer = new java.io.FileWriter(tempFile);
            writer.write(result);
            writer.close();

            // 打开查看
            String data = "file://" + tempFile.getAbsolutePath();
            Intent it = new Intent(ExtensionsActivity.this, HtmlViewerActivity.class);
            it.setData(android.net.Uri.parse(data));
            it.putExtra("canClear", true);
            startActivity(it);

            ToastUtil.show(ExtensionsActivity.this,
                "已加载 " + uniqueCount + " 条唯一RPC记录（过滤 " + duplicateCount + " 条重复）");
          }

        } catch (Exception e) {
          ToastUtil.show(ExtensionsActivity.this, "加载失败: " + e.getMessage());
        }
      }
    });

    btnFillWateredFriendList.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendItemsBroadcast("antForest", "fillWateredFriendList", null);
        ToastUtil.show(ExtensionsActivity.this, "已发送填入请求，请在森林日志查看结果！");
      }
    });

    // 初始化伪装版本过滑块按钮状态
    updateFakeVersionSliderButton();

    btnFakeVersionSlider.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // 保存输入的版本名和版本号到 VersionHook
        String versionName = etFakeVersionName.getText().toString().trim();
        String versionCodeStr = etFakeVersionCode.getText().toString().trim();
        if (!versionName.isEmpty()) {
          VersionHook.setVersionName(versionName);
          // 兼容旧 AppConfig
          AppConfig.INSTANCE.setFakeVersionName(versionName);
        }
        if (!versionCodeStr.isEmpty()) {
          try {
            long code = Long.parseLong(versionCodeStr);
            VersionHook.setVersionCode(code);
            // 兼容旧 AppConfig
            AppConfig.INSTANCE.setFakeVersionCode(code);
          } catch (NumberFormatException e) {
            ToastUtil.show(ExtensionsActivity.this, "版本号格式错误，请输入数字");
            return;
          }
        }

        boolean current = VersionHook.isVersionHookEnabled();
        VersionHook.setEnableVersionHook(!current);
        VersionHook.saveVersionConfig();
        // 兼容旧 AppConfig
        AppConfig.INSTANCE.setEnableFakeVersionSlider(!current);
        AppConfig.save();
        updateFakeVersionSliderButton();
        ToastUtil.show(ExtensionsActivity.this,
            !current ? "伪装版本过滑块：已开启（重启支付宝生效）" : "伪装版本过滑块：已关闭");
      }
    });
  }

  private void updateFakeVersionSliderButton() {
    boolean enabled = VersionHook.isVersionHookEnabled();
    if (!enabled) {
      // 兼容旧 AppConfig
      enabled = AppConfig.INSTANCE.getEnableFakeVersionSlider() != null
          && AppConfig.INSTANCE.getEnableFakeVersionSlider();
    }
    if (enabled) {
      btnFakeVersionSlider.setText(getString(R.string.fake_version_slider_on));
    } else {
      btnFakeVersionSlider.setText(getString(R.string.fake_version_slider_off));
    }
  }

  private void sendItemsBroadcast(String type, String method, String data) {
    Intent intent = new Intent("com.eg.android.AlipayGphone.sesame.rpctest");
    intent.putExtra("type", type);
    intent.putExtra("method", method);
    intent.putExtra("data", data);
    sendBroadcast(intent);
  }
}
