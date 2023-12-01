package canape.benjamin.runflutterrun.repositories;

import canape.benjamin.runflutterrun.model.ActivityComment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityCommentRepository extends CrudRepository<ActivityComment, Long> {
}