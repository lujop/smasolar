package cat.joanpujol.smasolar.modbus;

import static cat.joanpujol.smasolar.modbus.ModbusAccesType.READ_ONLY;
import static cat.joanpujol.smasolar.modbus.ModbusDataFormat.*;
import static cat.joanpujol.smasolar.modbus.ModbusDataType.*;

import java.math.BigDecimal;
import java.time.Instant;

public class ModbusRegister<T> implements ModbusValue<T> {

  public static final ModbusRegister<String> PROFILE_VERSION_NUMBER =
      new ModbusRegister<>(30001, "Version number of the SMA profile", U32, RAW, READ_ONLY);
  public static final ModbusRegister<String> SUSyID =
      new ModbusRegister<>(30003, "Device ID", U32, RAW, READ_ONLY);
  public static final ModbusRegister<String> MODBUS_DATA_CHANGE_COUNTER =
      new ModbusRegister<>(
          30007,
          "Modbus data change: counter value will increase if data in the Profile has changed (overflow)",
          U32,
          RAW,
          READ_ONLY);
  public static final ModbusRegister<String> DEVICE_CLASS =
      new ModbusRegister<>(30051, "Device class", U32, ENUM, READ_ONLY);
  public static final ModbusRegister<Instant> UTC_SYSTEM_TIME_COMUNICATION_PRODUCT =
      new ModbusRegister<>(30193, "UTC system time (s)", U32, DT, READ_ONLY);

  public static final ModbusRegister<Number> TOTAL_ENERGY_FED =
      new ModbusRegister<>(
          30513, "Total energy fed in on all line conductors (in Wh)", U64, FIX0, READ_ONLY);
  public static final ModbusRegister<Number> CURRENT_ACTIVE_POWER =
      new ModbusRegister<>(
          30775, "Total active power on all line conductors (W)", S32, FIX0, READ_ONLY);

  public static final ModbusRegister<BigDecimal> PV_POWER_LIMITATION_COMUNICATION =
      new ModbusRegister<>(
          31239, "PV power limitation via communication (in %)", U32, FIX2, READ_ONLY);
  public static final ModbusRegister<BigDecimal> AMBIENT_TEMPERATURE =
      new ModbusRegister<>(34609, "Ambient temperature (ºC)", S32, TEMP, READ_ONLY);

  public static final ModbusRegister<Number> CURRENT_BATTERY_STATE_OF_CHARGE =
      new ModbusRegister<>(30845, "Current battery state of charge (%)", U32, FIX0, READ_ONLY);
  public static final ModbusRegister<Number> NOMINAL_CAPACITY_BATTERY =
      new ModbusRegister<>(40187, "Nominal capacity of the battery (Wh)", U32, FIX0, READ_ONLY);

  private int registerNumber;
  private String desription;
  private ModbusDataType dataType;
  private ModbusDataFormat<T> dataFormat;
  private ModbusAccesType accesType;
  private Class type;

  public ModbusRegister(
      int registerNumber,
      String desription,
      ModbusDataType dataType,
      ModbusDataFormat<T> dataFormat,
      ModbusAccesType accesType) {
    this.registerNumber = registerNumber;
    this.desription = desription;
    this.dataType = dataType;
    this.dataFormat = dataFormat;
    this.type = type;
    this.accesType = accesType;
  }

  @Override
  public int getRegisterNumber() {
    return registerNumber;
  }

  @Override
  public String getDesription() {
    return desription;
  }

  @Override
  public ModbusDataType getDataType() {
    return dataType;
  }

  @Override
  public ModbusDataFormat<T> getDataFormat() {
    return dataFormat;
  }

  @Override
  public ModbusAccesType getAccesType() {
    return accesType;
  }
}
