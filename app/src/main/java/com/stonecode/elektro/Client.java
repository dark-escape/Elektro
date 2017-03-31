package com.stonecode.elektro;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import needle.Needle;

/**
 * Created by vishal on 3/25/17.
 */

public class Client /*extends AsyncTask<Void, Void, Void>*/ {
    String dstAddress;
    int dstPort;
    String response = "";
    TextView textResponse;
    Socket socket;

    Test test;

    Client(String addr, int port, Test test, TextView textResponse) {
        dstAddress = addr;
        dstPort = port;
        this.test = test;
        this.textResponse = textResponse;

    }

    //    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

			/*
             * notice: inputStream.read() will block if no data return
			 */
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
//        return response;
    }

    //    @Override
    protected void onPostExecute(Void result) {
        textResponse.append(response + "\n");
//        super.onPostExecute(result);
    }

    void clientRead() {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket(dstAddress, dstPort);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while (true) {
                        try {

                            DataInputStream dis = new DataInputStream(socket.getInputStream());
                            final String string = dis.readUTF();
                            if (!string.isEmpty()) {
                                Needle.onMainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        textResponse.append("Server: ");
                                        textResponse.append(string);

                                    }
                                });
                            }
                        } catch (IOException e) {
                            test.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textResponse.append("error\n");

                                }
                            });
                            try {
                                Thread.sleep(1000);
                                System.exit(0);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }).run();





    }

    void clientWrite(final String msg) {
        textResponse.append("Me: " + msg);
        Needle.onBackgroundThread().execute(
                new Runnable() {
                    @Override
                    public void run() {

                        try {
                            DataOutputStream dos = new DataOutputStream(
                                    socket.getOutputStream());

                            dos.writeUTF(msg);
                        } catch (IOException e) {
//                    e.printStackTrace();
                            Needle.onMainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    textResponse.append("error");

                                }
                            });
                            try {
                                Thread.sleep(1000);
                                System.exit(0);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
        );
    }
}
