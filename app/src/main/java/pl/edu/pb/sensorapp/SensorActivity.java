package pl.edu.pb.sensorapp;

import static pl.edu.pb.sensorapp.SensorDetailsActivity.EXTRA_SENSOR_TYPE_PARAMETER;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class SensorActivity extends AppCompatActivity {

    public static String KEY_EXTRA_SENSOR_TYPE;
    public static final String KEY_ARE_VISIBLE = "areVisible";
    public static final int SENSOR_DETAILS_ACTIVITY_REQUEST_CODE = 1;
    public static final int LOCATION_ACTIVITY_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private SensorAdapter adapter;
    private static final String SENSOR_APP_TAG = "SENSOR_APP_TAG";
    private final List<Integer> favourSensors = Arrays.asList(Sensor.TYPE_LIGHT, Sensor.TYPE_AMBIENT_TEMPERATURE);
    private boolean subtitleVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        sensorList.forEach(sensor -> {
            Log.d(SENSOR_APP_TAG, "Sensor name:" + sensor.getName());
            Log.d(SENSOR_APP_TAG, "Sensor vendor:" + sensor.getVendor());
            Log.d(SENSOR_APP_TAG, "Sensor max range:" + sensor.getMaximumRange());
        });

        if (adapter == null) {
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        if(savedInstanceState != null){
            subtitleVisible = savedInstanceState.getBoolean(KEY_ARE_VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ARE_VISIBLE, subtitleVisible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sensors_menu, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitles);
        } else {
            subtitleItem.setTitle(R.string.show_subtitles);
        }
        this.invalidateOptionsMenu();

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.show_subtitle:
                subtitleVisible = !subtitleVisible;
                int count = sensorList.size();
                String subtitle = getString(R.string.sensors_count, count);
                if(!subtitleVisible){
                    subtitle = null;
                }
                this.getSupportActionBar().setSubtitle(subtitle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder>{
        private final List<Sensor> sensors;

        public SensorAdapter(List<Sensor> items) {
            sensors=items;
        }

        @NonNull
        @Override
        public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            return new SensorHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SensorHolder sensorHolder, int i) {
            Sensor sensor = sensorList.get(i);
            sensorHolder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return sensors.size();
        }
    }

    private class SensorHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Sensor sensor;
        private TextView sensorNameTextView;
        private ImageView sensorImageView;

        public SensorHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.sensor_list_item, parent, false));
            itemView.setOnClickListener(this);

            sensorNameTextView = itemView.findViewById(R.id.sensor_name_text_view);
            sensorImageView = itemView.findViewById(R.id.sensor_image_view);

        }

        public void bind(Sensor sensor){
            this.sensor = sensor;
            sensorNameTextView.setText(sensor.getName());
            sensorImageView.setImageResource(R.drawable.sensor_icon);
            View itemContainer = itemView.findViewById(R.id.sensor_list_item);

            if(this.sensor.getType()== favourSensors.get(0) || this.sensor.getType()==favourSensors.get(1)){
                itemContainer.setBackgroundColor(getResources().getColor(R.color.green));
                itemContainer.setOnClickListener(v-> {
                    Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                    intent.putExtra(EXTRA_SENSOR_TYPE_PARAMETER, this.sensor.getType());
                    startActivityForResult(intent, SENSOR_DETAILS_ACTIVITY_REQUEST_CODE);
                });
            }else
            if(this.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                itemContainer.setBackgroundColor(getResources().getColor(R.color.orange));
                itemContainer.setOnClickListener(v-> {
                    Intent intent = new Intent(SensorActivity.this, LocationActivity.class);
                    startActivityForResult(intent, LOCATION_ACTIVITY_REQUEST_CODE);
                });
            }

        }

        @Override
        public void onClick(View view) {

        }
    }


}