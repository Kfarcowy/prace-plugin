package pl.przyklad.prace.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Zamienia zwykle litery A-Z (bez Q, X i Y - brak dla nich odpowiednikow
 * unicode small caps, zostaja jako zwykle male litery) na male kapitaliki
 * unicode (small caps). Cyfry, znaki specjalne, polskie znaki diakrytyczne
 * i kolory (& / sekcja) przechodza bez zmian.
 */
public final class SmallCaps {

    private static final Map<Character, Character> MAP = new HashMap<>();

    static {
        MAP.put('A', 'ᴀ');
        MAP.put('B', 'ʙ');
        MAP.put('C', 'ᴄ');
        MAP.put('D', 'ᴅ');
        MAP.put('E', 'ᴇ');
        MAP.put('F', 'ғ');
        MAP.put('G', 'ɢ');
        MAP.put('H', 'ʜ');
        MAP.put('I', 'ɪ');
        MAP.put('J', 'ᴊ');
        MAP.put('K', 'ᴋ');
        MAP.put('L', 'ʟ');
        MAP.put('M', 'ᴍ');
        MAP.put('N', 'ɴ');
        MAP.put('O', 'ᴏ');
        MAP.put('P', 'ᴘ');
        // Q pominiete (brak odpowiednika, zgodnie z ustaleniem)
        MAP.put('R', 'ʀ');
        MAP.put('S', 'ꜱ');
        MAP.put('T', 'ᴛ');
        MAP.put('U', 'ᴜ');
        MAP.put('V', 'ᴠ');
        MAP.put('W', 'ᴡ');
        // X, Y - brak oficjalnego unicode small caps, zostawiamy zwykle male litery
        MAP.put('X', 'x');
        MAP.put('Y', 'y');
        MAP.put('Z', 'ᴢ');
    }

    private SmallCaps() {
    }

    public static String convert(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            char upper = Character.toUpperCase(c);
            Character mapped = MAP.get(upper);
            sb.append(mapped != null ? mapped : c);
        }
        return sb.toString();
    }
}
