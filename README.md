# lujop's smasolar library   [![Build Status](https://travis-ci.com/lujop/smasolarlib.svg?branch=master)](https://travis-ci.com/lujop/smasolarlib) [![codecov](https://codecov.io/gh/lujop/smasolarlib/branch/master/graph/badge.svg)](https://codecov.io/gh/lujop/smasolarlib)
Library with interfaces to some sma devices.
This is a personal project to interactuate with some SMA devices that I've at home.

This project is absolutely unofficial and neither the author nor the application has any relationship with SMA Solar.

The initial scope of the project is to have interface to operate with:
- Read energy meter lectures using Energy meter broadcast protocol
- Read values from Sunny Boy inverter, Sunny Boy Storage, and Data Manager M using modbus

# Basic usage example

## EMeter
Read broadcasted emeter's lectures indefinitely:

    EMeterConfig eMeterConfig = EMeterConfig.newBuilder().build();
    new EMeterReader(eMeterConfig).create().subscribe(lecture -> {
     System.out.println("Readed lecture "+lecture);
    });
  
## Modbus
Read several register from SMA device. Library automatically do several requests and join them if registers can't be read in a single request:

    SmaModbusClient client = new SmaModbusClient("192.168.1.1", 502, device);
    SmaModbusRequest request =
        new SmaModbusRequest.Builder(SmaModbusRequest.Type.READ)
          .addRegister(ModbusRegister.NOMINAL_CAPACITY_BATTERY)
          .addRegister(ModbusRegister.MAXIMUM_CHARGE_BATTERY_POWER)
          .addRegister(ModbusRegister.MAXIMUM_DISCHARGE_BATTERY_POWER)
          .addRegister(ModbusRegister.CURRENT_BATTERY_STATE_OF_CHARGE)
          .build();
    client.read(request).subscribe(response -> {
      response.getAllRegisters().forEach( (reg,val)-> System.out.println(""+reg+":"+val));
    }
           
  
