package Domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by pm on 12/17/15.
 */
public interface EngineRe  extends CrudRepository<Engine,Integer>{
    List<Engine> findByCatid(Integer catid);

    @Transactional
    Long removeByEngineid(Integer engineid);

}
