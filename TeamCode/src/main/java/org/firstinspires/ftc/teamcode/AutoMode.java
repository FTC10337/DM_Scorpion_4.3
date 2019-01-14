/*
 *  AutoMode: Lower the robot, Unlatch, Place the Team Mark in the Depot
 */
package org.firstinspires.ftc.teamcode;

import android.graphics.Camera;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import java.util.List;

@Autonomous(name="Scorpion: AutoMode", group="DarkMatter2019")
///@Disabled
public class AutoMode extends Scorpion_AutoMode {

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
    public void autoRunPath() {

        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        scorpion.gyro.zeroGyro();    // Make sure gyro is zeroed at start

//        scorpion.intakePivot.setPivotPower(0.5);      //Pushing Pivot forward
//        sleep(500);
//        scorpion.intakePivot.setPivotPower(0.0);

//        scorpion.latch.latchLift.setPower((1.0));
//        sleep(1000);

//        scorpion.latch.latchLift.setPower(-0.5);   // Lowering Scorpion FullPower

        //scorpion.led.setLedColor(colors.Purple_Strobe);
//        ElapsedTime timer = new ElapsedTime();
//        timer.reset();
//        while (!scorpion.latch.touchSensorTop.isPressed() && opModeIsActive() && timer.milliseconds()<3000) {
//            telemetry.addData("Top Sensor", "is OFF");
//            telemetry.update();
//            //scorpion.led.setLedColor(colors.Black);
//        }
//        telemetry.addData("Top Sensor", "is ON");
//        telemetry.update();
//        scorpion.latch.latchLift.setPower(0);
//
//        sleep(1000);


        /** Testing drive with Gyro
         * Negative angle is turning Right and Positive angle to the Left */
        gyroDrive(DRIVE_SPEED, 24.0, 0.0);    // Drive FWD 24 inches
        sleep(3000);
        gyroTurn( TURN_SPEED, -5.0);         // Turn  CCW to -5 Degrees
        sleep(3000);
        gyroDrive(DRIVE_SPEED, -12.0, 0.0);    // Drive FWD 24 inches
        sleep(3000);
        gyroHold( TURN_SPEED, -5.0, 0.5);    // Hold -5 Deg heading for a 1/2 second
        //gyroDrive(DRIVE_SPEED, 12.0, -5.0);  // Drive FWD 12 inches at 5 degrees
        //gyroTurn( TURN_SPEED,  5.0);         // Turn  CW  to  5 Degrees
        sleep(3000);
        gyroDrive(DRIVE_SPEED, 12.0, 0.0);    // Drive FWD 24 inches
        sleep(3000);
        //gyroHold( TURN_SPEED,  5.0, 0.5);    // Hold  5 Deg heading for a 1/2 second
        //gyroTurn( TURN_SPEED,   0.0);         // Turn  CW  to   0 Degrees
        //gyroHold( TURN_SPEED,   0.0, 1.0);    // Hold  0 Deg heading for a 1 second
        gyroDrive(DRIVE_SPEED,-24.0, 0.0);    // Drive REV 24 inches
        sleep(3000);



//        gyroTurn(TURN_SPEED, -15, P_TURN_COEFF); //Turn to insure off hook
//        telemetry.addData("Turn Right", "15 Degrees");
//        telemetry.update();
//        sleep(500);
//
//        encoderDrive(DRIVE_SPEED,           //Driving forward to push the mineral
//                25,
//                25,
//                2.0);
//        telemetry.addData("Moving Forward", "25 inches");
//        telemetry.update();
//        sleep(300);
//
//        gyroTurn(TURN_SPEED, 15, P_TURN_COEFF); //Turn to insure off hook
//        telemetry.addData("Turn Left", "15 Degrees");
//        telemetry.update();
//        sleep(500);
//
//        encoderDrive(DRIVE_SPEED,           //Driving forward to push the mineral
//                -25,
//                -25,
//                2.0);
//        telemetry.addData("Moving Back", "25 inches");
//        telemetry.update();
//        sleep(300);
//        sleep(300);


        //sleep(5000);             // Go down for 5 seconds
//        gyroTurn(TURN_SPEED, 5, P_TURN_COEFF); //Turn to insure off hook
//        telemetry.addData("Finished turn", "");
//        sleep(500);
//        gyroTurn(TURN_SPEED, -5, P_TURN_COEFF); //Turn to insure off hook
//        telemetry.addData("Finished turn", "");
//        sleep(500);

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            if (tfod != null) {
                tfod.activate();
            }

            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        //scorpion.led.setLedColor(colors.Rainbow_Party_Palette);
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        if (updatedRecognitions.size() == 3) {
                            //scorpion.led.setLedColor(colors.Black);
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
                            if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                                if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                                    telemetry.addData("Gold Mineral Position", "Left");
                                    //scorpion.led.ledLeft.setPosition(colors.Gold);
//                                    gyroTurn(TURN_SPEED, 10, P_TURN_COEFF); //Turn to insure off hook
//                                    telemetry.addData("Finished turn", "");
//                                    sleep(500);


                                } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                    telemetry.addData("Gold Mineral Position", "Right");
                                    //scorpion.led.ledRight.setPosition(colors.Gold);
//                                    gyroTurn(TURN_SPEED, -5, P_TURN_COEFF); //Turn to insure off hook
//                                    telemetry.addData("Finished turn", "");
//                                    sleep(500);



//                                    gyroTurn(TURN_SPEED, 135, P_TURN_COEFF); //Turn to insure off hook
//                                    telemetry.addData("Finished turn", "");
//                                    sleep(500);
//
//                                    encoderDrive(DRIVE_SPEED,           //Driving forward to push the mineral
//                                            40,
//                                            40,
//                                            3.0);
//                                    sleep(300);


                                } else {
                                    telemetry.addData("Gold Mineral Position", "Center");
                                    telemetry.update();
                                    //scorpion.led.setLedColor(colors.Gold);
//                                    gyroTurn(TURN_SPEED, 3, P_TURN_COEFF); //Turn to insure off hook
//                                    telemetry.addData("Finished turn", "");
//                                    sleep(500);



//                                    scorpion.intakePivot.setPivotPower(0.5);      //Pushing Pivot forward
//                                    sleep(1000);
//
//                                    scorpion.intakePivot.intake.setPower(-1.0);     //Using the Intake to place the Team Mark
//                                    sleep(3000);
//                                    gyroTurn(TURN_SPEED, -180, P_TURN_COEFF);
//
//                                    encoderDrive(DRIVE_SPEED,           //Driving forward to push the mineral
//                                            -20,
//                                            -20,
//                                            2.0);
//                                    sleep(100);

                                }
                            }
                        }
                        telemetry.update();
                    }
                }
            }
        }

        if (tfod != null) {
            tfod.shutdown();
        }

//        gyroTurn(TURN_SPEED, 135, P_TURN_COEFF);
//        encoderDrive(TURN_SPEED,   -12, 12, 6);  // S2: Turn Right 6 Inches with 2.5 Sec timeout
//        encoderDrive(DRIVE_SPEED, 86, 86, 15.0);  // S3: Forward 86 Inches with 10 Sec timeout

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
