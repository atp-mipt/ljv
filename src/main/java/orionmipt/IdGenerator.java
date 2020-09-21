package orionmipt;

import java.util.IdentityHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private Integer inc = 0;

    private final IdentityHashMap<Object, String> objectsId = new IdentityHashMap<>();

    synchronized String getId(Object obj) {
        if (!objectsId.containsKey(obj)) {
            objectsId.put(obj, "n" + ++inc);
        }
        return objectsId.get(obj);
    }

}
