package cat.joanpujol.smasolar.modbus;

public interface ModbusValue<T> {
  int getRegisterNumber();

  String getDesription();

  ModbusDataType getDataType();

  ModbusDataFormat<T> getDataFormat();

  ModbusAccesType getAccesType();
}
