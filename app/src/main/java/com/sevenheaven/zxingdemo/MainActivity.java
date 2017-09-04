package com.sevenheaven.zxingdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ImageView.OnLongClickListener {
    ImageView qrcode1;
    ImageView qrcode2;
    ImageView qrcode3;
    ImageView qrcode4;
    ImageView qrcode5;
    ImageView qrcode6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrcode1 = (ImageView) findViewById(R.id.qrcode1);
        qrcode2 = (ImageView) findViewById(R.id.qrcode2);
        qrcode3 = (ImageView) findViewById(R.id.qrcode3);
        qrcode4 = (ImageView) findViewById(R.id.qrcode4);
        qrcode5 = (ImageView) findViewById(R.id.qrcode5);
        qrcode6 = (ImageView) findViewById(R.id.qrcode6);

        qrcode1.setImageBitmap(QRCode.createQRCode("https://github.com/jacky1234"));
        qrcode2.setImageBitmap(QRCode.createQRCodeWithLogo2("https://github.com/jacky1234", 500, drawableToBitmap(getResources().getDrawable(R.drawable.head))));
        qrcode3.setImageBitmap(QRCode.createQRCodeWithLogo3("https://github.com/jacky1234", 500, drawableToBitmap(getResources().getDrawable(R.drawable.head))));
        qrcode4.setImageBitmap(QRCode.createQRCodeWithLogo4("https://github.com/jacky1234", 500, drawableToBitmap(getResources().getDrawable(R.drawable.head))));
        qrcode5.setImageBitmap(QRCode.createQRCodeWithLogo5("https://github.com/jacky1234", 500, drawableToBitmap(getResources().getDrawable(R.drawable.head))));
        qrcode6.setImageBitmap(QRCode.createQRCodeWithLogo6("https://github.com/jacky1234", 500, drawableToBitmap(getResources().getDrawable(R.drawable.head))));

        qrcode1.setOnLongClickListener(this);
        qrcode2.setOnLongClickListener(this);
        qrcode3.setOnLongClickListener(this);
        qrcode4.setOnLongClickListener(this);
        qrcode5.setOnLongClickListener(this);
        qrcode6.setOnLongClickListener(this);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public boolean onLongClick(View v) {
        final Drawable drawable = ((ImageView) v).getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            final Bitmap bitmap = bitmapDrawable.getBitmap();
            runSaveImageWithCheck(this, bitmap);
        }

        return true;
    }

    private final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    private Bitmap currentBitmap;

    private void runSaveImageWithCheck(Context context, Bitmap bmp) {
        this.currentBitmap = bmp;
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        } else {
            saveImageToGallery(context, currentBitmap);
        }
    }

    /**
     * 完美解决实时更新图册功能。
     * link：http://blog.csdn.net/xu_fu/article/details/39158747
     *
     * @param context
     * @param bmp
     */
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "zxingGenerator");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);

            Toast.makeText(context, "保存成功！", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveImageToGallery(this, currentBitmap);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
