package net.webrtc.signaling.initializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import net.webrtc.signaling.handler.HttpServerHandler;

public class HttpsInitializer extends ChannelInitializer {

    private SslContext sslContext;

    public HttpsInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();

        channelPipeline.addLast(sslContext.newHandler(ch.alloc()));
        channelPipeline.addLast("httpServerCodec", new HttpServerCodec());
        channelPipeline.addLast("httpServerHandler", new HttpServerHandler());
    }
}
