package com.stonecode.elektro;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


public class NearbyActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
//        Connections.EndpointDiscoveryListener,
//        Connections.ConnectionRequestListener,
        View.OnClickListener,
        Connections.MessageListener {

//    private static final long TIMEOUT_ADVERTISE = 1000L * 30L;
//    private static final long TIMEOUT_DISCOVER = 1000L * 30L;

    @Retention(RetentionPolicy.CLASS)
    @IntDef({STATE_IDLE, STATE_READY, STATE_ADVERTISING, STATE_DISCOVERING, STATE_CONNECTED})
    public @interface NearbyConnectionState {
    }

    private static final int STATE_IDLE = 1023;
    private static final int STATE_READY = 1024;
    private static final int STATE_ADVERTISING = 1025;
    private static final int STATE_DISCOVERING = 1026;
    private static final int STATE_CONNECTED = 1027;


    private static final String TAG = "NearbyActivity";
    private boolean mIsHost = false;
    GoogleApiClient mGoogleApiClient;

    private TextView mDebugInfo;
    private EditText mMessageText;
    private AlertDialog mConnectionRequestDialog;
    private MyListDialog mMyListDialog;

    /**
     * The current state of the application
     **/
    @NearbyConnectionState
    private int mState = STATE_IDLE;

    /**
     * The endpoint ID of the connected peer, used for messaging
     **/
    private String mOtherEndpointId;

    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI,
            ConnectivityManager.TYPE_ETHERNET};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        findViewById(R.id.button_advertise).setOnClickListener(this);
        findViewById(R.id.button_discover).setOnClickListener(this);
        findViewById(R.id.button_send).setOnClickListener(this);

        // EditText
        mMessageText = (EditText) findViewById(R.id.edittext_message);

        // Debug text view
        mDebugInfo = (TextView) findViewById(R.id.debug_text);
        mDebugInfo.setMovementMethod(new ScrollingMovementMethod());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .useDefaultAccount()
                .build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean isConnectedToNetwork() {
        ConnectivityManager connManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        for (int networkType : NETWORK_TYPES) {
            NetworkInfo info = connManager.getNetworkInfo(networkType);
            if (info != null && info.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    private void debugLog(String msg) {
        Log.d(TAG, msg);
        mDebugInfo.append("\n" + msg);
    }

    private void startAdvertising() {
        debugLog("startAdvertising");
        if (!isConnectedToNetwork()) {
            debugLog("startAdvertising: not connected to WiFi network.");
            return;
        }

        // Identify that this device is the host
        mIsHost = true;

        // Advertising with an AppIdentifer lets other devices on the
        // network discover this application and prompt the user to
        // install the application.

        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        // The advertising timeout is set to run indefinitely
        // Positive values represent timeout in milliseconds
        long NO_TIMEOUT = 0L;

        String name = null;
        Nearby.Connections.startAdvertising(mGoogleApiClient, name, null, NO_TIMEOUT,
                new Connections.ConnectionRequestListener() {
                    @Override
                    public void onConnectionRequest(String endpointId, String endPointName, byte[] payload) {
                        NearbyActivity.this.onConnectionRequest(endpointId, endPointName, payload);
                    }
                }).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    // Device is advertising
                    debugLog("startAdvertising:onResult: SUCCESS");

                    updateViewVisibility(STATE_ADVERTISING);
                } else {
                    // Advertising failed - see statusCode for more details

                    // If the user hits 'Advertise' multiple times in the timeout window,
                    // the error will be STATUS_ALREADY_ADVERTISING
                    int statusCode = result.getStatus().getStatusCode();
                    debugLog("startAdvertising:onResult: FAILURE " + statusCode);
                    if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
                        debugLog("STATUS_ALREADY_ADVERTISING");
                    } else {
                        updateViewVisibility(STATE_READY);
                    }
                }
            }
        });
    }

    private void startDiscovery() {
        debugLog("startDiscovery");
        if (!isConnectedToNetwork()) {
            debugLog("startDiscovery: not connected to WiFi network.");
            return;
        }
        String serviceId = getString(R.string.service_id);

        // Set an appropriate timeout length in milliseconds
        long DISCOVER_TIMEOUT = 1000L;

        // Discover nearby apps that are advertising with the required service ID.
        Nearby.Connections.startDiscovery(mGoogleApiClient, serviceId, DISCOVER_TIMEOUT, new Connections.EndpointDiscoveryListener() {
            @Override
            public void onEndpointFound(String s, String s1, String s2) {
                NearbyActivity.this.onEndpointFound(s,s1,s2);
            }

            @Override
            public void onEndpointLost(String s) {
                NearbyActivity.this.onEndpointLost(s);
            }
        })
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            // Device is discovering
                            debugLog("startDiscovery:onResult: SUCCESS");

                            updateViewVisibility(STATE_DISCOVERING);
                        } else {
                            int statusCode = status.getStatusCode();
                            if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_DISCOVERING) {
                                debugLog("STATUS_ALREADY_DISCOVERING");
                            } else {
                                updateViewVisibility(STATE_READY);
                            }
                            // Advertising failed - see statusCode for more details
                        }
                    }
                });
    }

    private void sendMessage() {
        // Sends a reliable message, which is guaranteed to be delivered eventually and to respect
        // message ordering from sender to receiver. Nearby.Connections.sendUnreliableMessage
        // should be used for high-frequency messages where guaranteed delivery is not required, such
        // as showing one player's cursor location to another. Unreliable messages are often
        // delivered faster than reliable messages.
        String msg = mMessageText.getText().toString();
        Nearby.Connections.sendReliableMessage(mGoogleApiClient, mOtherEndpointId, msg.getBytes());

        mMessageText.setText(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_advertise:
                startAdvertising();
                break;
            case R.id.button_discover:
                startDiscovery();
                break;
            case R.id.button_send:
                sendMessage();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        debugLog("onConnected");
        updateViewVisibility(STATE_READY);
    }

    @Override
    public void onConnectionSuspended(int i) {
        debugLog("onConnectionSuspended: " + i);
        updateViewVisibility(STATE_IDLE);

        // Try to re-connect
//        assert mGoogleApiClient!=null;
        mGoogleApiClient.reconnect();
//        mGoogleApiClient.blockingConnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        debugLog("onConnectionFailed: " + connectionResult);
        updateViewVisibility(STATE_IDLE);
    }


    @Override
    public void onMessageReceived(String endpointId, byte[] payload, boolean b) {
        debugLog("onMessageReceived:" + endpointId + ":" + new String(payload));
    }

    @Override
    public void onDisconnected(String endpointId) {
        debugLog("onDisconnected:" + endpointId);

        updateViewVisibility(STATE_READY);
    }


    public void onConnectionRequest(final String endpointId, String endpointName, byte[] payload) {
        debugLog("onConnectionRequest:" + endpointId + ":" + endpointName);

        // This device is advertising and has received a connection request. Show a dialog asking
        // the user if they would like to connect and accept or reject the request accordingly.
        mConnectionRequestDialog = new AlertDialog.Builder(this)
                .setTitle("Connection Request")
                .setMessage("Do you want to connect to " + endpointName + "?")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        byte[] payload = null;
                        Nearby.Connections.acceptConnectionRequest(mGoogleApiClient, endpointId,
                                payload, NearbyActivity.this)
                                .setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        if (status.isSuccess()) {
                                            debugLog("acceptConnectionRequest: SUCCESS");

                                            mOtherEndpointId = endpointId;
                                            updateViewVisibility(STATE_CONNECTED);
                                        } else {
                                            debugLog("acceptConnectionRequest: FAILURE");
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Nearby.Connections.rejectConnectionRequest(mGoogleApiClient, endpointId);
                    }
                }).create();

        mConnectionRequestDialog.show();
    }

    /**
     * Change the application state and update the visibility on on-screen views '
     * based on the new state of the application.
     *
     * @param newState the state to move to (should be NearbyConnectionState)
     */
    private void updateViewVisibility(@NearbyConnectionState int newState) {
        mState = newState;
        switch (mState) {
            case STATE_IDLE:
                // The GoogleAPIClient is not connected, we can't yet start advertising or
                // discovery so hide all buttons
                findViewById(R.id.layout_nearby_buttons).setVisibility(View.GONE);
                findViewById(R.id.layout_message).setVisibility(View.GONE);
                break;
            case STATE_READY:
                // The GoogleAPIClient is connected, we can begin advertising or discovery.
                findViewById(R.id.layout_nearby_buttons).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_message).setVisibility(View.GONE);
                break;
            case STATE_ADVERTISING:
                break;
            case STATE_DISCOVERING:
                break;
            case STATE_CONNECTED:
                // We are connected to another device via the Connections API, so we can
                // show the message UI.
                findViewById(R.id.layout_nearby_buttons).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_message).setVisibility(View.VISIBLE);
                break;
        }
    }

    private void connectTo(final String endpointId, final String endpointName) {
        debugLog("connectTo:" + endpointId + ":" + endpointName);

        // Send a connection request to a remote endpoint. By passing 'null' for
        // the name, the Nearby Connections API will construct a default name
        // based on device model such as 'LGE Nexus 5'.
        String myName = null;
        byte[] myPayload = null;
        Nearby.Connections.sendConnectionRequest(mGoogleApiClient, myName,
                endpointId, myPayload, new Connections.ConnectionResponseCallback() {
                    @Override
                    public void onConnectionResponse(String remoteEndpointId, Status status,
                                                     byte[] bytes) {
                        if (status.isSuccess()) {
                            // Successful connection
                            debugLog("onConnectionResponse: " + endpointName + " SUCCESS");
                            Toast.makeText(NearbyActivity.this, "Connected to " + endpointName,
                                    Toast.LENGTH_SHORT).show();

                            mOtherEndpointId = endpointId;
                            updateViewVisibility(STATE_CONNECTED);
                        } else {
                            // Failed connection
                            debugLog("onConnectionResponse: " + endpointName + " FAILURE" + status.getStatusCode());

                        }
                    }
                }, this);
    }

    public void onEndpointFound(final String endpointId,
                                String serviceId, final String endpointName) {
        // This device is discovering endpoints and has located an advertiser.
        // Write your logic to initiate a connection with the device at
        // the endpoint ID
        Log.d(TAG, "onEndpointFound:" + endpointId + ":" + endpointName);

        // This device is discovering endpoints and has located an advertiser. Display a dialog to
        // the user asking if they want to connect, and send a connection request if they do.
        if (mMyListDialog == null) {
            // Configure the AlertDialog that the MyListDialog wraps
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Endpoint(s) Found")
                    .setCancelable(true)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mMyListDialog.dismiss();
                        }
                    });

            // Create the MyListDialog with a listener
            mMyListDialog = new MyListDialog(this, builder, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedEndpointName = mMyListDialog.getItemKey(which);
                    String selectedEndpointId = mMyListDialog.getItemValue(which);

                    connectTo(selectedEndpointId, selectedEndpointName);
                    mMyListDialog.dismiss();
                }
            });
        }

        mMyListDialog.addItem(endpointName, endpointId);
        mMyListDialog.show();

    }

    public void onEndpointLost(String endpointId) {
        debugLog("onEndpointLost:" + endpointId);

        // An endpoint that was previously available for connection is no longer. It may have
        // stopped advertising, gone out of range, or lost connectivity. Dismiss any dialog that
        // was offering a connection.
        if (mMyListDialog != null) {
            mMyListDialog.removeItemByValue(endpointId);
        }
    }
}
