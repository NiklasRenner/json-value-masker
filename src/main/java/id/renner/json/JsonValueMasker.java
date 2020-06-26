package id.renner.json;

import id.renner.json.transform.MaskValueTransformer;

import java.io.InputStream;
import java.util.Set;

public class JsonValueMasker {

    public String mask(InputStream inputStream, Set<String> maskFields) {
        var parser = new StreamingJsonParser(inputStream, new MaskValueTransformer(maskFields));
        return parser.parse();
    }
}
