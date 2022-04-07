package com.github.joshument;

import edu.wpi.first.wpilibj.SerialPort;

/**
 * This class is used for interfacing between an Arduino and a WPI capable robot (as it uses the wpi SerialPort library).
 * The class is relatively simple, by default only adding basic pin manipulation functionality, as it's expected that you would be doing all the Arduino
 * functionality on the more powerful Robot.
 */
public class ArduinoInterface {
    public SerialPort serialPort;

    /**
     * <p>Enum for digital voltages, used for <strong>ArduinoInterface.put()</string> and <strong>ArduinoInterface.get()<strong/></p>
     * 
     * <p>Depending on the microcontroller, HIGH can be 3.3V or 5.5V.
     */
    public static enum DigitalLevel {
        LOW,
        HIGH
    }

    /**
     * Enum for pin modes, used for <strong>ArduinoInterface.pinMode()</strong>
     * 
     * @see https://docs.arduino.cc/learn/microcontrollers/digital-pins
     */
    public static enum PinMode {
        INPUT,
        OUTPUT,
        INPUT_PULLUP
    }
    /**
     * Initializes the Arduino via the specified USB port and using the specified baud rate.
     * 
     * <p>This code communicates over the USB serial port, and certain code must be uploaded to the arudino to be able to interpret these commands.
     * You can find the code for said commands at https://www.google.com or implement it yourself using the guide at https://www.google.com
     * @param baudRate
     * The baud rate of the Serial Interface
     * @param port
     * The serial port (Either USB-2.0 port on the VMX-pi) to use for interfacing
     */
    public ArduinoInterface(int baudRate, SerialPort.Port port) {
        serialPort = new SerialPort(baudRate, port);
    }

    /**
     * Sets a pin to a certain mode using the Arduino
     * @param pin
     * @param mode
     * @return
     */
    public int pinMode(int pin, PinMode mode) {
        // In case of garbage output still in Serial buffer, flush it.
        serialPort.flush();

        serialPort.writeString("VMX PINMODE " + pin + " " + mode.name());

        // wait for output
        String code = new String();
        while(true) {
            if(serialPort.getBytesReceived() > 0) {
                char tempChar = (char) serialPort.read(1)[0];
                if (tempChar == '\n') {
                    break;
                }

                code += tempChar;
            }
        }

        code = code.replace("code ", "");
        return Integer.parseInt(code);
    }

    /**
     * Writes a value to a pin using the Arduino <strong>digitalWrite()</strong> function.
     * @param pin
     * The pin to perform the operation on.
     * @param level
     * The voltage level to be applied (HIGH or LOW).
     * @return
     * The error code of the operation.
     * There are 6 possible values (although not all are implemented):
     * <ul>
     *  <li>0 = success</li>
     *  <li>1 = character allocation failure</li>
     *  <li>2 = command does not start with <strong>VMX</strong></li>
     *  <li>3 = invalid pin</li>
     *  <li>4 = invalid pin mode</li>
     *  <li>5 = invalid command</li>
     * </ul>
     * In the case of a failure error code, inspection of the Serial Monitor may be necessary to properly debug.
     * @see https://www.arduino.cc/reference/en/language/functions/digital-io/digitalwrite/
     */
    public int put(int pin, DigitalLevel level) {
        // Flush the serial port in case of garbage in the serial Bus
        serialPort.flush();

        // All serial commands start with VMX as to distinguish them from any other serial calls.
        serialPort.writeString("VMX DIGITALWRITE " + pin + " " + level.name());
        System.out.println("VMX DIGITALWRITE " + pin + " " + level.name());

        // wait for output
        String code = new String();
        while(true) {
            if(serialPort.getBytesReceived() > 0) {
                char tempChar = (char) serialPort.read(1)[0];
                if (tempChar == '\n') {
                    break;
                }

                code += tempChar;
            }
        }

        code = code.replace("code ", "");
        return Integer.parseInt(code);
    }

    /**
     * Reads the value from a pin using the Arduino <strong>digitalRead()</strong> function.
     * Pins that are not set to <strong>INPUT</strong> or <strong>INPUT_PULLUP</strong> will produce undefined results.
     * @param pin
     * The pin to perform the operation on.
     * @return
     * The voltage level of the pin (HIGH or LOW).
     * @see https://www.arduino.cc/reference/en/language/functions/digital-io/digitalread/
     */
    public DigitalLevel get(int pin) {
        // Flush the serial port in case of garbage in the serial Bus
        serialPort.flush();

        // All serial commands start with VMX as to distinguish them from any other serial calls.
        serialPort.writeString("VMX DIGITALREAD " + pin );

        // wait for output
        String power = new String();
        while(true) {
            if(serialPort.getBytesReceived() > 0) {
                char tempChar = (char) serialPort.read(1)[0];
                if (tempChar == '\n') {
                    break;
                }

                power += tempChar;
            }
        }

        if(power.startsWith("code")) {
            System.out.println("get() function failed! The Arduino returned an error code?");
        }

        return (power.equals("1")) ? DigitalLevel.HIGH : DigitalLevel.LOW;
    }

    public int putAnalog(int pin, int power) {
        // Flush the serial port in case of garbage in the serial Bus
        serialPort.flush();

        // ALl serial commands start wth VMX as to distinguish them from any other serial calls.
        serialPort.writeString("VMX ANALOGWRITE " + pin + " " + power);

        // wait for output
        String code = new String();
        while(true) {
            if(serialPort.getBytesReceived() > 0) {
                char tempChar = (char) serialPort.read(1)[0];
                if (tempChar == '\n') {
                    break;
                }

                code += tempChar;
            }
        }

        code = code.replace("code ", "");
        return Integer.parseInt(code);
    }

    public int getAnalog(int pin) {
        // Flush the serial port in case of garbage in the serial Bus
        serialPort.flush();

        // ALl serial commands start wth VMX as to distinguish them from any other serial calls.
        serialPort.writeString("VMX ANALOG " + pin );
        // wait for output
        String power = new String();
        while(true) {
            if(serialPort.getBytesReceived() > 0) {
                char tempChar = (char) serialPort.read(1)[0];
                if (tempChar == '\n') {
                    break;
                }

                power += tempChar;
            }
        }

        if(power.startsWith("code")) {
            System.out.println("getAnalog() function failed! The Arduino returned an error code?");
        }
        
        return Integer.parseInt(power);
    }
}