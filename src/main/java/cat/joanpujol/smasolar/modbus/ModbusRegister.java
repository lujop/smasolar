package cat.joanpujol.smasolar.modbus;

import static cat.joanpujol.smasolar.modbus.ModbusAccesType.READ_ONLY;
import static cat.joanpujol.smasolar.modbus.ModbusDataFormat.*;
import static cat.joanpujol.smasolar.modbus.ModbusDataType.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * SMA modbus register
 *
 * @param <T>
 */
public class ModbusRegister<T> {

  public static final ModbusRegister<String> PROFILE_VERSION_NUMBER =
      new ModbusRegister<>(
          30001,
          "PROFILE_VERSION_NUMBER",
          "Version number of the SMA profile",
          U32,
          RAW,
          READ_ONLY);
  public static final ModbusRegister<String> SUSyID =
      new ModbusRegister<>(30003, "SUSyID", "Device ID", U32, RAW, READ_ONLY);
  public static final ModbusRegister<String> MODBUS_DATA_CHANGE_COUNTER =
      new ModbusRegister<>(
          30007,
          "MODBUS_DATA_CHANGE_COUNTER",
          "Modbus data change: counter value will increase if data in the Profile has changed (overflow)",
          U32,
          RAW,
          READ_ONLY);
  public static final ModbusRegister<String> DEVICE_CLASS =
      new ModbusRegister<>(30051, "DEVICE_CLASS", "Device class", U32, ENUM, READ_ONLY);
  public static final ModbusRegister<Instant> UTC_SYSTEM_TIME_COMUNICATION_PRODUCT =
      new ModbusRegister<>(
          30193, "UTC_SYSTEM_TIME_COMUNICATION_PRODUCT", "UTC system time (s)", U32, DT, READ_ONLY);

  public static final ModbusRegister<Number> TOTAL_ENERGY_FED =
      new ModbusRegister<>(
          30513,
          "TOTAL_ENERGY_FED",
          "Total energy fed in on all line conductors (in Wh)",
          U64,
          FIX0,
          READ_ONLY);
  public static final ModbusRegister<Number> CURRENT_ACTIVE_POWER =
      new ModbusRegister<>(
          30775,
          "CURRENT_ACTIVE_POWER",
          "Total active power on all line conductors (W)",
          S32,
          FIX0,
          READ_ONLY);

  public static final ModbusRegister<BigDecimal> PV_POWER_LIMITATION_COMUNICATION =
      new ModbusRegister<>(
          31239,
          "PV_POWER_LIMITATION_COMUNICATION",
          "PV power limitation via communication (in %)",
          U32,
          FIX2,
          READ_ONLY);
  public static final ModbusRegister<BigDecimal> AMBIENT_TEMPERATURE =
      new ModbusRegister<>(
          34609, "AMBIENT_TEMPERATURE", "Ambient temperature (ºC)", S32, TEMP, READ_ONLY);

  public static final ModbusRegister<Number> CURRENT_BATTERY_STATE_OF_CHARGE =
      new ModbusRegister<>(
          30845,
          "CURRENT_BATTERY_STATE_OF_CHARGE",
          "Current battery state of charge (%)",
          U32,
          FIX0,
          READ_ONLY);
  public static final ModbusRegister<Number> NOMINAL_CAPACITY_BATTERY =
      new ModbusRegister<>(
          40187,
          "NOMINAL_CAPACITY_BATTERY",
          "Nominal capacity of the battery (Wh)",
          U32,
          FIX0,
          READ_ONLY);

  private String name;
  private int registerNumber;
  private String desription;
  private ModbusDataType dataType;
  private ModbusDataFormat<T> dataFormat;
  private ModbusAccesType accesType;
  private Class type;

  public ModbusRegister(
      int registerNumber,
      String name,
      String desription,
      ModbusDataType dataType,
      ModbusDataFormat<T> dataFormat,
      ModbusAccesType accesType) {
    this.registerNumber = registerNumber;
    this.name = name;
    this.desription = desription;
    this.dataType = dataType;
    this.dataFormat = dataFormat;
    this.type = type;
    this.accesType = accesType;
  }

  public int getRegisterNumber() {
    return registerNumber;
  }

  public String getName() {
    return name;
  }

  public String getDesription() {
    return desription;
  }

  public ModbusDataType getDataType() {
    return dataType;
  }

  public ModbusDataFormat<T> getDataFormat() {
    return dataFormat;
  }

  public ModbusAccesType getAccesType() {
    return accesType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ModbusRegister<?> register = (ModbusRegister<?>) o;
    return Objects.equals(name, register.name);
  }

  @Override
  public int hashCode() {

    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return name;
  }
}
