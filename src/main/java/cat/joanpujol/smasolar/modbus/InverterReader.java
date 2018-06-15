package cat.joanpujol.smasolar.modbus;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.ReadInputRegistersRequest;
import com.digitalpetri.modbus.responses.ReadInputRegistersResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;
import java.time.Duration;

public class InverterReader {

  public static final int POWER_PURCHASED_ELECTRICITY = 30865;

  public static void main(String[] args) throws InterruptedException {
    ModbusTcpMasterConfig configDataManager =
        new ModbusTcpMasterConfig.Builder("192.168.1.114")
            .setPort(502)
            .setTimeout(Duration.ofSeconds(5))
            .build();
    ModbusTcpMaster masterDataManager = new ModbusTcpMaster(configDataManager);

    ModbusTcpMasterConfig configStorage =
        new ModbusTcpMasterConfig.Builder("192.168.1.104")
            .setPort(502)
            .setTimeout(Duration.ofSeconds(5))
            .build();
    ModbusTcpMaster masterStorage = new ModbusTcpMaster(configStorage);

    read(ModbusRegister.PV_POWER_LIMITATION_COMUNICATION, masterDataManager, 2);
    read(ModbusRegister.UTC_SYSTEM_TIME_COMUNICATION_PRODUCT, masterDataManager, 2);
    read(ModbusRegister.SUSyID, masterDataManager, 2);
    read(ModbusRegister.CURRENT_ACTIVE_POWER, masterDataManager, 2);
    read(ModbusRegister.AMBIENT_TEMPERATURE, masterDataManager, 2);
    read(ModbusRegister.DEVICE_CLASS, masterDataManager, 2);
    read(ModbusRegister.MODBUS_DATA_CHANGE_COUNTER, masterDataManager, 2);
    read(ModbusRegister.PROFILE_VERSION_NUMBER, masterDataManager, 2);
    read(ModbusRegister.TOTAL_ENERGY_FED, masterDataManager, 2);

    read(ModbusRegister.CURRENT_BATTERY_STATE_OF_CHARGE, masterStorage, 3);
    read(ModbusRegister.NOMINAL_CAPACITY_BATTERY, masterStorage, 3);
  }

  private static void read(ModbusRegister reg, ModbusTcpMaster master, int unitId) {
    var request =
        new ReadInputRegistersRequest(reg.getRegisterNumber(), reg.getDataType().getLength() / 2);
    master
        .sendRequest(request, unitId)
        .thenAccept(
            response -> {
              ByteBuf registers = ((ReadInputRegistersResponse) response).getRegisters();
              var value =
                  new ModbusValueReader().read(registers, reg.getDataType(), reg.getDataFormat());
              System.out.println(
                  reg.getDesription()
                      + ":"
                      + value
                      + " ("
                      + ByteBufUtil.hexDump(registers.resetReaderIndex())
                      + ")");
              ReferenceCountUtil.release(response);
            });
  }
}
