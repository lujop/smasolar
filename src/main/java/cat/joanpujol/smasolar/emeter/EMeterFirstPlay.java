package cat.joanpujol.smasolar.emeter;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.*;
import java.util.Enumeration;

public class EMeterFirstPlay extends Thread {
    private InetSocketAddress groupAddress;

    private EMeterFirstPlay(InetSocketAddress groupAddress) {
        this.groupAddress = groupAddress;
    }

    public static void main(String[] args) {
        InetSocketAddress groupAddress = new InetSocketAddress("239.12.255.254", 9522);
        new EMeterFirstPlay(groupAddress).run();
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            NetworkInterface ni = NetworkInterface.getByName("en5");
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            InetAddress localAddress = null;
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address instanceof Inet4Address) {
                    localAddress = address;
                }
            }

            Bootstrap b = new Bootstrap()
                    .group(group)
                    .channelFactory(new ChannelFactory<NioDatagramChannel>() {
                        @Override
                        public NioDatagramChannel newChannel() {
                            return new NioDatagramChannel(InternetProtocolFamily.IPv4);
                        }
                    })
                    .localAddress(localAddress, groupAddress.getPort())
                    .option(ChannelOption.IP_MULTICAST_IF, ni)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) {
                            ch.pipeline().addLast(new MulticastHandler());
                        }
                    });

            NioDatagramChannel ch = (NioDatagramChannel) b.bind(groupAddress.getPort()).sync().channel();
            ch.joinGroup(groupAddress, ni).sync();
            ch.closeFuture().await();
        } catch (InterruptedException | SocketException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private class MulticastHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
            ByteBuf byteBuf = msg.content().readBytes(msg.content().readableBytes());
            var content = ByteBufUtil.prettyHexDump(byteBuf);

            var smaHeader = byteBuf.readSlice(12);
            var dataLengthBuffer = byteBuf.readSlice(2);
            byteBuf.skipBytes(4);
            var susyBuffer = byteBuf.readSlice(2);
            var sernoBuffer = byteBuf.readSlice(4);
            var tickerBuffer = byteBuf.readSlice(4);
            var channelsBuffer = byteBuf.slice();

            var ticker = tickerBuffer.readUnsignedInt();
            var susy = susyBuffer.readUnsignedShort();
            var serno = sernoBuffer.readUnsignedInt();
            var serial = String.format("%05d%010d", susy, serno);
            var dataLengh = dataLengthBuffer.readUnsignedShort();

            //channelsBuffer = channelsBuffer.slice(0,dataLengh);
            while (channelsBuffer.isReadable())
                readObis(channelsBuffer);
            System.out.println("\n\n\n");
        }

        private void readObis(ByteBuf obis) {
            var channel = obis.readUnsignedByte();
            var idx = obis.readUnsignedByte();
            var typ = obis.readUnsignedByte();
            var tariff = obis.readUnsignedByte();
            if (typ == 8) {
                var val1 = obis.readUnsignedInt();
                var val2 = obis.readUnsignedInt();
                var val = val1 * 65536 + val2;
                System.out.println("channel=" + channel + " typ=" + typ + " idx=" + idx + ": " + val / 3600000f + "KWh");
            } else if (typ == 4) {
                var value = obis.readUnsignedInt();
                System.out.println("channel=" + channel + " typ=" + typ + " idx=" + idx + ": " + value * 0.1f + "W");
            }
        }


    }

    private class MulticastHandler2 extends SimpleChannelInboundHandler<Object> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
            System.out.println("receive2 " + msg);
        }
    }

}
