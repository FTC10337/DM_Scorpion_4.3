package org.firstinspires.ftc.teamcode.hardware;

import android.support.annotation.NonNull;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDeviceWithParameters;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;
import com.qualcomm.robotcore.util.TypeConversion;

/**
 * Source code brought in from:
 * https://github.com/Overlake-FTC-7330-2017/ftc_app/blob/TeamCode/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/hardware/pixycam/PixyCam.java
 * on 2017-10-19
 */
@I2cDeviceType()
@DeviceProperties(name="PixyCam", description = "PixyCam", xmlTag = "Pixy")
//@I2cSensor(name = "PixyCam", description = "PixyCam", xmlTag = "PixyCam")
public class PixyCam extends I2cDeviceSynchDeviceWithParameters<I2cDeviceSynch, PixyCam.PixyCamParams> {

    static final I2cAddr ADDRESS_I2C_DEFAULT = I2cAddr.create7bit(0x54);

    private static final int QUERY_BASE = 0x50;
    private static final int QUERY_CC = 0x58;
    private static final int EXTENDED_QUERY_BASE = 0x70;
    private static final int EXTENDED_QUERY_CC = 0x78;
    private static final int QUERY_COUNT = 14;
    private static final int QUERY_SIG_COUNT = 5;
    private static final int QUERY_CC_COUNT = 6;
    private static final int EXTENDED_QUERY_COUNT = 26;
    private static final int EXTENDED_QUERY_BLOCK_SIZE = 5;
    private static final int EXTENDED_QUERY_SIG_COUNT = 25;
    private static final int EXTENDED_QUERY_SIG_BLOCK_SIZE = 4;
    private static final int EXTENDED_QUERY_CC_COUNT = 25;
    private static final int EXTENDED_QUERY_CC_BLOCK_SIZE = 6;
    private static final int MIN_SIGNATURE = 1;
    private static final int MAX_SIGNATURE = 7;

    public static class PixyCamParams implements Cloneable {

        I2cAddr i2cAddr = ADDRESS_I2C_DEFAULT;

        public PixyCamParams clone() {
            try {
                return (PixyCamParams) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Internal Error: Parameters not cloneable");
            }
        }
    }

    /**
     * The ReadWindow used to do a PixyCam LEGO protocol GeneralQuery
     */
    private I2cDeviceSynch.ReadWindow generalReadWindow;
    /**
     * The ReadWindows used to do the PixyCam LEGO protocol SignatureQuery.
     */
    private I2cDeviceSynch.ReadWindow[] signatureReadWindow;

    /*
     * The ReadWindow used to do the extneded query for the enhanced FTC firmware
     */
    private I2cDeviceSynch.ReadWindow extendedReadWIndow;
    /**
     * The ReadWindows used to do the PixyCam LEGO protocol SignatureQuery.
     */
    private I2cDeviceSynch.ReadWindow[] extendedSigReadWindow;

    public PixyCam(I2cDeviceSynch deviceSynch) {
        super(deviceSynch, true, new PixyCamParams());

        this.generalReadWindow = new I2cDeviceSynch.ReadWindow(QUERY_BASE, QUERY_COUNT, I2cDeviceSynch.ReadMode.ONLY_ONCE);
        this.signatureReadWindow = new I2cDeviceSynch.ReadWindow[MAX_SIGNATURE];
        for (int i = MIN_SIGNATURE; i <= MAX_SIGNATURE; i++) {
            this.signatureReadWindow[i - 1] = NewLegoProtocolSignatureQueryReadWindow(QUERY_BASE, QUERY_SIG_COUNT, i);
        }

        this.extendedReadWIndow = new I2cDeviceSynch.ReadWindow(EXTENDED_QUERY_BASE, EXTENDED_QUERY_COUNT, I2cDeviceSynch.ReadMode.ONLY_ONCE);
        this.extendedSigReadWindow = new I2cDeviceSynch.ReadWindow[MAX_SIGNATURE];
        for (int i = MIN_SIGNATURE; i <= MAX_SIGNATURE; i++) {
            this.extendedSigReadWindow[i - 1] = NewLegoProtocolSignatureQueryReadWindow(EXTENDED_QUERY_BASE, EXTENDED_QUERY_SIG_COUNT, i);
        }

        this.deviceClient.setI2cAddress(ADDRESS_I2C_DEFAULT);

        super.registerArmingStateCallback(false);
        this.deviceClient.engage();
    }

