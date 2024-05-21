/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Vista;

import Controlador.conexion_RP;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author PC
 */
public class A_RN extends javax.swing.JPanel {
    
    conexion_RP con = new conexion_RP();
    Connection cn=con.conectar();
    /**
     * Creates new form A_RN
     */
    public A_RN() {
        initComponents();
        
        mostrarDatos();
    }
    
    public void convertirJTableAPDF(JTable tabla) {
        Rectangle pageSize = PageSize.A3.rotate();
        Document document = new Document(pageSize);
        try {
            
            String rutaPDF = "BusquedaNota.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(rutaPDF));
            document.open();

            try {
                Image imagen = Image.getInstance("C:\\Users\\jr860\\Desktop\\ProyetoIntegrador\\Prueba PF\\src\\img\\colegio_logo.png");
                imagen.scaleToFit(100, 50); 
                document.add(imagen);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al cargar la imagen", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            
            Paragraph infoDerecha = new Paragraph();
            infoDerecha.setAlignment(Element.ALIGN_RIGHT);

            // Usuario
            Chunk usuarioChunk = new Chunk("Usuario: Administrador", FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK));
            infoDerecha.add(usuarioChunk);
            Chunk fechaChunk = new Chunk("\nFecha: " + obtenerFechaActual(), FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK));
            infoDerecha.add(fechaChunk);
            Chunk horaChunk = new Chunk("\nHora: " + obtenerHoraActual(), FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK));
            infoDerecha.add(horaChunk);

            
            infoDerecha.setSpacingBefore(-50);

            document.add(infoDerecha);

            LineSeparator separator = new LineSeparator();
            separator.setLineColor(BaseColor.BLACK);
            separator.setLineWidth(1);
            Chunk linebreak = new Chunk(separator);
            document.add(linebreak);

            Paragraph espacio = new Paragraph(" ");
            document.add(espacio);

