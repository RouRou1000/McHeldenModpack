package com.rourou.heldenmod;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfigHandler {
    public static final ForgeConfigSpec SPEC;
    public static final Common COMMON;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        SPEC = builder.build();
    }

    public static class Common {
        public final ForgeConfigSpec.IntValue combatDurationSeconds;
        public final ForgeConfigSpec.IntValue maxHearts;
        public final ForgeConfigSpec.BooleanValue hudEnabled;
        public final ForgeConfigSpec.EnumValue<LogoutPunishment> logoutPunishment;
        public final ForgeConfigSpec.EnumValue<ZeroHeartsAction> onZeroHearts;
        public final ForgeConfigSpec.IntValue reviveCost;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            combatDurationSeconds = builder.comment("Combat duration in seconds").defineInRange("combatDurationSeconds", 30, 5, 600);
            maxHearts = builder.comment("Default max hearts per player").defineInRange("maxHearts", 3, 1, 20);
            hudEnabled = builder.comment("Enable hearts HUD").define("hudEnabled", true);
            logoutPunishment = builder.comment("Action when logging out in combat").defineEnum("logoutPunishment", LogoutPunishment.DEATH);
            onZeroHearts = builder.comment("What happens when player reaches 0 hearts").defineEnum("onZeroHearts", ZeroHeartsAction.SPECTATOR);
            reviveCost = builder.comment("How many HeroHearts must be consumed to craft/use a Revive item").defineInRange("reviveCost", 3, 1, 10);
            builder.pop();
        }
    }

    public enum LogoutPunishment { DEATH, NONE }
    public enum ZeroHeartsAction { SPECTATOR, BAN }
}
