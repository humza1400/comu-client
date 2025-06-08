package me.comu.api.stopwatch;

public class Stopwatch
{
    private long previousMS;

    public Stopwatch()
    {
        reset();
    }

    public boolean hasCompleted(long milliseconds)
    {
        return getCurrentMS() - previousMS >= milliseconds;
    }

    public boolean hasCompleted(long time, boolean reset) {
        if(getCurrentMS() - previousMS > time) {
            if(reset)
                reset();

            return true;
        }

        return false;
    }

    public void reset()
    {
        previousMS = getCurrentMS();
    }

    public long getPreviousMS()
    {
        return previousMS;
    }

    public long getElapsed() { return getCurrentMS() - previousMS; }

    public static long getCurrentMS()
    {
        return System.nanoTime() / 1000000;
    }
}
