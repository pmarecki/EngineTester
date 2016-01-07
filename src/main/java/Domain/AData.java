package Domain;

/**
 * Created by pm on 12/17/15.
 */

import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name="data")
public class AData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer dataid;
    Integer catid;
    String filename;

}
