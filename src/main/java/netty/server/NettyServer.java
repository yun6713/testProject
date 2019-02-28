package netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
/**
 * 事件驱动，NIO，异步
 * @author Administrator
 *
 */

public class NettyServer {
	public static final int PORT = 7878;
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			System.out.println("Server:");
			ServerBootstrap sb = new ServerBootstrap();
			//服务器事件组
			sb.group(bossGroup, workerGroup);
			//channel工厂
			sb.channel(NioServerSocketChannel.class);
			//频道处理
			sb.childHandler(new HelloServerInitializer());
			//监听绑定端口
			ChannelFuture cf = sb.bind(PORT).sync();
			//监听服务器关闭监听
			cf.channel().closeFuture().sync();
			
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
class HelloServerInitializer extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline cp = ch.pipeline();
		cp.addLast("frame", new DelimiterBasedFrameDecoder(8192,Delimiters.lineDelimiter()));
		cp.addLast("decoder",new StringDecoder());
		cp.addLast("encoder",new StringEncoder());
		cp.addLast("handler",new HelloServerHandler());
	}
	
}
class HelloServerHandler extends SimpleChannelInboundHandler<String>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println(ctx.channel().remoteAddress()+" say: "+msg);
		ctx.writeAndFlush("Receive your msg.\n");
		
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress()+" active. ");
		ctx.writeAndFlush("Welcome\n");
		super.channelActive(ctx);
	}
	
}