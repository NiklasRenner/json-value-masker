package id.renner.json.transform;

import id.renner.json.level.Level;

import java.util.Set;

public class MaskValueTransformer implements ValueTransformer {
    private final Set<String> maskedFields;

    public MaskValueTransformer(Set<String> maskedFields) {
        this.maskedFields = maskedFields;
    }

    @Override
    public String transform(Level context, String value) {
        if (maskedFields.contains(context.getCurrentKeyName())) {
            return "*".repeat(value.length());
        }

        return value;
    }
}