    private I2cDeviceSynch.ReadWindow NewLegoProtocolSignatureQueryReadWindow(int base, int count, int signature) {
        return new I2cDeviceSynch.ReadWindow(base + signature, count, I2cDeviceSynch.ReadMode.ONLY_ONCE);
    }

    private byte[] ReadEntireWindow(I2cDeviceSynch.ReadWindow readWindow) {
        this.deviceClient.setReadWindow(readWindow);
        return this.deviceClient.read(readWindow.getRegisterFirst(), readWindow.getRegisterCount());
    }

    /***
     *
     * @return a Block object containing details about the location of the largest detected block
     */
    public PixyBlock getBiggestBlock() {
        byte[] buffer = ReadEntireWindow(this.generalReadWindow);

        int signature = buffer[1] << 8 | buffer[0];

        return new PixyBlock(signature, -1, buffer[2], buffer[3], buffer[4], buffer[5]);
    }

    /**
     * @param signature is a value between 1 and 7 corresponding to the signature trained into the PixyCam.
     * @return a Block object containing details about the location of the largest detected block for the specified signature.
     */
    public PixyBlock getBiggestBlock(int signature) {
        if (signature < MIN_SIGNATURE || signature > MAX_SIGNATURE) {
            throw new IllegalArgumentException("signature must be between 1 and 7");
        }

        byte[] buffer = ReadEntireWindow(this.signatureReadWindow[signature - 1]);

        int x = getWord(buffer[6], buffer[7]);
        int y = getWord(buffer[8], buffer[9]);
        int width = getWord(buffer[10], buffer[11]);
        int height = getWord(buffer[12], buffer[13]);

        return new PixyBlock(signature, 1, x, y, width, height);
    }

    public PixyBlockList getBiggestBlocks() {
        byte[] buffer = ReadEntireWindow(this.extendedReadWIndow);

        PixyBlockList list = new PixyBlockList(buffer[0]);

        for (int i = 1; i < EXTENDED_QUERY_COUNT; i += EXTENDED_QUERY_BLOCK_SIZE) {
            int signature = TypeConversion.unsignedByteToInt(buffer[i]);

            list.add(new PixyBlock(signature, -1, buffer[i + 1], buffer[i + 2], buffer[i + 3], buffer[i + 4]));
        }

        return list;
    }

    public PixyBlockList getBiggestBlocks(int signature)
    {
        if (signature < MIN_SIGNATURE || signature > MAX_SIGNATURE) {
            throw new IllegalArgumentException("signature must be between 1 and 7");
        }

        byte[] buffer = ReadEntireWindow(this.extendedSigReadWindow[signature - 1]);

        PixyBlockList list = new PixyBlockList(buffer[0]);

        for (int i = 1; i < EXTENDED_QUERY_SIG_COUNT; i += EXTENDED_QUERY_SIG_BLOCK_SIZE) {
            list.add(new PixyBlock(signature, -1, buffer[i], buffer[i + 1], buffer[i + 2], buffer[i + 3]));
        }

        return list;
    }

    @Override
    protected boolean doInitialize() {
        return true;
    }

    @Override
    protected boolean internalInitialize(@NonNull PixyCamParams pixyCamParams) {
        this.parameters = pixyCamParams.clone();
        deviceClient.setI2cAddress(pixyCamParams.i2cAddr);

        return true;
    }

    private int getWord (byte low, byte high) {
        int value = TypeConversion.unsignedByteToInt(high) << 8;
        value += TypeConversion.unsignedByteToDouble(low);
        return value;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "PixyCam";
    }
}
