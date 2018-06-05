package cat.joanpujol.smasolar.emeter;

import cat.joanpujol.smasolar.emeter.impl.EMeterCreateObservableImpl;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Decodes emeter message")
class EMeterReaderTest {

  private EMeterReader instance;
  private EmbeddedChannel channel;

  @BeforeEach
  void init() {
    EMeterConfig eMeterConfig = EMeterConfig.newBuilder().build();
    instance =
        new EMeterReader(eMeterConfig) {
          @Override
          protected EMeterCreateObservableImpl createObservable() {
            return new EMeterCreateObservableImpl(getConfig()) {
              @Override
              protected AbstractChannel createChannel(
                  EMeterConfig config, SimpleChannelInboundHandler<EMeterLecture> processor)
                  throws SocketException, InterruptedException {
                channel =
                    new EmbeddedChannel(
                        new EMeterContentDecoder(),
                        processor,
                        new LoggingHandler(LogLevel.INFO),
                        new SimpleChannelInboundHandler<Object>() {
                          @Override
                          protected void channelRead0(ChannelHandlerContext ctx, Object msg)
                              throws Exception {
                            System.out.println(
                                "Unprocessable message received (previously logged with DEBUG level)");
                          }
                        });
                return channel;
              }
            };
          }
        };
  }

  @AfterEach
  void clean() {
    instance = null;
    channel = null;
  }

  @Test
  @DisplayName("Receive a correct message")
  void receiveOneMessage() {
    Observable<EMeterLecture> observable = instance.create();
    TestObserver<EMeterLecture> testObserver = observable.test();

    var msgDecoded = EMeterTestUtils.getCorrectMessage();
    DatagramPacket packet = new DatagramPacket(msgDecoded, new InetSocketAddress(9522));
    channel.writeInbound(packet);

    testObserver.assertSubscribed();
    testObserver.assertValueCount(1);
    testObserver.assertValue(
        lecture -> Objects.equals(lecture.getDeviceServiceNumber(), "003491901403938"));
  }

  @Test
  @DisplayName("Observable is properly closed")
  void properlyClosed() {
    Observable<EMeterLecture> observable = instance.create();
    TestObserver<EMeterLecture> testObserver = observable.test();

    testObserver.assertSubscribed();
    testObserver.assertNotTerminated();
    channel.close();
    testObserver.assertTerminated();
  }

  @Test
  @DisplayName("Read multiple messages")
  void receiveMultipleMessages() {
    Observable<EMeterLecture> observable = instance.create();
    TestObserver<EMeterLecture> testObserver = observable.test();

    channel.writeInbound(
        new DatagramPacket(EMeterTestUtils.getCorrectMessage(), new InetSocketAddress(9522)));
    channel.writeInbound(
        new DatagramPacket(EMeterTestUtils.getCorrectMessage(), new InetSocketAddress(9522)));
    channel.writeInbound(
        new DatagramPacket(EMeterTestUtils.getCorrectMessage(), new InetSocketAddress(9522)));

    testObserver.assertSubscribed();
    testObserver.assertValueCount(3);
  }
}
