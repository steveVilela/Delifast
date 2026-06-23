package com.delifast.controller;

import com.delifast.model.Producto;
import com.delifast.model.MovimientoInventario;
import com.delifast.service.ProductoService;
import com.delifast.service.MovimientoService;
import com.delifast.service.InsumoService;
import com.delifast.service.RecetaService;

import com.delifast.model.Administrador;
import com.delifast.model.Insumo;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/administrador/inventario")
public class InventarioController {

	@Autowired
    private ProductoService productoService;

    @Autowired
    private MovimientoService movimientoService;

    // ➕ AGREGA ESTAS LÍNEAS AQUÍ:
    @Autowired
    private InsumoService insumoService;

    @Autowired
    private RecetaService recetaService;
    
    // =======================================================================
    // 🗺️ ENTRADA ÚNICA DINÁMICA (Carga el subpanel derecho según el menú)
    // =======================================================================
    @GetMapping
    public String mostrarInventario(@RequestParam(value = "vista", required = false, defaultValue = "catalogo") String vista, 
                                    Model model, HttpSession session, RedirectAttributes redirectAttrs) {
        
        // Validación de seguridad centralizada para operaciones delicadas
        if (!"catalogo".equals(vista)) {
            Administrador adminLogueado = (Administrador) session.getAttribute("admin");
            if (adminLogueado == null) {
                redirectAttrs.addFlashAttribute("msgError", "Debe iniciar sesión para acceder a las opciones de control.");
                return "redirect:/administrador/login";
            }
        }

     // Carga selectiva de datos según el fragmento solicitado por la vista HTML
        switch (vista) {
        case "catalogo":
            List<Insumo> todosInsumos = insumoService.listarTodos();
            
            // 🔥 Contamos de forma dinámica cuántos insumos rompieron el stock mínimo
            long insumosCriticos = todosInsumos.stream()
                                               .filter(i -> i.getStock() <= i.getStockMinimo())
                                               .count();
            
            model.addAttribute("insumos", todosInsumos);
            model.addAttribute("alertasContador", insumosCriticos); // 👈 Enviamos el número a la vista
            break;
                
            case "stockMinimo":
                // 🔄 CORREGIDO: Las alertas de stock mínimo vigilan los insumos
                model.addAttribute("insumos", insumoService.listarTodos());
                break;
                
            case "recepcionarMercaderia":
                // 🔄 CORREGIDO: Se recepcionan insumos al almacén e historial de movimientos
                model.addAttribute("insumos", insumoService.listarTodos());
                model.addAttribute("movimientos", movimientoService.listarTodos());
                System.out.println("DEBUG RECEPCIÓN: Encontrados " + insumoService.listarTodos().size() + " insumos.");
                break;
                
            case "egreso":
                model.addAttribute("insumos", insumoService.listarTodos());
                model.addAttribute("movimientos", movimientoService.listarTodos());
                model.addAttribute("alertasContador", 0); // 👈 🛡️ BLINDAJE: Evita que SweetAlert busque un null y rompa la página
                break;
                
            case "recetas":
                // 💡 Para las recetas necesitas AMBOS: los platos de venta y los ingredientes
                model.addAttribute("productos", productoService.listarTodos()); // Combo de platos
                model.addAttribute("insumos", insumoService.listarTodos());     // Combo de ingredientes
                model.addAttribute("recetas", recetaService.listarTodas());     // Tabla de fórmulas
                break;
                
            case "kardex":
                model.addAttribute("movimientos", movimientoService.listarTodos());
                model.addAttribute("alertasContador", 0); // Blindaje para SweetAlert
                break;
                
            default:
                model.addAttribute("insumos", insumoService.listarTodos());
                vista = "catalogo";
                break;
        }
     // 🛡️ Al final de tu método mostrarInventario() en el controlador, añade esto antes del return:
        model.addAttribute("insumos", insumoService.listarTodos()); // Asegura que el modal siempre tenga datos que iterar
        model.addAttribute("vista", vista); 
        return "administrador/inventario/inventario";
    }

