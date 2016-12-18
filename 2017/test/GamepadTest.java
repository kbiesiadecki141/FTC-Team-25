package test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import static android.R.attr.name;

/**
 * FTC Team 25: Created by Katelyn Biesiadecki on 12/10/2016.
 */

@TeleOp(name = "Gamepad Test", group = "Team 25")
@Disabled
public class GamepadTest extends OpMode
{

    @Override
    public void init()
    {

    }

    @Override
    public void loop()
    {
        telemetry.addData("Left Joystick", gamepad1.left_stick_x);
        telemetry.addData("Right Joystick", gamepad1.right_stick_x);
    }
}
