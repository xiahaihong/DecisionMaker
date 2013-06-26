package com.example.decision.activities;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.decision.R;
import com.example.decision.modules.OptionsCard;
import com.example.decision.utils.Constants;

import java.util.Random;

/**
 * Created by haihong.xiahh on 13-6-25.
 */
public class CardActivity extends Activity implements SensorEventListener {
    TextView mTitleView;
    TextView mContentView;
    ListView mItemView;
    OptionsCard mCard;
    SensorManager mSensorManager;
    Vibrator mVibrator;
    private static final int FORCE_THRESHOLD = 350;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 2;
    long mLastShake;
    long mLastTime;
    long mLastForce;
    int mShakeCount = 0;
    private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_card_layout);
        initMessage();
        setView();
        initSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    private void initSensor(){
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void setView() {
        mTitleView = (TextView) this.findViewById(R.id.card_title);
        mContentView = (TextView) this.findViewById(R.id.card_content);
        mItemView = (ListView) this.findViewById(R.id.item_listview);
        mTitleView.setText(mCard.getmTitle());
        mContentView.setText(mCard.getmContent());
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                    CardActivity.this, android.R.layout.simple_list_item_single_choice, mCard.getmItemList());
        mItemView.setAdapter(stringArrayAdapter);
    }

    private void initMessage(){
        Intent intent = this.getIntent();
        mCard = (OptionsCard) intent.getSerializableExtra(Constants.INTENT_MSG);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        float[] values = sensorEvent.values;
        long now = System.currentTimeMillis();

        if ((now - mLastForce) > SHAKE_TIMEOUT) {
            mShakeCount = 0;
        }

/*        if ((now - mLastTime) > TIME_THRESHOLD) {
            if (sensorType == Sensor.TYPE_ACCELEROMETER){
                if (Math.abs(values[0]) > 14 ||
                        Math.abs(values[1]) > 14 ||
                        Math.abs(values[2]) > 14 ){
                    if ((++mShakeCount > SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)){
                        mLastShake = now;
                        mShakeCount = 0;
                        makeDecision();
                        mVibrator.vibrate(500);
                    }
                }
                mLastForce = now;
            }
            mLastTime = now;
        }*/
        if ((now - mLastTime) > TIME_THRESHOLD) {
            long diff = now - mLastTime;
            float speed = Math.abs(values[SensorManager.DATA_X] + values[SensorManager.DATA_Y] + values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 10000;
            if (speed > FORCE_THRESHOLD) {
                if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    mShakeCount = 0;
                    makeDecision();
                    mVibrator.vibrate(500);
                }
                mLastForce = now;
            }
            mLastTime = now;
            mLastX = values[SensorManager.DATA_X];
            mLastY = values[SensorManager.DATA_Y];
            mLastZ = values[SensorManager.DATA_Z];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void makeDecision(){
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int position = random.nextInt(mCard.getmItemList().size());
        mItemView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mItemView.setItemChecked(position, true);
        Toast.makeText(this, "shake : " + position, Toast.LENGTH_SHORT).show();
    }
}