 // =======================================================================
    // 📈 GESTIÓN DE PARÁMETROS TÉCNICOS DE INSUMOS (ALERTA Y BLOQUEO)
    // =======================================================================
    @PostMapping("/actualizarParametros") // 🟢 Coincide exactamente con el th:action del HTML
    public String actualizarParametrosTecnicos(@RequestParam("insumoId") int insumoId,
                                               @RequestParam("stockMinimo") int stockMinimo,
                                               @RequestParam("stockBloqueo") int stockBloqueo, // 🟢 Recibe el nuevo input
                                               RedirectAttributes redirectAttrs) {
        try {
            // Ejecutamos la actualización doble sobre tu servicio de insumos
            insumoService.actualizarParametrosTecnicos(insumoId, stockMinimo, stockBloqueo);
            
            redirectAttrs.addFlashAttribute("msgSuccess", "Parámetros de control (Alerta y Bloqueo) actualizados correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("msgError", "Error al actualizar parámetros: " + e.getMessage());
        }
        
        // Redirecciona manteniendo el panel de stock mínimo/configuración activo
        return "redirect:/administrador/inventario?vista=stockMinimo";
    }

 // =======================================================================
    // 📥 RECEPCIONAR MERCADERÍA (ENTRADA DE INSUMOS)
    // =======================================================================
    @PostMapping("/recepcionarMercaderia/guardar")
    public String guardarRecepcion(@RequestParam("insumoId") int insumoId, // 🔄 Cambiado a insumoId
                                   @RequestParam("cantidad") int cantidad,
                                   @RequestParam("motivo") String motivo,
                                   HttpSession session,
                                   RedirectAttributes redirectAttrs) {
        try {
            Administrador adminLogueado = (Administrador) session.getAttribute("admin");
            if (adminLogueado == null) {
                redirectAttrs.addFlashAttribute("msgError", "Sesión expirada. Vuelva a iniciar sesión.");
                return "redirect:/administrador/login";
            }

            // 🔄 Actualizamos el stock y la auditoría usando el servicio de INSUMOS
            insumoService.incrementarStock(insumoId, cantidad);
            movimientoService.registrar(insumoId, "INGRESO", cantidad, motivo, adminLogueado);
            
            // 🔄 Buscamos el insumo para armar el mensaje de éxito dinámico con su nombre
            Insumo actualizado = insumoService.buscarPorId(insumoId); 
            redirectAttrs.addFlashAttribute("msgSuccess", "¡Mercadería recepcionada! Se sumaron " + cantidad + 
                                " " + actualizado.getUnidadMedida() + " al stock de " + actualizado.getNombre() + ".");
                                
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("msgError", "Error al procesar: " + e.getMessage());
        }
        
        // 🔄 Redirección perfecta para el panel dinámico
        return "redirect:/administrador/inventario?vista=recepcionarMercaderia";
    } 

    // ==========================================
    // 🍎 EGRESO / SALIDA DE INSUMOS (MERMAS)
    // ==========================================
    @PostMapping("/registrarEgreso") 
    public String guardarEgreso(@RequestParam("insumoId") int insumoId, // 🔄 1. Parámetro cambiado a insumoId
                                @RequestParam("cantidad") int cantidad,
                                @RequestParam("motivo") String motivo, 
                                HttpSession session,                    
                                RedirectAttributes redirectAttrs) {
        try {
            // 🔍 Recuperamos el administrador bajo el atributo "admin" como lo tienes configurado
            Administrador adminLogueado = (Administrador) session.getAttribute("admin");
            if (adminLogueado == null) {
                redirectAttrs.addFlashAttribute("msgError", "Sesión expirada.");
                return "redirect:/administrador/login";
            }

            // 🔄 2. Pasamos el insumoId al servicio de auditoría de movimientos
            movimientoService.registrar(insumoId, "SALIDA", cantidad, motivo, adminLogueado);
            
            // 🔄 3. Descontamos las unidades usando el servicio correcto de insumos
            insumoService.decrementarStock(insumoId, cantidad);
            
            redirectAttrs.addFlashAttribute("msgSuccess", "Egreso registrado correctamente.");
            
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("msgError", "Error: " + e.getMessage());
        }
        
        // 🔄 Redirección perfecta para tu panel dinámico de subvistas
        return "redirect:/administrador/inventario?vista=egreso"; 
    }
    
 // ==========================================
    // ⚙️ PROCESAR GUARDADO DE COMPONENTE DE RECETA
    // ==========================================
    @PostMapping("/recetas/guardar")
    public String guardarComponenteReceta(@RequestParam("productoId") int productoId,
                                          @RequestParam("insumoId") int insumoId,
                                          @RequestParam("cantidadNecesaria") int cantidadNecesaria,
                                          RedirectAttributes redirectAttrs) {
        try {
            // El servicio se encarga de instanciar las entidades y persistir el insert
            recetaService.registrarComponente(productoId, insumoId, cantidadNecesaria);
            
            redirectAttrs.addFlashAttribute("msgSuccess", "¡Insumo vinculado correctamente a la receta del plato!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("msgError", "Error al guardar la fórmula: " + e.getMessage());
        }
        
        // Redirecciona manteniendo el panel de recetas activo y refrescado
        return "redirect:/administrador/inventario?vista=recetas";
    }
    
 // =======================================================================
 // 📄 EXPORTAR HISTORIAL DE KARDEX A PDF USANDO JASPERREPORTS
 // =======================================================================
 @GetMapping("/kardex/pdf")
 @ResponseBody // 👈 IMPORTANTE: Indica a Spring que devuelva un archivo, no una vista HTML
 public org.springframework.http.ResponseEntity<byte[]> exportarKardexPdf() {
     try {
         // 1. Obtener los datos reales desde tu servicio actual de movimientos
         List<MovimientoInventario> listaMovimientos = movimientoService.listarTodos();

         // 2. Mapear la lista de Java como el Data Source dinámico para Jasper
         net.sf.jasperreports.engine.data.JRBeanCollectionDataSource dataSource = 
                 new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(listaMovimientos);

         // 3. Cargar el archivo binario .jasper que pegaste en src/main/resources
        // java.io.InputStream reporteStream = getClass().getResourceAsStream("/reports/KardexReport.jasper");
         java.io.InputStream reporteStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/KardexReport.jasper");
         // 4. Mapa de parámetros (vacío, ya que usas Fields directos)
         java.util.Map<String, Object> parameters = new java.util.HashMap<>();

         // 5. Unir el diseño de la plantilla con los registros de la BD
         net.sf.jasperreports.engine.JasperPrint jasperPrint = 
                 net.sf.jasperreports.engine.JasperFillManager.fillReport(reporteStream, parameters, dataSource);

         // 6. Exportar el resultado final a un flujo de bytes en formato PDF
         byte[] pdfBytes = net.sf.jasperreports.engine.JasperExportManager.exportReportToPdf(jasperPrint);

         // 7. Configurar cabeceras HTTP para renderizar el PDF en el navegador
         org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
         headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
         headers.setContentDisposition(org.springframework.http.ContentDisposition.inline()
                 .filename("Reporte_Kardex_Delifast.pdf").build());

         return new org.springframework.http.ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);

     } catch (Exception e) {
         e.printStackTrace();
         return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
     }
 }
 
//=======================================================================
	// 🟢 EXPORTAR HISTORIAL DE KARDEX A EXCEL (APACHE POI)
	// =======================================================================
	@GetMapping("/kardex/excel")
	public void exportarKardexExcel(jakarta.servlet.http.HttpServletResponse response) {
		try {
			// 1. Obtener los mismos datos que usas para el PDF y la vista web
			List<MovimientoInventario> lista = movimientoService.listarTodos();

			// 2. Configurar las cabeceras HTTP para que el navegador entienda que es un Excel
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=Kardex_Delifast.xlsx");

			// 3. Crear el archivo Excel en memoria
			try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
				org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Historial Kardex");

				// --- Estilo elegante para la cabecera (Gris oscuro con texto blanco en negrita) ---
				org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
				org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
				headerStyle.setFont(headerFont);
				headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_80_PERCENT.getIndex());
				headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

				// 4. Crear los nombres de las columnas
				String[] columnas = {"ID MOV", "FECHA Y HORA", "TIPO", "INSUMO / MATERIA PRIMA", "CANTIDAD", "MOTIVO / DETALLE", "RESPONSABLE"};
				org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
				for (int i = 0; i < columnas.length; i++) {
					org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
					cell.setCellValue(columnas[i]);
					cell.setCellStyle(headerStyle);
				}

				// 5. Llenar la hoja de Excel con el contenido de la base de datos
				int rowIdx = 1;
				for (MovimientoInventario mov : lista) {
					org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);

					row.createCell(0).setCellValue(mov.getMovimientoId());
					row.createCell(1).setCellValue(mov.getFecha() != null ? mov.getFecha().toString() : "---");
					row.createCell(2).setCellValue(mov.getTipoMovimiento());
					row.createCell(3).setCellValue(mov.getInsumo() != null ? mov.getInsumo().getNombre() : "---");
					row.createCell(4).setCellValue(mov.getCantidad());
					row.createCell(5).setCellValue(mov.getMotivo());
					row.createCell(6).setCellValue(mov.getAdministrador() != null ? mov.getAdministrador().getEmail() : "---");
				}

				// Autoajustar las columnas al tamaño del texto para que se lea perfecto
				for (int i = 0; i < columnas.length; i++) {
					sheet.autoSizeColumn(i);
				}

				// 6. Escribir el flujo de datos directamente al navegador
				workbook.write(response.getOutputStream());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// =======================================================================
		// 📄 EXPORTAR HISTORIAL DE KARDEX A PDF DIRECTO (SIN JASPERREPORTS)
		// =======================================================================
		@GetMapping("/kardex/pdf-limpio")
		public void exportarKardexPdfLimpio(jakarta.servlet.http.HttpServletResponse response) {
			
			try {
				// 1. Obtener los datos reales de la base de datos
				List<MovimientoInventario> lista = movimientoService.listarTodos();

				// 2. Configurar los encabezados de respuesta HTTP para PDF
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "inline; filename=Kardex_Delifast.pdf");

				// 3. Crear el documento PDF en orientación Horizontal (Landscape) para que quepa todo holgadamente
				com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate());
				com.lowagie.text.pdf.PdfWriter.getInstance(document, response.getOutputStream());

				document.open();

				// --- FUENTES ---
				com.lowagie.text.Font fontTitulo = com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 16, java.awt.Color.DARK_GRAY);
				com.lowagie.text.Font fontHeader = com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 9, java.awt.Color.WHITE);
				com.lowagie.text.Font fontDatos = com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA, 9, java.awt.Color.BLACK);

				// 4. Agregar Título Principal
				com.lowagie.text.Paragraph titulo = new com.lowagie.text.Paragraph("REPORTE DE KARDEX DE ALMACÉN (DELIFAST)", fontTitulo);
				titulo.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
				titulo.setSpacingAfter(20);
				document.add(titulo);

				// 5. Crear la Tabla Estructurada (7 columnas igual al Excel)
				com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(7);
				table.setWidthPercentage(100);
				// Proporción del ancho de cada columna
				table.setWidths(new float[]{1.0f, 2.5f, 1.5f, 3.5f, 1.5f, 3.0f, 3.5f}); 

				// Cabeceras (Fondo gris oscuro, igual a tu diseño web/excel)
				String[] columnas = {"ID MOV", "FECHA Y HORA", "TIPO", "INSUMO / MATERIA PRIMA", "CANTIDAD", "MOTIVO / DETALLE", "RESPONSABLE"};
				java.awt.Color grisOscuro = new java.awt.Color(43, 43, 43);

				for (String col : columnas) {
					com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(col, fontHeader));
					cell.setBackgroundColor(grisOscuro);
					cell.setPadding(8);
					cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
					cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
					table.addCell(cell);
				}

				// 6. Recorrer la lista e inyectar las celdas de datos
				for (MovimientoInventario mov : lista) {
					table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(String.valueOf(mov.getMovimientoId()), fontDatos)));
					table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(mov.getFecha() != null ? mov.getFecha().toString() : "---", fontDatos)));
					table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(mov.getTipoMovimiento(), fontDatos)));
					table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(mov.getInsumo() != null ? mov.getInsumo().getNombre() : "---", fontDatos)));
					
					// Alinear cantidad a la derecha numéricamente
					com.lowagie.text.pdf.PdfPCell cellCant = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(String.valueOf(mov.getCantidad()), fontDatos));
					cellCant.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
					table.addCell(cellCant);
					
					table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(mov.getMotivo(), fontDatos)));
					table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(mov.getAdministrador() != null ? mov.getAdministrador().getEmail() : "---", fontDatos)));
				}

				// 7. Añadir la tabla armada al documento y cerrar
				document.add(table);
				document.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    
		
		
		
}