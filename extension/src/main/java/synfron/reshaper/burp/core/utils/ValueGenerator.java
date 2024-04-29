package synfron.reshaper.burp.core.utils;

import com.fasterxml.uuid.Generators;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.Encoder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

public class ValueGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static CustomProbabilityRandomizer<Integer> letterCountRandomizer;
    private static CustomProbabilityRandomizer<Character> consonantPopularityRandomizer;
    private static CustomProbabilityRandomizer<Character> vowelPopularityRandomizer;

    public static String uuidV3(String namespace, String name) {
        try {
            return Generators.nameBasedGenerator(StringUtils.isNotEmpty(namespace) ? UUID.fromString(namespace) : null, MessageDigest.getInstance("MD5")).generate(name).toString();
        } catch (NoSuchAlgorithmException e) {
            throw new WrappedException(e);
        }
    }

    public static String uuidV5(String namespace, String name) {
        try {
            return Generators.nameBasedGenerator(StringUtils.isNotEmpty(namespace) ? UUID.fromString(namespace) : null, MessageDigest.getInstance("SHA-1")).generate(name).toString();
        } catch (NoSuchAlgorithmException e) {
            throw new WrappedException(e);
        }
    }

    public static String uuidV4() {
        return Generators.randomBasedGenerator().generate().toString();
    }

    public static String ipAddressV4() {
        int[] ipParts = new int[4];
        Random random = new Random();

        ipParts[0] = random.nextInt(256);
        ipParts[1] = random.nextInt(256);
        ipParts[2] = random.nextInt(256);
        ipParts[3] = random.nextInt(256);

        if (
                (ipParts[0] == 10) ||
                        (ipParts[0] == 127) ||
                        (ipParts[0] == 192 && ipParts[1] == 168) ||
                        (ipParts[0] == 169 && ipParts[1] == 254) ||
                        (ipParts[0] == 172 && (ipParts[1] >= 16 && ipParts[1] <= 31)) ||
                        (ipParts[0] == 0 && ipParts[1] == 0 && ipParts[2] == 0 && ipParts[3] == 0) ||
                        (ipParts[0] == 255 && ipParts[1] == 255 && ipParts[2] == 255 && ipParts[3] == 255)
        ) {
            return ipAddressV4();
        }
        return String.format("%s.%s.%s.%s", ipParts[0], ipParts[1], ipParts[2], ipParts[3]);
    }

    public static String ipAddressV6() {
        byte[] bytes = new byte[16];
        try {
            random.nextBytes(bytes);
            return InetAddress.getByAddress(bytes).getHostAddress();
        } catch (UnknownHostException e) {
            throw new WrappedException(e);
        }
    }

    public static String currentDate(String format) {
        format = StringUtils.defaultString(format, "yyyy-MM-dd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return ZonedDateTime.now().format(formatter);
    }

    public static String currentTimestamp() {
        return Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
    }

    public static String timestamp(String minDate, String maxDate, String format) {
        format = StringUtils.defaultString(format, "yyyy-MM-dd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        ZonedDateTime now = ZonedDateTime.now();
        TemporalAccessor min = StringUtils.isNotEmpty(minDate) ? formatter.parseBest(minDate, ZonedDateTime::from, LocalDateTime::from, LocalDate::from) : now;
        TemporalAccessor max = StringUtils.isNotEmpty(maxDate) ? formatter.parseBest(maxDate, ZonedDateTime::from, LocalDateTime::from, LocalDate::from) : now;
        if (min instanceof ZonedDateTime) {
            return Long.toString(date((ZonedDateTime) min, (ZonedDateTime) max).toInstant().toEpochMilli());
        }
        if (min instanceof LocalDateTime) {
            return Long.toString(date((LocalDateTime) min, (LocalDateTime) max).toInstant(now.getOffset()).toEpochMilli());
        }
        return Long.toString(date((LocalDate) min, (LocalDate) max).toInstant(now.getOffset()).toEpochMilli());
    }

    public static String date(String minDate, String maxDate, String format) {
        format = StringUtils.defaultString(format, "yyyy-MM-dd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        ZonedDateTime now = ZonedDateTime.now();
        TemporalAccessor min = StringUtils.isNotEmpty(minDate) ? formatter.parseBest(minDate, ZonedDateTime::from, LocalDateTime::from, LocalDate::from) : now;
        TemporalAccessor max = StringUtils.isNotEmpty(maxDate) ? formatter.parseBest(maxDate, ZonedDateTime::from, LocalDateTime::from, LocalDate::from) : now;
        if (min instanceof ZonedDateTime) {
            return date((ZonedDateTime) min, (ZonedDateTime) max).format(formatter);
        }
        if (min instanceof LocalDateTime) {
            return date((LocalDateTime) min, (LocalDateTime) max).format(formatter);
        }
        return date((LocalDate) min, (LocalDate) max).format(formatter);
    }

    private static ZonedDateTime date(ZonedDateTime min, ZonedDateTime max) {
        return min.plus(random.nextLong(0L, max.toInstant().toEpochMilli() - min.toInstant().toEpochMilli()), ChronoUnit.MILLIS);
    }

    private static LocalDateTime date(LocalDateTime min, LocalDateTime max) {
        return min.plus(random.nextLong(0L, max.toInstant(ZoneOffset.UTC).toEpochMilli() - min.toInstant(ZoneOffset.UTC).toEpochMilli()), ChronoUnit.MILLIS);
    }

    private static LocalDateTime date(LocalDate min, LocalDate max) {
        return date(min.atStartOfDay(), max.atStartOfDay());
    }

    public static String password(int minLength, int maxLength, List<PasswordCharacterGroups> characterGroups) {
        String characters = characterGroups.stream().map(PasswordCharacterGroups::getCharacters).collect(Collectors.joining());
        List<Character> password = new ArrayList<>();
        int length = random.nextInt(minLength, maxLength + 1);
        for (int i = 0; i < length; i++) {
            if (i < characterGroups.size()) {
                String groupCharacters = characterGroups.get(i).getCharacters();
                password.add(groupCharacters.charAt(random.nextInt(0, groupCharacters.length())));
            } else {
                password.add(characters.charAt(random.nextInt(0, characters.length())));
            }
        }
        Collections.shuffle(password);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < password.size(); i++) {
            builder.append(password.get(i).charValue());
        }
        return builder.toString();
    }

    public static String paragraphs(int count, String separator) {
        return paragraphsFragment(count, separator, new StringBuilder()).toString();
    }

    private static StringBuilder paragraphsFragment(int count, String separator, StringBuilder builder) {
        for (int i = 0; i < count; i++) {
            paragraphFragment(builder);
            if (i + 1 < count) {
                builder.append(separator);
            }
        }
        return builder;
    }

    public static String paragraph() {
        return paragraphFragment(new StringBuilder()).toString();
    }

    private static StringBuilder paragraphFragment(StringBuilder builder) {
        Random random = new Random();
        int sentenceCount = random.nextInt(4, 8);
        for (int i = 0; i < sentenceCount; i++) {
            sentenceFragment(builder);
            if (i + 1 < sentenceCount) {
                builder.append(' ');
            }
        }
        return builder;
    }

    public static String sentences(int count, String separator) {
        return sentencesFragment(count, separator, new StringBuilder()).toString();
    }

    private static StringBuilder sentencesFragment(int count, String separator, StringBuilder builder) {
        for (int i = 0; i < count; i++) {
            sentenceFragment(builder);
            if (i + 1 < count) {
                builder.append(separator);
            }
        }
        return builder;
    }

    public static String sentence() {
        return sentenceFragment(new StringBuilder()).toString();
    }

    private static StringBuilder sentenceFragment(StringBuilder builder) {
        Random random = new Random();
        int wordCount = random.nextInt(4, 20);
        int commaPlacement = random.nextInt(5, 10);
        for (int i = 0; i < wordCount; i++) {
            wordFragment(i == 0, builder);
            if (i + 1 < wordCount) {
                if (i == commaPlacement) {
                    builder.append(',');
                    commaPlacement += random.nextInt(5, 10);
                }
                builder.append(' ');
            } else {
                builder.append('.');
            }
        }
        return builder;
    }

    public static String words(int count, String separator, boolean uppercaseFirst) {
        return wordsFragment(count, separator, uppercaseFirst, new StringBuilder()).toString();
    }

    private static StringBuilder wordsFragment(int count, String separator, boolean uppercaseFirst, StringBuilder builder) {
        for (int i = 0; i < count; i++) {
            wordFragment(uppercaseFirst, builder);
            if (i + 1 < count) {
                builder.append(separator);
            }
        }
        return builder;
    }

    public static String word(boolean uppercaseFirst) {
        return wordFragment(uppercaseFirst, new StringBuilder()).toString();
    }

    private static StringBuilder wordFragment(boolean uppercaseFirst, StringBuilder builder) {
        initLetterCountRandomizer();
        int count = letterCountRandomizer.next();
        if (count == 1) {
            builder.append(uppercase(vowelPopularityRandomizer.next(), uppercaseFirst));
        } else {
            boolean switchCharType = false;
            boolean isLastVowel = random.nextBoolean();
            for (int i = 0; i < count; i ++) {
                if (switchCharType) {
                    if (isLastVowel) {
                        builder.append(uppercase(consonantPopularityRandomizer.next(), false));
                    } else {
                        builder.append(uppercase(vowelPopularityRandomizer.next(), false));
                    }
                    isLastVowel = !isLastVowel;
                    switchCharType = false;
                } else {
                    boolean isVowel = random.nextBoolean();
                    if (isVowel) {
                        builder.append(uppercase(vowelPopularityRandomizer.next(), uppercaseFirst && i == 0));
                    } else {
                        builder.append(uppercase(consonantPopularityRandomizer.next(), uppercaseFirst && i == 0));
                    }
                    if (isLastVowel == isVowel) {
                        switchCharType = true;
                    }
                    isLastVowel = isVowel;
                }
                if (i == 0) {
                    switchCharType = true;
                }
            }
        }
        return builder;
    }

    private static char uppercase(char character, boolean shouldUppercase) {
        return shouldUppercase ? Character.toUpperCase(character) : character;
    }

    public static String bytes(int length, Encoder encoder) {
        byte[] bytesArr = new byte[length];
        random.nextBytes(bytesArr);
        return encoder.decode(bytesArr);
    }

    public static String integer(long minValue, long maxValue, int base) {
        return Long.toString(random.nextLong(minValue, maxValue), base);
    }

    private static void initLetterCountRandomizer() {
        if (letterCountRandomizer == null) {
            letterCountRandomizer = new CustomProbabilityRandomizer<>(random, Map.ofEntries(
                    Map.entry(1, 3),
                    Map.entry(2, 18),
                    Map.entry(3, 18),
                    Map.entry(4, 7),
                    Map.entry(5, 10),
                    Map.entry(6, 8),
                    Map.entry(7, 7),
                    Map.entry(8, 8),
                    Map.entry(9, 11),
                    Map.entry(10, 5),
                    Map.entry(11, 4),
                    Map.entry(12, 2),
                    Map.entry(13, 1)
            ));
            consonantPopularityRandomizer = new CustomProbabilityRandomizer<>(random, Map.ofEntries(
                    Map.entry('r', 758),
                    Map.entry('t', 695),
                    Map.entry('n', 665),
                    Map.entry('s', 573),
                    Map.entry('l', 549),
                    Map.entry('c', 454),
                    Map.entry('d', 338),
                    Map.entry('m', 300),
                    Map.entry('p', 190),
                    Map.entry('h', 300),
                    Map.entry('g', 247),
                    Map.entry('b', 207),
                    Map.entry('y', 178),
                    Map.entry('v', 101),
                    Map.entry('k', 110),
                    Map.entry('j', 20),
                    Map.entry('x', 29),
                    Map.entry('q', 20),
                    Map.entry('z', 27)
            ));
            vowelPopularityRandomizer = new CustomProbabilityRandomizer<>(random, Map.ofEntries(
                    Map.entry('e', 1116),
                    Map.entry('a', 850),
                    Map.entry('i', 754),
                    Map.entry('o', 716),
                    Map.entry('u', 363)
            ));
        }
    }
}
