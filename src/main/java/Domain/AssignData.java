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
public class AssignData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer inid;
    Integer datasetid;
    Integer dataid;

    public AssignData(){}
    public AssignData(Integer dsid, Integer did){
        datasetid = dsid;
        dataid = did;
    }
}