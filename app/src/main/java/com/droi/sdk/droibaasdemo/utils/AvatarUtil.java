package com.droi.sdk.droibaasdemo.utils;

import com.droi.sdk.droibaasdemo.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AvatarUtil {

    private static final String TAG = "AvatarUtils";
    private static final String AVATAR_DIR_NAME = "BaaSDemo";
    private static final String AVATAR_FILE_NAME = "avatar.png";
    private static final String TEMP_AVATAR_SCR_FILE_NAME = "AvatarSrcPhoto";
    private static final String TEMP_AVATAR_CROP_FILE_NAME = "AvatarCropPhoto";

    public static String getAvatarDirPath(Context context) {
        File file = context.getExternalFilesDir(null);
        if (file != null) {
            return file.getAbsolutePath();
        }

        file = context.getFilesDir();

        return file.getAbsolutePath();

    }

    public static void saveBitmap(Context context, Bitmap bitmap) {
        if (bitmap != null) {
            if (!Util.hasStoragePermission(context)) {
                return;
            }

            String urlDir = getAvatarDirPath(context);
            File file = new File(urlDir);
            if (!file.exists()) {
                file.mkdirs();
            }

            File avatar = new File(urlDir, AVATAR_FILE_NAME);
            try {
                // This method returns true if it creates a file, false if the
                // file already existed
                avatar.setWritable(true);
                if (!avatar.createNewFile()) {
                    // the file already existed
                    Log.i(TAG, "File already exists!");
                }

                FileOutputStream out = new FileOutputStream(avatar);
                out.write(createBitByteArray(bitmap));
                out.flush();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                // failed to update logo
            }
        } else {
            // invalid logo on server
        }
    }

    public static byte[] createBitByteArray(Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    // used to crop avatar
    public static Uri generateTempImageUri(Context context) {
        if (Util.hasStoragePermission(context)) {
            String avatarDir = Environment.getExternalStorageDirectory() + "/" + AVATAR_DIR_NAME;
            File dir = new File(avatarDir);
            if (dir.exists()) {
                dir.mkdir();
            }
            File file = new File(avatarDir, generateTempPhotoFileName());
            return Uri.fromFile(file);
        } else {
            Util.showMessage(context, R.string.sdcard_unavailable_hint);
            return null;
        }
    }

    public static Uri generateTempCroppedImageUri(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String avatarDir = Environment.getExternalStorageDirectory() + "/" + AVATAR_DIR_NAME;
            File dir = new File(avatarDir);
            if (!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(avatarDir, generateTempCroppedPhotoFileName());
            return Uri.fromFile(file);
        } else {
            Util.showMessage(context, R.string.sdcard_unavailable_hint);
            return null;
        }
    }

    private static String generateTempPhotoFileName() {
        return TEMP_AVATAR_SCR_FILE_NAME + ".jpg";
    }

    private static String generateTempCroppedPhotoFileName() {
        return TEMP_AVATAR_CROP_FILE_NAME + ".jpg";
    }

    public static void addCropExtras(Intent intent, int photoSize) {
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", photoSize);
        intent.putExtra("outputY", photoSize);
    }

    public static void addPhotoPickerExtras(Intent intent, Uri photoUri) {
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT,
        // photoUri));
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws Exception {
        Log.i(TAG, "getBitmapFromUri uri : " + uri);
        final InputStream imageStream = context.getContentResolver().openInputStream(uri);
        try {
            return BitmapFactory.decodeStream(imageStream);
        } finally {
            imageStream.close();
        }
    }
}
