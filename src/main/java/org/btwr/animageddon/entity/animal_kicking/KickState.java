package org.btwr.animageddon.entity.animal_kicking;

public final class KickState {
    public static final int DURATION = 20;

    public int progress = -1; // -1 = idle
    public int leg = 0; // 0 = left hind, 1 = right hind

    public void start(int leg) {
        this.progress = 0;
        this.leg = leg;
    }

    public boolean active() {
        return progress >= 0;
    }

    public void tick() {
        if (progress >= 0 && ++progress >= DURATION) {
            progress = -1;
        }
    }
}
