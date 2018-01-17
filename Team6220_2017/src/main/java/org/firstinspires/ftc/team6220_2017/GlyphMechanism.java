package org.firstinspires.ftc.team6220_2017;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Encapsulates functionalities for the glyph-scoring mechanism on our robot.
 */

public class GlyphMechanism
{
    MasterOpMode op;

    int[] glyphHeights;

    double motorCollectorCount = 0.5;

    private boolean wasStickPressed = false;

    // Tells us whether or not the rotation mechanism has been rotated for collecting an additional
    // glyph
    private boolean isRotated = false;

    // We pass in MasterOpMode so that this class can access important functionalities such as
    // telemetry and pause
    public GlyphMechanism (MasterOpMode mode, int[] GlyphHeights)
    {
        this.op = mode;
        this.glyphHeights = GlyphHeights;
    }

    // Similar to runToPosition, but uses a specialized PID loop for the glyphter.  Using runToPosition
    // can cause the glyphter motor to stall before reaching its target position
    public void driveGlyphterToPosition(int targetPosition, double maxPower)
    {
        double glyphterDiff = targetPosition - op.motorGlyphter.getCurrentPosition();
        double glyphterPower;

        // Check to see whether the glyphter is close enough to its target to stop moving
        while ((Math.abs(glyphterDiff) > Constants.GLYPHTER_TOLERANCE_TICKS) && op.opModeIsActive())
        {
            glyphterDiff = targetPosition - op.motorGlyphter.getCurrentPosition();

            // Transform glyphterDiff to motor power using PID
            op.GlyphterFilter.roll(glyphterDiff);
            glyphterPower = op.GlyphterFilter.getFilteredValue();

            // Ensure glyphter doesn't approach target position too slowly
            if (Math.abs(glyphterPower) < Constants.MINIMUM_GLYPHTER_POWER)
            {
                glyphterPower = Math.signum(glyphterPower) * Constants.MINIMUM_GLYPHTER_POWER;
            }

            // Ensure glyphter doesn't ever move faster than we want it to
            if (Math.abs(glyphterPower) > maxPower)
            {
                glyphterPower = Math.signum(glyphterPower) * maxPower;
            }

            op.motorGlyphter.setPower(glyphterPower);

            op.telemetry.addData("Glyphter Encoder Value: ", op.motorGlyphter.getCurrentPosition());
            // Note:  This gives the ABSOLUTE VALUE of the motor power
            op.telemetry.addData("Glyphter Power: ", op.motorGlyphter.getPower());
            op.telemetry.update();
        }

        op.motorGlyphter.setPower(0);
    }

    // Takes input used to move all parts of the glyph mechanism
    public void driveGlyphMech()
    {
        // Note:  REMEMBER to restart robot if program is stopped, then adjust position manually
        // Glyphter controls---------------------------------------------------
         // Drive glyphter manually
        if (Math.abs(op.gamepad2.right_stick_y) >= Constants.MINIMUM_JOYSTICK_POWER)
        {
            wasStickPressed = true;
            op.motorGlyphter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // Filter driver input to glyphter to increase ease of use
            double glyphterPower = op.stickCurve.getOuput(-op.gamepad2.right_stick_y);

            op.motorGlyphter.setPower(glyphterPower);
            op.telemetry.addData("rightStickY: ", glyphterPower);
            op.telemetry.update();
        }
        // Check to see if the joystick was just released.  If so, transition into automatic control
        else if (Math.abs(op.gamepad2.right_stick_y) < Constants.MINIMUM_JOYSTICK_POWER && wasStickPressed)
        {
            wasStickPressed = false;
            op.motorGlyphter.setPower(0);
            op.motorGlyphter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
         // Drive glyphter automatically
        else if (Math.abs(op.gamepad2.right_stick_y) < Constants.MINIMUM_JOYSTICK_POWER && !wasStickPressed)
        {
            // Protect against Start + A / B running glyph mechanism into ground
            if (op.driver2.isButtonJustPressed(Button.A) && !op.driver2.isButtonPressed(Button.START))
            {
                op.motorGlyphter.setTargetPosition(glyphHeights[0]);
                op.motorGlyphter.setPower(1.0);
            }
            else if (op.driver2.isButtonJustPressed(Button.B) && !op.driver2.isButtonPressed(Button.START))
            {
                op.motorGlyphter.setTargetPosition(glyphHeights[1]);
                op.motorGlyphter.setPower(1.0);
            }
            else if (op.driver2.isButtonJustPressed(Button.Y))
            {
                op.motorGlyphter.setTargetPosition(glyphHeights[2]);
                op.motorGlyphter.setPower(1.0);
            }
            else if (op.driver2.isButtonJustPressed(Button.X))
            {
                op.motorGlyphter.setTargetPosition(glyphHeights[3]);
                op.motorGlyphter.setPower(1.0);
            }
            // Stow glyph mechanism
            else if (op.driver2.isButtonJustPressed(Button.START))
            {
                op.motorGlyphter.setTargetPosition(0);
                op.motorGlyphter.setPower(1.0);
            }
        }
        //----------------------------------------------------------------------


        // Collector controls------------------------------------------------
         // Collect glyphs
        if (op.driver1.isButtonJustPressed(Button.DPAD_DOWN))
        {
            op.motorCollectorRight.setPower(-0.7);
            op.motorCollectorLeft.setPower(0.7);
        }
         // Score glyphs
        else if (op.driver1.isButtonJustPressed(Button.DPAD_UP))
        {
            op.motorCollectorRight.setPower(0.55);
            op.motorCollectorLeft.setPower(-0.55);

        }
         // Stack glyphs (need a slower speed for this)
        else if (op.driver1.isButtonJustPressed(Button.DPAD_RIGHT))
        {
            op.motorCollectorRight.setPower(0.3);
            op.motorCollectorLeft.setPower(-0.3);

        }
         // Stop collector
        else if (op.driver1.isButtonJustPressed(Button.DPAD_LEFT))
        {
            op.motorCollectorRight.setPower(0);
            op.motorCollectorLeft.setPower(0);
        }
        /*else if (op.driver1.isButtonJustPressed(Button.LEFT_STICK_PRESS))
        {
            motorCollectorCount += 0.05;
            op.motorCollectorLeft.setPower(-0.7);
            op.motorCollectorRight.setPower(motorCollectorCount);
        }
        else if (op.driver1.isButtonJustPressed(Button.RIGHT_STICK_PRESS))
        {
            motorCollectorCount -= 0.05;
            op.motorCollectorLeft.setPower(-0.7);
            op.motorCollectorRight.setPower(motorCollectorCount);
        }*/
        //---------------------------------------------------------------------


        // todo Adjust time and power constants
        // Rotation mechanism controls-----------------------------------------
        if (op.driver1.isButtonJustPressed(Button.LEFT_BUMPER))
        {
            // Rotate glyph mechanism opposite directions based on whether it has been flipped or not
            if (!isRotated)
            {
                op.glyphterRotationServo.setPower(1.0);

                // Wait until servo has reached target
                op.pauseWhileUpdating(0.5);
                op.glyphterRotationServo.setPower(0.05);
            }
            else if (isRotated)
            {
                op.glyphterRotationServo.setPower(-1.0);

                // Wait until servo has reached target
                op.pauseWhileUpdating(0.5);
                op.glyphterRotationServo.setPower(-0.05);
            }
        }
        //---------------------------------------------------------------------

        op.telemetry.addData("Glyphter Enc: ", op.motorGlyphter.getCurrentPosition());
        op.telemetry.addData("MotorCollectorCount: ", motorCollectorCount);
        op.telemetry.update();
    }
}
