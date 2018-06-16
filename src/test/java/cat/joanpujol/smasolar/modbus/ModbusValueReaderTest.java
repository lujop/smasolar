package cat.joanpujol.smasolar.modbus;

import static cat.joanpujol.smasolar.modbus.ModbusDataFormat.*;
import static cat.joanpujol.smasolar.modbus.ModbusDataType.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ModbusValueReaderTest {

  private ModbusValueReader instance;

  @BeforeEach
  void init() {
    instance = new ModbusValueReader();
  }

  @AfterEach
  void clean() {
    instance = null;
  }

  @Test
  @DisplayName("Check null values are correctly read as null")
  void readNullValues() {
    assertThat(instance.read(decodeHexDump("8000"), S16, RAW)).isNull();
    assertThat(instance.read(decodeHexDump("80000000"), S32, RAW)).isNull();
    assertThat(instance.read(Unpooled.wrappedBuffer(new byte[32]), STR32, RAW)).isNull();
    assertThat(instance.read(decodeHexDump("ffff"), U16, RAW)).isNull();
    assertThat(instance.read(decodeHexDump("ffffffff"), U32, RAW)).isNull();
    assertThat(instance.read(decodeHexDump("00fffffd"), U32_STATUS, RAW)).isNull();
    assertThat(instance.read(decodeHexDump("ffffffffffffffff"), U64, RAW)).isNull();
  }

  @Test
  @DisplayName("Read DT")
  void readDT() {
    var instant = instance.read(decodeHexDump("5b2423d8"), U32, DT);
    assertThat(instant).isEqualTo("2018-06-15T20:38:48Z");
  }

  @Test
  @DisplayName("Read TM")
  void readTM() {
    var instant =
        instance.read(
            decodeHexDump("5b2423d8"), U32, TM); // It's the same as DT?, not able to check it
    assertThat(instant).isEqualTo("2018-06-15T20:38:48Z");
  }

  @Test
  @DisplayName("Read ENUM")
  void readENUM() {
    // 8128 is Device class enum value for Communication products
    var instant = instance.read(decodeHexDump("00001fc0"), U32, ENUM);
    assertThat(instant).isEqualTo("8128");
  }

  @Test
  @DisplayName("Read FIX0")
  void readFIX0() {
    assertThat(instance.read(decodeHexDump("00002648"), U32, FIX0).intValue()).isEqualTo(9800);
    assertThat(instance.read(decodeHexDump("ffffff00"), U32, FIX0).longValue())
        .isEqualTo(4294967040L);
  }

  @Test
  @DisplayName("Read FIX1")
  void readFIX1() {
    assertThat(instance.read(decodeHexDump("00017ED1"), U32, FIX1)).isEqualTo("9800.1");
    assertThat(instance.read(decodeHexDump("ffffff01"), U32, FIX1)).isEqualTo("429496704.1");
  }

  @Test
  @DisplayName("Read FIX2")
  void readFIX2() {
    assertThat(instance.read(decodeHexDump("00017ED1"), U32, FIX2)).isEqualTo("980.01");
    assertThat(instance.read(decodeHexDump("ffffff01"), U32, FIX2)).isEqualTo("42949670.41");
  }

  @Test
  @DisplayName("Read FIX3")
  void readFIX3() {
    assertThat(instance.read(decodeHexDump("00017ED1"), U32, FIX3)).isEqualTo("98.001");
    assertThat(instance.read(decodeHexDump("ffffff01"), U32, FIX3)).isEqualTo("4294967.041");
  }

  @Test
  @DisplayName("Read FIX4")
  void readFIX4() {
    assertThat(instance.read(decodeHexDump("00017ED1"), U32, FIX4)).isEqualTo("9.8001");
    assertThat(instance.read(decodeHexDump("ffffff01"), U32, FIX4)).isEqualTo("429496.7041");
  }

  @Test
  @DisplayName("Read FW version")
  void readFW() {
    assertThat(instance.read(decodeHexDump("02041704"), U32, FW))
        .isEqualTo(new FirmwareVersion(2, 4, 23, FirmwareVersion.ReleaseType.R));
    assertThat(instance.read(decodeHexDump("13130004"), U32, FW))
        .isEqualTo(new FirmwareVersion(13, 13, 0, FirmwareVersion.ReleaseType.R));
  }

  @Test
  @DisplayName("Read HW version")
  void readHW() {
    assertThat(instance.read(decodeHexDump("00000001"), U32, HW).longValue()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Read IPV4 version")
  void readIPV4() throws UnknownHostException {
    assertThat(
            instance.read(
                decodeHexDump("3139322e3136382e302e3137300000003235352e3235352e3235352e30000000"),
                STR32,
                IP4))
        .isEqualTo(Inet4Address.getByName("192.168.0.170"));
  }

  @Test
  @DisplayName("Read RAW value")
  void readRAW() {
    assertThat(instance.read(decodeHexDump("714f0850"), U32, RAW)).isEqualTo("1901004880");
  }

  @Test
  @DisplayName("Read Temp")
  void readTemp() {
    assertThat(instance.read(decodeHexDump("00017ED1"), S32, TEMP)).isEqualTo("9800.1");
  }

  @Test
  @DisplayName("Read Time")
  void readTime() {
    assertThat(instance.read(decodeHexDump("00017ED1"), S32, TEMP)).isEqualTo("9800.1");
  }

  @Test
  @DisplayName("Read UTF8")
  void readUTF8Data() throws UnknownHostException {
    assertThat(
            instance.read(
                decodeHexDump("30303a34303a41443a39433a43313a3243000000000000000000000000000000"),
                STR32,
                UTF8))
        .isEqualTo("00:40:AD:9C:C1:2C");
  }

  private static ByteBuf decodeHexDump(String hexdumpValue) {
    var s16null = ByteBufUtil.decodeHexDump(hexdumpValue);
    return Unpooled.wrappedBuffer(s16null);
  }
}
