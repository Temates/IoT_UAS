package com.example.garbagedetector_uas_phenando;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MqttCallback {
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList lineEntries;
    ArrayList chartdata = new ArrayList<>();

    MqttClient client = null;
    int j = 1;
    float tmp_val = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String clientId = MqttClient.generateClientId();
        lineChart = findViewById(R.id.chart1);

        Log.d("MQTT", "set 1");


            try {
                client = new MqttClient("tcp://172.22.3.220:1883", clientId, new MemoryPersistence());
                client.setCallback(this);
                client.connect();
                client.subscribe("esp/dht/temperature", 2);
                Log.d("MQTT", "subs");
                Thread.sleep(5000);


            } catch (MqttException | InterruptedException e) {
                Log.d("MQTT", "Error");
                e.printStackTrace();
            }


    }



    public void Humidity(View view) throws MqttException {
        Intent i = new Intent(this, humidity.class);
        client.disconnect();
        startActivity(i);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d("MQTT", "dc");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d("MQTT", payload);
        tmp_val = Float.valueOf(payload);

        getEntries();
        lineDataSet = new LineDataSet(lineEntries, "Temperature Value(Celcius)");
        lineData  = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(12f);
        lineDataSet.setLineWidth(2f);
        lineChart.invalidate();

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
    private void getEntries() {
        lineEntries = new ArrayList<>();

        lineEntries.add(new Entry(j+1,tmp_val));
        chartdata.add(new Entry(j,tmp_val));
        lineEntries = chartdata;
        Log.d("MQTT", String.valueOf(j));
        if (j > 10){
                chartdata.remove(0);
                Log.d("MQTT", "pop_back");

            }
        j+= 1;


    }

    public void Distance(View view) throws MqttException {
        Intent o = new Intent(this, Distance.class);
        client.disconnect();
        startActivity(o);
    }

    public void Mass(View view) throws MqttException {
        Intent m = new Intent(this, Mass.class);
        client.disconnect();
        startActivity(m);
    }
}