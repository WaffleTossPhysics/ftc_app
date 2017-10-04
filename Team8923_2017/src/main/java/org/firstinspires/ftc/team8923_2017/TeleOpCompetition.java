package org.firstinspires.ftc.team8923_2017;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.lang.reflect.GenericSignatureFormatError;

/**
 *
 */

@TeleOp(name = "CapBot Teleop")
public class TeleOpCompetition extends MasterTeleOp
{
    @Override
    public void runOpMode() throws InterruptedException
    {
        InitHardware();

        waitForStart();

        while (opModeIsActive())
        {
            DriveOmni45TeleOp();
            idle();
        }
    }
}