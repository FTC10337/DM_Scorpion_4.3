/*
 *  This class defines and encapsulates everything around the Intake and Pivot
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakePivot {

    public DcMotor intake = null;
    public DcMotor pivot = null;
    private HardwareMap hwMap = null;

    //public IntakePivot() {}

    public void init (HardwareMap ahwMap) {

        hwMap = ahwMap;

        // Initialize the hardware variables.
        intake = hwMap.get(DcMotor.class, "intake");
        pivot = hwMap.get(DcMotor.class, "pivot");

        //Setting direction of motor's rotation
        intake.setDirection(DcMotor.Direction.FORWARD);
        pivot.setDirection(DcMotor.Direction.FORWARD);

        //Setting motors with zero power when initializing
        intake.setPower(0);
        pivot.setPower(0);

        //Setting Pivot motor with Zero Power Behavior
        pivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }



}
