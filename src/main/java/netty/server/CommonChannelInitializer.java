package netty.server;

import java.util.List;
import java.util.Map;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class CommonChannelInitializer extends ChannelInitializer<SocketChannel>{
	private Map<String,ChannelHandler> chs;
	
	public CommonChannelInitializer(Map<String, ChannelHandler> chs) {
		super();
		this.chs = chs;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		if(chs==null && chs.isEmpty()) {
			return;
		}
		
		chs.forEach(ch.pipeline()::addLast);
	}

}
