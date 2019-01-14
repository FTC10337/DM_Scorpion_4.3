/*
 *
 * Place to put all the common code shared by all Auto OpModes.  Each Auto type OpMode will extend this instead of
 * directly extending LinearOpMode and thus get all of the shared code.
 *
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import com.qualcomm.robotcore.util.Range;

abstract public class Scorpion_AutoMode extends LinearOpMode {

    /* Declare OpMode members. */
    ScorpionHW scorpion = new ScorpionHW();
    LedColorLibrary colors = new LedColorLibrary();

    static final double     COUNTS_PER_MOTOR_REV    = 7 ;    // Neverest 20
    static final double     DRIVE_GEAR_REDUCTION    = 40 * 72 / 48  ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (4 * COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                      (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;

    static final double     HEADING_THRESHOLD       = 1.0 ;    // As tight as we can make it with an integer gyro
    static final double     P_TURN_COEFF            = 0.011; // Larger is more responsive, but also less accurate
    static final double     P_TURN_COEFF_180        = 0.009; // For turns closer to 180 degrees. Less responsive, but more accurate to account for momentum coming out of long turns.
    static final double     P_TURN_COEFF_STRONG     = 0.150; // For small 1 degree adjustment turns
    static final double     P_DRIVE_COEFF_1         = 0.01;  // Larger is more responsive, but also less accurate
    static final double     P_DRIVE_COEFF_2         = 0.25;  // Intenionally large so robot "wiggles" around the target setpoint while driving

    @Override
    public void runOpMode() {

        RobotLog.i("DM14374 -- Pressed Init of AutoMode");

        // Init the hardware
        scorpion.init(hardwareMap); // ScorpionHW

        //scorpion.led.setLedColor(colors.Confetti);

        //setting motors to use Encoders and BRAKE mode
        scorpion.driveTrain.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        scorpion.driveTrain.setZeroMode(DcMotor.ZeroPowerBehavior.BRAKE);
        scorpion.driveTrain.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0",  "Starting at %7d :%7d",
                scorpion.driveTrain.lDrive.getCurrentPosition(),
                scorpion.driveTrain.rDrive.getCurrentPosition());

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Scorpion initialized");
        telemetry.update();

        RobotLog.i("DM14374 -- Drive train encoders reset");
        RobotLog.i("DM14374- Finished Init");

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Make sure our zero point on gyro is correct
        //scorpion.gyro.zeroGyro();

        // And execute the autoroutine -- must be defined in a child class for each Auto program
        autoRunPath();

        telemetry.addData("Path", "Complete");
        telemetry.update();

    }

    /**
     * This method should be Overriden by each OpMode to the code required to actually run the sequence for that Auto
     */
    abstract void autoRunPath();

    /**
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
//    public void encoderDrive(double speed, double leftInches, double rightInches, double timeoutS) {
//
//        int newLeftTargetF;
//        int newLeftTargetR;
//        int newRightTargetF;
//        int newRightTargetR;
//
//        ElapsedTime     runtime = new ElapsedTime();
//
//        // Ensure that the opmode is still active
//        if (opModeIsActive()) {
//
//            // Determine new target position, and pass to motor controller
//            newLeftTargetF = scorpion.driveTrain.leftFront.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
//            newLeftTargetR = scorpion.driveTrain.leftRear.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
//            newRightTargetF = scorpion.driveTrain.rightFront.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
//            newRightTargetR = scorpion.driveTrain.rightRear.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
//            scorpion.driveTrain.leftFront.setTargetPosition(newLeftTargetF);
//            scorpion.driveTrain.leftRear.setTargetPosition(newLeftTargetR);
//            scorpion.driveTrain.rightFront.setTargetPosition(newRightTargetF);
//            scorpion.driveTrain.rightRear.setTargetPosition(newRightTargetR);
//
//            // Turn On RUN_TO_POSITION
//            scorpion.driveTrain.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//            // reset the timeout time and start motion.
//            runtime.reset();
//
//            // Max power to both sides is the same
//            scorpion.driveTrain.setMotorPower(speed, speed);
//
//            // keep looping while we are still active, and there is time left, and both motors are running.
//            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
//            // its target position, the motion will stop.  This is "safer" in the event that the robot will
//            // always end the motion as soon as possible.
//            // However, if you require that BOTH motors have finished their moves before the robot continues
//            // onto the next step, use (isBusy() || isBusy()) in the loop test.
//            while (opModeIsActive() &&
//                   (runtime.seconds() < timeoutS) &&
//                   (scorpion.driveTrain.leftFront.isBusy()
//                           && scorpion.driveTrain.leftRear.isBusy()
//                           && scorpion.driveTrain.rightFront.isBusy()
//                           && scorpion.driveTrain.rightRear.isBusy())) {
//
//                // Display it for the driver.
//                telemetry.addData("Path1",  "Running to %7d :%7d :%7d :%7d", newLeftTargetF, newLeftTargetR, newRightTargetF, newRightTargetR);
//                telemetry.addData("Path2",  "Running at %7d :%7d :%7d :%7d",
//                                            scorpion.driveTrain.leftFront.getCurrentPosition(),
//                                            scorpion.driveTrain.leftRear.getCurrentPosition(),
//                                            scorpion.driveTrain.rightFront.getCurrentPosition(),
//                                            scorpion.driveTrain.rightRear.getCurrentPosition());
//                telemetry.update();
//            }
//
//            // Stop all motion;
//            scorpion.driveTrain.setMotorPower(0, 0);
//
//            // Turn off RUN_TO_POSITION
//            scorpion.driveTrain.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//
//            //  sleep(250);   // optional pause after each move
//        }
//    }

    /**
     *  Method to drive on a fixed compass bearing (angle), based on encoder counts.
     *  Move will stop if either of these conditions occur:
     *  1) Move gets to the desired position
     *  2) Driver stops the opmode running.
     *
     * @param speed      Target speed for forward motion.  Should allow for _/- variance for adjusting heading
     * @param distance   Distance (in inches) to move from current position.  Negative distance means move backwards.
     * @param angle      Absolute Angle (in Degrees) relative to last gyro reset.
     *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                   If a relative angle is required, add/subtract from current heading.
     */
    public void gyroDrive ( double speed, double distance, double angle) {
        int     newLeftDriveTarget;
        int     newRightDriveTarget;
        int     moveCounts;
        double  max;
        double  error;
        double  steer;
        double  leftSpeed;
        double  rightSpeed;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            moveCounts = (int)(distance * COUNTS_PER_INCH);
            newLeftDriveTarget = scorpion.driveTrain.lDrive.getCurrentPosition() + moveCounts;
            newRightDriveTarget = scorpion.driveTrain.rDrive.getCurrentPosition() + moveCounts;

            // Set Target and Turn On RUN_TO_POSITION
            scorpion.driveTrain.lDrive.setTargetPosition(newLeftDriveTarget);
            scorpion.driveTrain.rDrive.setTargetPosition(newRightDriveTarget);

            scorpion.driveTrain.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // start motion.
            speed = Range.clip(Math.abs(speed), 0.0, 1.0);
            scorpion.driveTrain.setMotorPower(speed, speed);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (scorpion.driveTrain.lDrive.isBusy()
                            && scorpion.driveTrain.rDrive.isBusy())) {

                // adjust relative speed based on heading error.
                error = getError(angle);
                steer = getSteer(error, P_DRIVE_COEFF_1);

                // if driving in reverse, the motor correction also needs to be reversed
                if (distance < 0)
                    steer *= -1.0;

                leftSpeed = speed - steer;
                rightSpeed = speed + steer;

                // Normalize speeds if either one exceeds +/- 1.0;
                max = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
                if (max > 1.0)
                {
                    leftSpeed /= max;
                    rightSpeed /= max;
                }

                scorpion.driveTrain.setMotorPower(leftSpeed, rightSpeed);

                // Display drive status for the driver.
                telemetry.addData("Err/St",  "%5.1f/%5.1f",  error, steer);
                telemetry.addData("Target",  "%7d:%7d",
                        newLeftDriveTarget,
                        newRightDriveTarget);
                telemetry.addData("Actual",  "%7d:%7d",
                        scorpion.driveTrain.lDrive.getCurrentPosition(),
                        scorpion.driveTrain.rDrive.getCurrentPosition());
                telemetry.addData("Speed",   "%5.2f:%5.2f",  leftSpeed, rightSpeed);
                telemetry.update();
            }

            // Stop all motion;
            scorpion.driveTrain.setMotorPower(0,0);

            // Turn off RUN_TO_POSITION
            scorpion.driveTrain.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    /**
     *  Method to spin on central axis to point in a new direction.
     *  Move will stop if either of these conditions occur:
     *  1) Move gets to the heading (angle)
     *  2) Driver stops the opmode running.
     *
     * @param speed Desired speed of turn.
     * @param angle      Absolute Angle (in Degrees) relative to last gyro reset.
     *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                   If a relative angle is required, add/subtract from current heading.
     */
    public void gyroTurn (  double speed, double angle) {

        RobotLog.i("DM14347- gyroTurn start  speed:" + speed +
                "  heading:" + angle);

        // keep looping while we are still active, and not on heading.
        while (opModeIsActive() && !onHeading(speed, angle, P_TURN_COEFF)) {
            // Allow time for other processes to run.
            // onHeading() does the work of turning us
            telemetry.update();
            //sleep(1);
        }

        RobotLog.i("DM14347- gyroTurn done   heading actual:" + scorpion.gyro.readGyro());
    }

    /**
     *  Method to obtain & hold a heading for a finite amount of time
     *  Move will stop once the requested time has elapsed
     *
     * @param speed      Desired speed of turn.
     * @param angle      Absolute Angle (in Degrees) relative to last gyro reset.
     *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                   If a relative angle is required, add/subtract from current heading.
     * @param holdTime   Length of time (in seconds) to hold the specified heading.
     */
    public void gyroHold( double speed, double angle, double holdTime) {

        ElapsedTime holdTimer = new ElapsedTime();

        // keep looping while we have time remaining.
        holdTimer.reset();
        while (opModeIsActive() && (holdTimer.time() < holdTime)) {
            // Update telemetry & Allow time for other processes to run.
            onHeading(speed, angle, P_TURN_COEFF);
            telemetry.update();
        }

        // Stop all motion;
        scorpion.driveTrain.setMotorPower(0, 0);
    }

    /**
     * Perform one cycle of closed loop heading control.
     *
     * @param speed     Desired speed of turn.
     * @param angle     Absolute Angle (in Degrees) relative to last gyro reset.
     *                  0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                  If a relative angle is required, add/subtract from current heading.
     * @param PCoeff    Proportional Gain coefficient
     * @return
     */
    boolean onHeading(double speed, double angle, double PCoeff) {
        double   error ;
        double   steer ;
        boolean  onTarget = false ;
        double leftSpeed;
        double rightSpeed;

        // determine turn power based on +/- error
        error = getError(angle);

        if (Math.abs(error) <= HEADING_THRESHOLD) {
            // Close enough so no need to move
            steer = 0.0;
            leftSpeed  = 0.0;
            rightSpeed = 0.0;
            onTarget = true;
        }
        else {
            // Calculate motor powers
            steer = getSteer(error, PCoeff);
            rightSpeed  = speed * steer;
            leftSpeed   = -rightSpeed;
        }

        // Send desired speeds to motors.
        scorpion.driveTrain.setMotorPower(leftSpeed, rightSpeed);

        // Send desired speeds to motors.
//        scorpion.driveTrain.setMotorPower(leftSpeed, rightSpeed);
//        telemetry.addData("Error ", error);
//        telemetry.update();

        telemetry.addData("Target", "%5.2f", angle);
        telemetry.addData("Err/St", "%5.2f/%5.2f", error, steer);
        telemetry.addData("Speed.", "%5.2f:%5.2f", leftSpeed, rightSpeed);
        telemetry.update();

        return onTarget;
    }

    /**
     * getError determines the error between the target angle and the robot's current heading
     * @param   targetAngle  Desired angle (relative to global reference established at last Gyro Reset).
     * @return  error angle: Degrees in the range +/- 180. Centered on the robot's frame of reference
     *          +ve error means the robot should turn LEFT (CCW) to reduce error.
     */

    public double getError(double targetAngle) {

        double robotError;

        // calculate error in -179 to +180 range  (
        robotError = targetAngle - scorpion.gyro.readGyro();
        while (robotError > 180)  robotError -= 360;
        while (robotError <= -180) robotError += 360;
        return robotError;
    }

    /**
     * returns desired steering force.  +/- 1 range.  +ve = steer left
     * @param error   Error angle in robot relative degrees
     * @param PCoeff  Proportional Gain Coefficient
     * @return
     */
    public double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }

}
