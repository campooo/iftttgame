package org.campooo.app.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * ckb on 16/1/14.
 */
public class MyAccessibilityService extends AccessibilityService {

    private static final int UNSTART = 0;
    private static final int STEP1 = 1;
    private static final int STEP2 = 2;
    private static final int STEP3 = 3;

    private int NOW = UNSTART;

    public static int AAA = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("Accessibility", "evemt" + event.toString());
        if (event.getSource() != null) {
//            if (event.getPackageName().toString().startsWith("com.tencent.mm")) {
//                if (!event.getText().isEmpty()) {
//                    if (event.getText().get(0).toString().contains("[微信红包]")) {
            if (UNSTART == NOW) {
                findAndPerformAction("[微信红包]");

            }
//                    }
//                }
//                findAndPerformAction("[微信红包]");
//            }
            if (STEP1 == NOW) {
                findAndPerformAction("领取红包");
            }
            if (STEP2 == NOW) {
                findAndPerformAction("领取红包");
                findAndPerformAction("拆红包");
            }
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("Accessibility", " accessibility connected ");

        //设置关心的事件类型
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED |
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED |
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;//两个相同事件的超时时间间隔
        setServiceInfo(info);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void findAndPerformAction(String text) {
        // 查找当前窗口中包含“安装”文字的按钮
        if (getRootInActiveWindow() == null)
            return;
        //通过文字找到当前的节点
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < nodes.size(); i++) {
            AccessibilityNodeInfo node = nodes.get(i);
            // 执行按钮点击行为
            if (node.isEnabled()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                if (NOW == UNSTART) {
                    NOW = STEP1;
                } else if (NOW == STEP1) {
                    NOW = STEP2;
                } else if (NOW == STEP2) {
                    NOW = UNSTART;
                }


            }
        }
    }


    @Override
    public void onInterrupt() {
        Log.d("Accessibility", " accessibility interrupt ");
    }
}
