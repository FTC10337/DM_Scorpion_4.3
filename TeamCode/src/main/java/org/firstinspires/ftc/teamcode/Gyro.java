/*
 *  This class defines and encapsulates everything around the Gyro
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.adafruit.AdafruitBNO055IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Gyro {

    private BNO055IMU gyro = null;
    private HardwareMap hwMap = null;

    // Variables used for reading Gyro
    private Orientation angles;
    private double      headingBias = 0.0;            // Gyro heading adjustment

    public Gyro() {}

    public void init (HardwareMap hwMap) {

        AdafruitBNO055IMU.Parameters parameters = new AdafruitBNO055IMU.Parameters();
        parameters.angleUnit       = AdafruitBNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = AdafruitBNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        //parameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample opmode
        parameters.mode = AdafruitBNO055IMU.SensorMode.IMU;
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        //parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "adaGyro".
        gyro = hwMap.get(BNO055IMU.class, "gyro");
        gyro.initialize(parameters);

    }

    /**
     * Read the current heading direction.  Use a heading bias if we recorded one at start to account for drift during
     * the init phase of match
     *
     * @return      Current heading (Z axis)
     */
    double readGyro() {
        angles = gyro.getAngularOrientation().toAxesReference(AxesReference.EXTRINSIC).toAxesOrder(AxesOrder.ZYX);
        return angles.secondAngle - headingBias;
    }

    /**
     * Record the current heading and use that as the 0 heading point for gyro reads
     * @return
     */
    void zeroGyro() {
        headingBias = readGyro();
    }

}


