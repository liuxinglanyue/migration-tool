package com.shata.migration.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class NettyClientPipelineFactory implements ChannelPipelineFactory {

	private SimpleChannelUpstreamHandler handler;
	
	public NettyClientPipelineFactory(SimpleChannelUpstreamHandler handler){
		this.handler = handler;
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = new DefaultChannelPipeline();
		pipeline.addLast("decoder", new StringDecoder());
		pipeline.addLast("encoder", new StringEncoder());
		pipeline.addLast("handler", handler);
		return pipeline;
	}

}
