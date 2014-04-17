package com.peacecraftec.redis;

public class RedisLib {

	private static boolean loaded = false;
	
	protected static void preloadClasses() {
		if(loaded) {
			return;
		}
		
		loaded = true;
		try {
			Class.forName("org.jboss.netty.channel.ExceptionEvent");
			Class.forName("org.jboss.netty.channel.Channels");
			Class.forName("org.jboss.netty.channel.Channels$1");
			Class.forName("org.jboss.netty.channel.Channels$2");
			Class.forName("org.jboss.netty.channel.Channels$3");
			Class.forName("org.jboss.netty.channel.Channels$4");
			Class.forName("org.jboss.netty.channel.Channels$5");
			Class.forName("org.jboss.netty.channel.Channels$6");
			Class.forName("org.jboss.netty.channel.Channels$7");
			Class.forName("org.jboss.netty.channel.Channels$8");
			Class.forName("org.jboss.netty.channel.socket.ChannelRunnableWrapper");
			Class.forName("org.jboss.netty.channel.DefaultExceptionEvent");
		} catch(Exception e) {
			System.err.println("Failed to preload netty classes!");
			e.printStackTrace();
		}
	}
	
}
