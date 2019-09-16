package com.example.richard.bluetoothrpi;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class USBCActivity extends AppCompatActivity {

    private static final String TAG = "USBACT";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbc);
        Toast.makeText(getApplicationContext(), "OnCreate",Toast.LENGTH_LONG).show();
        UsbManager usbManager = getSystemService(UsbManager.class);
        Map<String, UsbDevice> connectedDevices = usbManager.getDeviceList();
        for (UsbDevice device : connectedDevices.values()) {
            Toast.makeText(getApplicationContext(), "Device: "+ device.getDeviceName(),Toast.LENGTH_LONG).show();

            if (device.getVendorId() == 0x2341 && device.getProductId() == 0x0001) {
                Log.i(TAG, "Device found: " + device.getDeviceName());
                startSerialConnection(usbManager, device);
                break;
            }
        }

    }

    void startSerialConnection(UsbManager usbManager, UsbDevice device) {
        UsbDeviceConnection connection = usbManager.openDevice(device);
        UsbSerialDevice serial = UsbSerialDevice.createUsbSerialDevice(device, connection);

        if (serial != null && serial.open()) {
            serial.setBaudRate(115200);
            serial.setDataBits(UsbSerialInterface.DATA_BITS_8);
            serial.setStopBits(UsbSerialInterface.STOP_BITS_1);
            serial.setParity(UsbSerialInterface.PARITY_NONE);
            serial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            serial.read(mCallback);
        }
    }

    UsbSerialInterface.UsbReadCallback mCallback = (data) -> {
        String dataStr = null;

        try {
            dataStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Data received: " + dataStr);
    };
}
