package cat.joanpujol.smasolar.emeter;

import static org.assertj.core.api.Assertions.assertThat;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Decodes emeter message")
class EMeterContentDecoderTest {

  @Test
  @DisplayName("Decode correct message")
  void parseMessage() {
    var channel = new EmbeddedChannel(new EMeterContentDecoder());
    var msgDecoded = EMeterTestUtils.getCorrectMessage();
    DatagramPacket packet = new DatagramPacket(msgDecoded, new InetSocketAddress(9522));

    assertThat(channel.writeInbound(packet)).isTrue().as("Packet correctly queued");
    EMeterLecture reading = channel.readInbound();
    assertThat(reading).isNotNull().as("Decoded message must not be null");
    assertThat(reading.getDeviceServiceNumber()).isEqualTo("003491901403938");
    assertThat(reading.getTicker()).isEqualTo(2481853346L);

    assertThat(reading.getCurrentSum().getActivePower())
        .isCloseTo(634.7d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentSum().getNegativeActivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentSum().getReactivePower())
        .isCloseTo(71.1, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentSum().getNegativeReactivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentSum().getApparentPower())
        .isCloseTo(638.7d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentSum().getNegativeApparentPower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentSum().getPowerFactor())
        .isCloseTo(0.994d, Percentage.withPercentage(0.01d));

    assertThat(reading.getCurrentPhase1().getActivePower())
        .isCloseTo(634.7d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase1().getNegativeActivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase1().getReactivePower())
        .isCloseTo(71.1, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase1().getNegativeReactivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase1().getApparentPower())
        .isCloseTo(638.7d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase1().getNegativeActivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase1().getElectricCurrent())
        .isCloseTo(2.999d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase1().getVoltage())
        .isCloseTo(225.757d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase1().getPowerFactor())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));

    assertThat(reading.getCurrentPhase2().getActivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase2().getNegativeActivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase2().getReactivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase2().getNegativeReactivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase2().getApparentPower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase2().getNegativeActivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase2().getElectricCurrent())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase2().getVoltage())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase2().getPowerFactor())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));

    assertThat(reading.getCurrentPhase3().getActivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase3().getNegativeActivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase3().getReactivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase3().getNegativeReactivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase3().getApparentPower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase3().getNegativeActivePower())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase3().getElectricCurrent())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase3().getVoltage())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCurrentPhase3().getPowerFactor())
        .isCloseTo(0d, Percentage.withPercentage(0.01d));

    assertThat(reading.getCounterSum().getActivePower())
        .isCloseTo(694.7056d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterSum().getNegativeActivePower())
        .isCloseTo(15.9273d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterSum().getReactivePower())
        .isCloseTo(872.0977d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterSum().getNegativeReactivePower())
        .isCloseTo(83.8325d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterSum().getApparentPower())
        .isCloseTo(891.9797d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterSum().getNegativeActivePower())
        .isCloseTo(15.9273d, Percentage.withPercentage(0.01d));

    assertThat(reading.getCounterPhase1().getActivePower())
        .isCloseTo(694.7056d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase1().getNegativeActivePower())
        .isCloseTo(15.9273d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase1().getReactivePower())
        .isCloseTo(872.0977d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase1().getNegativeReactivePower())
        .isCloseTo(83.8325d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase1().getApparentPower())
        .isCloseTo(891.9797d, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase1().getNegativeActivePower())
        .isCloseTo(15.9273d, Percentage.withPercentage(0.01d));

    assertThat(reading.getCounterPhase2().getActivePower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase2().getNegativeActivePower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase2().getReactivePower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase2().getNegativeReactivePower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase2().getApparentPower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase2().getNegativeActivePower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));

    assertThat(reading.getCounterPhase3().getActivePower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase3().getNegativeActivePower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase3().getReactivePower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase3().getNegativeReactivePower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase3().getApparentPower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));
    assertThat(reading.getCounterPhase3().getNegativeActivePower())
        .isCloseTo(0, Percentage.withPercentage(0.01d));
  }

  @Test
  @DisplayName("Decode incorrect message")
  void parseIncorrectMessage() {
    var channel = new EmbeddedChannel(new EMeterContentDecoder());
    var msgDecoded = EMeterTestUtils.getCorrectMessage();
    msgDecoded.resetWriterIndex().writeByte(255); // Put an incorrect byte at SMA header
    DatagramPacket packet = new DatagramPacket(msgDecoded, new InetSocketAddress(9522));
    assertThat(channel.writeInbound(packet)).isTrue().as("Packet correctly queued");

    assertThat(channel.readInbound() instanceof EMeterLecture)
        .isFalse()
        .as("Invalid message must not be decoded as EMeter message");
  }
}
