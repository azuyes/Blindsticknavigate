package com.example.blindsticknavigate;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanSettings;

import java.util.UUID;

import io.reactivex.disposables.Disposable;

public class BleLowEnergy {
    final RxBleClient rxBleClient;
    RxBleDevice bleDevice = null;
    Disposable scanSubscription = null;
    Disposable connection = null;
    Disposable statechange = null;
    final Handler handler;

    BleLowEnergy(FragmentActivity activity, @NonNull Handler handler) {
        rxBleClient = RxBleClient.create(activity);
        this.handler = handler;
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        int REQUEST_ENABLE_BT = 1;
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

        final static UUID characteristicUuid = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");//白色班子
//    final static UUID characteristicUuid = UUID.fromString("000FFF4-0000-1000-8000-00805F9B34FB");//黑色班子

    private void connect() {
        //
        connection = bleDevice.establishConnection(false) // <-- autoConnect flag
                .flatMap(rxBleConnection -> rxBleConnection.setupNotification(characteristicUuid))
                .doOnNext(notificationObservable -> {
                    // Notification has been set up
                    Message msg = Message.obtain();
                    msg.what = profile.CONNECTED;
                    handler.sendMessage(msg);

                })
                .flatMap(notificationObservable -> notificationObservable) // <-- Notification has been set up, now observe value changes.
                .subscribe(
                        bytes -> {
                            // Given characteristic has been changes, here is the value.
                            Message msg = Message.obtain();
                            msg.what = bytes[0];
                            handler.sendMessage(msg);
                            System.out.println(msg.what);
                        },
                        throwable -> {
                            // Handle an error here.
                            throwable.printStackTrace();
                            Message msg = Message.obtain();
                            msg.what = profile.DISCONNECTED;
                            handler.sendMessage(msg);

                        }
                );
//        statechange = bleDevice.observeConnectionStateChanges()
//                .subscribe(
//                        connectionState -> {
//                            Message msg = Message.obtain();
//                            msg.what = connectionState.ordinal();
//                            handler.sendMessage(msg);
//                        },
//                        throwable -> {
//                            // Handle an error here.
//                        }
//                );


// When done... dispose and forget about connection teardown :)

    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.dispose();
            } catch (com.polidea.rxandroidble2.exceptions.BleDisconnectedException e) {
                Message msg = Message.obtain();
                msg.what = profile.DISCONNECTED;

            }
            connection = null;
            Message msg = Message.obtain();
            msg.what = profile.DISCONNECTED;

        }
    }

    public void scanThenConnect() {
        scanSubscription = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
                        // .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // change if needed
                        // .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES) // change if needed
                        .build()
                // add filters if needed
        )
                .subscribe(
                        scanResult -> {
                            // Process scanThenConnect result here.
                            String deviceName = scanResult.getBleDevice().getName();
                            if (deviceName != null && deviceName.contains("Simple")) {
                                bleDevice = scanResult.getBleDevice();
                                scanSubscription.dispose();// When done, just dispose.
                                connect();
                            }
                            if (deviceName != null && deviceName.contains("R2")) {
                                bleDevice = scanResult.getBleDevice();
                                scanSubscription.dispose();// When done, just dispose.
                                connect();
                            } else {
                                System.out.print(deviceName);
                            }
                        },
                        throwable -> {

                            throwable.printStackTrace();
                            // Handle an error here.
                        }
                );

    }
}

