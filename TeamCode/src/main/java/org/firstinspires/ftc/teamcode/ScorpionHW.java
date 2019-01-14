package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class ScorpionHW {

    private HardwareMap hwMap = null;

    //Creating new public class to combine Hardware classes and reuse in Arcade and Auto modes
    public DriveTrain   driveTrain   = new DriveTrain();
    public IntakePivot  intakePivot  = new IntakePivot();
    public LiftStinger liftStinger = new LiftStinger();
    public Gyro         gyro         = new Gyro();
    //public LED          led          = new LED();

    public void init (HardwareMap ahwMap) {

        hwMap = ahwMap;

        driveTrain.init(hwMap);
        intakePivot.init(hwMap);
        liftStinger.init(hwMap);
        //gyro.init(hwMap);
        //led.init(hwMap);

    }

}
