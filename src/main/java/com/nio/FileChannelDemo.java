package com.nio;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class FileChannelDemo {

    @Test
    public void readFile() throws Exception {
        //使用channel进行文件读写
        RandomAccessFile randomAccessFile = new RandomAccessFile("D:\\垃圾\\demo\\111.txt","rw");
        //构建fileChannel
        FileChannel channel = randomAccessFile.getChannel();
        //创建字符缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //channel.read如果读到末尾则返回-1
        while(channel.read(byteBuffer)>0){
            //读取缓冲区打印出来
            byteBuffer.flip();//重置position和limit
            //hasRemaining :如果缓冲区还有数据则继续
            while(byteBuffer.hasRemaining()){
                byte b = byteBuffer.get();
                System.out.println((char)b);
            }
            byteBuffer.clear();
        }
        channel.close();
    }

    @Test
    public void writeFile() throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile("D:\\垃圾\\demo\\112.txt","rw");

        FileChannel c = randomAccessFile.getChannel();
        byte[] bytes = "ws23223d33331233".getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, bytes.length);
        //buffer.flip();//会把limit置为position，只适合用到put之后
        while(buffer.hasRemaining()){
            c.write(buffer);
        }
        c.close();
    }



}
