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
    private HardwareMap hwMap = null;

    public LED() {}

    public void init (HardwareMap ahwMap) {

        hwMap = ahwMap;

        // Initialize the hardware variables.
        led1 = hwMap.get(Servo.class, "led1");

//        // Set the led1 servo for extended PWM range
//        if (led1.getController() instanceof ServoControllerEx) {
//            // Confirm its an extended range servo controller before we try to set to avoid crash
//            ServoControllerEx theControl = (ServoControllerEx) led1.getController();
//            int thePort = led1.getPortNumber();
//            PwmControl.PwmRange theRange = new PwmControl.PwmRange(500, 2500);
//            theControl.setServoPwmRange(thePort, theRange);
//        }

        //Setting LED lights with power OFF when initializing
        led1.setPosition(0.7745);

    }

    public void setColor (double colors) {
        led1.setPosition(colors);
    }

}
