/*
 *  FILENAME:       gpiointerrupt.c 
 *  
 *  DESCRIPTION: 
 *      HVAC Control System called by the main function.
 *
 *      This program is a time-based state machine with
 *      a full cycle occurring every one minute.
 *      Events occur at each 0.5 second mark and 1.0
 *      second mark.
 *
 *  AUTHOR INFO: 
 *
 *      NAME: RUBEN PEREZ   DATE: 03/21/2022
 *
 */

/*
 * Copyright (c) 2015-2020, Texas Instruments Incorporated
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * *  Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * *  Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * *  Neither the name of Texas Instruments Incorporated nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include <stdint.h>
#include <stddef.h>
#include <ti/drivers/GPIO.h>
#include "ti_drivers_config.h"
#include <ti/drivers/I2C.h>
#include <ti/drivers/UART.h>
#include <ti/drivers/Timer.h>

#define DISPLAY(x) UART_write(uart, &output, x);

// Global Variables
char output[64];
int bytesToSend;
unsigned int state;
signed int setPoint;
signed int temperature;
unsigned int seconds;
signed char hvacState;
volatile unsigned char TimerFlag = 0;

// Driver Handles - Global variables
UART_Handle uart;
Timer_Handle timer0;

// UART initialization function
void initUART(void)
{
    UART_Params uartParams;
    // Init the driver
    UART_init();
    // Configure the driver
    UART_Params_init(&uartParams);
    uartParams.writeDataMode = UART_DATA_BINARY;
    uartParams.readDataMode = UART_DATA_BINARY;
    uartParams.readReturnMode = UART_RETURN_FULL;
    uartParams.baudRate = 115200;
    // Open the driver
    uart = UART_open(CONFIG_UART_0, &uartParams);
    if (uart == NULL) {
            /* UART_open() failed */
            while (1);
    }
}

// Timer initialization function
void initTimer(void)
{
    Timer_Params params;
    // Driver Initialization
    Timer_init();
    // Driver Configuration
    Timer_Params_init(&params);
    params.period = 100000;
    params.periodUnits = Timer_PERIOD_US;
    params.timerMode = Timer_CONTINUOUS_CALLBACK;
    params.timerCallback = timerCallback;
    // Open the driver
    timer0 = Timer_open(CONFIG_TIMER_0, &params);
    if (timer0 == NULL) {
        /* Failed to initialized timer */
        while (1) {}
    }
    if (Timer_start(timer0) == Timer_STATUS_ERROR) {
        /* Failed to start timer */
        while (1) {}
    }
}

/*
 * Timer flag callback. Used by state machine
 */
void timerCallback(Timer_Handle myHandle, int_fast16_t status)
{
    TimerFlag = 1;
}

/*
 *
 *
 *  GPIO Button 0 Callback
 *
 *  Function to be called on GPIO Button 0 presses.
 *  In this instance, decreases the user setting
 *  for temperature.
 *
 *
 */
void gpioButtonFxn0(uint_least8_t index)
{
    // Decrease Set Temperature
    setPoint--;
}

/*
 *
 *
 *  GPIO Button 1 Callback
 *
 *  Function to be called on GPIO Button 1 presses.
 *  In this instance, increases the user setting
 *  for temperature.
 *
 *
 */
void gpioButtonFxn1(uint_least8_t index)
{
    // Increase Set Temperature
    setPoint++;
}


/*
 * I2C Global Variables
 *
 * These variables are used by the I2C serial
 * communication which is how the temperature
 * sensor communicates with the program.
 *
 */
static const struct {
    uint8_t address;
    uint8_t resultReg;
    char *id;
}

sensors[3] = {
                { 0x48, 0x0000, "11X" },
                { 0x49, 0x0000, "116" },
                { 0x41, 0x0001, "006" }
};


uint8_t txBuffer[1];
uint8_t rxBuffer[2];
I2C_Transaction i2cTransaction;

/*
 * I2c Driver Handle
 */
I2C_Handle i2c;

/*
 * Initialization of the I2C and Sensor
 *
 * This code initializes the sensor and the
 * serial communication port so that it can
 * communicate with the program and the logic
 * dictate the operation of the program.
 *
 */
void initI2C(void)
{
    int8_t i, found;
    I2C_Params i2cParams;
    DISPLAY(snprintf(output, 64, "Initializing I2C Driver - "))
    // Init the driver
    I2C_init();
    // Configure the driver
    I2C_Params_init(&i2cParams);
    i2cParams.bitRate = I2C_400kHz;
    // Open the driver
    i2c = I2C_open(CONFIG_I2C_0, &i2cParams);
    if (i2c == NULL)
    {
        DISPLAY(snprintf(output, 64, "Failed\n\r"))
         while (1);
    }
    DISPLAY(snprintf(output, 32, "Passed\n\r"))
    // Boards were shipped with different sensors.
    // Welcome to the world of embedded systems.
    // Try to determine which sensor we have.
    // Scan through the possible sensor addresses
    /* Common I2C transaction setup */
    i2cTransaction.writeBuf = txBuffer;
    i2cTransaction.writeCount = 1;
    i2cTransaction.readBuf = rxBuffer;
    i2cTransaction.readCount = 0;
    found = false;
    for (i=0; i<3; ++i)
    {
        i2cTransaction.slaveAddress = sensors[i].address;
        txBuffer[0] = sensors[i].resultReg;
        DISPLAY(snprintf(output, 64, "Is this %s? ", sensors[i].id))
        if (I2C_transfer(i2c, &i2cTransaction))
        {
            DISPLAY(snprintf(output, 64, "Found\n\r"))
         found = true;
            break;
        }
        DISPLAY(snprintf(output, 64, "No\n\r"))
    }
    if(found)
    {
        DISPLAY(snprintf(output, 64, "Detected TMP%s I2C address: %x\n\r", sensors[i].id, i2cTransaction.slaveAddress))
    }
    else
    {
        DISPLAY(snprintf(output, 64, "Temperature sensor not found, contact professor\n\r"))
    }
}

