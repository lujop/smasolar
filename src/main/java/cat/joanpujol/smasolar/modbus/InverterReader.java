package cat.joanpujol.smasolar.modbus;

import static cat.joanpujol.smasolar.modbus.ModbusAccesType.READ_ONLY;
import static cat.joanpujol.smasolar.modbus.ModbusDataFormat.*;
import static cat.joanpujol.smasolar.modbus.ModbusDataType.*;

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

    read(new ModbusRegister(30051, "Device class", U32, ENUM, READ_ONLY), masterDataManager, 1);
    read(
        new ModbusRegister(40037, "Nominal battery voltage", U32, FIX0, READ_ONLY),
        masterStorage,
        3);
    read(new ModbusRegister(30059, "Software version", U32, FW, READ_ONLY), masterStorage, 3);
    read(
        new ModbusRegister(31389, "Software version battert system", U32, FW, READ_ONLY),
        masterStorage,
        3);
    read(new ModbusRegister(41205, "HW", U32, HW, READ_ONLY), masterStorage, 3);
    read(new ModbusRegister(40159, "IPV4", STR32, IP4, READ_ONLY), masterStorage, 3);
    read(new ModbusRegister(30005, "RAW serial number", U32, RAW, READ_ONLY), masterStorage, 3);
    read(new ModbusRegister(30849, "Battery temp", S32, TEMP, READ_ONLY), masterStorage, 3);
    read(new ModbusRegister(30953, "Battery temp2", S32, TEMP, READ_ONLY), masterStorage, 3);
    read(
        new ModbusRegister(40649, "Time of automatic update", U32, TM, READ_ONLY),
        masterStorage,
        3);
    read(
        new ModbusRegister(40669, "Time of automatic update", U32, TM, READ_ONLY),
        masterStorage,
        3);
    read(
        new ModbusRegister(40671, "Time of automatic update", U32, TM, READ_ONLY),
        masterStorage,
        3);
    read(
        new ModbusRegister(40687, "Time of automatic update", U32, TM, READ_ONLY),
        masterStorage,
        3);
    read(
        new ModbusRegister(40761, "Time of automatic update", U32, TM, READ_ONLY),
        masterStorage,
        3);
    read(
        new ModbusRegister(40497, "Battery serial number", STR32, UTF8, READ_ONLY),
        masterStorage,
        3);
  }

  private static void read(ModbusValue reg, ModbusTcpMaster master, int unitId) {
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
