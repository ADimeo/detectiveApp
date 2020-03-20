package de.hpi3d.gamepgrog.trap.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresPermission;

public class PhoneStealer {

    @SuppressLint("HardwareIds")
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public static String getUserPhoneNumber(Context context) {
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tel != null) {
            String number = tel.getLine1Number();
            if (number.startsWith("0")) {
                number = "+49" + number.substring(1);
            }
            return number;
        }
        return "";
    }
}
