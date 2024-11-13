package me.hapyl.twitch;

public interface IScheduledTicking {

    void tick();

    default int tickPeriod() {
        return 20;
    }

}
