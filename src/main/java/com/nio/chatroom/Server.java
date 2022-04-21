package com.nio.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Server {
    private static ArrayList<SocketChannel> socketChannels = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        //创建serverSocketChannel
        ServerSocketChannel open = ServerSocketChannel.open();
        //绑定主机端口
        open.bind(new InetSocketAddress(9999));
        //非阻塞模式
        open.configureBlocking(false);
        //注册到selector上
        Selector selector = Selector.open();
        open.register(selector, SelectionKey.OP_ACCEPT);
        //循环监听selector状态
        while(true){
            //获取selector状态
            int select = selector.select();
            if(select<1){
                continue;
            }
            //有状态,获取key,遍历key,得到生效的那一个
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();

                iterator.remove();

                //判断是哪种类型的key
                if(selectionKey.isAcceptable()){
                    //这里的key表示有客户端的连接到了服务端
                    //让服务端socket去接受请求,再注册到Read key上
                    //获取对应这个客户端的socketchannel
                    SocketChannel channel = open.accept();
                    channel.configureBlocking(false);
                    channel.register(selector,SelectionKey.OP_READ);
                    socketChannels.add(channel);
                }else if(selectionKey.isReadable()){
                    /**
                     * 要从selectionKey中获取到已经就绪的channel
                     */
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    /**
                     * 创建buffer
                     */
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    /**
                     * 循环读取客户端请求信息
                     */
                    String request = "";
                    while (channel.read(byteBuffer) > 0) {
                        /**
                         * 切换buffer为读模式
                         */
                        byteBuffer.flip();
                        /**
                         * 读取buffer中的内容
                         */
                        request += Charset.forName("UTF-8").decode(byteBuffer);
                    }
                    /**
                     * 将channel再次注册到selector上，监听可读事件
                     */
                    channel.register(selector, SelectionKey.OP_READ);
                    /**
                     * 将客户端发送的请求信息 广播给其他客户端
                     */
                    if (request.length() > 0) {
                        /**
                         * 获取所有已接入的客户端channel
                         */
                        Set<SelectionKey> keys = selector.keys();
                        /**
                         * 循环遍历，向所有的channel广播信息
                         */
                        String finalRequest = request;

                        keys.forEach(key -> {
                            Channel targetChannel = key.channel();
                            //判断channel类型，以及排除掉发消息的客户端channel
                            if (targetChannel instanceof SocketChannel && targetChannel != channel) {
                                try {
                                    //发送消息到其他客户端
                                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(finalRequest));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                }
            }
        }
    }
}

