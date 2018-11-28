/*
 *  This class defines and encapsulates everything around the DriveTrain
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

public class DriveTrain {

    // Declare OpMode members.
    public DcMotor leftFront = null;
    public DcMotor leftRear = null;
    public DcMotor rightFront = null;
    public DcMotor rightRear = null;
    private HardwareMap hwMap = null;

    public DriveTrain() {}

    /**
     * Init the drive train.
     *
     * @param ahwMap -- the hardwareMap being used
     */
    public void init (HardwareMap ahwMap) {

        hwMap = ahwMap;

        // Initialize the hardware variables.
        leftFront  = hwMap.get(DcMotor.class, "left-front");
        leftRear = hwMap.get(DcMotor.class, "left-rear");
        rightFront = hwMap.get(DcMotor.class, "right-front");
        rightRear = hwMap.get(DcMotor.class, "right-rear");

        //Setting direction of motor's rotation
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftRear.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightRear.setDirection(DcMotor.Direction.FORWARD);

        //setting motors to use Encoders
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);       // Temporary until encoders fixed

        //Setting motors with zero power when initializing
        setMotorPower(0.0, 0.0);
    }

    /**
     * Set all drive motors to the specified mode
     *
     * @param mode -- What mode to use
     */
    public void setMode(DcMotor.RunMode mode) {
        leftFront.setMode(mode);
        leftRear.setMode(mode);
        rightFront.setMode(mode);
        rightRear.setMode(mode);
    }

    /**
     * Set power to drive train motors.  Insures range is within bounds of -1 to 1
     * @param left -- left side power
     * @param right -- right side power
     */
    public void setMotorPower (double left, double right) {
        left = Range.clip(left, -1.0, 1.0);
        right = Range.clip(right, -1.0, 1.0);
        leftFront.setPower(left);
        leftRear.setPower(left);
        rightFront.setPower(right);
        rightRear.setPower(right);
    }

    /**
     * Set the motors to FLOAT or BRAKE mode
     *
     * @param mode -- what mode to set motors to
     */
    public void setZeroMode(DcMotor.ZeroPowerBehavior mode) {
        leftFront.setZeroPowerBehavior(mode);
        leftRear.setZeroPowerBehavior(mode);
        rightFront.setZeroPowerBehavior(mode);
        rightRear.setZeroPowerBehavior(mode);
    }
    /**
     * Add deadzone to a stick value
     *
     * @param rawStick  Raw value from joystick read -1.0 to 1.0
     * @param dz   Deadzone value to use 0 to 0.999
     * @return    Value after deadzone processing
     */
    public double deadzone(double rawStick, double dz) {
        double stick;

        // Force limit to -1.0 to 1.0
        if (rawStick > 1.0) {
            stick = 1.0;
        } else if (rawStick < -1.0) {
            stick = -1.0;
        } else {
            stick = rawStick;
        }

        // Check if value is inside the dead zone
        if (stick >= 0.0){
            if (Math.abs(stick) >= dz)
                return (stick - dz)/(1 -  dz);
            else
                return 0.0;

        }
        else {
            if (Math.abs(stick) >= dz)
                return (stick + dz)/(1 - dz);
            else
                return 0.0;

        }
    }

    /**
     * Applies cubic smoothing equation to make robot easier to drive with finer joystick control.  Input should be dead zoned
     * before applying smoothing.
     *
     * @param x -- The joystick input
     * @return -- Cubic smoothed joystick output
     */
    public double smoothPowerCurve (double x) {
        //double a = this.getThrottle();
        double a = 1.0;         // Hard code to max smoothing
        double b = 0.05;      // Min power to overcome motor stall

        if (x > 0.0)
            return (b + (1.0-b)*(a*x*x*x+(1.0-a)*x));

        else if (x<0.0)
            return (-b + (1.0-b)*(a*x*x*x+(1.0-a)*x));
        else return 0.0;
    }




}


