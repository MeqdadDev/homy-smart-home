package meqdad.darweesh.homy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.io.IOException;


public class ManageActivity extends MainActivity {

//    public static Intent i;
    public static int CONTROLHOME = 5;
    public ToggleButton rgbRed, rgbGreen, rgbBlue, rgbToggling, lamp, buzzer, door, gas, ldr, all_off, music;
    public static String redButton = "red", blueButton = "blue", toggleButton = "toggle", greenButton = "green",
            actionControl = "aaa", redOn = "r", redOff = "q",greenOn = "g", greenOff = "q", blueOn = "b", blueOff = "q",
            toggleOn = "t", toggleOff = "q", lampOn = "s", lampOff = "a", doorOpen = "d", doorClose = "c", buzzerOn="z",
            buzzerOff = "x", sing1On = "l", sing2On= "m", ldrOn = "y", ldrOff = "e" ,
            allOffOn = "j",allOffOff = "f", gasOn = "k", gasOff = "h";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        rgbRed = (ToggleButton) findViewById(R.id.redToggle);
        gas = (ToggleButton) findViewById(R.id.gasSensorToggle);
        ldr = (ToggleButton) findViewById(R.id.ldrToggle);
        rgbGreen = (ToggleButton) findViewById(R.id.greenToggle);
        rgbBlue = (ToggleButton) findViewById(R.id.blueToggle);
        rgbToggling = (ToggleButton) findViewById(R.id.RGBToggle);
        lamp = (ToggleButton) findViewById(R.id.lampToggle);
        buzzer = (ToggleButton) findViewById(R.id.buzzerToggle);
        all_off = (ToggleButton) findViewById(R.id.allOfff);
        door = (ToggleButton) findViewById(R.id.doorToggle);
        music = (ToggleButton) findViewById(R.id.song);
        gasVal = (TextView) findViewById(R.id.gasValue);
        rgbRed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                if (isChecked){
                    editor.putString(actionControl, redOn);
                    editor.apply();
                    send(redOn);
                }
                else {
                    editor.putString(actionControl, redOff);
                    editor.apply();
                    send(redOff);
                }
            }
        });

        rgbGreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                if (isChecked){
                    editor.putString(actionControl, greenOn);
                    editor.apply();
                    send(greenOn);
                }
                else {
                    editor.putString(actionControl, greenOff);
                    editor.apply();
                    send(greenOff);
                }
            }
        });
        gas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                if (isChecked){
                    editor.putString(actionControl, gasOn);
                    editor.apply();
                    send(gasOn);
                }
                else {
                    editor.putString(actionControl, gasOff);
                    editor.apply();
                    send(gasOff);
                }
            }
        });
        ldr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                if (isChecked){
                    editor.putString(actionControl, ldrOn);
                    editor.apply();
                    send(ldrOn);
                }
                else {
                    editor.putString(actionControl, ldrOff);
                    editor.apply();
                    send(ldrOff);
                }
            }
        });

        rgbBlue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                if (isChecked){
                    editor.putString(actionControl, blueOn);
                    editor.apply();
                    send(blueOn);
                }
                else {
                    editor.putString(actionControl, blueOff);
                    editor.apply();
                    send(blueOff);
                }
            }
        });

        rgbToggling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                if (isChecked){
                    editor.putString(actionControl, toggleOn);
                    editor.apply();
                    send(toggleOn);
                }
                else {
                    editor.putString(actionControl, toggleOff);
                    editor.apply();
                    send(toggleOff);
                }
            }
        });

        lamp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                if (isChecked){
                    editor.putString(actionControl, lampOn);
                    editor.apply();
                    send(lampOn);
                }
                else {
                    editor.putString(actionControl, lampOff);
                    editor.apply();
                    send(lampOff);
                }
            }
        });

        door.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                if (isChecked){
                    editor.putString(actionControl, doorOpen);
                    editor.apply();
                    send(doorOpen);
                }
                else {
                    editor.putString(actionControl, doorClose);
                    editor.apply();
                    send(doorClose);
                }
            }
        });
        music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                if (isChecked){
                    editor.putString(actionControl, sing1On);
                    editor.apply();
                    send(doorOpen);
                }
                else {
                    editor.putString(actionControl, sing2On);
                    editor.apply();
                    send(doorClose);
                }
            }
        });
        buzzer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                if (isChecked){
                    editor.putString(actionControl, buzzerOn);
                    editor.apply();
                    send(buzzerOn);
                }
                else {
                    editor.putString(actionControl, buzzerOff);
                    editor.apply();
                    send(buzzerOff);
                }
            }
        });
        all_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                if (isChecked){
                    editor.putString(actionControl, allOffOn);
                    editor.apply();
                    send(allOffOn);
                }
                else {
                    editor.putString(actionControl, allOffOff);
                    editor.apply();
                    send(allOffOff);
                }
            }
        });


    }

}
