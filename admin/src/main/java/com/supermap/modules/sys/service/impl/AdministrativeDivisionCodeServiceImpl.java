package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermap.common.util.StringUtils;
import com.supermap.modules.sys.dao.AdministrativeDivisionCodeDao;
import com.supermap.modules.sys.dto.AdministrativeDivisionCodeDTO;
import com.supermap.modules.sys.entity.AdministrativeDivisionCodeEntity;
import com.supermap.modules.sys.service.AdministrativeDivisionCodeService;
import com.supermap.modules.sys.vo.ProvinceAndCityVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("administrativeDivisionCodeService")
public class AdministrativeDivisionCodeServiceImpl extends ServiceImpl<AdministrativeDivisionCodeDao, AdministrativeDivisionCodeEntity> implements AdministrativeDivisionCodeService {

    @Override
    public Page<AdministrativeDivisionCodeEntity> queryPage(AdministrativeDivisionCodeDTO dto) {
        LambdaQueryWrapper<AdministrativeDivisionCodeEntity> wrapper = new LambdaQueryWrapper<>(AdministrativeDivisionCodeEntity.class);
        return page(dto.page(), wrapper);
    }

    @Override
    public List<AdministrativeDivisionCodeEntity> getSubordinates(String code) {
        return list(new LambdaQueryWrapper<>(AdministrativeDivisionCodeEntity.class)
                .eq(AdministrativeDivisionCodeEntity::getPcode, code));
    }

    @Override
    public List<AdministrativeDivisionCodeEntity> getAllSubordinates(String code) {
        return baseMapper.getAllSubordinates(code);
    }

    @Override
    public AdministrativeDivisionCodeEntity getSuperior(String code) {
        AdministrativeDivisionCodeEntity entity = getByCode(code);
        if (entity == null)
            throw new RuntimeException("[" + code + "]的行政区划代码不存在");
        return getByCode(entity.getPcode());
    }

    @Override
    public ProvinceAndCityVO getProvinceAndCityByDistrictCode(String code) {
        AdministrativeDivisionCodeEntity city = getSuperior(code);
        AdministrativeDivisionCodeEntity province = getSuperior(city.getCode());
        return new ProvinceAndCityVO(province, city);
    }

    @Override
    public List<AdministrativeDivisionCodeEntity> tree(String administrativeDivisionCode) {
        if (StringUtils.isNotEmpty(administrativeDivisionCode)) {
            AdministrativeDivisionCodeEntity base = getByCode(administrativeDivisionCode);
            if (base == null)
                throw new RuntimeException("[" + administrativeDivisionCode + "]的行政区划代码不存在");

            List<AdministrativeDivisionCodeEntity> allSubordinates = getAllSubordinates(administrativeDivisionCode);
            buildTree(base, allSubordinates);
            return List.of(base);
        }

        List<AdministrativeDivisionCodeEntity> all = list();
        List<AdministrativeDivisionCodeEntity> roots = all.stream()
                .filter(e -> e.getLevel() == 1)
                .collect(Collectors.toList());
        roots.forEach(e -> buildTree(e, all));
        return roots;
    }

    @Override
    public List<AdministrativeDivisionCodeEntity> tree() {
        return tree(null);
    }

    @Override
    public List<AdministrativeDivisionCodeEntity> getProvinces() {
        return list(new LambdaQueryWrapper<>(AdministrativeDivisionCodeEntity.class)
                .eq(AdministrativeDivisionCodeEntity::getLevel, 1));
    }

    @Override
    public AdministrativeDivisionCodeEntity getByCode(String code) {
        return getOne(new LambdaQueryWrapper<>(AdministrativeDivisionCodeEntity.class)
                .eq(AdministrativeDivisionCodeEntity::getCode, code));
    }

    @Override
    public AdministrativeDivisionCodeEntity getByProvinceAndCityAndCounty(String province, String city, String county) {
        return baseMapper.getByProvinceAndCityAndCounty(province, city, county);
    }

    @Override
    public List<String> getCodeByCode(String code) {
        return baseMapper.getCodeByCode(code);
    }

    private void buildTree(AdministrativeDivisionCodeEntity entity,
                           List<AdministrativeDivisionCodeEntity> all) {
        List<AdministrativeDivisionCodeEntity> children = all.stream()
                .filter(e -> e.getPcode().equals(entity.getCode()))
                .collect(Collectors.toList());
        entity.setChildren(children);
        children.forEach(e -> buildTree(e, all));
    }

}