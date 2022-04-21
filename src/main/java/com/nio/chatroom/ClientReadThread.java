package com.nio.chatroom;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class ClientReadThread implements Runnable {
    private Selector selector;

    public ClientReadThread(Selector selector){
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            while (true) {
                //获取可用的channel数量
                int readyChannels = selector.select();
                if (readyChannels == 0) continue;
                /**
                 * 获取可用的channel的集合
                 */
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    /**
                     * 获取SelectionKey实例
                     */
                    SelectionKey selectionKey = iterator.next();
                    /**
                     * 移除set中的当前SelectionKey
                     */
                    iterator.remove();
                    /**
                     * 7.根据就绪状态，调用对应方法来处理业务逻辑
                     */

                    /**
                     * 如果是可读事件
                     */
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 可读事件处理器
     *
     * @param selectionKey
     * @param selector
     * @throws IOException
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        /**
         * 要从selectionKey中获取到已经就绪的channel
         */
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        /**
         * 创建buffer
         */
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        /**
         * 循环读取服务器端端响应信息
         */
        String response = "";
        while (channel.read(byteBuffer) > 0) {
            /**
             * 切换buffer为读模式
             */
            byteBuffer.flip();
            /**
             * 读取buffer中的内容
             */
            response += Charset.forName("UTF-8").decode(byteBuffer);
        }
        /**
         * 将channel再次注册到selector上，监听可读事件
         */
        channel.register(selector, SelectionKey.OP_READ);
        /**
         * 将服务器端的响应信息打印到本地
         */
        if (response.length() > 0) {
            System.out.println(response);
        }
    }


}
