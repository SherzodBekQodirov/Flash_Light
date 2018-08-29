package ru.startandroid.flashlight_new.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ru.startandroid.flashlight_new.helper.PermissionHelper;
import ru.startandroid.flashlight_new.utils.DeviceUtils;
import sherzodbek.flashlight.R;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private ImageButton imageButton;
    private Camera camera;
    private Camera.Parameters parameters;
    private Boolean isFlash = true;
    private Boolean isOn = false;
    private String status;
    private TextView textView, warning, battery_is_low;
    private int mProgressStatus = 0;
    private ProgressBar progressBar;
    private static final int PERMISSION_CAMERA = 1;
    private ImageView bat_low;
    private View viewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageButton = findViewById(R.id.image);
        textView = findViewById(R.id.textView);
        warning = findViewById(R.id.warning);
        battery_is_low = findViewById(R.id.battery_low);
        progressBar = findViewById(R.id.pb);
        bat_low = findViewById(R.id.batt_low);
        viewLayout = getLayoutInflater().inflate(R.layout.costom_toast, (ViewGroup) findViewById(R.id.layout));

        if (!PermissionHelper.isCameraEnabled(this)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        } else {
            openCamera();
            connectCameraService();
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (!DeviceUtils.hasCameraFlash(MainActivity.this)) {
                    showFlashNotFoundAlert();
                    return;
                }
                if (isFlash) {
                    if (!isOn) {
                        imageButton.setImageResource(R.drawable.off);
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameters);
                        camera.startPreview();
                        warning.setText("WARNING: Long-term use of this program may result in a malfunctioning phone battery");
                        warning.setTextColor(R.color.red);
                        isOn = true;
                        if (mProgressStatus < 20 && mProgressStatus > 10) {
                            battery_is_low.setText("BATTERY IS LOW! PLEASE TURN OFF FLASHLIGHT");
                            bat_low.setImageResource(R.drawable.batt_low);
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
                            bat_low.startAnimation(animation);
                        }
                        if (mProgressStatus < 10) {
                            warning.setText("Due to low battery level, you can not turn on the flashlight! PLEASE CHARGE BATTERY!");
                            warning.setTextColor(R.color.red);
                            imageButton.setImageResource(R.drawable.on);
                            imageButton.setEnabled(false);
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            camera.setParameters(parameters);
                            camera.stopPreview();

                            showToast();
                        }
                    } else {
                        imageButton.setImageResource(R.drawable.on);
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(parameters);
                        camera.stopPreview();
                        warning.setText("");
                        isOn = false;
                        battery_is_low.setText("");
                        bat_low.clearAnimation();
                        bat_low.setImageDrawable(null);
                    }

                } else {
                    showFlashNotFoundAlert();
                }
            }
        });
    }

    private void showToast() {
        Toast toast = Toast.makeText(getApplicationContext(), "Toast:Gravity.BUTTOM", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(viewLayout);
        toast.show();
    }

    private void showFlashNotFoundAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Error.....");
        builder.setMessage("FlashLight is not available this device");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openCamera() {
        camera = getCameraInstance();
        mContext = getApplicationContext();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver, iFilter);
        if (DeviceUtils.hasCameraFlash(this)) {
            camera = Camera.open();
            parameters = camera.getParameters();
            isFlash = true;
        }
    }

    public void broadcastMessage() {
        Intent intent = new Intent();
        intent.setAction("com.example.flashlight.LightWidgetReceiver.LIGHT_STATUS");
        intent.putExtra("Status", status);
        sendBroadcast(intent);
    }

    public void connectCameraService() {
        if (camera == null) {
            camera = Camera.open();
            parameters = camera.getParameters();
        }
    }

    public void onFlashLight() {
        if (!isFlash) {
            status = "ON";
            if (camera == null || parameters == null) {
                return;
            }
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
            isFlash = true;
            imageButton.setImageResource(R.drawable.on);
            broadcastMessage();
        }
    }

    public void offFlashLight() {
        if (isFlash) {
            status = "OFF";
            if (camera == null || parameters == null) {
                return;
            }
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.stopPreview();
            isFlash = false;
            imageButton.setImageResource(R.drawable.off);
            broadcastMessage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LOG", "onDestroy");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LOG", "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LOG", "onRestart");

    }

    @Override
    protected void onResume() {
        super.onResume();
         connectCameraService(); // camera permission conflict, first need ask permission
        if (!isFlash) {
            offFlashLight();
        }
        Log.d("LOG", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LOG", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LOG", "onStop");
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);// Display the battery level in TextView
            float percentage = level / (float) scale;
            mProgressStatus = (int) ((percentage) * 100);

            textView.setText("" + mProgressStatus + "%");
            progressBar.setProgress(mProgressStatus);
        }
    };

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                    connectCameraService();
                }
                return;
            }
        }
    }
}