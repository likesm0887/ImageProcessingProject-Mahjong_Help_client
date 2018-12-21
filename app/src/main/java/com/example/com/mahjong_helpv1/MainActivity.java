package com.example.com.mahjong_helpv1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.SendObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    public static final int CROP_PHOTO = 2;
    private Button takePhoto;
    private Button deliver;
    private Button button;
    private ImageView picture;
    private Uri imageUri;
    private Bitmap bitmap;
    public static File tempFile;
    private TextView textView;
    private EditText editText;
    public static final int REQUEST_PICKER_AND_CROP=5;
    private TextView pleaseTakePicture;
    private String num;
    private ArrayList<String> listen = new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePhoto = (Button) findViewById(R.id.take_photo);
        deliver = (Button) findViewById(R.id.deliver);
        pleaseTakePicture = (TextView)findViewById(R.id.pleaseTakePicture);
        picture = (ImageView) findViewById(R.id.picture);
        takePhoto.setOnClickListener(this);
        deliver.setOnClickListener(this);


        Intent reIntentObj = getIntent(); /* 取得傳入的 Intent 物件 */
        Bundle bundle = reIntentObj.getExtras();

        if (bundle != null && bundle.containsKey("num") ){
            num = bundle.getString("num");

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo:
                openCamera(this);
                break;
            case R.id.deliver:
                deliver.setEnabled(false);
                Thread a =  new Thread(runnable);
                a.start();
                try {
                    a.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,OuptutActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("listen", listen);
                intent.putExtras(bundle);   // 記得put進去，不然資料不會帶過去哦
                startActivity(intent);

        }
    }

    Runnable runnable = (new Runnable() {
        @Override
        public void run() {
            try {
                connect();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    });


    private void connect() throws ClassNotFoundException {
        Socket socket;
        try {
            socket = new Socket("140.124.181.168", 40000);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //读取图片到ByteArrayOutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            SendObject sendObject = new SendObject(bytes, num);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(sendObject);
            //傳完圖片
            out.flush();

            ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream());
            SendObject data2=(SendObject) in.readObject();

            in.close();
            socket.close();
            listen =data2.listenCard;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(this.getResources(), photo);
            picture.setImageDrawable(drawable);
        }

    }
        //activity判斷 並且將圖片輸出
        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data)
        {
            switch (requestCode) {
                case PHOTO_REQUEST_CAREMA:
                    if (resultCode == RESULT_OK) {

                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(imageUri, "image/*");
                        intent.putExtra("scale", true);
                        Uri uri = imageUri;
                        intent.setDataAndType(uri, "image/*");
                        //intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 7);
                        intent.putExtra("aspectY", 0.9);
                        intent.putExtra("outputX", 437* Integer.parseInt(num));
                        intent.putExtra("outputY", 1000);
                        intent.putExtra("scale", true);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent.putExtra("return-data", false);
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        intent.putExtra("noFaceDetection", true); // no face detection
                        intent = Intent.createChooser(intent, "裁剪图片");
                        startActivityForResult(intent, REQUEST_PICKER_AND_CROP);
                        pleaseTakePicture.setText("");
                        //bitmap=Gray.getGrayImage(bitmap);
                        //bitmap=Sobel.doSobel(bitmap);
                    }
                    break;
                case REQUEST_PICKER_AND_CROP:
                        try {
                            bitmap = BitmapFactory.decodeStream(getContentResolver()
                                    .openInputStream(imageUri));
                            picture.setImageBitmap(bitmap);
                           // doCropPhoto(bitmap);
                            //Bitmap photo1 = data.getParcelableExtra("data");
                            //picture.setImageBitmap(photo1);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    break;

            }
        }

        //開啟相機
        public void openCamera (Activity activity)
        {
            //獲取系統版本
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            // 激活相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 判断存储卡是否可以用，可用进行存储
                SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                        "yyyy_MM_dd_HH_mm_ss");
                String filename = timeStampFormat.format(new Date());
                tempFile = new File(Environment.getExternalStorageDirectory(),
                        filename + ".jpg");
                if (currentapiVersion < 24) {
                    // 从文件中创建uri
                    imageUri = Uri.fromFile(tempFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                } else {
                    //兼容android7.0 使用共享文件的形式
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                    //检查是否有存储权限，以免崩溃
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        //申请WRITE_EXTERNAL_STORAGE权限
                        Toast.makeText(this, "请开启存储权限", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                }
            activity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
        }




}
