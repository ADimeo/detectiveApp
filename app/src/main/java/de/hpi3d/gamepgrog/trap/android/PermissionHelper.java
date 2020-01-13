package de.hpi3d.gamepgrog.trap.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

import de.hpi3d.gamepgrog.trap.future.ArrayExt;
import de.hpi3d.gamepgrog.trap.future.Consumer;
import de.hpi3d.gamepgrog.trap.future.Promise;

public class PermissionHelper {

    @SuppressLint("UseSparseArrays")
    private static Map<Integer, Consumer<Boolean>> permissionCallbacks = new HashMap<>();
    private static int lastPermissionsIndex = 0;

    public static Promise<Boolean> setPermissions(Activity app, String[] permissions) {
        Promise<Boolean> p = Promise.create();

        if (!hasPermissions(app, permissions)) {
            ActivityCompat.requestPermissions(app, permissions, nextPermissionIndex());
            permissionCallbacks.put(lastPermissionsIndex, p::resolve);
        } else {
            p.resolve(true);
        }

        return p;
    }

    private static int nextPermissionIndex() {
        if (Integer.MAX_VALUE == lastPermissionsIndex)
            throw new IllegalStateException("To many permissions, integer overflow will occur");

        return ++lastPermissionsIndex;
    }

    public static void onPermission(int requestCode, int[] grantResults) {
        boolean isGranted = ArrayExt.allMatch(ArrayExt.toIntList(grantResults), PermissionHelper::isGranted);

        if (permissionCallbacks.containsKey(requestCode)) {
            permissionCallbacks.get(requestCode).accept(isGranted);
            permissionCallbacks.remove(requestCode);
        }
    }

    public static boolean hasPermissions(Context c, String[] permissions) {
        return ArrayExt.allMatch(permissions, (permission) -> hasPermission(c, permission));
    }

    public static boolean hasPermission(Context c, String permission) {
        return isGranted(ContextCompat.checkSelfPermission(c, permission));
    }

    public static boolean isGranted(int grantResult) {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }
}
