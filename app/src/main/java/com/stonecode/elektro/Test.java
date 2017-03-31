package com.stonecode.elektro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import needle.Needle;

public class Test extends AppCompatActivity {

    public TextView tv;
    WifiApManager manager;
    EditText et;
    Button host, clientBtn, send;
    Server server;
    Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tv = (TextView) findViewById(R.id.tv_one);

        tv.setMovementMethod(new ScrollingMovementMethod());
        manager = new WifiApManager(this);

        et = (EditText) findViewById(R.id.msg);
        host = (Button) findViewById(R.id.host);
        clientBtn = (Button) findViewById(R.id.client);
        send = (Button) findViewById(R.id.send);
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server = new Server(Test.this);
                tv.setText(server.getIpAddress() + ":" + server.getPort() + "\n");
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        server.serverSend(et.getText().toString());
                    }
                });
            }
        });

        clientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();

            }
        });

    }

    private void scan() {
        manager.getClientList(false, new FinishScanListener() {

            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {
                tv.setText("WifiApState: " + manager.getWifiApState() + "\n\n");
                tv.append("Clients: \n");
                for (ClientScanResult clientScanResult : clients) {
                    tv.append("####################\n");
                    tv.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
                    tv.append("Device: " + clientScanResult.getDevice() + "\n");
                    tv.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
                    tv.append("isReachable: " + clientScanResult.isReachable() + "\n");
//                    startSock(clients);
                    client = new Client(clientScanResult.getIpAddr(), 8080, Test.this,tv);
                    Needle.onBackgroundThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            client.clientRead();
                        }
                    });

//                    Client myClient = new Client(clientScanResult.getIpAddr(), 8080, tv);
//                    myClient.execute();
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.clientWrite(et.getText().toString());
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }


    private void startSock(ArrayList<ClientScanResult> clients) {

        Socket socket = null;
        try {
            String host = clients.get(0).getIpAddr();
            int port = 4444;
            socket = new Socket(host, port);

            //Send the message to the server
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            String number = "Hey";

            String sendMessage = number + "\n";
            bw.write(sendMessage);
            bw.flush();
            tv.setText("Message sent to the server : " + sendMessage + "\n");

            //Get the return message from the server
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String message = br.readLine();
            System.out.println("Message received from the server : " + message);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            //Closing the socket
            try {
                assert socket != null;
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void startStockServer() {
        Socket socket = null;
        try {

            int port = 4444;
            ServerSocket serverSocket = new ServerSocket(port);
            tv.setText("Server Started and listening to the port 4444\n");

            //Server is running always. This is done using this while(true) loop
            while (true) {
                //Reading the message from the clientBtn
                socket = serverSocket.accept();
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String number = br.readLine();
                tv.setText("Message received from clientBtn is " + number + "\n");

                //Multiplying the number by 2 and forming the return message
                String returnMessage;
                returnMessage = "Messsage received";

                //Sending the response back to the clientBtn.
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(returnMessage);
                tv.setText("Message sent to the clientBtn is " + returnMessage + "\n");
                bw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert socket != null;
            try {
                socket.close();
            } catch (IOException ignored) {

            }
        }
    }

}
