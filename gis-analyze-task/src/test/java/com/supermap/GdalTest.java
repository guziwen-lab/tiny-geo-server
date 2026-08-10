package com.supermap;

import com.supermap.gdal.GdalTool;
import com.supermap.gdal.info.LayerMeta;
import com.supermap.task.GisAnalyzeTaskApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author gzw
 */
@SpringBootTest(classes = GisAnalyzeTaskApplication.class)
public class GdalTest {

    @Autowired
    private GdalTool gdalTool;

    @Test
    public void listGdbLayersTest() {
        List<String> layers = gdalTool.listGdbLayers("/Users/guziwen/Downloads/python检测分析任务/2024地类图斑/500101.gdb");
        System.out.println(layers);
    }

    @Test
    public void queryLayerMetaTest() {
        LayerMeta layerMeta = gdalTool.queryLayerMeta("/Users/guziwen/Downloads/python检测分析任务/监测图斑/11北京市/110101东城区/110101ZT.shp",
                "110101ZT");
        System.out.println(layerMeta);

        layerMeta = gdalTool.queryLayerMeta("/Users/guziwen/Downloads/python检测分析任务/2024地类图斑/500101.gdb",
                "dltb");
        System.out.println(layerMeta);
    }

    @Test
    public void detectEncodingTest() {
        String encoding = gdalTool.detectEncoding("/Users/guziwen/Downloads/python检测分析任务/监测图斑/11北京市/110101东城区/110101ZT.shp",
                "110101ZT");
        System.out.println(encoding);
    }

}
