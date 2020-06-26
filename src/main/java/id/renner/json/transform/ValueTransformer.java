package id.renner.json.transform;

import id.renner.json.level.Level;

public interface ValueTransformer {

    String transform(Level context, String value);
}
