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

        // Arcade(POV) Mode uses left stick to go forward, and right stick to turn.
        double drive = -gamepad1.left_stick_y;
        double turn  =  gamepad1.right_stick_x;
        double lift = gamepad2.left_stick_y;
        double pivotControl = gamepad2.right_stick_y;

        //Activating Turbo mode with GamePad1 right bumper
        if (gamepad1.right_bumper) {
            turbo = true;
            turnCoefficient = 2;
            driveCoefficient = 1;
            telemetry.addData("TURBO is", "ON");
        }else {
            turbo = false;
            turnCoefficient = 4;
            driveCoefficient = 3;
            telemetry.addData("TURBO is", "OFF");
        }

        //Activating Intake with GamePad2 right and left bumpers
        if (gamepad2.left_bumper) {
            scorpion.intakePivot.intake.setPower(-1.0);
            scorpion.led.setColor(colors.Dark_Red);
        }else if (gamepad2.right_bumper) {
            scorpion.intakePivot.intake.setPower(1.0);
            scorpion.led.setColor(colors.Purple_Strobe);
        }else {
            scorpion.intakePivot.intake.setPower(0);
        }

        // Smooth and DeadZone the joystick values for DriveTrain
        drive        = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(drive, 0.10)) / driveCoefficient;
        turn         = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(turn, 0.10)) / turnCoefficient;
        leftPower    = Range.clip(drive + turn, -1.0, 1.0) ;
        rightPower   = Range.clip(drive - turn, -1.0, 1.0) ;

        // Smooth and DeadZone the LatchLift and Pivot inputs before using
        lift         = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(lift, 0.1));
        liftPower    = Range.clip(lift, -1.0, 1.0);
        pivotControl = scorpion.driveTrain.smoothPowerCurve(scorpion.driveTrain.deadzone(pivotControl, 0.1));
        pivotPower   = Range.clip(pivotControl/3, -0.5, 0.5);

        // Send calculated power to wheels
        scorpion.driveTrain.setPower(leftPower, rightPower);

        // Send calculated power to Pivot and LatchLift
        scorpion.intakePivot.pivot.setPower(pivotPower);
        scorpion.latch.latchLift.setPower(liftPower);

        // Update the encoder data every 1/10 second
        if (runtime.milliseconds() > 10) {
            runtime.reset();

            telemetry.addData("Path0",  "Now at %7d :%7d :%7d :%7d",
                    scorpion.driveTrain.leftFront.getCurrentPosition(),    // labeled B
                    scorpion.driveTrain.leftRear.getCurrentPosition(),     // labeled C
                    scorpion.driveTrain.rightFront.getCurrentPosition(),   // labeled A
                    scorpion.driveTrain.rightRear.getCurrentPosition());   // labeled D
                    // Show the elapsed game time and wheel power.
                    //telemetry.addData("Status", "Run Time: " + runtime.toString());
                    telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
                    telemetry.update();
        }

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {}

}
