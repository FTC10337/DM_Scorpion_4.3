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
    public DcMotor pivot2 = null;
    public DcMotor intake = null;
    public DcMotor extend = null;
    private HardwareMap hwMap = null;

    public IntakePivot() {}

    public void init (HardwareMap ahwMap) {

        hwMap = ahwMap;

        // Initialize the hardware variables.
        pivot1 = hwMap.get(DcMotor.class, "pivot1");
        pivot2 = hwMap.get(DcMotor.class, "pivot2");
        intake = hwMap.get(DcMotor.class, "intake");
        extend = hwMap.get(DcMotor.class, "extend");

        //Setting direction of motor's rotation
        pivot1.setDirection(DcMotor.Direction.FORWARD);
        pivot2.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);
        extend.setDirection(DcMotor.Direction.FORWARD);

        //setting motors to use Encoders
        setPivotMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setPivotMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Setting motors with zero power when initializing
        setPivotPower(0);
        intake.setPower(0);
        extend.setPower(0);

        //Setting Pivot motor with Zero Power Behavior
        pivot1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivot2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extend.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    public void setPivotMode(DcMotor.RunMode mode) {
        pivot1.setMode(mode);
        pivot2.setMode(mode);
        extend.setMode(mode);
    }

    public void setPivotPower (double power) {
        pivot1.setPower(power);
        pivot2.setPower(power);
    }

}
