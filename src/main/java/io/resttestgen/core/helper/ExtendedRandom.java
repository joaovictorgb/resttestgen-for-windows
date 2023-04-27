package io.resttestgen.core.helper;

import com.google.common.io.Resources;
import org.iban4j.CountryCode;
import org.iban4j.Iban;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Extension of the java.util.Random class providing primitives for random strings, lengths, and other.
 */
public class ExtendedRandom extends Random {

    /**
     * Returns a positive integer.
     * @return the positive integer.
     */
    public int nextNonNegativeInt() {
        return Math.abs(nextInt());
    }
    /**
     * Generates a random length (integer) value according to the minimum and maximum values provided. Useful for
     * strings or arrays lengths.
     * @param minLength minimum length, null = 0
     * @param maxLength maximum length, if null no maximum value is used
     * @return an integer value among minLength (or 0 if null) and maxLength, or without bound if maxLength == null
     */
    public Integer nextLength(Integer minLength, Integer maxLength) {
        if (maxLength != null && maxLength == 0) {
            return 0;
        }
        if (minLength == null || minLength < 0) {
            minLength = 0;
        }
        int length = (int) Math.abs(this.nextGaussian() * 10) + minLength;
        if (maxLength != null) {
            if (length > maxLength) {
                length -= length - maxLength + nextInt(maxLength - minLength);
            }
        }
        return length;
    }

    /**
     * Generates a random string in different formats, of a random length.
     * @return the generated string.
     */
    public String nextString() {
        return nextString(nextLength(0, 127));
    }

    /**
     * Generates a random string in different formats, of a given length.
     * @param length the wanted length for the string.
     * @return the generated string.
     */
    public String nextString(int length) {
        int p = nextInt(100);

        if (p < 60) {
            return nextWord(length);
        } else if (p < 70) {
            return nextPhrase(length);
        } else if (p < 72) {
            return nextPhoneNumber();
        } else if (p < 74) {
            return nextEmail();
        } else if (p < 76) {
            return nextDate();
        } else if (p < 78) {
            return nextTime();
        } else if (p < 80) {
            return nextUUID();
        } else if (p < 82) {
            return nextHex(length);
        } else if (p < 84) {
            return nextURI();
        } else if (p < 86) {
            return nextSSN();
        } else if (p < 88) {
            return nextNumeric();
        } else if (p < 90) {
            return nextBase64();
        } else if (p < 92) {
            return nextIBAN();
        } else if (p < 93) {
            return nextIPV4();
        } else if (p < 94) {
            return nextIPV6();
        }
        return nextRandomString(length);
    }

    /**
     * Generates a string of random characters with a random length.
     * @return the generated string.
     */
    public String nextRandomString() {
        return nextRandomString(nextLength(0, 127));
    }

    /**
     * Generates a random string within the given lengths
     * @param length length of the string
     * @return the generated string
     */
    public String nextRandomString(int length) {
        // Characters that are common in strings
        String commonChars = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

        // Characters that are special, but not rare
        String specialChars = ".:,;-_#+*'\"?=/\\!$@&%€£<>";

        // Characters that are rare
        String rareChars = "àèìòùéç§°^|";

        // StringBuilder to which random characters are appended
        StringBuilder generatedString = new StringBuilder();

        String sourceString;
        for (int i = 0; i < length; i++) {
            int p = this.nextInt(1000);
            if (p < 955) {
                sourceString = commonChars;
            } else if (p < 992) {
                sourceString = specialChars;
            } else {
                sourceString = rareChars;
            }
            int index = this.nextInt(sourceString.length());

            // Double quotes are escaped to prevent errors in JSON. FIXME: apply escaping only to JSON renderer
            if (sourceString.charAt(index) == '"') {
                generatedString.append("\\\"");
            } else if (sourceString.charAt(index) == '\\') {
                generatedString.append("\\\\");
            } else {
                generatedString.append(sourceString.charAt(index));
            }
        }

        return generatedString.toString();
    }

