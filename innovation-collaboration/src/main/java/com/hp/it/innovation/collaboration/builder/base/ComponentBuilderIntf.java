package com.hp.it.innovation.collaboration.builder.base;

import com.hp.it.innovation.collaboration.dto.ComponentDTO;
import com.hp.it.innovation.collaboration.model.Component;

public interface ComponentBuilderIntf<T extends Component, DTO extends ComponentDTO> {
    public void transferBaseEntityToDTO(Component entity, ComponentDTO dto, boolean includeSite);

    public void transferBaseDTOToEntity(ComponentDTO dto, Component entity, boolean includeSite);

    public DTO getComponent(T entity);

    public T getComponent(DTO dto);

}
