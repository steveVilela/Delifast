package com.delifast.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import com.delifast.dto.ProductoReporteDTO;
import java.io.IOException;
import java.util.List;

public class ExcelReporteService {

    public static void exportarPlatos(HttpServletResponse response, List<ProductoReporteDTO> listaPlatos) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_platos_top.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Top Platos");

        // Estilos de fuentes y celdas
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // 📋 Fila de Cabeceras
        Row headerRow = sheet.createRow(0);
        String[] headers = {"TOP", "PLATO / PRODUCTO", "CANTIDAD VENDIDA", "TOTAL RECAUDADO"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 📥 Llenado de Datos comerciales
        int rowNum = 1;
        int top = 1;
        for (ProductoReporteDTO p : listaPlatos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(top++);
            row.createCell(1).setCellValue(p.getNombre());
            row.createCell(2).setCellValue(p.getCantidadVendida() + " und");
            row.createCell(3).setCellValue("S/ " + String.format("%.2f", p.getTotalRecaudado()));
        }

        // Autoajustar las columnas para que no se corte el texto
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    public static void exportarClientes(HttpServletResponse response, List<com.delifast.dto.ClienteReporteDTO> listaClientes) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_clientes_top.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clientes Frecuentes");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"DNI", "CLIENTE", "N° PEDIDOS", "TOTAL INVERTIDO"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (com.delifast.dto.ClienteReporteDTO c : listaClientes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(c.getDni());
            row.createCell(1).setCellValue(c.getNombreCompleto());
            row.createCell(2).setCellValue(c.getTotalPedidos() + " visitas");
            row.createCell(3).setCellValue("S/ " + String.format("%.2f", c.getTotalInvertido()));
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    public static void exportarCaja(HttpServletResponse response, List<com.delifast.dto.CajaReporteDTO> listaCaja) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_arqueo_caja.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Arqueo de Caja");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"MÉTODO DE PAGO", "N° DE TRANSACCIONES", "TOTAL ACUMULADO"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (com.delifast.dto.CajaReporteDTO c : listaCaja) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(c.getMetodoPago());
            row.createCell(1).setCellValue(c.getCantidadTransacciones() + " ventas");
            row.createCell(2).setCellValue("S/ " + String.format("%.2f", c.getTotalRecaudado()));
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
}