    /**
     * Returns a random word.
     * @return the word.
     */
    public String nextWord() {
        return nextWord(nextLength(1, 19));
    }

    /**
     * Returns an English word of the given length
     * @param length length of the word (From 1 to 19 both inclusive)
     * @return the word
     */
    public String nextWord(int length) {
        if (length > 19 || length < 1) {
            return nextWord();
        }

        URL url = Resources.getResource("random_word/word" + length + ".txt");
        try {
            String text = Resources.toString(url, StandardCharsets.UTF_8);
            String[] list = text.split("\n");
            return list[nextInt(list.length)].replace("\r", "").replace("\n", "");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Returns a random date (from 2000/01/01 inclusive to 2020/01/01 inclusive)
     * @return the date
     */
    public String nextDate() { return nextDate(LocalDate.of(2000, 1, 1),
            LocalDate.of(2020, 1, 1)); }

    /**
     * Returns a random date between min and max
     * @param min inclusive min date
     * @param max inclusive max date
     * @return the date
     */
    public String nextDate(LocalDate min, LocalDate max) {
        return nextDateTime(LocalDateTime.of(min, LocalTime.MIN), LocalDateTime.of(max, LocalTime.MIN),
                "yyyy/MM/dd");
    }

    /**
     * Returns a random date and time (from 2000/01/01 inclusive to 2020/01/02 inclusive)
     * @return the date
     */
    public String nextDateTime() {
        return nextDateTime(LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0)); }

    /**
     * Returns a random date and time between min and max
     * @param min inclusive min date and time
     * @param max inclusive min date and time
     * @return the date
     */
    public String nextDateTime(LocalDateTime min, LocalDateTime max) {
        return nextDateTime(min, max, "yyyy/MM/dd HH:mm:ss");
    }

    public String nextTime()
    {
        return nextTime(LocalTime.of(0, 0, 0), LocalTime.of(23, 59, 59));
    }
    public String nextTime(LocalTime min, LocalTime max)
    {
        return nextDateTime(LocalDateTime.of(2000, 1, 1, min.getHour(), min.getMinute(), min.getSecond()),
                LocalDateTime.of(2000, 1, 1, max.getHour(), max.getMinute(), max.getSecond()), "HH:mm:ss");
    }

    public String nextTimeDuration()
    {
        return nextTime(LocalTime.of(0, 0, 0), LocalTime.of(0, 1, 0));
    }

    /**
     * Returns a random date between min and max with a specific format
     * @param min inclusive min date and time
     * @param max inclusive min date and time
     * @param format requested string format
     * @return string with the requested format
     */
    private String nextDateTime(LocalDateTime min, LocalDateTime max, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        long seconds = nextLongInclusive(0, SECONDS.between(min, max));
        LocalDateTime randomDate = min.plusSeconds(seconds);
        return randomDate.format(dateTimeFormatter);
    }

    /**
     * Returns a random binary string
     * @return the random binary string
     */
    public String nextBinaryString() {
        return nextBinaryString(nextIntBetween(1, 256));
    }

    /**
     * Returns a random binary string with a specific length.
     * @param length the length of the string.
     * @return the random binary string.
     */
    public String nextBinaryString(int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (nextInt(2) == 0) {
                s.append("1");
            }
            else {
                s.append("0");
            }
        }

        return s.toString();
    }

    /**
     * Returns a random base64 string.
     * @return the random string in base64.
     */
    public String nextBase64() {
        byte[] randomBytes = new byte[nextIntBetween(1, 100)];
        nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }

    /**
     * Returns a UUID string.
     * @return the UUID string.
     */
    public String nextUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns a random hex string
     * @return the hex string
     */
    public String nextHex() {
        return nextHex(32);
    }

