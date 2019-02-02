package org.firstinspires.ftc.teamcode;
/*
 * testing Gyro in Autonomous mode
 */

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.LedColorLibrary;
import org.firstinspires.ftc.teamcode.ScorpionHW;

import java.util.List;

@Autonomous(name="Auto_Depot_Side", group="DarkMatter2019")
//@Disabled
public class Auto_Depot_Side extends LinearOpMode {

    ScorpionHW scorpion = new ScorpionHW();

    static final double     COUNTS_PER_MOTOR_REV    = 7 ;    // Neverest 20
    static final double     DRIVE_GEAR_REDUCTION    = 40 * 72 / 48  ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (1.88 * COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;

    static final double     HEADING_THRESHOLD       = 1.0 ;    // As tight as we can make it with an integer gyro
    static final double     P_TURN_COEFF            = 0.011; // Larger is more responsive, but also less accurate
    static final double     P_TURN_COEFF_180        = 0.009; // For turns closer to 180 degrees. Less responsive, but more accurate to account for momentum coming out of long turns.
    static final double     P_TURN_COEFF_STRONG     = 0.150; // For small 1 degree adjustment turns
    static final double     P_DRIVE_COEFF_1         = 0.001;  // Larger is more responsive, but also less accurate
    static final double     P_DRIVE_COEFF_2         = 0.35;  // Intentionally large so robot "wiggles" around the target setpoint while driving


    private BNO055IMU imu;
    private Orientation lastAngles = new Orientation();
    double globalAngle;
    double power = .50;
    double correction;


    //Vuforia properties
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    private static final String VUFORIA_KEY = "AS3fDIb/////AAABmVlwDHOlLk7XkLQ9Z7m+vgYewFkchY4MpL+PfbJolvC/DFMYH6OMxo3PAR8T1escnF3nDl14w55XNWXovd11AKFXfeS6T3oidtGUj0iXLdh/RPdm3tK2MucFj+oUX9WWSoeGYpli/rVZ+aOvKkaStAQnGr7BvvJyEnj1rAtqVEFqA3S5bAUVryEU8vKt5m7g3fGAiFrnWBPeelRpYDm2pmXBzWZHyiSeTIYQMaQsFTCrt0k9rBz2tx2b9IxDLjdN11xOxTwfyb1mhgL1gvyspt4+k184JSZbrt6x0O580SdVoKYwAoWsoHgUnsWGR4df67TOd8JJ3UlW4ebUWq/Y5j6LzsDXewjxojnycUu58kc6";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() throws InterruptedException {

        scorpion.init(hardwareMap); // ScorpionHW

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);

        telemetry.addData("Scorpion", "calibrating Gyro...");
        telemetry.update();

        // make sure gyro is calibrated before continuing.
        while (!isStopRequested() && !imu.isGyroCalibrated())
        {
            sleep(50);
            idle();
        }

        telemetry.addData("Scorpion", "waiting for start");
        telemetry.addData("Gyro calibration status", imu.getCalibrationStatus().toString());
        telemetry.update();

        // wait for start button.
        waitForStart();

        telemetry.addData("Scorpion", "running");
        telemetry.update();

        sleep(1000);

        // TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that first.
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        scorpion.led.setLedColor(scorpion.colors.Purple_Heartbeat_Slow);

        //lowering the robot
        scorpion.led.setLedColor(scorpion.colors.Strobe_Red);
        scorpion.liftStinger.lift.setPower(-1);
        sleep(4200);
        scorpion.liftStinger.lift.setPower(0);
        sleep(500);
        scorpion.led.setLedColor(scorpion.colors.Black);
        scorpion.liftStinger.stinger.setPosition(0.5);
        sleep(500);

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            if (tfod != null) {
                tfod.activate();
            }

            while (opModeIsActive()) {
                if (tfod != null) {
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        if (updatedRecognitions.size() == 3) {
                            int goldMineralX = -1;
                            int silverMineral1X = -1;
                            int silverMineral2X = -1;
                            for (Recognition recognition : updatedRecognitions) {
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = (int) recognition.getLeft();
                                } else if (silverMineral1X == -1) {
                                    silverMineral1X = (int) recognition.getLeft();
                                } else {
                                    silverMineral2X = (int) recognition.getLeft();
                                }
                            }

                            telemetry.addData("1 imu heading", lastAngles.firstAngle);
                            telemetry.addData("2 global heading", globalAngle);
                            telemetry.addData("3 correction", correction);
                            telemetry.update();

                            if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                                if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {

                                    telemetry.addData("Gold Mineral Position", "Left");
                                    telemetry.update();

                                    scorpion.led.ledLeft.setPosition(scorpion.colors.Strobe_Gold);

                                    encoderDrive(DRIVE_SPEED, 5); //Forward
                                    turn(TURN_SPEED, 20);  //Turn Left
                                    encoderDrive(DRIVE_SPEED, 42); //Forward

                                    scorpion.led.setLedColor(scorpion.colors.Black);

                                    if (tfod != null) {
                                        tfod.shutdown();
                                    }

                                    turn(TURN_SPEED, -64);  //Turn Right
                                    encoderDrive(DRIVE_SPEED, 20); //Forward

                                    scorpion.intakePivot.intake.setPower(-0.75); //Intake Out the TeamMark
                                    sleep(1500);
                                    scorpion.intakePivot.intake.setPower(0);
                                    sleep(500);

                                    scorpion.led.setLedColor(scorpion.colors.Twinkles_Lava_Palette);

                                    encoderDrive(1, -75); //Reverse

                                    scorpion.liftStinger.stinger.setPosition(0.15); // stinger back

                                    scorpion.liftStinger.lift.setPower(1); //Lower the Lift
                                    sleep(4500);
                                    scorpion.liftStinger.lift.setPower(0);
                                    sleep(1000);

                                    scorpion.led.setLedColor(scorpion.colors.Black);



                                } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {

                                    telemetry.addData("Gold Mineral Position", "Right");
                                    telemetry.update();

                                    scorpion.led.ledRight.setPosition(scorpion.colors.Strobe_Gold);

                                    encoderDrive(DRIVE_SPEED, 5); //Forward
                                    turn(TURN_SPEED, -20);  //Turn Right
                                    encoderDrive(DRIVE_SPEED, 30); //Forward

                                    scorpion.led.setLedColor(scorpion.colors.Black);

                                    if (tfod != null) {
                                        tfod.shutdown();
                                    }

                                    turn(TURN_SPEED, 43);  //Turn Left
                                    encoderDrive(DRIVE_SPEED, 34); //Forward

                                    scorpion.intakePivot.intake.setPower(-0.5); //Intake Out the TeamMark
                                    sleep(1000);
                                    scorpion.intakePivot.intake.setPower(0);
                                    sleep(500);

                                    turn(TURN_SPEED, -64);  //Turn Right

                                    scorpion.led.setLedColor(scorpion.colors.Twinkles_Lava_Palette);

                                    encoderDrive(1, -77); //Reverse

                                    scorpion.liftStinger.stinger.setPosition(0.15); // stinger back

                                    scorpion.liftStinger.lift.setPower(1); //Lower the Lift
                                    sleep(4500);
                                    scorpion.liftStinger.lift.setPower(0);
                                    sleep(1000);

                                    scorpion.led.setLedColor(scorpion.colors.Black);


                                } else {
                                    telemetry.addData("Gold Mineral Position", "Center");
                                    telemetry.update();

                                    scorpion.led.setLedColor(scorpion.colors.Strobe_Gold);

                                    encoderDrive(DRIVE_SPEED, 57); //Forward

                                    scorpion.led.setLedColor(scorpion.colors.Black);

                                    if (tfod != null) {
                                        tfod.shutdown();
                                    }

                                    scorpion.intakePivot.intake.setPower(-0.5); //Intake Out the TeamMark
                                    sleep(1000);
                                    scorpion.intakePivot.intake.setPower(0);
                                    sleep(500);

                                    turn(TURN_SPEED, -50);  //Turn Right
                                    encoderDrive(1, -18); //Reverse

                                    turn(TURN_SPEED, 10);  //Turn Left

                                    scorpion.led.setLedColor(scorpion.colors.Twinkles_Lava_Palette);

                                    encoderDrive(1, -70); //Reverse

                                    scorpion.liftStinger.stinger.setPosition(0.15); // stinger back

                                    scorpion.liftStinger.lift.setPower(1); //Lower the Lift
                                    sleep(4500);
                                    scorpion.liftStinger.lift.setPower(0);
                                    sleep(1000);

                                    scorpion.led.setLedColor(scorpion.colors.Black);

                                }
                            }
                        }
                        telemetry.update();
                    }
                }
            }

            // turn the motors off.
            scorpion.driveTrain.setMotorPower(0, 0);
        }

        if (tfod != null) {
            tfod.shutdown();
        }

    }

    /**
     *  Method to drive on a fixed compass bearing (angle), based on encoder counts.
     *  Move will stop if either of these conditions occur:
     *  1) Move gets to the desired position
     *  2) Driver stops the opmode running.
     *
     * @param speed      Target speed for forward motion.  Should allow for _/- variance for adjusting heading
     * @param distance   Distance (in inches) to move from current position.  Negative distance means move backwards.
     * @param //timeoutS      Absolute Angle (in Degrees) relative to last gyro reset.
     *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                   If a relative angle is required, add/subtract from current heading.
     */
    public void encoderDrive(double speed, double distance) {

        int     newLeftDrive;
        int     newRightDrive;
        int     moveCounts;

        ElapsedTime     runtime = new ElapsedTime();

        // Use gyro to drive in a straight line.
        correction = checkDirection();

        //setting motors to use Encoders
        scorpion.driveTrain.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        scorpion.driveTrain.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        scorpion.driveTrain.setZeroMode(DcMotor.ZeroPowerBehavior.BRAKE);

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            moveCounts = (int)(distance * COUNTS_PER_INCH);
            newLeftDrive = scorpion.driveTrain.lDrive.getCurrentPosition() + moveCounts;
            newRightDrive = scorpion.driveTrain.rDrive.getCurrentPosition() + moveCounts;

            // Set Target and Turn On RUN_TO_POSITION
            scorpion.driveTrain.lDrive.setTargetPosition(newLeftDrive);
            scorpion.driveTrain.rDrive.setTargetPosition(newRightDrive);

            scorpion.driveTrain.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // start motion.
            speed = Range.clip(Math.abs(speed), 0.0, 1.0);
            scorpion.driveTrain.setMotorPower(speed + correction, speed);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                   //(runtime.seconds() < timeoutS) &&
                   (scorpion.driveTrain.lDrive.isBusy()
                           && scorpion.driveTrain.rDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Target",  "%7d:%7d",
                        newLeftDrive,
                        newRightDrive);
                telemetry.addData("Actual",  "%7d:%7d",
                        scorpion.driveTrain.lDrive.getCurrentPosition(),
                        scorpion.driveTrain.rDrive.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            scorpion.driveTrain.setMotorPower(0, 0);

            // Turn off RUN_TO_POSITION
            scorpion.driveTrain.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }
    }

    /**
     * Resets the cumulative angle tracking to zero.
     */
    private void resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        globalAngle = 0;
    }

    /**
     * Get current cumulative angle rotation from last reset.
     * @return Angle in degrees. + = left, - = right.
     */
    private double getAngle() {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;
        lastAngles = angles;

        return globalAngle;
    }

    /**
     * See if we are moving in a straight line and if not return a power correction value.
     * @return Power adjustment, + is adjust left - is adjust right.
     */
    private double checkDirection() {
        // The gain value determines how sensitive the correction is to direction changes.
        // You will have to experiment with your robot to get small smooth direction changes
        // to stay on a straight line.
        double correction, angle, gain = 1;

        angle = getAngle();

        if (angle == 0)
            correction = 0;             // no adjustment.
        else
            correction = -angle;        // reverse sign of angle for correction.

        correction = correction * P_DRIVE_COEFF_1;

        return correction;
    }

    /**
     * Rotate left or right the number of degrees. Does not support turning more than 180 degrees.
     * @param degrees Degrees to turn, + is left - is right
     */
    private void turn(double power, int degrees) {

        double  leftPower, rightPower;

        // restart imu movement tracking.
        resetAngle();

        // getAngle() returns + when rotating counter clockwise (left) and - when rotating
        // clockwise (right).

        if (degrees < 0)
        {   // turn right.
            leftPower = power;
            rightPower = -power;
        }
        else if (degrees > 0)
        {   // turn left.
            leftPower = -power;
            rightPower = power;
        }
        else return;

        // set power to rotate.
        scorpion.driveTrain.setMotorPower(leftPower, rightPower);

        // rotate until turn is completed.
        if (degrees < 0)
        {
            // On right turn we have to get off zero first.
            while (opModeIsActive() && getAngle() == 0) {}

            while (opModeIsActive() && getAngle() > degrees) {}
        }
        else    // left turn.
            while (opModeIsActive() && getAngle() < degrees) {}

        // turn the motors off.
        scorpion.driveTrain.setMotorPower(0, 0);

        // wait for rotation to stop.
        sleep(1000);

        // reset angle tracking on new heading.
        resetAngle();
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam");

        //parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }
}
