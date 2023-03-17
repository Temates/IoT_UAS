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

public class humidity extends AppCompatActivity implements MqttCallback {
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList lineEntries;
    MqttClient client = null;
    ArrayList chartdata = new ArrayList<>();

    int l = 0;
    float hum_val = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity);
        Intent i = getIntent();
        int l = new Integer(0);
        lineChart = findViewById(R.id.chart2);
        String clientId = MqttClient.generateClientId();
        try {
            client = new MqttClient("tcp://172.22.3.220:1883" , clientId, new MemoryPersistence());
            client.setCallback(this);
            client.connect();
            client.subscribe("esp/dht/humidity", 0);
            Log.d("MQTT", "connect");

        } catch (MqttException e) {
            Log.d("MQTTAndroid", "Error");
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d("MQTT", "dc");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        hum_val = Float.valueOf(payload);
        Log.d("MQTT", payload);
        getEntries();
        lineDataSet = new LineDataSet(lineEntries, "Humidity Value(%)");
        lineData  = new LineData(lineDataSet);
        Log.d("MQTT","pop_back2");
        lineChart.setData(lineData);
        lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(12f);
        lineDataSet.setLineWidth(2f);
        lineChart.invalidate();
    }

    private void getEntries() {
        lineEntries = new ArrayList<>();

        lineEntries.add(new Entry(l+1,hum_val));
        chartdata.add(new Entry(l,hum_val));
        lineEntries = chartdata;
        if (l > 10){
            chartdata.remove(0);
            Log.d("MQTT","pop_back");
        }
        l+= 1;
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public void Distance(View view) throws MqttException {
        Intent o = new Intent(this, Distance.class);
        client.disconnect();
        startActivity(o);
    }

    public void Temperature(View view) throws MqttException {
        Intent t = new Intent(this, MainActivity.class);
        client.disconnect();
        startActivity(t);
    }

    public void Mass(View view) throws MqttException {
        Intent m = new Intent(this, Mass.class);
        client.disconnect();
        startActivity(m);
    }
}