/*
 *  This class defines and encapsulates everything around Latch and Lift
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class LiftStinger {

    public DcMotor lift = null;
    public Servo stinger = null;
    public TouchSensor touchSensorBottom = null;
    public TouchSensor touchSensorTop = null;
    private HardwareMap hwMap = null;

    public LiftStinger() {}

    public void init (HardwareMap ahwMap) {

        hwMap = ahwMap;

        // Initialize the hardware variables.
        lift = hwMap.get(DcMotor.class, "lift");
        stinger = hwMap.get(Servo.class, "stinger");
        touchSensorBottom = hwMap.get(TouchSensor.class, "touchSensor1");
        touchSensorTop = hwMap.get(TouchSensor.class, "touchSensor2");

        //Setting direction of motor's and servo's rotation
        lift.setDirection(DcMotor.Direction.REVERSE);
        stinger.setDirection(Servo.Direction.REVERSE);

        //Setting Lift motor with zero power when initializing
        lift.setPower(0);

        //Setting servo to position 0 when initialized
        stinger.setPosition(0.15);

        //Setting Lift motor with Zero Power Behavior
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

}
