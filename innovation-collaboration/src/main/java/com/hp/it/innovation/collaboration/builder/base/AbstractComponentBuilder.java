package com.hp.it.innovation.collaboration.builder.base;

import com.hp.it.innovation.collaboration.dto.ComponentDTO;
import com.hp.it.innovation.collaboration.model.Component;

public abstract class AbstractComponentBuilder<T extends Component, DTO extends ComponentDTO> implements
                                                                                              ComponentBuilderIntf<T, DTO> {
    public void transferBaseDTOToEntity(ComponentDTO dto, Component entity, boolean includeSite) {
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCreateDate(dto.getCreateDate());
        entity.setUpdateDate(dto.getUpdateDate());
        if (includeSite) {
            //TODO transfer site dto
        }
    }

    public void transferBaseEntityToDTO(Component entity, ComponentDTO dto, boolean includeSite) {
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        if (includeSite) {
            //TODO transfer site entity
        }
    }

}
