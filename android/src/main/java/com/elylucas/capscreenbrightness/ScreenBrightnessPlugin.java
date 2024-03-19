package com.elylucas.capscreenbrightness;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ScreenBrightness")
public class ScreenBrightnessPlugin extends Plugin {

    @PluginMethod
    public void setBrightness(PluginCall call) {
        try {
            Float brightness = call.getFloat("brightness");

            if (brightness == null) {
                brightness = getDefaultBrightness();
            }

            Activity activity = getActivity();
            WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();

            Float finalBrightness = brightness;
            activity.runOnUiThread(
                () -> {
                    layoutParams.screenBrightness = finalBrightness;
                    activity.getWindow().setAttributes(layoutParams);
                    call.resolve();
                }
            );
        } catch (Exception e) {
            Log.d("ExceptionBrightness", e.getMessage());
            call.resolve();
        }
    }

    @PluginMethod
    public void getBrightness(PluginCall call) {
        WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
        JSObject ret = new JSObject();
        call.resolve(
            new JSObject() {
                {
                    put("brightness", layoutParams.screenBrightness);
                }
            }
        );
    }

    private Float getDefaultBrightness() {
        try {
            int systemBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            return systemBrightness / 255.0f;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0.5f;
        }
    }
}
