package Domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by pm on 12/17/15.
 */
public interface ADataRe extends CrudRepository<AData,Integer> {
    List<AData> findByCatid(Integer catid);
    @Transactional
    Long removeByDataid(Integer dataid);
}
