package Domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pm on 12/17/15.
 */
public interface ResultRe extends CrudRepository<Result,Integer> {
    @Transactional
    Long removeByEngineid(Integer engineid);

    @Transactional
    Long removeByEngineidAndDataid(Integer engineid, Integer dataid);

    @Transactional
    Long removeByDataid(Integer dataid);

    Result findByEngineidAndDataid(Integer engineid, Integer dataid);
}
