package team25core;
/*
 * FTC Team 25: cmacfarl, September 03, 2015
 */

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

public class SingleShotTimerTask extends RobotTask {

    public enum EventKind {
        EXPIRED,
    }

    public class SingleShotTimerEvent extends RobotEvent {

        public EventKind kind;

        public SingleShotTimerEvent(RobotTask task, EventKind kind)
        {
            super(task);
            this.kind = kind;
        }

        @Override
        public String toString()
        {
            return (super.toString() + "Single Shot Timer Event " + kind);
        }
    }

    protected ElapsedTime timer;
    protected int timeout;

    public SingleShotTimerTask(Robot robot, int timeout)
    {
        super(robot);

        this.timeout = timeout;
    }

    public void start()
    {
         timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    }

    @Override
    public void stop()
    {
        robot.removeTask(this);
    }

    @Override
    public String toString()
    {
        RobotLog.i("Single Shot Timer Progress: %d milliseconds out of %d milliseconds", timer.time(), timeout);
        return "Single Shot Timer Task running";
    }

    @Override
    public boolean timeslice()
    {
        if (timer.time() > timeout) {
            robot.queueEvent(new SingleShotTimerEvent(this, EventKind.EXPIRED));
            return true;
        } else {
            return false;
        }
    }
}
