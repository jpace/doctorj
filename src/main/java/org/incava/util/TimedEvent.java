package org.incava.util;



/**
 * Times an event, from when the object is created, until when the
 * <code>end</code> method is invoked.
 */
public class TimedEvent
{
    public long duration;

    private long startTime;

    private TimedEventSet set;

    public TimedEvent(TimedEventSet set)
    {
        this.set = set;
        this.startTime = System.currentTimeMillis();
    }

    public void end()
    {
        tr.Ace.log("ending");
        duration = System.currentTimeMillis() - startTime;
        set.add(duration);
        tr.Ace.log("ended");
    }
}
