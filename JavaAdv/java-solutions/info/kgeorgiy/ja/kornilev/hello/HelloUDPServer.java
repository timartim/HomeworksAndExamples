package info.kgeorgiy.ja.kornilev.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelloUDPServer implements HelloServer {
    DatagramSocket socket;
    ExecutorService executor;

    @Override
    public void start(int port, int threads) {
        executor = Executors.newFixedThreadPool(threads);
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        for (int i = 1; i <= threads; i++) {
            executor.submit(() -> {
                while (!socket.isClosed()) {
                    byte[] receiveBuffer;
                    try {
                        receiveBuffer = new byte[socket.getReceiveBufferSize()];
                        // :NOTE: желательно переиспользовать буфер между чтениями, так как жалко выделять getReceiveBufferSize каждый раз
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        socket.receive(receivePacket);
                        String gotten = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                        //System.out.println("Server Received packet: " + gotten);
                        String send = "Hello, " + gotten;
                        byte[] sendBuffer = send.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, 0, sendBuffer.length, receivePacket.getSocketAddress());
                        //System.out.println("Server Send packet: " + send);
                        socket.send(sendPacket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
    @Override
    public void close() {
        executor.shutdown();
        socket.close();
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        int threads = Integer.parseInt(args[1]);
        try(HelloServer server = new HelloUDPServer();){
            server.start(port, threads);
            // :NOTE: сервер запустится и сразу выключится
        }
    }
}
