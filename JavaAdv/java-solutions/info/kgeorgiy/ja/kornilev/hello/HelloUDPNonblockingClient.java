package info.kgeorgiy.ja.kornilev.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;


public class HelloUDPNonblockingClient implements HelloClient {
    final int timeout = 100;

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        InetAddress adress = null;
        try {
            adress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new RuntimeException("could not connect to given host");
        }
        try (Selector selector = Selector.open()) {
            ByteBuffer[] buffers = new ByteBuffer[threads];
            for (int i = 0; i < threads; i++) {
                DatagramChannel datagramChannel = DatagramChannel.open();
                datagramChannel.configureBlocking(false);
                datagramChannel.register(selector, SelectionKey.OP_WRITE, i);
                buffers[i] = ByteBuffer.allocate(datagramChannel.socket().getReceiveBufferSize());
            }
            for (int i = 0; i < threads; i++) {
                InetAddress gottenAdress = adress;
                int finalI = i;
                for (int j = 0; j < requests; j++) {
                    String message = prefix + finalI + "_" + j;
                    try (DatagramSocket socket = new DatagramSocket()) {
                        byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length, gottenAdress, port);
                        byte[] receiveBuffer = new byte[socket.getReceiveBufferSize()];
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        String expected = "Hello, " + message;
                        socket.setSoTimeout(timeout);
                        while (true) {
                            try {
                                System.out.println("Client Send packet " + message);
                                socket.send(packet);
                                socket.receive(receivePacket);
                                String gotten = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), StandardCharsets.UTF_8);
                                System.out.println("Received packet " + gotten);
                                if (!gotten.equals(expected)) {
                                    System.out.println("G         ot wrong message from server");
                                    System.err.println("CHECKING PACKETS: " + expected + " " + gotten);
                                    continue;
                                }
                                break;
                            } catch (IOException e) {
                                System.err.println(e);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("I/O exception occured");
        }

    }
}


