package com.nio.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Client2 {

    public static void main(String[] args) throws IOException {
        //create client SocketChannel
        SocketChannel sc = SocketChannel.open();
        //connect to 9999 port
        sc.connect(new InetSocketAddress("127.0.0.1",9999));
        //set blocking mode
        sc.configureBlocking(false);


        //start thread going read Operator
        System.out.println("开启读 线程");
        Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_READ);
        new Thread(new ClientReadThread(selector)).start();

        //send data to server
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            String next = scanner.next();
            next ="李四:"+next;

            ByteBuffer wrap = Charset.forName("utf-8").encode(next);
            //send
            sc.write(wrap);
        }
    }
}