            BaseColor titleColor = new BaseColor(23, 32, 49); 
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, titleColor);

            String textoTitulo = "Reporte de Notas";
            Paragraph titulo = new Paragraph(textoTitulo.toUpperCase(), titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER); // Centrar el texto
            titulo.setSpacingAfter(30f);
            document.add(titulo);

            PdfPTable pdfTable = new PdfPTable(15); 
            pdfTable.setWidthPercentage(100);
            pdfTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            pdfTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfTable.getDefaultCell().setBackgroundColor(BaseColor.GRAY);

            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            BaseColor headerBackgroundColor = new BaseColor(193, 101, 1, 255);

            DefaultTableModel miModelo = (DefaultTableModel) tabla.getModel(); 
            for (int i = 0; i < miModelo.getColumnCount(); i++) {
                String columnName = miModelo.getColumnName(i).toUpperCase(); // Convertir a mayúsculas
                PdfPCell headerCell = new PdfPCell(new Phrase(columnName, headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerCell.setBackgroundColor(headerBackgroundColor); 
                headerCell.setFixedHeight(30f); 
                pdfTable.addCell(headerCell);
            }
            
            com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL, BaseColor.WHITE);
            BaseColor rowBackgroundColor = new BaseColor(22, 24, 36);

            float alturaFila = 20f; 
            float padding = 2f; // Espacio adicional muy apretado

            for (int i = 0; i < miModelo.getRowCount(); i++) {
                for (int j = 0; j < miModelo.getColumnCount(); j++) {
                    Object cellValue = miModelo.getValueAt(i, j);
                    PdfPCell dataCell = new PdfPCell(new Phrase(cellValue != null ? cellValue.toString() : "", dataFont));
                    dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    dataCell.setFixedHeight(alturaFila);
                    dataCell.setPadding(padding); // Establecer el espacio adicional
                    dataCell.setBackgroundColor(rowBackgroundColor); // Establecer el color de fondo de la fila
                    pdfTable.addCell(dataCell);
                }
            }
            
            document.add(pdfTable);

        } catch (DocumentException | IOException e) {
            JOptionPane.showMessageDialog(null, "Error al generar el PDF", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
    
    private String obtenerFechaActual() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String obtenerHoraActual() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    public void convertirJTableAExcel(JTable tabla) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("Registro de Notas");
            int filaInicio = 3; 
            int columnaInicio = 2;

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 18);
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);

            Row titleRow = sheet.createRow(filaInicio);
            Cell titleCell = titleRow.createCell(columnaInicio);
            titleCell.setCellValue("Registro de Notas");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(filaInicio, filaInicio, columnaInicio, columnaInicio + tabla.getColumnCount() - 1));

            sheet.createRow(filaInicio + 1);

            byte[] rgb = {(byte) 193, (byte) 101, (byte) 1}; // Color rgba(193,101,1,255)
            XSSFColor headerColor = new XSSFColor(new java.awt.Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF), null);

            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(headerColor);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            XSSFFont headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(filaInicio + 2);
            for (int i = 0; i < tabla.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(columnaInicio + i);
                cell.setCellValue(tabla.getColumnName(i).toUpperCase()); // Convertir a mayúsculas
                cell.setCellStyle(headerStyle);
            }

            byte[] rgbRow = {(byte) 22, (byte) 24, (byte) 36}; // Color rgba(22,24,36,255)
            XSSFColor rowColor = new XSSFColor(new java.awt.Color(rgbRow[0] & 0xFF, rgbRow[1] & 0xFF, rgbRow[2] & 0xFF), null);

            XSSFCellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setFillForegroundColor(rowColor);
            dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            XSSFFont dataFont = workbook.createFont();
            dataFont.setColor(IndexedColors.WHITE.getIndex());
            dataStyle.setFont(dataFont);

            for (int i = 0; i < tabla.getRowCount(); i++) {
                Row row = sheet.createRow(filaInicio + 3 + i);
                for (int j = 0; j < tabla.getColumnCount(); j++) {
                    Cell cell = row.createCell(columnaInicio + j);
                    cell.setCellValue(tabla.getValueAt(i, j).toString());
                    cell.setCellStyle(dataStyle); 
                }
            }

            for (int i = 0; i < tabla.getColumnCount(); i++) {
                sheet.setColumnWidth(i + columnaInicio, 15 * 256); 
            }

            String rutaExcel = "ReporteNotas.xlsx";
            try (FileOutputStream fileOut = new FileOutputStream(rutaExcel)) {
                workbook.write(fileOut);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al generar el Excel", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        content = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        txtNombres = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtNota1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNota = new javax.swing.JTable();
        btnAñadir = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        txtNota2 = new javax.swing.JTextField();
        txtNota3 = new javax.swing.JTextField();
        txtNota4 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtApellidoPaterno = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtApellidoMaterno = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtGrado = new javax.swing.JComboBox<>();
        txtSeccion = new javax.swing.JComboBox<>();
        btnBuscar = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        btnBuscarCurso = new javax.swing.JButton();
        txtCurso = new javax.swing.JTextField();
        txtID1 = new javax.swing.JTextField();
        txtID2 = new javax.swing.JTextField();
        btnBuscar1 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();

        content.setBackground(new java.awt.Color(255, 255, 255));
        content.setPreferredSize(new java.awt.Dimension(1000, 673));

        jPanel2.setBackground(new java.awt.Color(22, 25, 37));

        jLabel1.setFont(new java.awt.Font("Corbel", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("REGISTRO GENERAL DE NOTAS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(83, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("ID:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Nombres:");

        txtID.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtID.setEnabled(false);
        txtID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDActionPerformed(evt);
            }
        });

        txtNombres.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNombres.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNombres.setEnabled(false);
        txtNombres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombresActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("1°Bimestre:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("4°Bimestre:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("3°Bimestre:");

        txtNota1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNota1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNota1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNota1ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("2°Bimestre:");

        tblNota.setBackground(new java.awt.Color(22, 25, 37));
        tblNota.setForeground(new java.awt.Color(255, 255, 255));
        tblNota.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblNota.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNotaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblNota);

        btnAñadir.setBackground(new java.awt.Color(22, 25, 37));
        btnAñadir.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAñadir.setForeground(new java.awt.Color(255, 255, 255));
        btnAñadir.setText("Agregar");
        btnAñadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirActionPerformed(evt);
            }
        });

        btnEliminar.setBackground(new java.awt.Color(22, 25, 37));
        btnEliminar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnActualizar.setBackground(new java.awt.Color(22, 25, 37));
        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        txtNota2.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNota2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNota2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNota2ActionPerformed(evt);
            }
        });

        txtNota3.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNota3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNota3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNota3ActionPerformed(evt);
            }
        });

        txtNota4.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNota4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNota4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNota4ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Apellido Paterno:");

        txtApellidoPaterno.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtApellidoPaterno.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtApellidoPaterno.setEnabled(false);
        txtApellidoPaterno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtApellidoPaternoActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Apellido Materno:");

        txtApellidoMaterno.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtApellidoMaterno.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtApellidoMaterno.setEnabled(false);
        txtApellidoMaterno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtApellidoMaternoActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Grado:");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Seccion:");

        txtGrado.setBackground(new java.awt.Color(22, 25, 37));
        txtGrado.setForeground(new java.awt.Color(255, 255, 255));
        txtGrado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECCIONE", "INICIAL", "PRIMARIA", "SECUNDARIA" }));
        txtGrado.setEnabled(false);

        txtSeccion.setBackground(new java.awt.Color(22, 25, 37));
        txtSeccion.setForeground(new java.awt.Color(255, 255, 255));
        txtSeccion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECCIONE", "1°", "2°", "3°", "4°", "5°", "6°" }));
        txtSeccion.setEnabled(false);

        btnBuscar.setBackground(new java.awt.Color(22, 25, 37));
        btnBuscar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscar.setText("Buscar Alumno");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Curso:");

        btnBuscarCurso.setBackground(new java.awt.Color(22, 25, 37));
        btnBuscarCurso.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBuscarCurso.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscarCurso.setText("Seleccionar curso");
        btnBuscarCurso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarCursoActionPerformed(evt);
            }
        });

        txtCurso.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtCurso.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCurso.setEnabled(false);
        txtCurso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCursoActionPerformed(evt);
            }
        });

        txtID1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtID1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtID1.setEnabled(false);
        txtID1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtID1ActionPerformed(evt);
            }
        });

        txtID2.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtID2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtID2.setEnabled(false);
        txtID2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtID2ActionPerformed(evt);
            }
        });

        btnBuscar1.setBackground(new java.awt.Color(22, 25, 37));
        btnBuscar1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBuscar1.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscar1.setText("Buscar ");
        btnBuscar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar1ActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("ID_Estudiante:");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("ID_Curso:");

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/salir.png"))); // NOI18N
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel17MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout contentLayout = new javax.swing.GroupLayout(content);
        content.setLayout(contentLayout);
        contentLayout.setHorizontalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentLayout.createSequentialGroup()
                        .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(contentLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNota2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(55, 55, 55)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNota3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNota4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE))
                            .addGroup(contentLayout.createSequentialGroup()
                                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(contentLayout.createSequentialGroup()
                                            .addComponent(btnAñadir, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(451, 451, 451)
                                            .addComponent(btnBuscar1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 966, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(contentLayout.createSequentialGroup()
                                            .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(contentLayout.createSequentialGroup()
                                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(jLabel17))
                                            .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(contentLayout.createSequentialGroup()
                                                    .addGap(46, 46, 46)
                                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                                                    .addComponent(txtID1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(57, 57, 57)
                                                    .addComponent(jLabel4)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                                                    .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(68, 68, 68)
                                                    .addComponent(jLabel10)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(txtApellidoPaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentLayout.createSequentialGroup()
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(184, 184, 184)))))
                                    .addGroup(contentLayout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtID2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(26, 26, 26)
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnBuscarCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(contentLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtGrado, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSeccion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(31, 31, 31)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50))))
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtID1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtApellidoPaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtGrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSeccion)
                    .addComponent(btnBuscar))
                .addGap(26, 26, 26)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBuscarCurso)
                        .addComponent(txtCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(txtID2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNota2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNota3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNota4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAñadir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, 1009, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIDActionPerformed

    private void txtNombresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombresActionPerformed

    private void txtNota1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNota1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNota1ActionPerformed

    private void btnAñadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirActionPerformed
        try {
            // Verificar si los campos obligatorios están vacíos
            if (txtNombres.getText().isEmpty() || txtApellidoPaterno.getText().isEmpty() ||
                txtApellidoMaterno.getText().isEmpty() || txtCurso.getText().isEmpty() || txtNota1.getText().isEmpty() || txtNota2.getText().isEmpty() ||
                txtNota3.getText().isEmpty() || txtNota4.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos antes de agregar.");
                return; // Salir del método si algún campo obligatorio está vacío
            }

            // Verificar si ya existe un registro con los mismos datos
            String query = "SELECT COUNT(*) FROM nota_general WHERE nombres = ? AND apellido_paterno = ? AND apellido_materno = ? AND curso = ?";
            PreparedStatement statement = cn.prepareStatement(query);
            statement.setString(1, txtNombres.getText());
            statement.setString(2, txtApellidoPaterno.getText());
            statement.setString(3, txtApellidoMaterno.getText());
            statement.setString(4, txtCurso.getText());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count > 0) {
                JOptionPane.showMessageDialog(null, "Ya existe un registro con los mismos datos.");
                limpiarEntradas();
                return; 
            }

            // Preparar la consulta SQL
            PreparedStatement ps = cn.prepareStatement("INSERT INTO nota_general (id_estudiantil, nombres, apellido_paterno, apellido_materno, grado, seccion, id_curso, curso, nota1, nota2, nota3, nota4, pf, rendimiento, descuento, year) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            String idEs = txtID1.getText();
            ps.setString(1, idEs);
            
            String nombres = txtNombres.getText();
            ps.setString(2, nombres);

            String apellidoPaterno = txtApellidoPaterno.getText();
            ps.setString(3, apellidoPaterno);

            String apellidoMaterno = txtApellidoMaterno.getText();
            ps.setString(4, apellidoMaterno);

            String grado = txtGrado.getSelectedItem().toString();
            ps.setString(5, grado);
            
            String seccion = txtSeccion.getSelectedItem().toString();
            ps.setString(6, seccion);
            
            String idCur = txtID2.getText();
            ps.setString(7, idCur);
            
            String curso = txtCurso.getText();
            ps.setString(8, curso);

            int nota1 = Integer.parseInt(txtNota1.getText());
            ps.setInt(9, nota1);

            int nota2 = Integer.parseInt(txtNota2.getText());
            ps.setInt(10, nota2);

            int nota3 = Integer.parseInt(txtNota3.getText());
            ps.setInt(11, nota3);

            int nota4 = Integer.parseInt(txtNota4.getText());
            ps.setInt(12, nota4);

            // Calcular promedio final
            double promedioFinal = (nota1 + nota2 + nota3 + nota4) / 4.0;
            ps.setDouble(13, promedioFinal);

            // Determinar si se otorga la beca
            String rendimiento = (promedioFinal >= 12) ? "Aprobado" : "Reprobado";
            ps.setString(14, rendimiento);
            
            // Determinar si se aplica el descuento
            String descuento = (promedioFinal >= 17) ? "Si" : "No";
            ps.setString(15, descuento);

            // Obtener el año actual
            Calendar calendar = Calendar.getInstance();
            int año = calendar.get(Calendar.YEAR);
            ps.setInt(16, año);

            // Ejecutar la consulta
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Datos GUARDADOS CORRECTAMENTE");
            mostrarDatos();

            limpiarEntradas();

        } catch(SQLException e) {
            System.out.println("ERROR AL REGISTRAR LAS NOTAS" + e);
        }
    }//GEN-LAST:event_btnAñadirActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // Verificar si se ha seleccionado una fila en la tabla
        int filaSeleccionada = tblNota.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione una fila para eliminar.");
            return;
        }

        // Confirmar si el usuario está seguro de eliminar la fila
        int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar la fila seleccionada?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return; // Cancelar la eliminación si el usuario selecciona "No"
        }

        try {
            // Obtener el ID del estudiante desde la fila seleccionada en la tabla
            int idEstudiante = Integer.parseInt(tblNota.getValueAt(filaSeleccionada, 0).toString());

            // Preparar la consulta SQL para eliminar la fila de la base de datos
            PreparedStatement ps = cn.prepareStatement("DELETE FROM nota_general WHERE id_nota=?");
            ps.setInt(1, idEstudiante);

            // Ejecutar la consulta SQL para eliminar la fila
            int filasEliminadas = ps.executeUpdate();

            if (filasEliminadas > 0) {
                JOptionPane.showMessageDialog(null, "Fila eliminada correctamente.");
                mostrarDatos(); // Actualizar la tabla para reflejar los cambios
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar la fila. Por favor, inténtelo de nuevo.");
            }

        } catch (SQLException e) {
            System.out.println("Error al eliminar la fila: " + e.getMessage());
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        try {
            // Verificar si se ha seleccionado una fila en la tabla
            int filaSeleccionada = tblNota.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione una fila para actualizar.");
                return;
            }

            // Obtener el ID del estudiante desde la fila seleccionada en la tabla
            int idEstudiante = Integer.parseInt(tblNota.getValueAt(filaSeleccionada, 0).toString());

            // Obtener las nuevas notas de los campos de texto
            int nuevaNota1 = Integer.parseInt(txtNota1.getText());
            int nuevaNota2 = Integer.parseInt(txtNota2.getText());
            int nuevaNota3 = Integer.parseInt(txtNota3.getText());
            int nuevaNota4 = Integer.parseInt(txtNota4.getText());

            // Calcular el promedio final con las nuevas notas
            double promedioFinal = (nuevaNota1 + nuevaNota2 + nuevaNota3 + nuevaNota4) / 4.0;

            // Determinar si se otorga la beca con las nuevas notas
            String rendimiento = (promedioFinal >= 12) ? "Aprobado" : "Reprobado";
            
            // Determinar si se aplica el descuento
            String descuento = (promedioFinal >= 17) ? "Si" : "No";

            // Preparar la consulta SQL para actualizar las notas, el promedio final y la beca
            PreparedStatement ps = cn.prepareStatement("UPDATE nota_general SET nota1=?, nota2=?, nota3=?, nota4=?, pf=?, rendimiento=?, descuento=? WHERE id_estudiantil=?");

            // Establecer las nuevas notas, promedio final y beca en la consulta SQL
            ps.setInt(1, nuevaNota1);
            ps.setInt(2, nuevaNota2);
            ps.setInt(3, nuevaNota3);
            ps.setInt(4, nuevaNota4);
            ps.setDouble(5, promedioFinal);
            ps.setString(6, rendimiento);
            ps.setString(7, descuento);
            ps.setInt(8, idEstudiante);
            
            // Ejecutar la consulta SQL para actualizar las notas, el promedio final y la beca
            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas > 0) {
                JOptionPane.showMessageDialog(null, "Notas actualizadas correctamente.");
                mostrarDatos(); // Actualizar la tabla para reflejar los cambios
                limpiarEntradas();
            } else {
                JOptionPane.showMessageDialog(null, "Error al actualizar las notas. Por favor, inténtelo de nuevo.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese valores numéricos válidos para las notas.");
        } catch (SQLException e) {
            System.out.println("Error al actualizar las notas: " + e.getMessage());
        }
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void txtNota2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNota2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNota2ActionPerformed

    private void txtNota3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNota3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNota3ActionPerformed

    private void txtNota4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNota4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNota4ActionPerformed

    private void txtApellidoPaternoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApellidoPaternoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtApellidoPaternoActionPerformed

    private void txtApellidoMaternoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApellidoMaternoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtApellidoMaternoActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // Crear un JDialog para la ventana de búsqueda
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ventana de Búsqueda Estudiantil", true);

        // Crear la tabla para mostrar los datos de registro de profesores
        JTable tabla = new JTable();
        JScrollPane scrollPane = new JScrollPane(tabla);
        dialogo.add(scrollPane);

        // Crear un modelo de tabla
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombres");
        modelo.addColumn("Apell.Paterno");
        modelo.addColumn("Apell.Materno");
        modelo.addColumn("DNI");
        modelo.addColumn("Fech.Nac");
        modelo.addColumn("Genero");
        modelo.addColumn("Fech.Matricula");
        modelo.addColumn("Grado");
        modelo.addColumn("Seccion");
        modelo.addColumn("Direccion");
        modelo.addColumn("Teléfono");
        tabla.setModel(modelo);

        // Llenar la tabla con los datos de la base de datos
        try {
            String consultaSql = "SELECT * FROM registro_alumnos";
            Statement st = cn.createStatement();
            java.sql.ResultSet rs = st.executeQuery(consultaSql);
            while (rs.next()) {
                Object[] fila = new Object[12];
                for (int i = 0; i < 12; i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos de la base de datos: " + e.getMessage());
        }

        // Configurar el tamaño y la ubicación del JDialog
        dialogo.setSize(850, 450);
        dialogo.setLocationRelativeTo(this);
        
        // Agregar botones "Agregar" y "Salir"
        JButton btnAgregar = new JButton("Agregar");
        JButton btnSalir = new JButton("Salir");
        
        // Configurar ActionListener para el botón "Agregar"
        btnAgregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obtener la fila seleccionada
            int filaSeleccionada = tabla.getSelectedRow();
            if (filaSeleccionada != -1) {
                // Obtener los datos de la fila seleccionada
                Object[] fila = new Object[modelo.getColumnCount()];
                for (int i = 0; i < fila.length; i++) {
                    fila[i] = modelo.getValueAt(filaSeleccionada, i);
                }
                // Mostrar los datos en los JTextField correspondientes del JPanel
                txtID1.setText(fila[0].toString());
                txtNombres.setText(fila[1].toString());
                txtApellidoPaterno.setText(fila[2].toString());
                txtGrado.setSelectedItem(fila[8].toString());
                txtSeccion.setSelectedItem(fila[9].toString());
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione una fila para agregar.");
            }
            }
        });
        
        // Configurar ActionListener para el botón "Salir"
        btnSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogo.dispose(); // Cerrar el JDialog
            }
        });

        // Crear un panel para contener los botones
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAgregar);
        panelBotones.add(btnSalir);

        // Agregar el panel de botones al JDialog
        dialogo.add(panelBotones, BorderLayout.SOUTH);

        dialogo.setVisible(true); // Mostrar el JDialog
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void tblNotaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNotaMouseClicked
        int fila = this.tblNota.getSelectedRow();
        this.txtID.setText(this.tblNota.getValueAt(fila, 0).toString());
        this.txtID1.setText(this.tblNota.getValueAt(fila, 1).toString());
        this.txtNombres.setText(this.tblNota.getValueAt(fila, 2).toString());
        this.txtApellidoPaterno.setText(this.tblNota.getValueAt(fila, 3).toString());
        this.txtApellidoMaterno.setText(this.tblNota.getValueAt(fila, 4).toString());
        this.txtGrado.setSelectedItem(this.tblNota.getValueAt(fila, 5).toString());
        this.txtSeccion.setSelectedItem(this.tblNota.getValueAt(fila, 6).toString());
        this.txtID2.setText(this.tblNota.getValueAt(fila, 7).toString());
        this.txtCurso.setText(this.tblNota.getValueAt(fila, 8).toString());
        this.txtNota1.setText(this.tblNota.getValueAt(fila, 9).toString());
        this.txtNota2.setText(this.tblNota.getValueAt(fila, 10).toString());
        this.txtNota3.setText(this.tblNota.getValueAt(fila, 11).toString());
        this.txtNota4.setText(this.tblNota.getValueAt(fila, 12).toString());
    }//GEN-LAST:event_tblNotaMouseClicked

    private void btnBuscarCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarCursoActionPerformed
        // Crear un JDialog para la ventana de búsqueda
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ventana de Cursos", true);

        // Crear la tabla para mostrar los datos de registro de profesores
        JTable tabla = new JTable();
        JScrollPane scrollPane = new JScrollPane(tabla);
        dialogo.add(scrollPane);

        // Crear un modelo de tabla
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Curso");
        tabla.setModel(modelo);

        // Llenar la tabla con los datos de la base de datos
        try {
            String consultaSql = "SELECT * FROM curso";
            Statement st = cn.createStatement();
            java.sql.ResultSet rs = st.executeQuery(consultaSql);
            while (rs.next()) {
                Object[] fila = new Object[2];
                for (int i = 0; i < 2; i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos de la base de datos: " + e.getMessage());
        }

        // Configurar el tamaño y la ubicación del JDialog
        dialogo.setSize(850, 450);
        dialogo.setLocationRelativeTo(this);
        
        // Agregar botones "Agregar" y "Salir"
        JButton btnAgregar = new JButton("Agregar");
        JButton btnSalir = new JButton("Salir");
        
        // Configurar ActionListener para el botón "Agregar"
        btnAgregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obtener la fila seleccionada
            int filaSeleccionada = tabla.getSelectedRow();
            if (filaSeleccionada != -1) {
                // Obtener los datos de la fila seleccionada
                Object[] fila = new Object[modelo.getColumnCount()];
                for (int i = 0; i < fila.length; i++) {
                    fila[i] = modelo.getValueAt(filaSeleccionada, i);
                }
                // Mostrar los datos en los JTextField correspondientes del JPanel
                txtID2.setText(fila[0].toString());
                txtCurso.setText(fila[1].toString());
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione una fila para agregar.");
            }
            }
        });
        
        // Configurar ActionListener para el botón "Salir"
        btnSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogo.dispose(); // Cerrar el JDialog
            }
        });

        // Crear un panel para contener los botones
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAgregar);
        panelBotones.add(btnSalir);

        // Agregar el panel de botones al JDialog
        dialogo.add(panelBotones, BorderLayout.SOUTH);

        dialogo.setVisible(true); // Mostrar el JDialog
    }//GEN-LAST:event_btnBuscarCursoActionPerformed

    private void txtCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCursoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCursoActionPerformed

    private void txtID1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtID1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtID1ActionPerformed

    private void txtID2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtID2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtID2ActionPerformed

    private void btnBuscar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscar1ActionPerformed
        // Mostrar un cuadro de diálogo para que el usuario seleccione qué desea buscar
        String opcionBusqueda = JOptionPane.showInputDialog(null, "¿Qué desea buscar? (id,nombre, apellido,curso,rendimiento)", "Buscar", JOptionPane.QUESTION_MESSAGE);

        if (opcionBusqueda != null) {
            try {
                // Preparar la consulta SQL
                String consulta = "";
                switch (opcionBusqueda.toLowerCase()) {
                    case "id":
                    String idAlum= JOptionPane.showInputDialog(null, "Ingrese el id del alumno a buscar:", "Buscar por curso", JOptionPane.QUESTION_MESSAGE);
                    if (idAlum != null) {
                        consulta = "SELECT * FROM nota_general WHERE id_estudiantil = '" + idAlum + "'";
                    }
                    break;
                    case "nombre":
                        String nombre = JOptionPane.showInputDialog(null, "Ingrese el nombre a buscar:", "Buscar por nombre", JOptionPane.QUESTION_MESSAGE);
                        if (nombre != null) {
                            consulta = "SELECT * FROM nota_general WHERE nombres = '" + nombre + "'";
                        }
                        break;
                    case "apellido":
                        String apellido = JOptionPane.showInputDialog(null, "Ingrese el apellido a buscar:", "Buscar por apellido", JOptionPane.QUESTION_MESSAGE);
                        if (apellido != null) {
                            consulta = "SELECT * FROM nota_general WHERE apellido_paterno = '" + apellido + "' OR apellido_materno = '" + apellido + "'";
                        }
                        break;
                    case "curso":
                        String curso = JOptionPane.showInputDialog(null, "Ingrese el curso a buscar:", "Buscar por curso", JOptionPane.QUESTION_MESSAGE);
                        if (curso != null) {
                            consulta = "SELECT * FROM nota_general WHERE curso = '" + curso + "'";
                        }
                        break;
                    case "rendimiento":
                        String rendimiento = JOptionPane.showInputDialog(null, "Ingrese el resultado a buscar:", "Buscar por curso", JOptionPane.QUESTION_MESSAGE);
                        if (rendimiento != null) {
                            consulta = "SELECT * FROM nota_general WHERE rendimiento = '" + rendimiento + "'";
                        }
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Opción de búsqueda no válida", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                }

                // Ejecutar la consulta SQL y actualizar la tabla
                Statement stmt = cn.createStatement();
                ResultSet rs = stmt.executeQuery(consulta);

                // Crear un DefaultTableModel y añadir las columnas
                DefaultTableModel modelo = new DefaultTableModel();
                modelo.addColumn("ID");
                modelo.addColumn("Nombres");
                modelo.addColumn("Apell.Paterno");
                modelo.addColumn("Apell.Materno");
                modelo.addColumn("Grado");
                modelo.addColumn("Seccion");
                modelo.addColumn("Curso");
                modelo.addColumn("Nota1");
                modelo.addColumn("Nota2");
                modelo.addColumn("Nota3");
                modelo.addColumn("Nota4");
                modelo.addColumn("P.F");
                modelo.addColumn("Rendimiento");
                modelo.addColumn("Descuento");
                modelo.addColumn("Año");

                // Añadir filas al modelo
                while (rs.next()) {
                    Object[] fila = new Object[15];
                    fila[0] = rs.getObject(2); 
                    fila[1] = rs.getObject(3); 
                    fila[2] = rs.getObject(4); 
                    fila[3] = rs.getObject(5); 
                    fila[4] = rs.getObject(6); 
                    fila[5] = rs.getObject(7); 
                    fila[6] = rs.getObject(9); 
                    fila[7] = rs.getObject(10); 
                    fila[8] = rs.getObject(11); 
                    fila[9] = rs.getObject(12); 
                    fila[10] = rs.getObject(13); 
                    fila[11] = rs.getObject(14); 
                    fila[12] = rs.getObject(15); 
                    fila[13] = rs.getObject(16); 
                    fila[14] = rs.getObject(17); 
                    modelo.addRow(fila);
                }

                // Crear la tabla para mostrar los datos de la consulta
                JTable tabla = new JTable(modelo);
                JScrollPane scrollPane = new JScrollPane(tabla);

                // Crear un JDialog para mostrar la tabla
                JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Resultados de la Búsqueda", true);
                dialogo.setSize(850, 450);
                dialogo.setLocationRelativeTo(this);

                // Agregar el JScrollPane al JDialog
                dialogo.add(scrollPane, BorderLayout.CENTER);

                // Agregar un botón "Salir" al JDialog
                JPanel panelBotones = new JPanel();
                JButton btnSalir = new JButton("Salir");
                btnSalir.addActionListener(e -> dialogo.dispose());
                panelBotones.add(btnSalir);
                dialogo.add(panelBotones, BorderLayout.SOUTH);
                
                // Agregar botones
                JButton btnPDF = new JButton("Exportar a PDF");
                btnPDF.addActionListener(e -> convertirJTableAPDF(tabla));

                JButton btnExcel = new JButton("Exportar a Excel");
                btnExcel.addActionListener(e -> convertirJTableAExcel(tabla));

                // Agregar botones al panel de botones
                panelBotones.add(btnPDF);
                panelBotones.add(btnExcel);
                // Mostrar el JDialog
                dialogo.setVisible(true);

            } catch (SQLException e) {
                System.out.println("Error al buscar en la base de datos: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnBuscar1ActionPerformed

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
        academico p = new academico();
        p.setSize(1000, 670);
        p.setLocation(0,0);

        content.removeAll();
        content.add(p, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }//GEN-LAST:event_jLabel17MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnAñadir;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnBuscar1;
    private javax.swing.JButton btnBuscarCurso;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JPanel content;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblNota;
    private javax.swing.JTextField txtApellidoMaterno;
    private javax.swing.JTextField txtApellidoPaterno;
    private javax.swing.JTextField txtCurso;
    private javax.swing.JComboBox<String> txtGrado;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtID1;
    private javax.swing.JTextField txtID2;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtNota1;
    private javax.swing.JTextField txtNota2;
    private javax.swing.JTextField txtNota3;
    private javax.swing.JTextField txtNota4;
    private javax.swing.JComboBox<String> txtSeccion;
    // End of variables declaration//GEN-END:variables
    
    private void mostrarDatos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("ID_Estudiante");
        modelo.addColumn("Nombres");
        modelo.addColumn("Apell.Paterno");
        modelo.addColumn("Apell.Materno");
        modelo.addColumn("Grado");
        modelo.addColumn("Seccion");
        modelo.addColumn("ID_Curso");
        modelo.addColumn("Curso");
        modelo.addColumn("Nota1");
        modelo.addColumn("Nota2");
        modelo.addColumn("Nota3");
        modelo.addColumn("Nota4");
        modelo.addColumn("P.F");
        modelo.addColumn("Rendimiento");
        modelo.addColumn("Descuento");
        modelo.addColumn("Año");
        tblNota.setModel(modelo);
        String consultasql="select*from nota_general";
        String data[]=new String[17];

        Statement st;
        try {
            st = cn.createStatement();
            ResultSet rs=st.executeQuery(consultasql);
            while(rs.next()) {
                data[0]=rs.getString(1);
                data[1]=rs.getString(2);
                data[2]=rs.getString(3);
                data[3]=rs.getString(4);
                data[4]=rs.getString(5);
                data[5]=rs.getString(6);
                data[6]=rs.getString(7);
                data[7]=rs.getString(8);
                data[8]=rs.getString(9);
                data[9]=rs.getString(10);
                data[10]=rs.getString(11);
                data[11]=rs.getString(12);
                data[12]=rs.getString(13);
                data[13]=rs.getString(14);
                data[14]=rs.getString(15);
                data[15]=rs.getString(16);
                data[16]=rs.getString(17);
                modelo.addRow(data);
            }
        } catch(SQLException e) {
            System.out.println("Error al mostrar Datos "+ e);
        }
    }

    private void limpiarEntradas() {
        txtID1.setText("");
        txtID.setText("");
        txtNombres.setText("");
        txtApellidoPaterno.setText("");
        txtApellidoMaterno.setText("");
        txtGrado.setSelectedIndex(0);
        txtSeccion.setSelectedIndex(0);
        txtID2.setText("");
        txtCurso.setText("");
        txtNota1.setText("");
        txtNota2.setText("");
        txtNota3.setText("");
        txtNota4.setText("");
    }
    
}
