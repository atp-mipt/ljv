package orionmipt;

import java.util.IdentityHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private final AtomicInteger inc = new AtomicInteger(0);

    private final IdentityHashMap<Object, String> objectsId = new IdentityHashMap<>();


    String getId(Object obj) {
        if (!objectsId.containsKey(obj)) {
            objectsId.put(obj, "n" + inc.incrementAndGet());
        }
        return objectsId.get(obj);
    }

}
