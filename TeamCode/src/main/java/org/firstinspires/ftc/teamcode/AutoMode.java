/*
 *  AutoMode: Lower the robot, Unlatch, Place the Team Mark in the Depot
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.RobotLog;

@Autonomous(name="Scorpion: AutoMode", group="DarkMatter2019")
//@Disabled
public class AutoMode extends Scorpion_AutoOpMode {

    @Override
    public void autoRunPath() {

        scorpion.gyro.zeroGyro();    // Make sure gyro is zeroed at start

        scorpion.latch.latchLift.setPower(-1.0);   // Lowering Scorpion FullPower
        sleep(5000);             // Go down for 5 seconds
        scorpion.latch.latchLift.setPower(0.0);

        gyroTurn(TURN_SPEED, -5, P_TURN_COEFF); //Turn to insure off hook
        sleep(500);

        encoderDrive(DRIVE_SPEED,           //Driving forward to push the mineral
                20,
                20,
                2.0);
        sleep(300);

        scorpion.intakePivot.pivot.setPower(-0.5);      //Pushing Pivot forward
        sleep(200);

        scorpion.intakePivot.intake.setPower(-1.0);     //Using the Intake to place the Team Mark
        sleep(3000);

//        gyroTurn(TURN_SPEED, 135, P_TURN_COEFF);
//        encoderDrive(TURN_SPEED,   -12, 12, 6);  // S2: Turn Right 6 Inches with 2.5 Sec timeout
//        encoderDrive(DRIVE_SPEED, 86, 86, 15.0);  // S3: Forward 86 Inches with 10 Sec timeout

    }

}
