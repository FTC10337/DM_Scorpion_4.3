/*
 *  This class defines and encapsulates everything around the Intake and Pivot
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

public class IntakePivot {

    public DcMotor pivot1 = null;
    public DcMotor intake = null;
    private HardwareMap hwMap = null;

    public IntakePivot() {}

    public void init (HardwareMap ahwMap) {

        hwMap = ahwMap;

        // Initialize the hardware variables.
        pivot1 = hwMap.get(DcMotor.class, "pivot1");
        intake = hwMap.get(DcMotor.class, "intakeSpin");

        //Setting direction of motor's rotation
        pivot1.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);

        //Setting motors with zero power when initializing
        setPivotPower(0);
        intake.setPower(0);

        //Setting Pivot motor with Zero Power Behavior
        pivot1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    public void setPivotPower (double power) {
        pivot1.setPower(power);
    }

}
