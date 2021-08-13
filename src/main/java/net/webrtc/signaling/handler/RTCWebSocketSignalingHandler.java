package net.webrtc.signaling.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class RTCWebSocketSignalingHandler extends ChannelInboundHandlerAdapter {

    private ChannelGroup channelGroup;

    public RTCWebSocketSignalingHandler(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof WebSocketFrame) {
            System.out.println("frame:" + msg);
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;

            for(Channel channel : channelGroup) {
                if(channel.equals(ctx.channel())) {
                    continue;
                }

                System.out.println(textWebSocketFrame.text());
                channel.writeAndFlush(textWebSocketFrame);
            }
        }
    }
}
