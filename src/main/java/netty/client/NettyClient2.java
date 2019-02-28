package netty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import netty.server.NettyServer;

public class NettyClient2 {
	public static final String HOST = "localhost";
	public static void main(String[] args) throws IOException, InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			System.out.println("Client:");
			Bootstrap b = new Bootstrap();
			b.group(group)
				.channel(NioSocketChannel.class)
				.handler(new HelloClientInitializer2());
			Channel ch = b.connect(HOST, NettyServer.PORT).sync().channel();
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				String line = br.readLine();
				if(line!=null) {
//					System.out.println(line);
					ch.writeAndFlush(line+"\n");
				}else {
					break;
				}
			}
		} finally {
			group.shutdownGracefully();
		}
	}

}
class HelloClientInitializer2 extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline cp = ch.pipeline();
		cp.addLast("frame", new DelimiterBasedFrameDecoder(8192,Delimiters.lineDelimiter()));
		cp.addLast("decoder",new StringDecoder());
		cp.addLast("encoder",new StringEncoder());
		cp.addLast("handler",new HelloClientHandler2());
	}
	
}
class HelloClientHandler2 extends SimpleChannelInboundHandler<String>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println(ctx.channel().remoteAddress()+" ,server says: "+msg);
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Channel active");
		super.channelActive(ctx);
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Channel inactive");
		super.channelInactive(ctx);
	}
}