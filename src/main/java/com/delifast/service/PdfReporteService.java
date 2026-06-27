package com.delifast.service;


import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import com.delifast.dto.ProductoReporteDTO;
import java.awt.Color;
import java.io.IOException;
import java.util.List;

public class PdfReporteService {

    public static void exportarPlatos(HttpServletResponse response, List<ProductoReporteDTO> listaPlatos) throws IOException {
        response.setContentType("application/pdf");
    //  Esto le dice al navegador que renderice el PDF en pantalla
        response.setHeader("Content-Disposition", "inline; filename=reporte_kardex.pdf");
       
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // 🎨 Fuentes institucionales para el diseño
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(33, 37, 41));
        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        // Cabecera del Documento PDF
        Paragraph pTitulo = new Paragraph("📊 DELIFAST - REPORTES COMERCIALES", titleFont);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitulo);

        Paragraph pSub = new Paragraph("Ranking de Platos Más Vendidos (Top Ventas)\nGenerado automáticamente por el Sistema\n\n", subFont);
        pSub.setAlignment(Element.ALIGN_CENTER);
        document.add(pSub);

        // Estructura de la Tabla (4 columnas)
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 5.0f, 2.5f, 3.0f}); // Proporciones de tamaño

        // Celdas de Encabezado (con fondo oscuro elegante)
        agregarCabecera(table, "TOP", headerFont);
        agregarCabecera(table, "PLATO / PRODUCTO", headerFont);
        agregarCabecera(table, "CANTIDAD VENDIDA", headerFont);
        agregarCabecera(table, "TOTAL RECAUDADO", headerFont);

        // Iteración de la lista exacta de tu DAO
        int contador = 1;
        for (ProductoReporteDTO p : listaPlatos) {
            table.addCell(new PdfPCell(new Phrase(String.valueOf(contador++), cellFont)));
            
            PdfPCell cellNombre = new PdfPCell(new Phrase(p.getNombre(), cellFont));
            cellNombre.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cellNombre);
            
            table.addCell(new PdfPCell(new Phrase(p.getCantidadVendida() + " und", cellFont)));
            table.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", p.getTotalRecaudado()), cellFont)));
        }

        document.add(table);
        document.close();
    }

    private static void agregarCabecera(PdfPTable table, String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(new Color(44, 62, 80)); // Gris/Azul oscuro corporativo
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        table.addCell(cell);
    }
    
    public static void exportarClientes(HttpServletResponse response, List<com.delifast.dto.ClienteReporteDTO> listaClientes) throws IOException {
        response.setContentType("application/pdf");
    //  Esto le dice al navegador que renderice el PDF en pantalla
        response.setHeader("Content-Disposition", "inline; filename=reporte_kardex.pdf");
        
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(33, 37, 41));
        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        Paragraph pTitulo = new Paragraph("📊 DELIFAST - REPORTES COMERCIALES", titleFont);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitulo);

        Paragraph pSub = new Paragraph("Historial de Clientes Frecuentes (Fidelidad)\nGenerado automáticamente por el Sistema\n\n", subFont);
        pSub.setAlignment(Element.ALIGN_CENTER);
        document.add(pSub);

        // Tabla de 4 columnas emparejada con tu interfaz web
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 4.5f, 2.5f, 2.5f});

        agregarCabecera(table, "DNI", headerFont);
        agregarCabecera(table, "CLIENTE", headerFont);
        agregarCabecera(table, "N° PEDIDOS", headerFont);
        agregarCabecera(table, "TOTAL INVERTIDO", headerFont);

        for (com.delifast.dto.ClienteReporteDTO c : listaClientes) {
            table.addCell(new PdfPCell(new Phrase(c.getDni(), cellFont)));
            
            PdfPCell cellNombre = new PdfPCell(new Phrase(c.getNombreCompleto(), cellFont));
            cellNombre.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cellNombre);
            
            table.addCell(new PdfPCell(new Phrase(c.getTotalPedidos() + " visitas", cellFont)));
            table.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", c.getTotalInvertido()), cellFont)));
        }

        document.add(table);
        document.close();
    }
    
    public static void exportarCaja(HttpServletResponse response, List<com.delifast.dto.CajaReporteDTO> listaCaja) throws IOException {
        response.setContentType("application/pdf");
    //  Esto le dice al navegador que renderice el PDF en pantalla
        response.setHeader("Content-Disposition", "inline; filename=reporte_kardex.pdf");
        
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(33, 37, 41));
        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        Paragraph pTitulo = new Paragraph("📊 DELIFAST - REPORTES COMERCIALES", titleFont);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitulo);

        Paragraph pSub = new Paragraph("Arqueo de Caja y Canales de Pago\nGenerado automáticamente por el Sistema\n\n", subFont);
        pSub.setAlignment(Element.ALIGN_CENTER);
        document.add(pSub);

        // Tabla de 3 columnas emparejada con tu BD
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4.0f, 4.0f, 4.0f});

        agregarCabecera(table, "MÉTODO DE PAGO", headerFont);
        agregarCabecera(table, "N° DE TRANSACCIONES", headerFont);
        agregarCabecera(table, "TOTAL ACUMULADO", headerFont);

        for (com.delifast.dto.CajaReporteDTO c : listaCaja) {
            table.addCell(new PdfPCell(new Phrase(c.getMetodoPago(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(c.getCantidadTransacciones() + " ventas", cellFont)));
            table.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", c.getTotalRecaudado()), cellFont)));
        }

        document.add(table);
        document.close();
    }
}