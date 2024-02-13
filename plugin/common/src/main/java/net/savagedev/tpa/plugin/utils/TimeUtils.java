package net.savagedev.tpa.plugin.utils;

public final class TimeUtils {
    private TimeUtils() {
        throw new UnsupportedOperationException();
    }

    public static String formatTime(long milliseconds, TimeLengthFormat timeFormat) {
        long[] times = new long[6];

        times[0] = milliseconds / 1000L % 60L; // Seconds
        times[1] = milliseconds / (1000L * 60L) % 60L; // Minutes
        times[2] = milliseconds / (1000L * 3600L) % 24L; // Hours
        times[3] = milliseconds / (1000L * 86400L) % 30L; // Days
        times[4] = milliseconds / (1000L * 86400L * 30L) % 12L; // Months
        times[5] = milliseconds / (1000L * 86400L * 365L); // Years

        return formatTimes(times, timeFormat);
    }

    private static String formatTimes(long[] times, TimeLengthFormat format) {
        StringBuilder builder = new StringBuilder();
        String[] names = format.getTimeFormat();

        for (int i = times.length - 1; i >= 0; i--) {
            long time = times[i];

            if (time <= 0) {
                continue;
            }

            String name = names[i];

            if (time > 1 && format == TimeLengthFormat.LONG) {
                name = name + "s";
            }

            builder.append(" ").append(time).append(format == TimeLengthFormat.LONG ? " " : "").append(name);
        }

        return builder.toString().trim();
    }

    public enum TimeLengthFormat {
        LONG("second", "minute", "hour", "day", "month", "year"),
        SHORT("s", "m", "h", "d", "m", "y");

        private final String[] timeFormat;

        TimeLengthFormat(String... timeFormat) {
            this.timeFormat = timeFormat;
        }

        public String[] getTimeFormat() {
            return this.timeFormat;
        }
    }
}
