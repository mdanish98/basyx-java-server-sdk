package org.eclipse.digitaltwin.basyx.core;

import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface BaSyxCrudRepository<T> extends CrudRepository<T, String> {
	
	@NonNull Iterable<T> findAll(PaginationInfo paginationInfo);

}
