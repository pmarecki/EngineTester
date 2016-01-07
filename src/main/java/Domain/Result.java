package Domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Only valid results stored here;
 *
 * Enough info to store temporary result
 */
@Data
@Entity
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer resultid;
    Integer catid;
    Integer engineid;
    Integer dataid;

    String filename;

}

