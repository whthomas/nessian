package top.nessian.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.nessian.server.annotation.NessianAPIScanner;

/**
 * Created by whthomas on 15/11/7.
 */
public class NessianServer {

    private static Logger logger = LoggerFactory.getLogger(NessianServer.class);

    private static final int PORT = 8080;

    private static void start(){

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new NessianServerInitializer());

            Channel ch = b.bind(PORT).sync().channel();

            logger.info(" server is starting...... ");

            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    /**
     * 启动 server
     *
     * @param packageName
     */
    public static void start(String packageName){
        NessianAPIScanner.scanner(packageName);
        start();
    }

    public static void main(String args[]){
        start("test.whthomas.business");
    }

}
