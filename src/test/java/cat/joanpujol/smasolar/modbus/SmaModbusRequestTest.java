package cat.joanpujol.smasolar.modbus;

import static cat.joanpujol.smasolar.modbus.ModbusDataFormat.FIX0;
import static cat.joanpujol.smasolar.modbus.ModbusDataType.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SmaModbusRequestTest {
  private SmaModbusRequest singleRegisterRequest;
  private SmaModbusRequest multipleRegisterAtomicRequest;
  private SmaModbusRequest multipleRegisterNonAtomicRequest;

  @BeforeEach
  void init() {
    singleRegisterRequest =
        new SmaModbusRequest.Builder(SmaModbusRequest.Type.READ)
            .addRegister(ModbusRegister.NOMINAL_CAPACITY_BATTERY)
            .build();
    multipleRegisterAtomicRequest =
        new SmaModbusRequest.Builder(SmaModbusRequest.Type.READ)
            .addRegister(ModbusRegister.NOMINAL_CAPACITY_BATTERY)
            .addRegister(ModbusRegister.MAXIMUM_CHARGE_BATTERY_POWER)
            .addRegister(ModbusRegister.MAXIMUM_DISCHARGE_BATTERY_POWER)
            .build();
    multipleRegisterNonAtomicRequest =
        new SmaModbusRequest.Builder(SmaModbusRequest.Type.READ)
            .addRegister(ModbusRegister.NOMINAL_CAPACITY_BATTERY)
            .addRegister(ModbusRegister.MAXIMUM_CHARGE_BATTERY_POWER)
            .addRegister(ModbusRegister.MAXIMUM_DISCHARGE_BATTERY_POWER)
            .addRegister(ModbusRegister.CURRENT_BATTERY_STATE_OF_CHARGE)
            .build();
  }

  @AfterEach
  void clean() {
    singleRegisterRequest = null;
    multipleRegisterAtomicRequest = null;
    multipleRegisterNonAtomicRequest = null;
  }

  @Test
  @DisplayName("Check request atomicity")
  void testAtomicRequests() {
    assertThat(singleRegisterRequest.isAtomic());
    assertThat(singleRegisterRequest.subdivideInAtomicRequests()).hasSize(1);

    assertThat(multipleRegisterAtomicRequest.isAtomic()).isTrue();
    assertThat(multipleRegisterAtomicRequest.subdivideInAtomicRequests()).hasSize(1);

    assertThat(multipleRegisterNonAtomicRequest.isAtomic()).isFalse();
    assertThat(multipleRegisterNonAtomicRequest.subdivideInAtomicRequests().size())
        .isGreaterThan(1);
  }

  @Test
  @DisplayName("Check request atomicity edge cases")
  void testAtomicRequestsEdgeCases() {
    var atomicEdgeCase1 =
        new SmaModbusRequest.Builder(SmaModbusRequest.Type.READ)
            .addRegister(
                new ModbusRegister(0, "REG1", "desc", U16, FIX0, ModbusAccesType.READ_ONLY))
            .addRegister(
                new ModbusRegister(
                    0 + SmaModbusRequest.READ_MAX_REGISTERS_NUMBER - 1,
                    "REG1",
                    "desc",
                    U16,
                    FIX0,
                    ModbusAccesType.READ_ONLY))
            .build();
    assertThat(atomicEdgeCase1.isAtomic()).isTrue();
    var atomicEdgeCase2 =
        new SmaModbusRequest.Builder(SmaModbusRequest.Type.READ)
            .addRegister(
                new ModbusRegister(0, "REG1", "desc", U32, FIX0, ModbusAccesType.READ_ONLY))
            .addRegister(
                new ModbusRegister(
                    0 + SmaModbusRequest.READ_MAX_REGISTERS_NUMBER - 2,
                    "REG1",
                    "desc",
                    U32,
                    FIX0,
                    ModbusAccesType.READ_ONLY))
            .build();
    assertThat(atomicEdgeCase2.isAtomic()).isTrue();
    var nonAtomicEdgeCase =
        new SmaModbusRequest.Builder(SmaModbusRequest.Type.READ)
            .addRegister(
                new ModbusRegister(0, "REG1", "desc", U32, FIX0, ModbusAccesType.READ_ONLY))
            .addRegister(
                new ModbusRegister(
                    0 + SmaModbusRequest.READ_MAX_REGISTERS_NUMBER - 1,
                    "REG1",
                    "desc",
                    U32,
                    FIX0,
                    ModbusAccesType.READ_ONLY))
            .build();
    assertThat(nonAtomicEdgeCase.isAtomic()).isFalse();
  }

  @Test
  @DisplayName("Check request division into atomic requests")
  void testSubdivideInAtomicRequests() {
    assertThat(multipleRegisterNonAtomicRequest.subdivideInAtomicRequests()).hasSize(2);

    SmaModbusRequest subrequest1 =
        multipleRegisterNonAtomicRequest.subdivideInAtomicRequests().get(0);
    SmaModbusRequest subrequest2 =
        multipleRegisterNonAtomicRequest.subdivideInAtomicRequests().get(1);
    assertThat(subrequest1.getRegisters())
        .containsExactly(ModbusRegister.CURRENT_BATTERY_STATE_OF_CHARGE);
    assertThat(subrequest2.getRegisters())
        .containsExactly(
            ModbusRegister.NOMINAL_CAPACITY_BATTERY,
            ModbusRegister.MAXIMUM_CHARGE_BATTERY_POWER,
            ModbusRegister.MAXIMUM_DISCHARGE_BATTERY_POWER);
  }

  @Test
  @DisplayName("Check request division into atomic requests")
  void testCalculateNumberOfRegistersToReadInAtomicRequest() {
    int expected =
        ModbusRegister.MAXIMUM_DISCHARGE_BATTERY_POWER.getRegisterNumber()
            - ModbusRegister.NOMINAL_CAPACITY_BATTERY.getRegisterNumber()
            + ModbusRegister.MAXIMUM_DISCHARGE_BATTERY_POWER.getDataType().getLength() / 2;
    assertThat(expected)
        .isEqualTo(multipleRegisterAtomicRequest.calculateNumberOfRegistersToReadInAtomicRequest());
  }
}
