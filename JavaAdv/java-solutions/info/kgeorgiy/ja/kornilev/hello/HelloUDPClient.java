package info.kgeorgiy.ja.kornilev.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;


public class HelloUDPClient implements HelloClient {
    final static int TIMEOUT = 100;

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        Phaser phaser = new Phaser(1);
        final InetAddress adress;
        try {
            adress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new RuntimeException("could not connect to given host");
        }
        for (int i = 1; i <= threads; i++) {
            int finalI = i;
            phaser.register();
            executor.submit(() -> {
                for (int j = 1; j <= requests; j++) {
                    String message = prefix + finalI + "_" + j;
                    try (DatagramSocket socket = new DatagramSocket()) {
                        byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length, adress, port);
                        byte[] receiveBuffer = new byte[socket.getReceiveBufferSize()];
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        String expected = "Hello, " + message;
                        socket.setSoTimeout(TIMEOUT);
                        while (true) {
                            try {
                                //System.out.println("Client Send packet " + message);
                                socket.send(packet);
                                socket.receive(receivePacket);
                                String gotten = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), StandardCharsets.UTF_8);
                                //System.out.println("Received packet " + gotten);
                                if (!gotten.equals(expected)) {
                                    //System.out.println("Got wrong message from server");
                                    //System.err.println("CHECKING PACKETS: " + expected + " " + gotten);
                                    continue;
                                }
                                break;
                            }catch (IOException e){
                                System.err.println(e);// :NOTE: понятное сообщение об ошибке
                            }
                        }
                    } catch (IOException e) {
                        System.err.println(e);// :NOTE: понятное сообщение об ошибке
                    }
                }
                phaser.arrive(); // :NOTE: можно обойтись без phaser, если правильно пользоваться executor
            });
        }
        phaser.arriveAndAwaitAdvance();
        executor.shutdown();
    }
}

