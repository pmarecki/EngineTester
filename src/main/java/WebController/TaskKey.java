package WebController;
import lombok.Data;

/**
 * Key for structures cashing the status of computation.
 */

@Data
public class TaskKey {
    int engineid;
    int dataid;

    public TaskKey() {
    }

    public TaskKey(int engineid, int dataid) {
        this.engineid = engineid;
        this.dataid = dataid;
    }
}
