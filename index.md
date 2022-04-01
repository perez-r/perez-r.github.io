# Ruben Perez
## _Contact_

e-mail: ruben.perez@snhu.edu 

# Portfolio Artifact One

### [HVAC State Machine](https://github.com/perez-r/perez-r.github.io/blob/main/gpiointerrupt.c)
Using a microcontroller, this program is an HVAC control system that senses senses temperature and enables or disables heating or cooling system depending on the reading. Uses GPIO inputs to control temperature settings and utilizes UART and I2C for communications communications. 


```markdown

/* Snippet of the main function */

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
```
