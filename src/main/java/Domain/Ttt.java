package Domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by pm on 12/17/15.
 */

@Data
@Entity
public class Ttt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer tttid;
    String txt;
}
