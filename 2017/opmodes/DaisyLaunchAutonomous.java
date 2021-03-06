package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import team25core.DeadReckon;
import team25core.DeadReckonTask;
import team25core.FourWheelGearedDriveDeadReckon;
import team25core.GamepadTask;
import team25core.PersistentTelemetryTask;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.RunToEncoderValueTask;
import team25core.SingleShotTimerTask;

/**
 * FTC Team 25: Created by Katelyn Biesiadecki on 11/5/2016.
 */
@Autonomous(name = "Daisy: Launch Autonomous", group = "Team25")
public class DaisyLaunchAutonomous extends Robot
{
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor rearLeft;
    private DcMotor rearRight;
    private DcMotor launcher;
    private DcMotor conveyor;
    private Servo capServo;
    private DeadReckonTask deadReckonTask;
    private RunToEncoderValueTask runToPositionTask;
    private SingleShotTimerTask stt;
    private boolean launched;
    private PersistentTelemetryTask ptt;
    private FourWheelGearedDriveDeadReckon path;
    private final int TICKS_PER_INCH = Daisy.TICKS_PER_INCH;
    private final int TICKS_PER_DEGREE = Daisy.TICKS_PER_DEGREE;
    private final double STRAIGHT_SPEED = Daisy.STRAIGHT_SPEED;
    private final double TURN_SPEED = Daisy.TURN_SPEED;
    private final int LAUNCH_POSITION = Daisy.LAUNCH_POSITION;
    private int turnMultiplier = 1;

    private AutonomousPath pathChoice = AutonomousPath.CAP_BALL;
    private AutonomousAction actionChoice = AutonomousAction.LAUNCH_2;

    public enum Alliance {
        RED,
        BLUE,
    }

    public enum AutonomousPath {
        CORNER_PARK,
        CENTER_PARK,
        CAP_BALL,
        LAUNCH,
    }

    public enum AutonomousAction {
        LAUNCH_1,
        LAUNCH_2,
        BEACON_1,
        BEACON_2,
    }

    @Override
    public void handleEvent(RobotEvent e) {
        if (e instanceof GamepadTask.GamepadEvent) {
            GamepadTask.GamepadEvent event = (GamepadTask.GamepadEvent) e;

            if (event.kind == GamepadTask.EventKind.BUTTON_X_DOWN) {
                selectAlliance(Alliance.BLUE);
                ptt.addData("ALLIANCE", "Blue");
            } else if (event.kind == GamepadTask.EventKind.BUTTON_B_DOWN) {
                selectAlliance(Alliance.RED);
                ptt.addData("ALLIANCE", "Red");
            } else if (event.kind == GamepadTask.EventKind.LEFT_TRIGGER_DOWN) {
                pathChoice = AutonomousPath.CORNER_PARK;
                ptt.addData("AUTONOMOUS", "Corner Park");
            } else if (event.kind == GamepadTask.EventKind.RIGHT_TRIGGER_DOWN) {
                pathChoice = AutonomousPath.LAUNCH;
                ptt.addData("LAUNCH", "Stay");
            } else if (event.kind == GamepadTask.EventKind.LEFT_BUMPER_DOWN) {
                actionChoice = AutonomousAction.LAUNCH_1;
                ptt.addData("LAUNCH", "Launch 1 Ball");
            } else if (event.kind == GamepadTask.EventKind.RIGHT_BUMPER_DOWN) {
                actionChoice = AutonomousAction.LAUNCH_2;
                ptt.addData("LAUNCH", "Launch 2 Balls");
            }
        }

        if (e instanceof SingleShotTimerTask.SingleShotTimerEvent) {
            conveyor.setPower(0);
            addTask(runToPositionTask);
        } else if (e instanceof RunToEncoderValueTask.RunToEncoderValueEvent) {
            RunToEncoderValueTask.RunToEncoderValueEvent event = (RunToEncoderValueTask.RunToEncoderValueEvent) e;
            if (event.kind == RunToEncoderValueTask.EventKind.DONE) {
                if (!launched) {
                    conveyor.setPower(0.5);
                    addTask(stt);
                    launched = true;
                } else {
                    addTask(deadReckonTask);
                }
            }
        }
    }

    private void selectAlliance(Alliance color)
    {
        if (color == Alliance.BLUE) {
            // Do blue setup.
            turnMultiplier = -1;
        } else {
            // Do red setup.
            turnMultiplier = 1;
        }
    }

    private FourWheelGearedDriveDeadReckon pathSetup(AutonomousPath pathChoice)
    {
        FourWheelGearedDriveDeadReckon path = new FourWheelGearedDriveDeadReckon(this, TICKS_PER_INCH, TICKS_PER_DEGREE,
                frontLeft, frontRight, rearLeft, rearRight);

        if (pathChoice == AutonomousPath.CORNER_PARK) {
            path.addSegment(DeadReckon.SegmentType.STRAIGHT,  77, STRAIGHT_SPEED);
        } else if (pathChoice == AutonomousPath.CENTER_PARK) {
            path.addSegment(DeadReckon.SegmentType.STRAIGHT,  77, STRAIGHT_SPEED);
        } else if (pathChoice == AutonomousPath.CAP_BALL) {
            path.addSegment(DeadReckon.SegmentType.STRAIGHT,  77, STRAIGHT_SPEED);
        } else if (pathChoice == AutonomousPath.LAUNCH) {
            path.addSegment(DeadReckon.SegmentType.STRAIGHT,   0, STRAIGHT_SPEED);
        }

        return path;
    }

    @Override
    public void init()
    {
        frontLeft  = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        rearLeft   = hardwareMap.dcMotor.get("rearLeft");
        rearRight  = hardwareMap.dcMotor.get("rearRight");
        launcher   = hardwareMap.dcMotor.get("launcher");
        conveyor   = hardwareMap.dcMotor.get("conveyor");
        capServo   = hardwareMap.servo.get("capServo");

        capServo.setPosition(1.0);

        runToPositionTask = new RunToEncoderValueTask(this, launcher, LAUNCH_POSITION, 1.0);

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        stt = new SingleShotTimerTask(this, 2000);
        launched = false;

        // Telemetry setup.
        ptt = new PersistentTelemetryTask(this);
        this.addTask(ptt);
        ptt.addData("Press (X) to select", "Blue alliance!");
        ptt.addData("Press (B) to select", "Red alliance!");
        ptt.addData("Press (LEFT TRIGGER) to select", "Center Park!");
        ptt.addData("Press (RIGHT TRIGGER) to select", "Just launch!");
        ptt.addData("Press (LEFT BUMPER) to select", "Launch 1 Ball!");
        ptt.addData("Press (RIGHT BUMPER) to select", "Launch 2 Balls!");


        // Alliance selection.
        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1));
    }

    @Override
    public void start()
    {
        path = pathSetup(pathChoice);
        deadReckonTask = new DeadReckonTask(this, path);

        addTask(runToPositionTask);
        //addTask(deadReckonTask);
    }
}
