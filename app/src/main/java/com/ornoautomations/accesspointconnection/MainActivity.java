package com.ornoautomations.accesspointconnection;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("onCreate");
        try {
            buildSocket(20000);
            System.out.println("buildSocket");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    public Socket buildSocket(int readTimeoutMillis) throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(readTimeoutMillis);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bindSocketToSoftAp(socket);
        }
        return socket;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void bindSocketToSoftAp(Socket socket) throws IllegalArgumentException, IOException {
        ConnectivityManager connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network softAp = null;
        for (Network network : connMan.getAllNetworks()) {
            System.out.println("Inspecting network:  " + network);
            NetworkInfo networkInfo = connMan.getNetworkInfo(network);
            System.out.println("Inspecting network info:  " + networkInfo);
            // Android doesn't have any means of directly
            // asking "I want the Network obj for the Wi-Fi network with SSID <foo>".
            // Instead, you have to infer it.  Let's hope that getExtraInfo() doesn't
            // ever change...
            //String dequotifiedNetworkExtraSsid = WiFi.deQuotifySsid(networkInfo.getExtraInfo());
            //String dequotifiedTargetSsid = WiFi.deQuotifySsid(softAPSSID);

            String dequotifiedNetworkExtraSsid = networkInfo.getExtraInfo();
            String dequotifiedTargetSsid = "\"FindMe\"";
            System.out.println("Network extra info: '" + dequotifiedNetworkExtraSsid + "'");
            System.out.println("And the SSID we were to connect to: '" + dequotifiedTargetSsid + "'");
            if (dequotifiedTargetSsid.equalsIgnoreCase(dequotifiedNetworkExtraSsid)) {
                softAp = network;
                break;
            }
        }

        if (softAp == null) {
            // If this ever fails, fail VERY LOUDLY to make sure we hear about it...
            // FIXME: report the error via analytics instead of/in addition to crashing
            /*throw new IllegalArgumentException(
                    "Could not find Network for SSID ");*/
            System.out.println("Could not find Network for SSID");
            return;
        }
        softAp.bindSocket(socket);
        System.out.println("bindSocket");
    }
}
