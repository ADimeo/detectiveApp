package de.hpi3d.gamepgrog.trap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hpi3d.gamepgrog.trap.future.Consumer;

public class PermissionHelper {

    @SuppressLint("UseSparseArrays")
    private static Map<Integer, Consumer<Boolean>> permissionCallbacks = new HashMap<>();
    private static int lastPermissionsIndex = 0;

    public static void setPermission(Activity app, String permission, Consumer<Boolean> callback) {
        if (ContextCompat.checkSelfPermission(app, permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(app, new String[]{permission}, nextPermissionIndex());
            permissionCallbacks.put(lastPermissionsIndex, callback);
        } else {
            callback.accept(true);
        }
    }

    private static int nextPermissionIndex() {
        if (Integer.MAX_VALUE == lastPermissionsIndex)
            throw new IllegalStateException("To many permissions, integer overflow will occur");

        return ++lastPermissionsIndex;
    }

    public static void onPermission(int requestCode, int[] grantResults) {
        boolean isGranted = grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (permissionCallbacks.containsKey(requestCode)) {
            permissionCallbacks.get(requestCode).accept(isGranted);
            permissionCallbacks.remove(requestCode);
        }
    }
}
