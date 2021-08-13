package net.webrtc.signaling.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.concurrent.GlobalEventExecutor;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private WebSocketServerHandshaker webSocketServerHandshaker;
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelGroup.add(ctx.channel());
        System.out.println("added: " + ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            HttpHeaders headers = request.headers();

            if(headers.get(HttpHeaderNames.CONNECTION).equalsIgnoreCase("Upgrade")
                    && headers.get(HttpHeaderNames.UPGRADE).equalsIgnoreCase("WebSocket")) {
                ctx.pipeline().replace(this, "webSocketHandler", new RTCWebSocketSignalingHandler(channelGroup));
                handleHandshake(ctx.channel(), request);
            }
        }

    }

    private void handleHandshake(Channel channel, HttpRequest request) {
        WebSocketServerHandshakerFactory factory = getHandshakeFactory(request);
        webSocketServerHandshaker = factory.newHandshaker(request);

        if(webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channel);
        }
        else {
            webSocketServerHandshaker.handshake(channel, request);
            channelGroup.add(channel);
        }
    }

    private String getWebSocketUrl(HttpRequest request) {
        return "wss://" + request.headers().get(HttpHeaderNames.HOST) + ":9090";
    }

    private WebSocketServerHandshakerFactory getHandshakeFactory(HttpRequest request) {
        return new WebSocketServerHandshakerFactory(getWebSocketUrl(request), null, false);
    }
}
