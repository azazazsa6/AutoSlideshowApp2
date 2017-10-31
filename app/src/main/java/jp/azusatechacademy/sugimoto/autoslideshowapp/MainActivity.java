package jp.azusatechacademy.sugimoto.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;
    Handler mHandler = new Handler();
    ImageView mImageView;
    Cursor mCursor;

    Button mNextButton;
    Button mBackButton;
    Button mStartorStopButton;

    private void getCursor() {
        ContentResolver resolver = getContentResolver();
        mCursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );
        mCursor.moveToFirst();
        setImg();
    }

    private void setImg() {
        int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = mCursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView1.setImageURI(imageUri);
    }

    private void showImgNext() {
        if (mCursor != null) {
            boolean next = mCursor.moveToNext();
            if (next) {
                setImg();
            } else {
                mCursor.moveToFirst();
                setImg();
            }
        }
    }

    private void showImgBack() {
        if (mCursor != null) {
            boolean next = mCursor.moveToPrevious();
            if (next) {
                setImg();
            } else {
                mCursor.moveToLast();
                setImg();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getCursor();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getCursor();
        }

        mNextButton = (Button) findViewById(R.id.button1);
        mBackButton = (Button) findViewById(R.id.button2);
        mStartorStopButton = (Button) findViewById(R.id.button3);

        mImageView = (ImageView) findViewById(R.id.imageView1);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImgNext();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImgBack();
            }
        });

        mStartorStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimer == null) {
                    mStartorStopButton.setText("停止");
                    mNextButton.setEnabled(false);
                    mBackButton.setEnabled(false);
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showImgNext();
                                }
                            });
                        }
                    }, 2000, 2000);
                } else {
                    mStartorStopButton.setText("再生");
                    mNextButton.setEnabled(true);
                    mBackButton.setEnabled(true);
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCursor();
                } else {
                    Toast.makeText(this, "パーミッションが拒否されたので，アプリを終了します。", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }
}