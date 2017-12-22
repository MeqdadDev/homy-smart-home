package meqdad.darweesh.homy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static meqdad.darweesh.homy.ManageActivity.CONTROLHOME;
import static meqdad.darweesh.homy.ManageActivity.actionControl;
import static meqdad.darweesh.homy.ManageActivity.allOffOff;
import static meqdad.darweesh.homy.ManageActivity.allOffOn;
import static meqdad.darweesh.homy.ManageActivity.blueButton;
import static meqdad.darweesh.homy.ManageActivity.buzzerOff;
import static meqdad.darweesh.homy.ManageActivity.buzzerOn;
import static meqdad.darweesh.homy.ManageActivity.gasOff;
import static meqdad.darweesh.homy.ManageActivity.gasOn;
import static meqdad.darweesh.homy.ManageActivity.greenButton;
import static meqdad.darweesh.homy.ManageActivity.greenOff;
import static meqdad.darweesh.homy.ManageActivity.greenOn;
//import static meqdad.darweesh.homy.ManageActivity.i;
import static meqdad.darweesh.homy.ManageActivity.lampOff;
import static meqdad.darweesh.homy.ManageActivity.lampOn;
import static meqdad.darweesh.homy.ManageActivity.ldrOff;
import static meqdad.darweesh.homy.ManageActivity.ldrOn;
import static meqdad.darweesh.homy.ManageActivity.redButton;
import static meqdad.darweesh.homy.ManageActivity.redOff;
import static meqdad.darweesh.homy.ManageActivity.redOn;
import static meqdad.darweesh.homy.ManageActivity.sing1On;
import static meqdad.darweesh.homy.ManageActivity.sing2On;
import static meqdad.darweesh.homy.ManageActivity.toggleButton;
import static meqdad.darweesh.homy.ManageActivity.toggleOff;
import static meqdad.darweesh.homy.ManageActivity.toggleOn;

public class MainActivity extends AppCompatActivity {

    // GUI Components
    private TextView mBluetoothStatus;
    public static TextView gasVal;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    public static BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private ImageButton next;
    public static final String TAG = MainActivity.class.getSimpleName();
    public static Handler mHandler; // Our main handler that will receive callback notifications
    public static ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    public static BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothStatus = (TextView)findViewById(R.id.bluetoothStatus);
        mScanBtn = (Button)findViewById(R.id.scan);
        mOffBtn = (Button)findViewById(R.id.off);
        mDiscoverBtn = (Button)findViewById(R.id.discover);
        mListPairedDevicesBtn = (Button)findViewById(R.id.PairedBtn);
        next = (ImageButton) findViewById(R.id.nxtBtn);
        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = "..";
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (readMessage.charAt(0) == 'g'){

                        gasVal.setText(readMessage/*.substring(1,3)*/);
                    } //else (if (readMessage.charAt(0) == 'u'))

                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {

            next.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(mConnectedThread != null) //First check to make sure thread created
                        //mConnectedThread.write("b");
                        next(v);
                }
            });


            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(v);
                }
            });
        }
    }

    public void next(View view) {

        Intent intent = new Intent(this, ManageActivity.class);
        startActivityForResult(intent, CONTROLHOME);
    }

    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }


    public void send(String action){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (action.equals(greenOn)){
            action = prefs.getString(actionControl,greenOn);
            mConnectedThread.write(action);
        }else if (action.equals(greenOff)) {
            action = prefs.getString(actionControl, greenOff);
            mConnectedThread.write(action);
        }else if (action.equals(toggleOff)){
            action = prefs.getString(actionControl,toggleOff);
            mConnectedThread.write(action);
        }else if (action.equals(redOff)){
            action = prefs.getString(actionControl,redOff);
            mConnectedThread.write(action);
        }else if (action.equals(redOn)){
            action = prefs.getString(actionControl,redOn);
            mConnectedThread.write(action);
        }else if (action.equals(toggleOn)){
            action = prefs.getString(actionControl,toggleOn);
            mConnectedThread.write(action);
        } else if (action.equals(lampOn)){
            action = prefs.getString(actionControl, lampOn);
            mConnectedThread.write(action);
        } else if (action.equals(action)){
            action = prefs.getString(actionControl,lampOff);
            mConnectedThread.write(action);
        } else if (action.equals(action)) {
            action = prefs.getString(actionControl, buzzerOn);
            mConnectedThread.write(action);
        } else if (action.equals(action)) {
            action = prefs.getString(actionControl, buzzerOff);
            mConnectedThread.write(action);
        } else if (action.equals(action)) {
            action = prefs.getString(actionControl, ldrOn);
            mConnectedThread.write(action);
        } else if (action.equals(action)) {
            action = prefs.getString(actionControl, ldrOff);
            mConnectedThread.write(action);
        } else if (action.equals(action)) {
            action = prefs.getString(actionControl, gasOn);
            mConnectedThread.write(action);
        } else if (action.equals(action)) {
            action = prefs.getString(actionControl, gasOff);
            mConnectedThread.write(action);
        } else if (action.equals(action)) {
            action = prefs.getString(actionControl, allOffOn);
            mConnectedThread.write(action);
        } else if (action.equals(action)) {
            action = prefs.getString(actionControl, allOffOff);
            mConnectedThread.write(action);
        }else if (action.equals(action)) {
            action = prefs.getString(actionControl, sing1On);
            mConnectedThread.write(action);
        }else if (action.equals(action)) {
            action = prefs.getString(actionControl, sing2On);
            mConnectedThread.write(action);
        }

    }
    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){

        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Enabled");
            }
            else
                mBluetoothStatus.setText("Disabled");
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Bluetooth disabled");
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    private void discover(View view){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(intentAction)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(View view){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private static BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(50); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                Toast.makeText(getApplicationContext(),"Sending Data to Arduino..", Toast.LENGTH_SHORT).show();
                mmOutStream.write(bytes);
                Toast.makeText(getApplicationContext(),"Data sent.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}