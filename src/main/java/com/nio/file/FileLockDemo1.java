package com.nio.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class FileLockDemo1 {

    public static void main(String[] args) throws IOException {
        String input = "demo";
        RandomAccessFile randomAccessFile = new RandomAccessFile("D:\\垃圾\\demo\\111.txt","rw");
        FileChannel channel = randomAccessFile.getChannel();
        channel.lock();
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();


    }
}
