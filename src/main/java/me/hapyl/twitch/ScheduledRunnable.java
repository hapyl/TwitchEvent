package me.hapyl.twitch;

import com.google.common.collect.Sets;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public class ScheduledRunnable extends BukkitRunnable {

    private final Set<IScheduledTicking> ticking;
    private int i;

    public ScheduledRunnable() {
        ticking = Sets.newHashSet();
    }

    public void registerTicking(@NonNull IScheduledTicking ticking) {
        this.ticking.add(ticking);
    }

    @Override
    public final void run() {
        this.ticking.forEach(ticking -> {
            final int period = ticking.tickPeriod();

            if (i > 0 && i % period == 0) {
                ticking.tick();
            }
        });

        ++i;
    }
}
