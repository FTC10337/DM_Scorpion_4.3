/*
 *  This class defines ArcadeMode
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

@TeleOp(name="ArcadeMode", group="DarkMatter2019")
//@Disabled
public class ArcadeMode extends OpMode {

    ScorpionHW scorpion = new ScorpionHW();

    private ElapsedTime runtime = new ElapsedTime();
    boolean turbo = false;
    double turnCoefficient = 4;
    double driveCoefficient = 2;

    @Override
    public void init() {

        scorpion.init(hardwareMap);
        //scorpion.led.setLedColor(scorpion.colors.Confetti);
        //scorpion.led.setLedColor(scorpion.colors.Purple_Strobe);
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
            scorpion.led.setLedColor(scorpion.colors.Green);
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


        // ----- GAMEPAD 1 -----
        // Drive and Turn
        double drive = -gamepad1.left_stick_y;
        double turn  =  gamepad1.right_stick_x;

        //Turbo mode with GamePad1 "a" button
        if (gamepad1.left_bumper || gamepad1.right_bumper) {
            turbo = true;
            turnCoefficient = 2;
            driveCoefficient = 1;
            telemetry.addData("TURBO is", "ON");
            scorpion.led.setLedColor(scorpion.colors.Light_Chase_Red);
        }else {
            turbo = false;
            turnCoefficient = 4;
            driveCoefficient = 2;
            telemetry.addData("TURBO is", "OFF");
            scorpion.led.setLedColor(scorpion.colors.Black);
        }


        // ----- GAMEPAD 2 -----
        // Lift, Pivot and PivotExtend
        double liftControl = gamepad2.left_stick_y;
        double pivotControl = gamepad2.right_stick_y;
        double pivotExtend = -gamepad2.right_stick_x;

        //Stinger control to Latch and Unlatch with GamePad2 "a" and "b"
        if (gamepad2.x) {
            scorpion.liftStinger.stinger.setPosition(0.15);
        } else if (gamepad2.y) {
            scorpion.liftStinger.stinger.setPosition(0.5);
        }

        //Intake control with GamePad2 right and left bumpers
        if (gamepad2.left_bumper) {
            scorpion.intakePivot.intake.setPower(-1);
            telemetry.addData("Intake", "out");
            scorpion.led.setLedColor(scorpion.colors.Breath_Gray);
        }else if (gamepad2.right_bumper) {
            scorpion.intakePivot.intake.setPower(0.5);
            telemetry.addData("Intake", "in");
            scorpion.led.setLedColor(scorpion.colors.Breath_Blue);
        }else {
            scorpion.intakePivot.intake.setPower(0);
        }

        //IntakeDoor control with GamePad2 "Dpad"
        if (gamepad2.dpad_up) {
            scorpion.intakePivot.intakeDoor.setPosition(0.2);
        } else if (gamepad2.dpad_down) {
            scorpion.intakePivot.intakeDoor.setPosition(0.5);
        }


        // Smooth and DeadZone joystick values for DriveTrain
        drive        = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(drive, 0.10)) / driveCoefficient;
        turn         = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(turn, 0.10)) / turnCoefficient;
        leftPower    = Range.clip(drive + turn, -1.0, 1.0) ;
        rightPower   = Range.clip(drive - turn, -1.0, 1.0) ;

        // Smooth and DeadZone Lift, Pivot and PivotExtend inputs before using
        liftControl         = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(liftControl, 0.1));
        liftPower = Range.clip(liftControl, -1.0, 1.0);

        pivotControl = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(pivotControl, 0.1));
        pivotPower   = Range.clip(pivotControl , -1, 1);

        pivotExtend         = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(pivotExtend, 0.1));
        pivotExtendPower    = Range.clip(pivotExtend , -0.5, 0.5);

        // Touch sensor to limit and stop the Lift
        if (scorpion.liftStinger.touchSensorTop.isPressed()) {
            telemetry.addData("Top Sensor", "is ON");
            telemetry.update();
            liftPower    = Range.clip(liftControl, 0.0, 1.0);
        } else if (scorpion.liftStinger.touchSensorBottom.isPressed()) {
            telemetry.addData("Bottom Sensor", "is ON");
            telemetry.update();
            liftPower    = Range.clip(liftControl, -1.0, 0.0);
        }


        // Send calculated power to DriveTrain, Lift, Pivot and PivotExtender
        scorpion.driveTrain.setMotorPower(leftPower, rightPower);
        scorpion.liftStinger.lift.setPower(liftPower);
        scorpion.intakePivot.setPivotPower(pivotPower);
        scorpion.intakePivot.extend.setPower(pivotExtendPower);

        // Update the encoder data every 1/10 second
        if (runtime.milliseconds() > 10) {
            runtime.reset();
                telemetry.addData("DriveTrain",  "Left %7d : Right %7d",
                    scorpion.driveTrain.lDrive.getCurrentPosition(),
                    scorpion.driveTrain.rDrive.getCurrentPosition());
                telemetry.addData("Pivot",  "Pivot1 %7d : Pivot2 %7d",
                    scorpion.intakePivot.pivot1.getCurrentPosition(),
                    scorpion.intakePivot.pivot2.getCurrentPosition());
                telemetry.addData("PivotExtend",  "%7d",
                    scorpion.intakePivot.extend.getCurrentPosition());
                telemetry.addData("Lift",  "%7d",
                    scorpion.liftStinger.lift.getCurrentPosition());
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