/*
 *
 *
 * I2C Temperature Reading
 *
 * Returns the value for the current temperature reading
 * from the I2C sensor. Temperature value returned in degrees Celsius.
 * Called at 0.5 and 1.0 seconds in the state machine.
 *
 *
 */
int16_t readTemp(void)
{
    int j;
    int16_t temperature = 0;
    i2cTransaction.readCount = 2;
    if (I2C_transfer(i2c, &i2cTransaction))
    {
        /*
         * Extract degrees C from the received data;
         * see TMP sensor datasheet
         */
        temperature = (rxBuffer[0] << 8) | (rxBuffer[1]);
        temperature *= 0.0078125;
        /*
         * If the MSB is set '1', then we have a 2's complement
         * negative value which needs to be sign extended
         */
        if (rxBuffer[0] & 0x80)
        {
            temperature |= 0xF000;
        }
    }
    else
    {
        DISPLAY(snprintf(output, 64, "Error reading temperature sensor (%d)\n\r",i2cTransaction.status))
        DISPLAY(snprintf(output, 64, "Please power cycle your board by unplugging USB and plugging back in.\n\r"))
    }
    return temperature;
}

/*
 *
 * HVAC Status Function
 *
 * Reads the current temperature from I2C,
 * then determines whether to enable heating,
 * cooling or power down to stand-by mode to conserve energy.
 *
 * States:
 * -1: Heating mode enabled
 *  0: Stand-by mode
 *  1: Cooling mode enabled
 *
 *
 */
void TempCallback()
{
    temperature = readTemp();

    if (temperature < setPoint) {
        hvacState = -1;
        GPIO_write(CONFIG_GPIO_LED_0, CONFIG_GPIO_LED_ON);
    }
    else if (temperature > setPoint ) {
        hvacState = 1;
        GPIO_write(CONFIG_GPIO_LED_0, CONFIG_GPIO_LED_ON);
     }
    else {
        hvacState = 0;
        GPIO_write(CONFIG_GPIO_LED_0, CONFIG_GPIO_LED_OFF);
    }
}

/*
 *
 * UART Output Callback Function
 *
 * Function called every second to output the status of the system.
 * Outputs the current temperature, set temperature,
 * the state of the climate control system (-1 heat, 0 off, 1 cool),
 * and system up-time in seconds.
 *
 *
 */
void UARTCallback()
{
    DISPLAY(snprintf(output, 64, "<%02d,%02d,%d,%04d>\n\r", temperature, setPoint, hvacState, seconds))
    UART_write(uart, &output, bytesToSend);
}


/*
 *
 *  Main Thread
 *
 *  Main function called by main_nortos.c. Represents the
 *  main programs functionality, containing the state machine.
 *
 *
 */
void *mainThread(void *arg0)
{
    /* Call driver init functions */
    GPIO_init();

    /* Configure the LED and button pins */
    GPIO_setConfig(CONFIG_GPIO_LED_0, GPIO_CFG_OUT_STD | GPIO_CFG_OUT_LOW);



    GPIO_setConfig(CONFIG_GPIO_BUTTON_0, GPIO_CFG_IN_PU | GPIO_CFG_IN_INT_FALLING);

    /* Install Button callback */
    GPIO_setCallback(CONFIG_GPIO_BUTTON_0, gpioButtonFxn0);

    /* Enable interrupts */
    GPIO_enableInt(CONFIG_GPIO_BUTTON_0);

    /*
     *  If more than one input pin is available for your device, interrupts
     *  will be enabled on CONFIG_GPIO_BUTTON1.
     */
    if (CONFIG_GPIO_BUTTON_0 != CONFIG_GPIO_BUTTON_1) {
        /* Configure BUTTON1 pin */
        GPIO_setConfig(CONFIG_GPIO_BUTTON_1, GPIO_CFG_IN_PU | GPIO_CFG_IN_INT_FALLING);

        /* Install Button callback */
        GPIO_setCallback(CONFIG_GPIO_BUTTON_1, gpioButtonFxn1);
        GPIO_enableInt(CONFIG_GPIO_BUTTON_1);
    }

    // Variable initializations
    hvacState = 0;
    state = 0;
    setPoint = 29;
    seconds = 0;

    // Peripheral initializations
    initUART();
    initI2C();
    initTimer();

    /*
     *
     * State Machine Loop
     *
     * At 0.0 seconds (state == 0), temperature checked and setting updated, status output
     * every 0.5 seconds (state == 5), temperature checked and setting updated
     * every 1.0 second (state == 10), temperature checked and settings updated, status output, state reset to begin cycle again
     *
     *
     */
    while (1) {
        state++;
        if (state == 5) {
            TempCallback();
        }
        else if (state == 10) {
            seconds++;
            UARTCallback();
            TempCallback();
            state = 0;
        }

        else if (state == 0) {
            UARTCallback();
            TempCallback();
        }

        while (!TimerFlag) {}
        TimerFlag = 0;
    }

    return (NULL);
}
