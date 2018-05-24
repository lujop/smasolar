package cat.joanpujol.smasolar.emeter;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import java.net.*;
import java.util.Enumeration;

/**
 * Subscribes to EMETER multicast group and contiously retrieve and prints to console it's lectures
 */
public class EMeterReader {
  private InetSocketAddress groupAddress;

  private EMeterReader(InetSocketAddress groupAddress) {
    this.groupAddress = groupAddress;
  }

  public static void main(String[] args) {
    InetSocketAddress groupAddress = new InetSocketAddress("239.12.255.254", 9522);
    new EMeterReader(groupAddress).run();
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

      Bootstrap b =
          new Bootstrap()
              .group(group)
              .channelFactory(
                  new ChannelFactory<NioDatagramChannel>() {
                    @Override
                    public NioDatagramChannel newChannel() {
                      return new NioDatagramChannel(InternetProtocolFamily.IPv4);
                    }
                  })
              .localAddress(localAddress, groupAddress.getPort())
              .option(ChannelOption.IP_MULTICAST_IF, ni)
              .option(ChannelOption.SO_REUSEADDR, true)
              .handler(
                  new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    public void initChannel(NioDatagramChannel ch) {
                      ch.pipeline()
                          .addLast(new EMeterContentDecoder())
                          .addLast(new EMeterReadingProcessor());
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

  class EMeterReadingProcessor extends SimpleChannelInboundHandler<EMeterLecture> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EMeterLecture msg) throws Exception {
      System.out.println(msg);
    }
  }
}
