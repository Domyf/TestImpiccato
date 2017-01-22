/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 20/12/16 13.48
 */

package com.hdevteam.impiccato;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

public abstract class PermissionUtils {

    private static String location_permission_denied = "Accedere alla posizione è necessario per consentire all'applicazione di trovare dispositivi Bluetooth vicini\n" +
            "Se il permesso è stato rifiutato permanentemente, puoi abilitarlo da Impostazioni->App->Impiccato";
    private static String permission_rationale_location = "Accedere alla posizione è necessario per consentire all'applicazione di trovare dispositivi Bluetooth vicini";

    public static void requestPermission(AppCompatActivity activity, int requestId, String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            PermissionUtils.RationaleDialog.newInstance(requestId).show(activity.getSupportFragmentManager(), "dialog");
        } else
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestId);
    }

    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults, String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i]))
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public static class PermissionDeniedDialog extends DialogFragment {
        public static PermissionDeniedDialog newInstance() {
            Bundle arguments = new Bundle();
            PermissionDeniedDialog dialog = new PermissionDeniedDialog();
            dialog.setArguments(arguments);
            return dialog;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(location_permission_denied)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        }
    }
    public static class RationaleDialog extends DialogFragment {
        private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";

        public static RationaleDialog newInstance(int requestCode) {
            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
            RationaleDialog dialog = new RationaleDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            final int requestCode = arguments.getInt(ARGUMENT_PERMISSION_REQUEST_CODE);

            return new AlertDialog.Builder(getActivity())
                    .setMessage(PermissionUtils.permission_rationale_location)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    requestCode);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }
    }
}
