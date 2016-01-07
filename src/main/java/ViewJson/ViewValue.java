package ViewJson;

import Domain.Result;
import lombok.Data;

/**
 * Class reporting results of computation to the front end.
 */
@Data
public class ViewValue {
    Double value;
    Integer timems;
    String status;
    Integer nextReloadMs;   //when to ask for update
}
