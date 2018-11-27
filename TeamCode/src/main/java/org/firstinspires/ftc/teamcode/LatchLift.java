/*
 *  This class defines and encapsulates everything around Latch and Lift
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class LatchLift {

    public DcMotor latchLift = null;
    public TouchSensor touchSensorBottom = null;
    public TouchSensor touchSensorTop = null;
    private HardwareMap hwMap = null;

    public LatchLift() {}

    public void init (HardwareMap ahwMap) {

        hwMap = ahwMap;

        // Initialize the hardware variables.
        latchLift = hwMap.get(DcMotor.class, "latch-lift");
        touchSensorBottom = hwMap.get(TouchSensor.class, "touchSensor1");
        touchSensorTop = hwMap.get(TouchSensor.class, "touchSensor2");

        //Setting direction of motor's rotation
        latchLift.setDirection(DcMotor.Direction.REVERSE);

        //Setting motors with zero power when initializing
        latchLift.setPower(0);

        //Setting Pivot motor with Zero Power Behavior
        latchLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

}
