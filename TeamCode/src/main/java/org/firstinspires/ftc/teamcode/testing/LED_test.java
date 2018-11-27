/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.LED;
import org.firstinspires.ftc.teamcode.LedColorLibrary;

@TeleOp(name="LED_Test", group="Iterative Opmode")
@Disabled
public class LED_test extends LinearOpMode {

    private Servo led1 = null;
    private Servo led2 = null;
    private TouchSensor touchSensorBottom = null;
    private TouchSensor touchSensorTop = null;
    LedColorLibrary colors = new LedColorLibrary();
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        //Initialize the hardware variables.
        led1 = hardwareMap.get(Servo.class, "led1");
        led2 = hardwareMap.get(Servo.class, "led2");

        touchSensorBottom = hardwareMap.get(TouchSensor.class, "touchSensor1");
        touchSensorTop = hardwareMap.get(TouchSensor.class, "touchSensor2");

        //Setting LED lights with power OFF when initializing
        led1.setPosition(colors.Black);
        led2.setPosition(colors.Black);

        telemetry.addData("Scorpion Says", "Hello DarkMatter!");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {


            if (touchSensorBottom.isPressed()) {
                telemetry.addData("Bottom Sensor", "is ON");
                telemetry.update();
                led1.setPosition(colors.Red);
                led2.setPosition(colors.Red);
            } else {
                telemetry.addData("Lift", "is moving");
                telemetry.update();
                led1.setPosition(colors.Confetti);
                led2.setPosition(colors.Confetti);
            }

            if (touchSensorTop.isPressed()) {
                telemetry.addData("Top Sensor", "is ON");
                telemetry.update();
                led1.setPosition(colors.Red);
                led2.setPosition(colors.Red);
            } else {
                telemetry.addData("Lift", "is moving");
                telemetry.update();
                led1.setPosition(colors.Confetti);
                led2.setPosition(colors.Confetti);
            }


//
//            led1.setPosition(colors.Red);
//            led2.setPosition(colors.Red);
//            sleep(5000);
//            led1.setPosition(colors.Confetti);
//            led2.setPosition(colors.Confetti);
//            sleep(5000);
//            led1.setPosition(colors.Violet);
//            led2.setPosition(colors.Violet);
//            sleep(5000);
//            led1.setPosition(colors.Lime);
//            led2.setPosition(colors.Lime);
//            sleep(5000);
//            led1.setPosition(colors.Color_Waves_Party_Palette);
//            led2.setPosition(colors.Color_Waves_Party_Palette);
//            sleep(5000);
//            led1.setPosition(colors.Fire_Medium);
//            led2.setPosition(colors.Fire_Medium);
//            sleep(5000);
//            led1.setPosition(colors.Light_Chase_Blue);
//            led2.setPosition(colors.Light_Chase_Blue);
//            sleep(5000);
//            led1.setPosition(colors.Strobe_Gold);
//            led2.setPosition(colors.Strobe_Gold);
//            sleep(5000);
//            led1.setPosition(colors.Heartbeat_White);
//            led2.setPosition(colors.Heartbeat_White);
//            sleep(5000);

        }

    }





}
