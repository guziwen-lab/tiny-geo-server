package com.supermap.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.chart.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author gzw
 */
@Slf4j
public class WordUtils {

    private static final String REPLACE_SYMBOL = "$";

    public static void replaceDocumentText(XWPFDocument document, Map<String, Object> contentMap) {
        WordUtils.replaceKey(contentMap);

        // 替换段落中的指定文字
        Iterator<XWPFParagraph> itPara = document.getParagraphsIterator();
        while (itPara.hasNext()) {
            XWPFParagraph paragraph = itPara.next();
            replaceText(paragraph, contentMap);
        }

        // 替换表格中的指定文字
        Iterator<XWPFTable> itTable = document.getTablesIterator();
        while (itTable.hasNext()) {
            XWPFTable table = itTable.next();
            int rcount = table.getNumberOfRows();
            for (int i = 0; i < rcount; i++) {
                XWPFTableRow row = table.getRow(i);
                List<XWPFTableCell> cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    //单元格中有段落，得做段落处理
                    List<XWPFParagraph> paragraphs = cell.getParagraphs();
                    for (XWPFParagraph paragraph : paragraphs) {
                        replaceText(paragraph, contentMap);
                    }
                }
            }
        }
    }

    /**
     * 判断段落中是否包含文字
     *
     * @param paragraph 段落
     * @return 包含返回true, 不包含返回false
     */
    private static boolean checkParagraphContainsText(XWPFParagraph paragraph, String word) {
        return paragraph.getText().contains(word);
    }

    /**
     * 替换run的文字
     *
     * @param paragraph  段落
     * @param contentMap 替换map
     */
    private static void replaceText(XWPFParagraph paragraph, Map<String, Object> contentMap) {
        if (!checkParagraphContainsText(paragraph, REPLACE_SYMBOL)) {
            return;
        }

        List<XWPFRun> runs = paragraph.getRuns();
        for (XWPFRun run : runs) {
            String runText = run.getText(run.getTextPosition());
            if (StrUtil.isBlank(runText)) {
                continue;
            }
            for (Map.Entry<String, Object> entry : contentMap.entrySet()) {
                runText = runText.replace(entry.getKey(), entry.getValue().toString());
            }
            run.setText(runText, 0);
        }
    }

    /**
     * 调用替换柱状图数据
     */
    public static void replaceBarCharts(POIXMLDocumentPart poixmlDocumentPart,
                                        List<String> labels,
                                        List<String> fldNameArr,
                                        List<Map<String, String>> dataList) {
        XWPFChart chart = (XWPFChart) poixmlDocumentPart;

        //根据属性第一列名称切换数据类型
        CTChart ctChart = chart.getCTChart();
        CTPlotArea plotArea = ctChart.getPlotArea();

        CTBarChart barChart = plotArea.getBarChartArray(0);
        List<CTBarSer> barSerList = barChart.getSerList();  // 获取柱状图单位

        //刷新内置excel数据
        refreshExcel(chart, labels, fldNameArr, dataList);
        //刷新页面显示数据
        refreshGraphContent(barChart, barSerList, fldNameArr, dataList, 1);
    }

    /**
     * 调用替换折线图数据
     */
    public static void replaceLineCharts(POIXMLDocumentPart poixmlDocumentPart,
                                         List<String> labels,
                                         List<String> fldNameArr,
                                         List<Map<String, String>> dataList) {
        XWPFChart chart = (XWPFChart) poixmlDocumentPart;

        //根据属性第一列名称切换数据类型
        CTChart ctChart = chart.getCTChart();
        CTPlotArea plotArea = ctChart.getPlotArea();

        CTLineChart lineChartArray = plotArea.getLineChartArray(0);
        List<CTLineSer> serList = lineChartArray.getSerList();

        //刷新内置excel数据
        refreshExcel(chart, labels, fldNameArr, dataList);
        //刷新页面显示数据
        refreshGraphContent(lineChartArray, serList, fldNameArr, dataList, 1);
    }

    /**
     * 调用替换饼图数据
     */
    public static void replacePieCharts(POIXMLDocumentPart poixmlDocumentPart,
                                        List<String> labels,
                                        List<String> fldNameArr,
                                        List<Map<String, String>> dataList) {
        XWPFChart chart = (XWPFChart) poixmlDocumentPart;

        //根据属性第一列名称切换数据类型
        CTChart ctChart = chart.getCTChart();
        CTPlotArea plotArea = ctChart.getPlotArea();

        CTPieChart pieChart = plotArea.getPieChartArray(0);
        List<CTPieSer> pieSerList = pieChart.getSerList();  // 获取饼图单位

        //刷新内置excel数据
        refreshExcel(chart, labels, fldNameArr, dataList);
        //刷新页面显示数据
        refreshGraphContent(pieChart, pieSerList, fldNameArr, dataList, 1);
    }

    /**
     * 调用替换柱状图、折线图组合数据
     */
    public static void replaceCombinationCharts(POIXMLDocumentPart poixmlDocumentPart,
                                                List<String> labels,
                                                List<String> fldNameArr,
                                                List<Map<String, String>> dataList) {
        XWPFChart chart = (XWPFChart) poixmlDocumentPart;
        chart.getCTChart();

        //根据属性第一列名称切换数据类型
        CTChart ctChart = chart.getCTChart();
        CTPlotArea plotArea = ctChart.getPlotArea();

        CTBarChart barChart = plotArea.getBarChartArray(0);
        List<CTBarSer> barSerList = barChart.getSerList();  // 获取柱状图单位
        //刷新内置excel数据
        refreshExcel(chart, labels, fldNameArr, dataList);
        //刷新页面显示数据   数据中下标1开始的是柱状图数据，所以这个是1
        refreshGraphContent(barChart, barSerList, fldNameArr, dataList, 1);

        CTLineChart lineChart = plotArea.getLineChartArray(0);
        List<CTLineSer> lineSerList = lineChart.getSerList();   // 获取折线图单位
        //刷新页面显示数据   数据中下标3开始的是折线图的数据，所以这个是3
        refreshGraphContent(lineChart, lineSerList, fldNameArr, dataList, 2);
    }

    /**
     * @param doc        docx解析对象
     * @param tableIndex 第几个表格
     */
    public static XWPFTable getTable(XWPFDocument doc, int tableIndex) {
        return doc.getTables().get(tableIndex);
    }

    /**
     * 为表格插入行数，此处不处理表头，所以从第二行开始
     *
     * @param table     需要插入数据的表格
     * @param tableList 插入数据集合
     * @param index     在几行后开始插入数据 1为第一行
     */
    public static void insertTable(XWPFTable table, List<List<String>> tableList, int index) {
        //创建与数据一致地行数
        for (int i = 0; i < tableList.size(); i++) {
            table.createRow();
        }
        int length = table.getRows().size() - index;
        for (int i = 0; i < length; i++) {
            XWPFTableRow newRow = table.getRow(i + index);
            List<XWPFTableCell> cells = newRow.getTableCells();
            for (int j = 0; j < cells.size(); j++) {
                XWPFTableCell cell = cells.get(j);
                cell.setText(tableList.get(i).get(j));
            }
        }
    }

    /**
     * 获取word模板中的所有图表元素，用map存放
     */
    public static Map<String, POIXMLDocumentPart> getPOIXMLDocumentParts(XWPFDocument doc) {
        // 获取word模板中的所有图表元素，用map存放
        // 为什么不用list保存：查看doc.getRelations()的源码可知，源码中使用了hashMap读取文档图表元素，
        // 对relations变量进行打印后发现，图表顺序和文档中的顺序不一致，也就是说relations的图表顺序不是文档中从上到下的顺序
        Map<String, POIXMLDocumentPart> chartsMap = new HashMap<>();
        //动态刷新图表
        List<POIXMLDocumentPart> relations = doc.getRelations();
        for (POIXMLDocumentPart poixmlDocumentPart : relations) {
            if (poixmlDocumentPart instanceof XWPFChart) {  // 如果是图表元素
                String str = poixmlDocumentPart.toString();
                String key = str.replaceAll("Name: ", "")
                        .replaceAll(" - Content Type: application/vnd\\.openxmlformats-officedocument\\.drawingml\\.chart\\+xml", "")
                        .trim();
                chartsMap.put(key, poixmlDocumentPart);
            }
        }
        return chartsMap;
    }

    /**
     * 获取word模板中的对应名称的图表元素
     * 返回null则查询不到
     */
    public static POIXMLDocumentPart getPOIXMLDocumentPart(XWPFDocument doc, String chartsName) {
        List<POIXMLDocumentPart> relations = doc.getRelations();
        for (POIXMLDocumentPart poixmlDocumentPart : relations) {
            // 如果是图表元素
            if (poixmlDocumentPart instanceof XWPFChart) {
                String str = poixmlDocumentPart.toString();
                String key = str.replaceAll("Name: ", "")
                        .replaceAll(" - Content Type: application/vnd\\.openxmlformats-officedocument\\.drawingml\\.chart\\+xml", "")
                        .trim();
                if (key.equals(chartsName)) {
                    return poixmlDocumentPart;
                }
            }
        }
        return null;
    }

    /**
     * 刷新内置excel数据
     */
    public static boolean refreshExcel(XWPFChart chart,
                                       List<String> labels,
                                       List<String> fldNameArr,
                                       List<Map<String, String>> dataList) {
        boolean result = true;
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");
        // 根据数据创建excel第一行标题行
        for (int i = 0; i < labels.size(); i++) {
            if (sheet.getRow(0) == null) {
                sheet.createRow(0).createCell(i).setCellValue(labels.get(i) == null ? "" : labels.get(i));
            } else {
                sheet.getRow(0).createCell(i).setCellValue(labels.get(i) == null ? "" : labels.get(i));
            }
        }

        //遍历数据行
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, String> baseFormMap = dataList.get(i);//数据行
            //titles字段属性
            for (int j = 0; j < fldNameArr.size(); j++) {
                String base = baseFormMap.get(fldNameArr.get(j));
                if (sheet.getRow(i + 1) == null) {
                    if (j == 0) {
                        try {
                            sheet.createRow(i + 1).createCell(j).setCellValue(base == null ? "" : base);
                        } catch (Exception e) {
                            sheet.createRow(i + 1).createCell(j).setCellValue(Objects.requireNonNullElse(base, ""));
                        }
                    }
                } else {
                    if (base != null) {
                        BigDecimal b = new BigDecimal(base);
                        double value = b.doubleValue();

                        if (value == 0) {
                            sheet.getRow(i + 1).createCell(j);
                        } else {
                            sheet.getRow(i + 1).createCell(j).setCellValue(value);
                        }
                    }
                }
            }
        }
        // 更新嵌入的workbook
        List<POIXMLDocumentPart> pxdList = chart.getRelations();
        if (!pxdList.isEmpty()) {
            for (POIXMLDocumentPart poixmlDocumentPart : pxdList) {
                if (poixmlDocumentPart.toString().contains("sheet")) {//判断为sheet再去进行更新表格数据
                    try {
                        OutputStream xlsOut = poixmlDocumentPart.getPackagePart().getOutputStream();
                        wb.write(xlsOut);
                        xlsOut.close();
                    } catch (IOException e) {
                        log.error("刷新excel失败", e);
                        result = false;
                    } finally {
                        try {
                            wb.close();
                        } catch (IOException e) {
                            log.error("刷新excel失败", e);
                            result = false;
                        }
                    }
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 刷新柱状图或者折线图数据方法
     */
    public static void refreshGraphContent(Object typeChart,
                                           List<?> serList,
                                           List<String> fldNameArr,
                                           List<Map<String, String>> dataList,
                                           int position) {
        //更新数据区域
        for (int i = 0; i < serList.size(); i++) {
            //CTSerTx tx=null;
            CTAxDataSource cat = null;
            CTNumDataSource val = null;

            if (typeChart instanceof CTLineChart) {
                CTLineSer ser = ((CTLineChart) typeChart).getSerArray(i);
                cat = ser.getCat();
                // 获取图表的值
                val = ser.getVal();
            } else if (typeChart instanceof CTPieChart) {
                CTPieSer ser = ((CTPieChart) typeChart).getSerArray(i);
                cat = ser.getCat();
                val = ser.getVal();
            } else if (typeChart instanceof CTBarChart) {
                CTBarSer ser = ((CTBarChart) typeChart).getSerArray(i);
                cat = ser.getCat();
                val = ser.getVal();
            }
            // strData.set
            assert cat != null;
            CTStrData strData = cat.getStrRef().getStrCache();
            CTNumData numData = val.getNumRef().getNumCache();
            strData.setPtArray((CTStrVal[]) null); // unset old axis text
            numData.setPtArray((CTNumVal[]) null); // unset old values

            // set model
            long idx = 0;
            for (Map<String, String> stringStringMap : dataList) {
                //判断获取的值是否为空
                String value = "0";
                if (stringStringMap.get(fldNameArr.get(i + position)) != null) {
                    value = new BigDecimal(stringStringMap.get(fldNameArr.get(i + position))).toString();
                }
                if (!"0".equals(value)) {
                    CTNumVal numVal = numData.addNewPt();//序列值
                    numVal.setIdx(idx);
                    numVal.setV(value);
                }
                CTStrVal sVal = strData.addNewPt();//序列名称
                sVal.setIdx(idx);
                sVal.setV(stringStringMap.get(fldNameArr.get(0)));
                idx++;
            }
            numData.getPtCount().setVal(idx);
            strData.getPtCount().setVal(idx);

            //赋值横坐标数据区域
            String axisDataRange = new CellRangeAddress(1, dataList.size(), 0, 0)
                    .formatAsString("Sheet1", true);
            cat.getStrRef().setF(axisDataRange);

            //数据区域
            String numDataRange = new CellRangeAddress(1, dataList.size(), i + position, i + position)
                    .formatAsString("Sheet1", true);
            val.getNumRef().setF(numDataRange);
        }
    }

    /**
     * 给替换的字符加{}
     */
    public static Map<String, Object> replaceKey(Map<String, Object> map) {
        Map<String, Object> tempMap = new HashMap<>(map);
        tempMap.forEach((k, v) -> {
            map.put(replaceKey(k), v);
            map.remove(k);
        });
        return map;
    }

    /**
     * 给替换的字符加{}
     */
    private static String replaceKey(String key) {
        return "${" + key + "}";
    }

    /**
     * 插入一行，将某一行的样式复制到新增行
     *
     * @param sourceRowIndex 需要复制的行位置
     * @param newRowIndex    需要新增一行的位置
     */
    public static XWPFTableRow insertRow(XWPFTable table, int sourceRowIndex, int newRowIndex) {
        // 在表格中指定的位置新增一行
        XWPFTableRow targetRow = table.insertNewTableRow(newRowIndex);
        // 获取需要复制行对象
        XWPFTableRow copyRow = table.getRow(sourceRowIndex);
        // 复制行对象
        targetRow.getCtRow().setTrPr(copyRow.getCtRow().getTrPr());
        // 或许需要复制的行的列
        List<XWPFTableCell> copyCells = copyRow.getTableCells();
        // 复制列对象
        XWPFTableCell targetCell;
        for (XWPFTableCell copyCell : copyCells) {
            targetCell = targetRow.addNewTableCell();
            targetCell.getCTTc().setTcPr(copyCell.getCTTc().getTcPr());
            if (copyCell.getParagraphs() != null && !copyCell.getParagraphs().isEmpty()) {
                targetCell.getParagraphs().get(0).getCTP().setPPr(copyCell.getParagraphs().get(0).getCTP().getPPr());
                if (copyCell.getParagraphs().get(0).getRuns() != null
                        && !copyCell.getParagraphs().get(0).getRuns().isEmpty()) {
                    XWPFRun cellR = targetCell.getParagraphs().get(0).createRun();
                    cellR.setBold(copyCell.getParagraphs().get(0).getRuns().get(0).isBold());
                }
            }
        }

        return targetRow;
    }

    /**
     * 插入一行，将某一行的样式复制到新增行
     *
     * @param sourceTableRow 需要复制的行
     * @param newRowIndex    需要新增一行的位置
     */
    public static XWPFTableRow insertNewRowFromSourceRow(XWPFTableRow sourceTableRow, int newRowIndex) {
        // 在表格中指定的位置新增一行
        XWPFTableRow targetRow = sourceTableRow.getTable().insertNewTableRow(newRowIndex);
        // 复制行对象
        targetRow.getCtRow().setTrPr(sourceTableRow.getCtRow().getTrPr());
        // 或许需要复制的行的列
        List<XWPFTableCell> copyCells = sourceTableRow.getTableCells();
        // 复制列对象
        XWPFTableCell targetCell;
        for (XWPFTableCell copyCell : copyCells) {
            targetCell = targetRow.addNewTableCell();
            targetCell.getCTTc().setTcPr(copyCell.getCTTc().getTcPr());
            if (copyCell.getParagraphs() != null && !copyCell.getParagraphs().isEmpty()) {
                targetCell.getParagraphs().get(0).getCTP().setPPr(copyCell.getParagraphs().get(0).getCTP().getPPr());
                if (copyCell.getParagraphs().get(0).getRuns() != null
                        && !copyCell.getParagraphs().get(0).getRuns().isEmpty()) {
                    XWPFRun cellR = targetCell.getParagraphs().get(0).createRun();
                    cellR.setBold(copyCell.getParagraphs().get(0).getRuns().get(0).isBold());
                }
            }
        }

        return targetRow;
    }

    /**
     * 写入图片在word中
     *
     * @param document    文档
     * @param replaceKey  被替换的名称
     * @param pictureType 图片格式 {@link Document}
     * @param picture     图片
     * @param width       宽度（像素）
     * @param height      高度（像素）
     */
    public static void insertGraphic(XWPFDocument document, String replaceKey, InputStream picture, int pictureType,
                                     int width, int height) {
        //段落集合
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            if (!checkParagraphContainsText(paragraph, REPLACE_SYMBOL))
                continue;

            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                //替换模板原来位置
                replaceKey = replaceKey(replaceKey);
                String runText = run.getText(run.getTextPosition());
                if (runText.contains(replaceKey)) {
                    try {
                        run.setText("", 0);
                        run.addPicture(picture, pictureType, "sample.jpeg", Units.toEMU(width), Units.toEMU(height));
                    } catch (IOException | InvalidFormatException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            }
            break;
        }
    }

    public static XWPFTableRow getRowWithColor(XWPFTable table, int cellNum, String color) {
        XWPFTableRow row = table.createRow();
        for (int i = 0; i < cellNum; i++) {
            getCellWithColor(row, color);
        }
        return row;
    }

    public static XWPFTableCell getCellWithColor(XWPFTableRow row, String color) {
        XWPFTableCell cell = row.createCell();
        setCellBackgroundColor(cell, color);
        return cell;
    }

    public static void setCellBackgroundColor(XWPFTableCell cell, String color) {
        cell.getCTTc().addNewTcPr().addNewShd().setFill(color);
    }

}
