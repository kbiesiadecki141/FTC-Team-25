
package test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;

import team25core.GyroTask;
import team25core.Robot;
import team25core.RobotEvent;

/**
 * FTC Team 25: Created by Katelyn Biesiadecki on 12/17/16.
 */
@Autonomous(name = "Worm Gear Test", group = "Team 25")
@Disabled
public class WormGearTest extends Robot {
    Servo servo;

    @Override
    public void handleEvent(RobotEvent e)
    {
        // Nothing.
    }

    @Override
    public void init()
    {
        servo = hardwareMap.servo.get("servo");
    }

    @Override
    public void start()
    {
        servo.setPosition(1);
    }
}
