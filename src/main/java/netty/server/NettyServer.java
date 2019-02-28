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
 * �¼�������NIO���첽
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
			//�������¼���
			sb.group(bossGroup, workerGroup);
			//channel����
			sb.channel(NioServerSocketChannel.class);
			//Ƶ������
			sb.childHandler(new HelloServerInitializer());
			//�����󶨶˿�
			ChannelFuture cf = sb.bind(PORT).sync();
			//�����������رռ���
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