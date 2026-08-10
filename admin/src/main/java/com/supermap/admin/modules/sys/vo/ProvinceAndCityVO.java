package com.supermap.admin.modules.sys.vo;

import com.supermap.admin.modules.sys.entity.AdministrativeDivisionCodeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author gzw
 */
@Data
@AllArgsConstructor
public class ProvinceAndCityVO {

    private AdministrativeDivisionCodeEntity province;

    private AdministrativeDivisionCodeEntity city;

}
