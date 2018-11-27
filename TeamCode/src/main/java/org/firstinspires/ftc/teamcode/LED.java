/*
 *  This class defines and encapsulates everything around LED lights
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoControllerEx;

public class LED {

    public Servo led1 = null;
    public Servo led2 = null;
    private HardwareMap hwMap = null;

    public LED() {}

    public void init (HardwareMap ahwMap) {

        hwMap = ahwMap;

        // Initialize the hardware variables.
        led1 = hwMap.get(Servo.class, "led1");
        led2 = hwMap.get(Servo.class, "led2");

        //Setting LED lights with power OFF when initializing
        led1.setPosition(0.7745);
        led2.setPosition(0.7745);

    }

    public void setColor (double colors) {
        led1.setPosition(colors);
        led2.setPosition(colors);
    }

}
