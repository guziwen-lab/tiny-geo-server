package com.supermap.modules.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermap.modules.sys.dto.AdministrativeDivisionCodeDTO;
import com.supermap.modules.sys.entity.AdministrativeDivisionCodeEntity;
import com.supermap.modules.sys.vo.ProvinceAndCityVO;

import java.util.List;

/**
 * 行政区划代码表
 *
 * @author gzw
 */
public interface AdministrativeDivisionCodeService extends IService<AdministrativeDivisionCodeEntity> {

    Page<AdministrativeDivisionCodeEntity> queryPage(AdministrativeDivisionCodeDTO dto);

    List<AdministrativeDivisionCodeEntity> getSubordinates(String code);

    List<AdministrativeDivisionCodeEntity> getAllSubordinates(String code);

    AdministrativeDivisionCodeEntity getSuperior(String code);

    ProvinceAndCityVO getProvinceAndCityByDistrictCode(String code);

    List<AdministrativeDivisionCodeEntity> tree(String administrativeDivisionCode);

    List<AdministrativeDivisionCodeEntity> tree();

    List<AdministrativeDivisionCodeEntity> getProvinces();

    AdministrativeDivisionCodeEntity getByCode(String code);

    /**
     * 根据省市县获取行政区划代码
     *
     * @param province 省
     * @param city     市（如果是直辖市，传县名）
     * @param county   区县
     * @return {@link  AdministrativeDivisionCodeEntity}
     */
    AdministrativeDivisionCodeEntity getByProvinceAndCityAndCounty(String province, String city, String county);

    List<String> getCodeByCode(String code);

}

