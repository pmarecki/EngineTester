package Domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

//import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by pm on 12/17/15.
 *
 * Transactional on class not needed.
 */
@Transactional
public interface AssignDataRe extends CrudRepository<AssignData, Integer> {
    List<AssignData> findByDatasetid(Integer datasetid);
    @Transactional
    Long removeByDatasetid(Integer datasetid);
    @Transactional
    Long removeByDataid(Integer dataid);
}
