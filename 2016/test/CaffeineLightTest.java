package test;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.LightSensor;

import org.swerverobotics.library.interfaces.Autonomous;

import opmodes.NeverlandMotorConstants;
import team25core.DeadReckon;
import team25core.DeadReckonTask;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.TwoWheelGearedDriveDeadReckon;

/**
 * Created by Izzie on 2/26/2016.
 */

@Autonomous(name = "TEST Display Color")
public class CaffeineLightTest extends Robot {

    protected int frontMinimum;
    protected int backMinimum;

    protected int TICKS_PER_INCH = NeverlandMotorConstants.ENCODER_TICKS_PER_INCH;
    protected int TICKS_PER_DEGREE = NeverlandMotorConstants.ENCODER_TICKS_PER_DEGREE;

    protected DcMotor leftTread;
    protected DcMotor rightTread;
    protected LightSensor light;
    protected DeadReckon deadReckonStraight;
    protected DeadReckonTask deadReckonStraightTask;

    @Override
    public void handleEvent(RobotEvent e) {

    }

    @Override
    public void init() {
        rightTread = hardwareMap.dcMotor.get("rightTread");
        leftTread = hardwareMap.dcMotor.get("leftTread");
        light = hardwareMap.lightSensor.get("light");

        deadReckonStraight = new TwoWheelGearedDriveDeadReckon(this, TICKS_PER_INCH, TICKS_PER_DEGREE, leftTread, rightTread);
        deadReckonStraight.addSegment(DeadReckon.SegmentType.STRAIGHT, 15, 0.251);

        deadReckonStraightTask = new DeadReckonTask(this, deadReckonStraight);
    }

    @Override
    public void start() {

        int currentFront = light.getLightDetectedRaw();
        int currentBack = light.getLightDetectedRaw();

        telemetry.addData("FRONT: ", currentFront);
        telemetry.addData("BACK: ", currentBack);

        if (currentFront < frontMinimum) {
            frontMinimum = currentFront;
        } else if (currentBack < backMinimum) {
            backMinimum = currentBack;
        }

        telemetry.addData("FRONT Minimum: ", frontMinimum);
        telemetry.addData("BACK Minimum: ", backMinimum);

        addTask(deadReckonStraightTask);
    }
}