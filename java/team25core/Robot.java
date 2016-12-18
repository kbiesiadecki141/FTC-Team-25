package team25core;
/*
 * FTC Team 25: cmacfarl, August 31, 2015
 */

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Robot extends OpMode {

    ConcurrentLinkedQueue<RobotTask> tasks;
    ConcurrentLinkedQueue<RobotEvent> events;

    public Robot()
    {
        tasks = new ConcurrentLinkedQueue<RobotTask>();
        events = new ConcurrentLinkedQueue<RobotEvent>();
    }

    public abstract void handleEvent(RobotEvent e);

    public void addTask(RobotTask task)
    {
        tasks.add(task);
        task.start();
    }

    public void removeTask(RobotTask task)
    {
        tasks.remove(task);
    }

    public void queueEvent(RobotEvent event)
    {
        events.add(event);
    }

    public boolean taskRunning(RobotTask task)
    {
        return tasks.contains(task);
    }

    public void init()
    {
        // TODO: ??
    }

    public void init_loop() {
        if (gamepad1 == null) {
            return;
        }

        telemetry.update();
        loop();
    }

    @Override
    public void loop()
    {
        RobotEvent e;

        /*
         * A list of tasks to give timeslices to.  A task remains in the list
         * until it tells the Robot that it is finished (true: I'm done, false: I have
         * more work to do), at which point it is stopped.
         */
        for (RobotTask t : tasks) {
            if (t.timeslice()) {
                t.stop();
            }
        }

        /*
         * This is a straight FIFO queue.  Pull an event off the queue, process it,
         * move on to the next one.
         */
        e = events.poll();
        while (e != null) {
            e.handleEvent();
            e = events.poll();
        }

        telemetry.update();
    }
}
