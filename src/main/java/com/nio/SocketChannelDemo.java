package com.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class SocketChannelDemo {

    @Test
    public void server() throws IOException, InterruptedException {
        //1.创建服务端
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //2.绑定端口
        ssc.bind(new InetSocketAddress(9995));
        //3.设置非阻塞式
        ssc.configureBlocking(false);
        //创建字符缓冲区
        ByteBuffer bf = ByteBuffer.allocate(1024);
        //4.获取服务端的socket，等待客户端响应，因为是非组赛的所以没有客户端就会返回null
        while(true){
            SocketChannel sc = ssc.accept();
            if(sc!=null){
                sc.configureBlocking(false);
                //获取到了客户端的连接
                SocketAddress remoteAddress = sc.getRemoteAddress();
                System.out.println(remoteAddress);
                //读取客户端发送来的数据到缓冲区，再从缓冲区中打印出来
                while(sc.read(bf)>0){
                    bf.flip();
                    System.out.println(new java.lang.String(bf.array(),"utf-8"));
                    bf.clear();
                }
//                ByteBuffer wrap = ByteBuffer.wrap("1234".getBytes());
//                wrap.rewind();
//                sc.write(wrap);
                sc.close();
            }else{
                //循环等待
                System.out.println("server wait... ");
                Thread.sleep(2000);
            }

        }
    }

    @Test
    public void socketChannelClient() throws IOException {
        //创建socketChannel
        SocketChannel sc = SocketChannel.open();
        //绑定ip 端口
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9995);

        sc.connect(address);
        //设置非阻塞模式
        sc.configureBlocking(false);


        //创建缓冲区，写数据
        ByteBuffer bf = ByteBuffer.wrap("from client 123456....".getBytes());
        sc.write(bf);
//        Scanner scanner = new Scanner(System.in);
//            String str = scanner.next();
//        sc.close();

    }
}
