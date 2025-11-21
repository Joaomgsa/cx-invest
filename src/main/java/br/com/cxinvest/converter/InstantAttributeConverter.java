package br.com.cxinvest.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.sql.Timestamp;
import java.util.regex.Pattern;


@Converter(autoApply = false)
public class InstantAttributeConverter implements AttributeConverter<Instant, String> {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;
    // pattern for 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss.SSS...' (space between date and time)
    private static final Pattern SQL_DATETIME_REGEX = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}(\\\\.\\d+)?$");

    @Override
    public String convertToDatabaseColumn(Instant instant) {
        return instant == null ? null : ISO.format(instant);
    }

    @Override
    public Instant convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;

        String raw = dbData.trim();

        // 1) Try ISO instant (e.g. 2025-11-16T16:00:00Z)
        try {
            return Instant.parse(raw);
        } catch (Exception ignored) {
        }

        // Normalize: remove surrounding quotes if any, normalize whitespace
        String normalized = raw;
        // Remove trailing Z or timezone offsets like +00:00
        if (normalized.endsWith("Z")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        } else {
            // remove timezone offsets like +00:00 or -03:00
            int plus = Math.max(normalized.lastIndexOf('+'), normalized.lastIndexOf('-'));
            if (plus > normalized.indexOf(' ')) {
                // offset exists after date/time separator
                normalized = normalized.substring(0, plus);
            }
        }

        // Replace 'T' with space to match SQL-style timestamp if present
        normalized = normalized.replace('T', ' ').trim();

        // If there's anything after fractional seconds that's non-digit, strip it
        int dot = normalized.indexOf('.');
        if (dot >= 0) {
            int i = dot + 1;
            while (i < normalized.length() && Character.isDigit(normalized.charAt(i))) i++;
            normalized = normalized.substring(0, Math.min(normalized.length(), i));
            // ensure we keep the seconds part if fraction removed incorrectly
            if (normalized.endsWith(".")) normalized = normalized.substring(0, normalized.length() - 1);
        }

        // 2) If matches SQL datetime pattern, use Timestamp.valueOf (robust for space-separated SQL timestamps)
        try {
            if (SQL_DATETIME_REGEX.matcher(normalized).matches()) {
                Timestamp ts = Timestamp.valueOf(normalized);
                return ts.toInstant();
            }
        } catch (Exception ignored) {
        }

        // 3) Try parsing with a formatter that accepts optional fraction
        try {
            DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd HH:mm:ss")
                    .optionalStart()
                    .appendLiteral('.')
                    .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, false)
                    .optionalEnd()
                    .toFormatter();

            LocalDateTime ldt = LocalDateTime.parse(normalized, dtf);
            return ldt.toInstant(ZoneOffset.UTC);
        } catch (Exception ignored) {
        }

        throw new IllegalArgumentException("Não foi possível converter data do BD: " + dbData);
    }
}
