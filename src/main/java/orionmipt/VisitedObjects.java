package orionmipt;

//- Wrapper for objects that get visited.
class VisitedObject {
    Object obj;

    VisitedObject(Object obj) {
        this.obj = obj;
    }

    public boolean equals(Object other) {
        return obj == ((VisitedObject) other).obj;
    }

    public int hashCode() {
        return System.identityHashCode(obj);
    }
}
