/*
 *  This class defines ArcadeMode
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

@TeleOp(name="ArcadeMode", group="DarkMatter2019")
//@Disabled
public class ArcadeMode extends OpMode {

    ScorpionHW scorpion = new ScorpionHW();
    LedColorLibrary colors = new LedColorLibrary();

    private ElapsedTime runtime = new ElapsedTime();
    boolean turbo = false;
    double turnCoefficient = 4;
    double driveCoefficient = 3;

    @Override
    public void init() {

        scorpion.init(hardwareMap);
        //scorpion.led.setLedColor(colors.Confetti);
        //scorpion.led.setLedColor(colors.Purple_Strobe);
        // Tell the driver that initialization is complete.
        telemetry.addData("Scorpion Says", "Hello DarkMatter!");
        telemetry.update();
        RobotLog.i("Initialized, Ready to Start!");

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {}

    @Override
    public void start() {

        runtime.reset();

        while (runtime.seconds() > 20 && runtime.seconds() < 30) {
            //scorpion.led.setLedColor(colors.Green);

            telemetry.addData("Time to Latch", "20sec");
        }
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        // Setup a variable for each drive wheel to save power level for telemetry
        double leftPower;
        double rightPower;
        double liftPower;
        double pivotPower;
        double pivotExtendPower;

        // Arcade Mode controls
        double drive = -gamepad1.left_stick_y;
        double turn  =  gamepad1.right_stick_x;
        double liftControl = gamepad2.left_stick_y;
        double pivotControl = -gamepad2.right_stick_y;
        double pivotExtend = gamepad2.right_stick_x;

        //Activating Turbo mode with GamePad1 right bumper
        if (gamepad1.right_bumper) {
            turbo = true;
            turnCoefficient = 2;
            driveCoefficient = 1;
            telemetry.addData("TURBO is", "ON");
            //scorpion.led.setLedColor(colors.Dark_Red);
        }else {
            turbo = false;
            turnCoefficient = 4;
            driveCoefficient = 3;
            telemetry.addData("TURBO is", "OFF");
            //scorpion.led.setLedColor(colors.Black);
        }

        //Activating Intake with GamePad2 right and left bumpers
        if (gamepad2.left_bumper) {
            scorpion.intakePivot.intake.setPower(-1);
            telemetry.addData("Intake", "-1.0");
        }else if (gamepad2.right_bumper) {
            scorpion.intakePivot.intake.setPower(0.5);
            telemetry.addData("Intake", "1.0");
        }else {
            scorpion.intakePivot.intake.setPower(0);
        }

        //Stinger control to Latch and Unlatch
        if (gamepad2.dpad_right) {
            scorpion.liftStinger.stinger.setPosition(0.2);
        } else if (gamepad2.dpad_left) {
            scorpion.liftStinger.stinger.setPosition(0.5);
        }


        // Smooth and DeadZone the joystick values for DriveTrain
        drive        = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(drive, 0.10)) / driveCoefficient;
        turn         = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(turn, 0.10)) / turnCoefficient;
        leftPower    = Range.clip(drive + turn, -1.0, 1.0) ;
        rightPower   = Range.clip(drive - turn, -1.0, 1.0) ;

        // Smooth and DeadZone the Lift, Pivot and PivotExtend inputs before using
        liftControl         = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(liftControl, 0.1));
        liftPower = Range.clip(liftControl, -1.0, 1.0);

//        if (gamepad2.y) {
//            liftPower = Range.clip(lift, -1.0, 1.0);
//        } else {
//            liftPower = Range.clip(lift, -.75, .75);
//        }
        pivotControl = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(pivotControl, 0.1));
        pivotPower   = Range.clip(pivotControl , -1, 1);

        pivotExtend         = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(pivotExtend, 0.1));
        pivotExtendPower    = Range.clip(pivotExtend , -0.5, 0.5);

        // Touch/Magnet sensor
        if (scorpion.liftStinger.touchSensorTop.isPressed() && ! gamepad2.y) {
            telemetry.addData("Top Sensor", "is ON");
            telemetry.update();
            liftPower    = Range.clip(liftControl, 0.0, 1.0);
        } else if (scorpion.liftStinger.touchSensorBottom.isPressed() && ! gamepad2.y) {
            telemetry.addData("Bottom Sensor", "is ON");
            telemetry.update();
            liftPower    = Range.clip(liftControl, -1.0, 0.0);
        }


        // Send calculated power to wheels
        scorpion.driveTrain.setMotorPower(leftPower, rightPower);

        // Send calculated power to Pivot and LiftStinger
        scorpion.liftStinger.lift.setPower(liftPower);
        scorpion.intakePivot.setPivotPower(pivotPower);
        scorpion.intakePivot.extend.setPower(pivotExtendPower);

        // Update the encoder data every 1/10 second
        if (runtime.milliseconds() > 10) {
            runtime.reset();

                telemetry.addData("Motors",  "%7d :%7d",
                    scorpion.driveTrain.rDrive.getCurrentPosition(),
                    scorpion.driveTrain.lDrive.getCurrentPosition());
                    // Show the elapsed game time and wheel power.
                    //telemetry.addData("Status", "Run Time: " + runtime.toString());
                    telemetry.update();
        }

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {}

}
