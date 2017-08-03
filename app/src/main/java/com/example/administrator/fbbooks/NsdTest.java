package com.example.administrator.fbbooks;

/**
 * Created by RAJATHKRISHNA.JAGANA on 7/28/2017.
 */

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.net.InetAddress;

public class NsdTest {
    private static final String NSD_SERVICE_NAME = "TestService";
    private static final String NSD_SERVICE_TYPE = "_http._tcp.";
    private int mPort;
    private InetAddress mHost;
    private Context mContext;
    private NsdManager mNsdManager;
    private android.net.nsd.NsdManager.DiscoveryListener mDiscoveryListener;
    private android.net.nsd.NsdManager.ResolveListener mResolveListener;

    public NsdTest(Context context) {
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startListening() {
        initializeResolveListener();
        initializeDiscoveryListener();
        mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
        mNsdManager.discoverServices(NSD_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopListening() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d("NSDService test","Resolve failed");
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                NsdServiceInfo info = serviceInfo;
                Log.d("NSDService test","Resolve failed");
                mHost = info.getHost();
                mPort = info.getPort();
                Log.d("NSDService test","Service resolved :" + mHost + ":" + mPort);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.d("NSDService test","Discovery failed");
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.d("NSDService test","Stopping discovery failed");
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d("NSDService test","Discovery started");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d("NSDService test","Discovery stopped");
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                NsdServiceInfo info = serviceInfo;
                Log.d("NSDService test","Service found: " + info.getServiceName());
                if (info.getServiceName().equals(NSD_SERVICE_NAME))
                    mNsdManager.resolveService(info, mResolveListener);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                NsdServiceInfo info = serviceInfo;
                Log.d("NSDService test","Service lost: " + info.getServiceName());
            }
        };
    }
}