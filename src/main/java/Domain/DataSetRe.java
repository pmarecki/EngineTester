package Domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by pm on 12/17/15.
 */
public interface DataSetRe extends CrudRepository<DataSet,Integer>{
    List<DataSet> findByCatid(Integer catid);

    @Transactional
    Long removeByDatasetid(Integer datasetid);
}
