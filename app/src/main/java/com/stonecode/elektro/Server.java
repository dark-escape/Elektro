package com.stonecode.elektro;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import needle.Needle;

/**
 * Created by vishal on 3/25/17.
 */

public class Server {
    Test activity;
    ServerSocket serverSocket;
    String message = "";
    Socket socket;
    static final int socketServerPORT = 8080;

    public Server(Test activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        int count = 0;

        @Override
        public void run() {
            try {
                // create ServerSocket using specified port
                serverSocket = new ServerSocket(socketServerPORT);

                while (true) {
                    socket = serverSocket.accept();
                    // block the call until connection is created and return
                    // Socket object
//                    count++;
//                    message += "#" + count + " from "
//                            + socket.getInetAddress() + ":"
//                            + socket.getPort() + "\n";
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    final String string = dis.readUTF();
                    Needle.onMainThread().execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (!string.isEmpty()) {

                                        activity.tv.append("Client:"
                                                + string);
                                    }
                                }
                            }
                    );

//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            activity.tv.append("connected"+"\n");
//                        }
//                    });

//                    SocketServerReplyThread socketServerReplyThread =
//                            new SocketServerReplyThread(socket, count,"You are connected.");
//                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    void serverSend(String rep) {
        new SocketServerReplyThread(socket,0,rep).run();
    }

    public class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;
        String reply;

        SocketServerReplyThread(Socket socket, int c, String reply) {
            hostThreadSocket = socket;
            cnt = c;
            this.reply=reply;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from Server, you are #" + cnt;

            try {
                if (hostThreadSocket==null) return;
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(reply);
                printStream.close();

//                message += "replayed: " + msgReply + "\n";
//
//                activity.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
////                        activity.tv.append(message+"\n");
//                    }
//                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    activity.tv.append("Me:"+message+"\n");
                }
            });
        }

    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

}