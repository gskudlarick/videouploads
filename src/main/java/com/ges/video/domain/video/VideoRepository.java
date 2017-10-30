package com.ges.video.domain.video;

import org.springframework.data.repository.CrudRepository;






//CRUD refers Create, Read, Update, Delete

public interface VideoRepository extends CrudRepository<Video, Long> {
	
	Video findByMediaId(int media_id);

}
