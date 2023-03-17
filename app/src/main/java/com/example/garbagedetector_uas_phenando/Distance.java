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

public class Distance extends AppCompatActivity implements MqttCallback {
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList lineEntries;
    MqttClient client = null;
    ArrayList chartdata = new ArrayList<>();
    int k = 0;
    float dis_val = 0;
    String can_cap = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);
        Intent o = getIntent();
        int k = new Integer(0);
        lineChart = findViewById(R.id.chart3);
        String clientId = MqttClient.generateClientId();
        try {
            client = new MqttClient("tcp://172.22.3.220:1883" , clientId, new MemoryPersistence());
            client.setCallback(this);
            client.connect();
            client.subscribe("esp/hcsr04/distance", 2);
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
        dis_val = Float.valueOf(payload);
        Log.d("MQTT", payload);
        getEntries();
        lineDataSet = new LineDataSet(lineEntries, "Distance Value(cm)");
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

        lineEntries.add(new Entry(k+1,dis_val));
        chartdata.add(new Entry(k,dis_val));
        lineEntries = chartdata;
        if (k > 10){
            chartdata.remove(0);
            Log.d("MQTT","pop_back");
        }
        k+= 1;
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
    public void Temperature(View view) throws MqttException {
        Intent t = new Intent(this, MainActivity.class);
        client.disconnect();
        startActivity(t);
    }

    public void Humidity(View view) throws MqttException {
        Intent i = new Intent(this, humidity.class);
        client.disconnect();
        startActivity(i);
    }

    public void Mass(View view) throws MqttException {
        Intent m = new Intent(this, Mass.class);
        client.disconnect();
        startActivity(m);
    }

    public void Alarm(View view) {

        publishMessage(can_cap);
    }

    private void publishMessage(String can_cap) {
        if (client.isConnected() == false) {
            try {
                client.connect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        MqttMessage m = new MqttMessage();
        m.setPayload(can_cap.getBytes());
        m.setQos(2);
        m.setRetained(false);
        try{
            Log.d("MQTTAndroid","Publish");
            client.publish( "esp/alarm", m);
        }   catch (MqttException e) {
            Log.d("MQTT","Error");
            e.printStackTrace();
        }
    }
}