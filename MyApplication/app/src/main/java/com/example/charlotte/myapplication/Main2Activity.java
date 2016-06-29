package com.example.charlotte.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity implements SensorEventListener {

    private int numMeasures;
    private float[][] lastAccelerometerValues= new float[200][3];
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private TextView testView;
    private Button reportButton;
    public static final float MOVING_THRESHOLD=0.01f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

         testView = (TextView) findViewById(R.id.acceleration_test_text);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        reportButton = (Button)findViewById(R.id.report_button);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float average = getAccelerationAverage(lastAccelerometerValues);
                if (average>MOVING_THRESHOLD) {
                    testView.setText("MOVING "+average);
                }
                else {

                    testView.setText("NOT MOVING "+average);
                }
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        numMeasures++;
        final float alpha = 0.8f;

        float[] gravity=new float[3];
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        float[] linear_acceleration = new float[3];
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];


        if (numMeasures>=5)
        {

            numMeasures=1;
        }



        lastAccelerometerValues[numMeasures-1][0]=linear_acceleration[0];
        lastAccelerometerValues[numMeasures-1][1]=linear_acceleration[1];
        lastAccelerometerValues[numMeasures-1][2]=linear_acceleration[2];

       // Log.d("TAG","average is: "+getAccelerationAverage(lastAccelerometerValues));



    }

    public float getAccelerationAverage(float[][] measures)
    {
        float sum=0.0f;
        int notNullValues=0;
        for (float[] axxisArray: measures)
        {
           // Log.d("TAG", "magnitude: "+getMagnitude(axxisArray)+ "x: "+axxisArray[0]+" y: "+axxisArray[1]+ " z: "+axxisArray[2]);
            sum+=getMagnitude(axxisArray);
            //Log.d("TAG", "current sum= "+sum);

        }

//        Log.d("TAG", "sum= "+sum+ "measures length: "+measures.length+ "sum/measures.length: "+sum/measures.length);

        return  sum/measures.length;
    }

    public float getMagnitude(float[] axxisMeasure)
    {

        return (float) Math.sqrt(axxisMeasure[0]*axxisMeasure[0]+axxisMeasure[1]*axxisMeasure[1]+axxisMeasure[2]*axxisMeasure[2]);
    }


    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
