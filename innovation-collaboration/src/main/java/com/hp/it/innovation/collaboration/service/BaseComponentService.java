package com.hp.it.innovation.collaboration.service;

import com.hp.it.innovation.collaboration.dao.BaseDAO;
import com.hp.it.innovation.collaboration.dto.ComponentDTO;
import com.hp.it.innovation.collaboration.model.Component;

public interface BaseComponentService<T extends Component, DTO extends ComponentDTO, D extends BaseDAO<T, Long>> extends
                                                                                                               BaseService<T, D, Long> {
}
