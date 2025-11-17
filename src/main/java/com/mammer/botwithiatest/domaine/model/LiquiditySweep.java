package com.mammer.botwithiatest.domaine.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class LiquiditySweep {

    public enum Type {
        BUY_SWEEP,
        SELL_SWEEP
    }

    private final Type type;
    private final double level;
    // ============  should check ZonedDateTime with localdatetime
    private final LocalDateTime swingTime;
    private final LocalDateTime sweepCandleTime;

    public LiquiditySweep(Type type, double level, LocalDateTime swingTime, LocalDateTime sweepCandleTime) {
        this.type = type;
        this.level = level;
        this.swingTime = swingTime;
        this.sweepCandleTime = sweepCandleTime;
    }

    public Type getType() {
        return type;
    }

    public double getLevel() {
        return level;
    }

    // ============  should check ZonedDateTime with localdatetime
    public LocalDateTime getSwingTime() {
        return swingTime;
    }

    public LocalDateTime getSweepCandleTime() {
        return sweepCandleTime;
    }

    public boolean isBuySweep() {
        return type == Type.BUY_SWEEP;
    }

    public boolean isSellSweep() {
        return type == Type.SELL_SWEEP;
    }

    @Override
    public String toString() {
        return "LiquiditySweep{" +
                "type=" + type +
                ", level=" + level +
                ", swingTime=" + swingTime +
                ", sweepCandleTime=" + sweepCandleTime +
                '}';
    }

}
