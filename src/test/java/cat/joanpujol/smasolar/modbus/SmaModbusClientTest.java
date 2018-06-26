package cat.joanpujol.smasolar.modbus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cat.joanpujol.smasolar.modbus.devices.DefaultSmaModbusDevice;
import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.requests.ReadInputRegistersRequest;
import com.digitalpetri.modbus.responses.ReadInputRegistersResponse;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SmaModbusClientTest {
  private SmaModbusClient client;
  private ModbusTcpMaster internalModbusClient;
  private SmaModbusDevice device;

  @BeforeEach
  void init() {
    internalModbusClient = mock(ModbusTcpMaster.class);
    device = new DefaultSmaModbusDevice();
    client =
        new SmaModbusClient("192.168.1.1", 502, device) {
          @Override
          protected ModbusTcpMaster createModbusClient() {
            return internalModbusClient;
          }
        };
  }

  @AfterEach
  void clean() {
    client = null;
    internalModbusClient = null;
    device = null;
  }

  @Test
  void testNonAtomicRequest() {
    SmaModbusRequest request =
        new SmaModbusRequest.Builder(SmaModbusRequest.Type.READ)
            .addRegister(ModbusRegister.NOMINAL_CAPACITY_BATTERY)
            .addRegister(ModbusRegister.MAXIMUM_CHARGE_BATTERY_POWER)
            .addRegister(ModbusRegister.MAXIMUM_DISCHARGE_BATTERY_POWER)
            .addRegister(ModbusRegister.CURRENT_BATTERY_STATE_OF_CHARGE)
            .build();

    SmaModbusRequest subrequest1 = request.subdivideInAtomicRequests().get(0);
    SmaModbusRequest subrequest2 = request.subdivideInAtomicRequests().get(1);

    ReadInputRegistersRequest modbussubreq1 =
        argThat(
            (ReadInputRegistersRequest req) ->
                req != null && req.getAddress() == subrequest1.getFirstRegisterNumber());
    var resp1 = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump("00000000"));
    when(internalModbusClient.sendRequest(modbussubreq1, anyInt()))
        .thenReturn(CompletableFuture.completedFuture(new ReadInputRegistersResponse(resp1)));

    var resp2 = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump("00002648000009c400000a5a"));
    ReadInputRegistersRequest modbussubreq2 =
        argThat(
            (ReadInputRegistersRequest req) ->
                req != null && req.getAddress() == subrequest2.getFirstRegisterNumber());
    when(internalModbusClient.sendRequest(modbussubreq2, anyInt()))
        .thenReturn(CompletableFuture.completedFuture(new ReadInputRegistersResponse(resp2)));

    var testObserver = client.read(request).test();
    testObserver.assertComplete();
    assertThat(testObserver.valueCount()).isOne();

    SmaModbusResponse response = testObserver.values().get(0);
    assertThat(response.getRegisterValue(ModbusRegister.NOMINAL_CAPACITY_BATTERY)).isEqualTo(9800L);
    assertThat(response.getRegisterValue(ModbusRegister.CURRENT_BATTERY_STATE_OF_CHARGE))
        .isEqualTo(0L);
    assertThat(response.getRegisterValue(ModbusRegister.MAXIMUM_CHARGE_BATTERY_POWER))
        .isEqualTo(2500L);
    assertThat(response.getRegisterValue(ModbusRegister.MAXIMUM_DISCHARGE_BATTERY_POWER))
        .isEqualTo(2650L);
  }
}
