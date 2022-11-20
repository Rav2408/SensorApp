package pl.edu.pb.sensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {

    public static final String EXTRA_SENSOR_TYPE_PARAMETER = "EXTRA_SENSOR_TYPE";
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView sensorTextView;
    private TextView sensorValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_details_activity);

        sensorTextView = findViewById(R.id.sensor_details_name);
        sensorValueTextView = findViewById(R.id.sensor_details_value);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor( getIntent().getIntExtra(EXTRA_SENSOR_TYPE_PARAMETER, Sensor.TYPE_LIGHT));

        if (sensor == null)
        {
            sensorTextView.setText(R.string.missing_sensor);
            sensorValueTextView.setText("");
        }
        else{
            sensorTextView.setText(sensor.getName());
            sensorValueTextView.setText("");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        float currentValue = sensorEvent.values[0];
        switch(type){
            case Sensor.TYPE_LIGHT:
                sensorValueTextView.setText(String.valueOf(currentValue));
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sensorValueTextView.setText(Float.toString(currentValue));
                break;
            default:
                sensorValueTextView.setText(String.valueOf(currentValue));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
