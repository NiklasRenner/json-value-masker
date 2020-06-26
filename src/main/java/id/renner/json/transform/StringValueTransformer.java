package id.renner.json.transform;

import id.renner.json.level.Level;

public interface StringValueTransformer {

    String transform(Level context, String value);
}
