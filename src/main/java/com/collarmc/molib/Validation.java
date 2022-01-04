package com.collarmc.molib;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validation {
    private static final Pattern STRIP_PATTERN = Pattern.compile("(?<!<@)[&§](?i)[0-9a-fklmnorx]");
    private static final Pattern ADD_UUID_PATTERN = Pattern.compile("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)");
    private static final Pattern VALID_MC_NAME = Pattern.compile("^\\w{3,16}$");

    /**
     * Strips input of all Minecraft formatting goop
     * @param input to strip
     * @return clean string
     */
    public static String stripMinecraft(String input) {
        if (input == null) return "";
        return STRIP_PATTERN.matcher(input).replaceAll("").trim();
    }

    /**
     * Checks if minecraft formatted
     * @param input to test
     * @return valid
     */
    public static boolean isMinecraftFormatted(String input) {
        return STRIP_PATTERN.matcher(input).matches();
    }

    /**
     * Checks if valid minecraft username
     * @param input to test
     * @return valid
     */
    public static boolean isValidMinecraftUsername(String input) {
        return !isMinecraftFormatted(input) && VALID_MC_NAME.matcher(input).matches();
    }

    /**
     * Makes sure any Mojang UUIDs are parsed correctly
     * @param possibleUUID to parse
     * @return uuid
     */
    public static UUID parseUUID(String possibleUUID) {
        try {
            return UUID.fromString(possibleUUID);
        } catch (IllegalArgumentException e) {
            Matcher matcher = ADD_UUID_PATTERN.matcher(possibleUUID);
            if (matcher.matches()) {
                return UUID.fromString(matcher.replaceAll("$1-$2-$3-$4-$5"));
            }
            throw e;
        }
    }

    public static String toMinecraftId(UUID id) {
        return id.toString().replace("-", "");
    }

    /**
     * Checks if either most or least significant bits of an account UUID are zero
     * @param id to test
     * @return if sus or not
     */
    public static boolean isMinecraftIdSuss(UUID id) {
        return id.getMostSignificantBits() != 0 || id.getLeastSignificantBits() != 0;
    }

    private Validation() {}
}
