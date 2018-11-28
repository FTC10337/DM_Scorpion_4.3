/*
 *  This class defines and encapsulates everything around LED lights
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoControllerEx;

public class LED {

    public Servo ledRight = null;
    public Servo ledLeft = null;
    private HardwareMap hwMap = null;

    public LED() {}

    public void init (HardwareMap ahwMap) {

        hwMap = ahwMap;

        // Initialize the hardware variables.
        ledRight = hwMap.get(Servo.class, "led1");
        ledLeft = hwMap.get(Servo.class, "led2");

        //Setting LED lights with power OFF when initializing
        setLedColor(0.7745);
    }

    public void setLedColor (double colors) {
        ledRight.setPosition(colors);
        ledLeft.setPosition(colors);
    }

}
