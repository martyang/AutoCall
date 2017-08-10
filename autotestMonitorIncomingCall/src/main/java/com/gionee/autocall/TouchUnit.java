package com.gionee.autocall;

import android.annotation.SuppressLint;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.util.SparseArray;
import android.view.Display;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//InputManager.getInstance().injectInputEvent(ev,InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT);
// 这句代码当中因为InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT的值为1,所以代码中可能会用1代替而不是使用变量名INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT代替
public class TouchUnit {
    private static int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;

    @SuppressLint({"NewApi", "NewApi"})
    public static void clickOnScreen(float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        SparseArray<MotionEvent.PointerCoords> mPointers = new SparseArray<MotionEvent.PointerCoords>();
        MotionEvent.PointerCoords c = new MotionEvent.PointerCoords();
        c.x = x;
        c.y = y;
        c.pressure = 0;
        c.size = 0;
        mPointers.append(0, c);

        int pointerCount = mPointers.size();
        int[] pointerIds = new int[pointerCount];
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            pointerIds[i] = mPointers.keyAt(i);
            pointerCoords[i] = mPointers.valueAt(i);
        }
        int mSource = InputDevice.SOURCE_TOUCHSCREEN;


        @SuppressWarnings("deprecation")
        MotionEvent ev = MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
                pointerCount, pointerIds, pointerCoords, 0, 1, 1, 0, 1,
                mSource, 1);
        getInstance(ev);
        //InputManager.getInstance().injectInputEvent(ev, 1);


        @SuppressWarnings("deprecation")
        MotionEvent ev1 = MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
                pointerCount, pointerIds, pointerCoords, 0, 1, 1, 0, 1, mSource, 1);
        getInstance(ev1);
        //InputManager.getInstance().injectInputEvent(ev1, 1);

    }

    private static void simulateKeystroke(int KeyCode) {
        getInstance(new KeyEvent(KeyEvent.ACTION_DOWN, KeyCode));
        //InputManager.getInstance().injectInputEvent(
        //        new KeyEvent(KeyEvent.ACTION_DOWN, KeyCode), 1);
        getInstance(new KeyEvent(KeyEvent.ACTION_UP, KeyCode));
        //InputManager.getInstance().injectInputEvent(
        //        new KeyEvent(KeyEvent.ACTION_UP, KeyCode), 1);

    }

    private static void doInjectKeyEvent(MotionEvent mEvent) {
        getInstance(mEvent);
        // try {
        //     InputManager.getInstance().injectInputEvent(mEvent, 1);
//
        // } catch (Exception e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
    }

    @SuppressLint({"NewApi", "NewApi"})
    public static MotionEvent getMontionEvent(float x, float y, int action) {
        long downTime = SystemClock.uptimeMillis();
        SparseArray<MotionEvent.PointerCoords> mPointers = new SparseArray<MotionEvent.PointerCoords>();
        MotionEvent.PointerCoords c = new MotionEvent.PointerCoords();
        c.x = x;
        c.y = y;
        c.pressure = 1;
        c.size = 5;
        mPointers.append(0, c);

        int pointerCount = mPointers.size();
        int[] pointerIds = new int[pointerCount];
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            pointerIds[i] = mPointers.keyAt(i);
            pointerCoords[i] = mPointers.valueAt(i);
        }
        int mSource = InputDevice.SOURCE_TOUCHSCREEN;

        @SuppressWarnings("deprecation")
        MotionEvent ev = MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), action, pointerCount, pointerIds,
                pointerCoords, 0, 1, 1, 0, 1, mSource, 1);
        return ev;
    }

    public static void drag(float fromX, float fromY, float toX, float toY,
                            int stepCount) {
        MotionEvent event = getMontionEvent(fromX, fromY,
                MotionEvent.ACTION_DOWN);
        getInstance(event);
        //InputManager.getInstance().injectInputEvent(event, 1);

        float y = fromY;
        float x = fromX;

        float yStep = (toY - fromY) / stepCount;
        float xStep = (toX - fromX) / stepCount;
        // waitForIdleSync();
        for (int i = 0; i < stepCount; i++) {
            y += yStep;
            x += xStep;
            event = getMontionEvent(x, y, MotionEvent.ACTION_MOVE);
            getInstance(event);
            //InputManager.getInstance().injectInputEvent(event, 1);
        }

        event = getMontionEvent(toX, toY, MotionEvent.ACTION_UP);
        getInstance(event);
        //InputManager.getInstance().injectInputEvent(event, 1);

    }

    public static int[] getScreenXY(Window window) {
        int[] location = new int[2];

        //	Activity nActivity = (Activity) mContext;

        Display mDisplay = window.getWindowManager().getDefaultDisplay();

        int screenX = mDisplay.getWidth();

        int screenY = mDisplay.getHeight();
        location[0] = screenX;
        location[1] = screenY;

        return location;
    }

    public static InputManager getInstance(InputEvent event) {
        InputManager instance = null;
        try {
            Class clazz = Class.forName("android.hardware.input.InputManager");
            Method getInstance = clazz.getDeclaredMethod("getInstance");
            instance = (InputManager) getInstance.invoke(clazz);
            Method injectInputEvent = clazz.getDeclaredMethod("injectInputEvent", InputEvent.class, int.class);
            injectInputEvent.invoke(instance, event,INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return instance;
    }

}
