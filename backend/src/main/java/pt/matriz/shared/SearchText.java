package pt.matriz.shared;

import java.text.Normalizer;
import java.util.Locale;

public final class SearchText {
    private SearchText() {}
    public static String fold(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
    }
}
