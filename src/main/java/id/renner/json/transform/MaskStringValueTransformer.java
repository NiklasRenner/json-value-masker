package id.renner.json.transform;

import id.renner.json.level.Level;
import id.renner.json.level.ObjectLevel;

import java.util.Set;

public class MaskStringValueTransformer implements StringValueTransformer {
    private final Set<String> maskedFields;

    public MaskStringValueTransformer(Set<String> maskedFields) {
        this.maskedFields = maskedFields;
    }

    @Override
    public String transform(Level level, String value) {
        if (level instanceof ObjectLevel) {
            var objectLevel = (ObjectLevel) level;
            if (maskedFields.contains(objectLevel.getCurrentKeyName())) {
                return "*".repeat(value.length());
            }
        }

        return value;
    }
}
