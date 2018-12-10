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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.SendObject;
import com.example.com.mahjong_helpv1.ImageProcessing.Gray;
import com.example.com.mahjong_helpv1.ImageProcessing.Sobel;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    public static final int CROP_PHOTO = 2;
    private Button takePhoto;
    private Button deliver;
    private ImageView picture;
    private Uri imageUri;
    private Bitmap bitmap;
    public static File tempFile;
    private TextView textView;
    private EditText editText;
    public static final int REQUEST_PICKER_AND_CROP=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePhoto = (Button) findViewById(R.id.take_photo);
        deliver = (Button) findViewById(R.id.button);
        picture = (ImageView) findViewById(R.id.picture);
        takePhoto.setOnClickListener(this);
        deliver.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo:
                openCamera(this);
                break;
            case R.id.button:
                new Thread(runnable).start();

        }
    }

    Runnable runnable = (new Runnable() {
        @Override
        public void run() {
            connect();
        }
    });


    private void connect() {
        Socket socket;
        try {
            socket = new Socket("140.124.181.168", 40000);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //读取图片到ByteArrayOutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            SendObject sendObject = new SendObject(bytes, editText.getText().toString());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(sendObject);
            //傳完圖片

            out.flush();
            out.close();
            socket.close();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //load opencv
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    Mat imageMat = new Mat();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    //load opencv
    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public Intent startPhotoZoom(Bitmap bitmap) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        // 设置裁剪
        intent.putExtra("data", bitmap);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 10);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 340);
        intent.putExtra("return-data", true);
        return intent;
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
                        intent.putExtra("aspectX", 5);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("outputX", 300);
                        intent.putExtra("outputY", 100);
                        intent.putExtra("scale", true);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent.putExtra("return-data", false);
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        intent.putExtra("noFaceDetection", true); // no face detection
                        intent = Intent.createChooser(intent, "裁剪图片");
                        startActivityForResult(intent, REQUEST_PICKER_AND_CROP);
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
