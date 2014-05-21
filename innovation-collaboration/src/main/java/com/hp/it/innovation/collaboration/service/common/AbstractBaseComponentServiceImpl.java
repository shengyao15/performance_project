package com.hp.it.innovation.collaboration.service.common;

import com.hp.it.innovation.collaboration.dao.BaseDAO;
import com.hp.it.innovation.collaboration.dto.ComponentDTO;
import com.hp.it.innovation.collaboration.model.Component;

public abstract class AbstractBaseComponentServiceImpl<T extends Component, DTO extends ComponentDTO, D extends BaseDAO<T, Long>>
                                                                                                                                  extends
                                                                                                                                  AbstractService<T, D, Long> {
}