    /**
     * Returns a random hex string with a specific length
     * @param length the length of the hex string
     * @return the hex string
     */
    public String nextHex(int length) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(Integer.toHexString(nextInt()));
        }
        sb.setLength(length);
        return sb.toString();
    }

    /**
     * Return a new random IBAN string
     * @return the IBAN string
     */
    public String nextIBAN() {
        return Iban.random().toFormattedString();
    }

    /**
     * Returns a new random IBAN with the specified string
     * @param country the first two char of the country (example: IT)
     * @return the IBAN string
     */
    public String nextIBAN(CountryCode country) {
        return Iban.random(country).toFormattedString();
    }

    enum TypeSSN {        /**
         * xxx-xx-xxx
         */
        First,        /**
         * xxxxxxxxx
         */
        Second,
        /**
         * xxx-xxxxxx
         */
        Third,
        /**
         * xxxxx-xxxx
         */
        Fourth
    }

    /**
     * Returns a new random Social Security Number
     * @return the SSN string
     */
    public String nextSSN()
    {
        return nextSSN(TypeSSN.values()[nextInt(4)]);
    }

    /**
     * Returns a new random Social Security Number with a specific format
     * @param typeSSN the format of the SSN
     * @return the SSN string
     */
    public String nextSSN(TypeSSN typeSSN)
    {
        switch (typeSSN.ordinal())
        {
            case 1:
                return nextSSNSecondFormat();
            case 2:
                return nextSSNThirdFormat();
            case 3:
                return nextSSNFourthFormat();
            default:
                return nextSSNFirstFormat();
        }
    }

    /**
     * First format of the SSN
     * @return the SSN String
     */
    private String nextSSNFirstFormat() {
        return nextNumeric(3) + "-" + nextNumeric(2) + "-" +
                nextNumeric(3);
    }

    public <E> Optional<E> nextElement(Collection<E> e) {
        if (e.size() == 0) {
            return Optional.empty();
        }
        return e.stream().skip(nextInt(e.size())).findFirst();
    }

    public Double nextDouble(Double min, Double max) {
        if (min <= max) {
            return min + nextDouble() * (max - min);
        }
        return null;
    }

    /**
     * Second format of the SSN
     * @return the SSN String
     */
    private String nextSSNSecondFormat() {
        return nextNumeric(9);
    }
    /**
     * Third format of the SSN
     * @return the SSN String
     */
    private String nextSSNThirdFormat() {
        return nextNumeric(3) + "-" + nextNumeric(6);
    }

    /**
     * Fourth format of the SSN
     * @return the SSN String
     */
    private String nextSSNFourthFormat() {
        return nextNumeric(5) + "-" + nextNumeric(4);
    }

    public String nextPhrase() {
        return nextPhrase(100);
    }

    public String nextPhrase(int length) {
        StringBuilder s = new StringBuilder();
        while (s.length() < length) {
            s.append(nextWord(nextIntBetweenInclusive(1, Math.min(length - s.length() + 1, 19)))).append(" ");
        }
        s.setLength(length);
        return s.toString();
    }

    public String nextLetterString() {
        return nextLetterString(10);
    }
    public String nextLetterString(int length) {
        String base = "qwertyuiopasdfghjklzxcvbnm";
        StringBuilder s = new StringBuilder();
        while (s.length() < length)
        {
            s.append(base.charAt(nextIntBetween(0, base.length())));
        }
        return s.toString();
    }

    /**
     * Returns a string with base 10 numerical characters
     * @return the numeric string
     */
    public String nextNumeric()
    {
        return nextNumeric(10);
    }

    /**
     * Returns a string with base 10 numerical characters with the specified length
     * @param length the length of the string
     * @return the numeric string
     */
    public String nextNumeric(int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            s.append(nextInt(10));
        }
        return s.toString();
    }

    /**
     * Return a random phone number
     * @return the random phone number
     */
    public String nextPhoneNumber()
    {
        switch (nextIntBetweenInclusive(0, 5)) {
            default:
            case 0:
                return String.format("%04d", nextLength(0, 9999)) + nextNumeric(7);
            case 1:
                return String.format("%04d", nextLength(0, 9999)) + " " + nextNumeric(7);
            case 2:
                return "+" + nextIntBetweenInclusive(0, 9999) + " " + nextNumeric(11);
            case 3:
                return "+" + nextIntBetweenInclusive(0, 9999) + nextNumeric(11);
            case 4:
                return "(+" + nextIntBetweenInclusive(0, 9999) + ") " + nextNumeric(11);
            case 5:
                return "+" + nextIntBetweenInclusive(0, 9999) + "-" + nextNumeric(11);
        }
    }
    /**
     * Return a random email (example@site.com)
     * @return the random email
     */
    public String nextEmail()
    {
        String[] domain = new String[] {"gmail.com", "live.com", "outlook.com"};

        String email;
        if (nextIntBetweenInclusive(0, 100) < 80) {
            email = domain[nextInt(domain.length)];
        }
        else {
            email = nextDomain(true);
        }

        return nextLetterString(nextIntBetween(1, 10)) + "@" + email;
    }

    public String nextURI() {
        StringBuilder str = new StringBuilder(nextProtocol() + "://" + nextDomain(true) + "/");

        for (int i = 0; i < nextInt(2); i++) {
            str.append(nextString(10)).append("/");
        }

        return str.toString();
    }

    public String nextIPV4() {
        return nextIntBetweenInclusive(0, 254) + "." +
                nextIntBetweenInclusive(0, 254) + "." +
                nextIntBetweenInclusive(0, 254) + "." +
                nextIntBetweenInclusive(0, 254);
    }
    /**
     * This method generates IPv6 addresses
     */
    public  String nextIPV6() {
        StringJoiner joiner = new StringJoiner(":");

        for (int i = 0; i < generateIPv6Address().length; i++) {
            joiner.add(generateIPv6Address()[i]);
        }

        return joiner.toString();
    }

    private final char[] scope = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private String[] generateIPv6Address(){
        String[] randAddress = {"", "", "", "", "", "", "", ""};

        for (int i = 0; i < randAddress.length; i++) {
            for(int j = 0; j < 4; j++){
                randAddress[i] = randAddress[i] + scope[nextIntBetween(0, 15 + 1)];
            }
        }

        return randAddress;
    }

    public String nextDomain(boolean topDomain) {
        String[] topDomainString = {"com", "it", "info", "ch", "de", "gov", "co.uk", "io", "me"};

        StringBuilder s = new StringBuilder(nextLetterString(nextIntBetween(1, 10)));
        for (int i = 0; i < nextInt(2); i++) {
            s.append(".").append(nextLetterString(nextIntBetweenInclusive(1, 10)));
        }

        if (topDomain) {
            s.append(".").append(topDomainString[nextInt(topDomainString.length)]);
        }

        return s.toString();
    }

    public String nextProtocol() {
        return "http";
    }

    /**
     * Returns a random integer between start (inclusive) and end (exclusive)
     * @param start from (inclusive)
     * @param end to (exclusive)
     * @return the random integer
     */
    public int nextIntBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start - 1));
    }

    /**
     * Returns a random integer between start (inclusive) and end (exclusive)
     * @param start from (inclusive)
     * @param end to (inclusive)
     * @return the random integer
     */
    public int nextIntBetweenInclusive(int start, int end) {
        return nextIntBetween(start, end + 1);
    }

    /**
     * Returns a random long between start (inclusive) and end (exclusive)
     * @param start from (inclusive)
     * @param end to (exclusive)
     * @return the random long
     */
    public long nextLong(long start, long end) {
        return start + ((long) (nextDouble() * (end - start - 1)));
    }

    /**
     * Returns a random long between start (inclusive) and end (exclusive)
     * @param start from (inclusive)
     * @param end to (exclusive)
     * @return the random long
     */
    public long nextLongInclusive(long start, long end) {
        return nextLong(start, end + 1);
    }

}
