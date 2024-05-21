/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Vista;

import Controlador.conexion_RP;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.demo.DateChooserPanel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

/**
 *
 * @author PC
 */
public class A_ME extends javax.swing.JPanel {
    
    conexion_RP con = new conexion_RP();
    Connection cn=con.conectar();
    /**
     * Creates new form A_ME
     */
    public A_ME() {
        initComponents();
        
        mostrarDatos();
    }
    
    // --------------------- METODOS PARA GUARDAR DATOS EN UN PDF Y EXCEL --------------------------
    
    public void convertirJTableAPDF() {
        Rectangle pageSize = PageSize.A3.rotate();
        Document document = new Document(pageSize);
        try {
            
            String rutaPDF = "RegistroAlumnos.pdf";
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

            String textoTitulo = "Reporte de Alumnos";
            Paragraph titulo = new Paragraph(textoTitulo.toUpperCase(), titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER); // Centrar el texto
            titulo.setSpacingAfter(30f);
            document.add(titulo);

            PdfPTable pdfTable = new PdfPTable(12); 
            pdfTable.setWidthPercentage(100);
            pdfTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            pdfTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfTable.getDefaultCell().setBackgroundColor(BaseColor.GRAY);

            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            BaseColor headerBackgroundColor = new BaseColor(193, 101, 1, 255);

            DefaultTableModel miModelo = (DefaultTableModel) tblEstudiantil.getModel(); 
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
    
    public void convertirJTableAExcel() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("Registro de Alumnos");
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
            titleCell.setCellValue("Registro de Alumnos");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(filaInicio, filaInicio, columnaInicio, columnaInicio + tblEstudiantil.getColumnCount() - 1));

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
            for (int i = 0; i < tblEstudiantil.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(columnaInicio + i);
                cell.setCellValue(tblEstudiantil.getColumnName(i).toUpperCase()); // Convertir a mayúsculas
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

            for (int i = 0; i < tblEstudiantil.getRowCount(); i++) {
                Row row = sheet.createRow(filaInicio + 3 + i);
                for (int j = 0; j < tblEstudiantil.getColumnCount(); j++) {
                    Cell cell = row.createCell(columnaInicio + j);
                    cell.setCellValue(tblEstudiantil.getValueAt(i, j).toString());
                    cell.setCellStyle(dataStyle); 
                }
            }

            for (int i = 0; i < tblEstudiantil.getColumnCount(); i++) {
                sheet.setColumnWidth(i + columnaInicio, 15 * 256); 
            }

            String rutaExcel = "RegistroMatricula.xlsx";
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
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        txtNombres = new javax.swing.JTextField();
        txtApellidoPaterno = new javax.swing.JTextField();
        txtApellidoMaterno = new javax.swing.JTextField();
        txtDNI = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEstudiantil = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btnAñadir = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        txtGenero = new javax.swing.JComboBox<>();
        txtGrado = new javax.swing.JComboBox<>();
        txtFechNac = new com.toedter.calendar.JDateChooser();
        txtFechMatricula = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        txtSeccion = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();

        content.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(22, 25, 37));

        jLabel1.setFont(new java.awt.Font("Corbel", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("MATRICULA ESTUDIANTIL");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(85, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78))
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

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Apellido Paterno:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Apellido Materno:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("DNI:");

        txtID.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtID.setEnabled(false);
        txtID.setPreferredSize(new java.awt.Dimension(64, 28));
        txtID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDActionPerformed(evt);
            }
        });

        txtNombres.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNombres.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNombres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombresActionPerformed(evt);
            }
        });

        txtApellidoPaterno.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtApellidoPaterno.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtApellidoPaterno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtApellidoPaternoActionPerformed(evt);
            }
        });

        txtApellidoMaterno.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtApellidoMaterno.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtApellidoMaterno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtApellidoMaternoActionPerformed(evt);
            }
        });

        txtDNI.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtDNI.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDNI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDNIActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Fech.Nacimiento:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Genero:");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Fech.Matricula:");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Grado:");

        tblEstudiantil.setBackground(new java.awt.Color(22, 25, 37));
        tblEstudiantil.setForeground(new java.awt.Color(255, 255, 255));
        tblEstudiantil.setModel(new javax.swing.table.DefaultTableModel(
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
        tblEstudiantil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEstudiantilMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblEstudiantil);

        jButton5.setBackground(new java.awt.Color(22, 25, 37));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Exportar EXCEL");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(22, 25, 37));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Exportar PDF");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

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

        txtGenero.setBackground(new java.awt.Color(22, 25, 37));
        txtGenero.setForeground(new java.awt.Color(255, 255, 255));
        txtGenero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECCIONE", "MASCULINO", "FEMENINO " }));

        txtGrado.setBackground(new java.awt.Color(22, 25, 37));
        txtGrado.setForeground(new java.awt.Color(255, 255, 255));
        txtGrado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECCIONE", "INICIAL", "PRIMARIA", "SECUNDARIA" }));

        txtFechNac.setBackground(new java.awt.Color(22, 25, 37));
        txtFechNac.setForeground(new java.awt.Color(255, 255, 255));

        txtFechMatricula.setBackground(new java.awt.Color(22, 25, 37));
        txtFechMatricula.setForeground(new java.awt.Color(255, 255, 255));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Seccion:");

        txtSeccion.setBackground(new java.awt.Color(22, 25, 37));
        txtSeccion.setForeground(new java.awt.Color(255, 255, 255));
        txtSeccion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECCIONE", "1°", "2°", "3°", "4°", "5°", "6°" }));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Direccion:");

        txtDireccion.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtDireccion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDireccion.setPreferredSize(new java.awt.Dimension(64, 28));
        txtDireccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDireccionActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Telefono:");

        txtTelefono.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtTelefono.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTelefono.setPreferredSize(new java.awt.Dimension(64, 28));
        txtTelefono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefonoActionPerformed(evt);
            }
        });

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/salir.png"))); // NOI18N
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel15MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout contentLayout = new javax.swing.GroupLayout(content);
        content.setLayout(contentLayout);
        contentLayout.setHorizontalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentLayout.createSequentialGroup()
                        .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtApellidoMaterno, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtApellidoPaterno, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                                .addComponent(txtNombres, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtID, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(contentLayout.createSequentialGroup()
                                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(contentLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel10))
                                    .addGroup(contentLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel12)
                                            .addComponent(jLabel11))))
                                .addGap(18, 18, 18)
                                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtGrado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtFechMatricula, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtSeccion, 0, 177, Short.MAX_VALUE)
                                    .addComponent(txtGenero, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(contentLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addGroup(contentLayout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtFechNac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addGap(18, 18, 18)
                        .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addGap(18, 18, 18)
                        .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(contentLayout.createSequentialGroup()
                        .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, contentLayout.createSequentialGroup()
                                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnAñadir, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(contentLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(contentLayout.createSequentialGroup()
                                        .addGap(23, 23, 23)
                                        .addComponent(txtDNI, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, contentLayout.createSequentialGroup()
                                .addGap(657, 657, 657)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5))
                    .addComponent(jLabel15))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(254, 254, 254))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 988, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentLayout.createSequentialGroup()
                        .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(contentLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(contentLayout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentLayout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(txtFechNac, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtGenero, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtApellidoPaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtFechMatricula, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGrado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDNI, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSeccion))
                .addGap(32, 32, 32)
                .addGroup(contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAñadir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIDActionPerformed

    private void txtNombresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombresActionPerformed

    private void txtApellidoPaternoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApellidoPaternoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtApellidoPaternoActionPerformed

    private void txtApellidoMaternoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApellidoMaternoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtApellidoMaternoActionPerformed

    private void txtDNIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDNIActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDNIActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        convertirJTableAExcel();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        convertirJTableAPDF();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnAñadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirActionPerformed
        try {
            // Verificar si los campos obligatorios están vacíos
            if (txtNombres.getText().isEmpty() ||
                txtApellidoPaterno.getText().isEmpty() || txtApellidoMaterno.getText().isEmpty() || txtFechNac.getDate() == null || txtGenero.getSelectedIndex() == 0 ||
                txtDNI.getText().isEmpty() || txtTelefono.getText().isEmpty() ||
                txtDireccion.getText().isEmpty() || txtFechMatricula.getDate() == null || txtGrado.getSelectedIndex() == 0 ||
                txtSeccion.getSelectedIndex() == 0 || txtDireccion.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos antes de agregar.");
                return; // Salir del método si algún campo obligatorio está vacío
            }
            
            PreparedStatement ps=cn.prepareStatement("INSERT INTO registro_alumnos (nombres, apellido_paterno, apellido_materno, dni, fech_nac, genero, fech_matri, grado, seccion, direccion, telefono) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, txtNombres.getText());
            ps.setString(2, txtApellidoPaterno.getText());
            ps.setString(3, txtApellidoMaterno.getText());
            
            // Convertir el ID a entero y establecerlo en la consulta preparada
            int dni = Integer.parseInt(txtDNI.getText());
            ps.setInt(4, dni);
            
            // Convertir la fecha de txtInicio al formato adecuado antes de establecerla en el PreparedStatement
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fechaNac = dateFormat.format(txtFechNac.getDate());
            ps.setString(5, fechaNac);
            
            ps.setString(6, txtGenero.getSelectedItem().toString());
            
            // Ya no es necesario la convercion porque ya lo hemos hecho anteoriormente en el txtFechNac
            String fechaMat = dateFormat.format(txtFechMatricula.getDate());
            ps.setString(7, fechaMat);
            
            ps.setString(8, txtGrado.getSelectedItem().toString());
            ps.setString(9, txtSeccion.getSelectedItem().toString());
            ps.setString(10, txtDireccion.getText());
            
            // Convertir el ID a entero y establecerlo en la consulta preparada
            int tel = Integer.parseInt(txtTelefono.getText());
            ps.setInt(11, tel);
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Datos GUARDADOS CORRECTAMENTE");
            mostrarDatos();
            
            limpiarEntradas();
            
        } catch(SQLException e) {
            System.out.println("ERROR AL REGISTRAR PROFESORES" +e);
        }
    }//GEN-LAST:event_btnAñadirActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        int filaSeleccionada = tblEstudiantil.getSelectedRow();
    
        if (filaSeleccionada == -1) {
            // Si no se ha seleccionado ninguna fila, mostrar un mensaje y salir del método
            JOptionPane.showMessageDialog(null, "Por favor, seleccione una fila para eliminar.");
            return;
        }

        // Obtener el ID del registro seleccionado
        String id = tblEstudiantil.getValueAt(filaSeleccionada, 0).toString();

        // Mostrar un mensaje de confirmación para asegurarse de que el usuario realmente quiere eliminar el registro
        int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro que desea eliminar este registro?");

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Si el usuario confirma que quiere eliminar el registro, proceder con la eliminación
            try {
                // Crear la consulta SQL para eliminar el registro con el ID especificado
                String consultaSQL = "DELETE FROM registro_alumnos WHERE id_estudiantil = ?";

                // Preparar la sentencia SQL
                PreparedStatement ps = cn.prepareStatement(consultaSQL);
                ps.setString(1, id);

                // Ejecutar la sentencia SQL para eliminar el registro
                int resultado = ps.executeUpdate();

                // Verificar si se eliminó correctamente
                if (resultado > 0) {
                    JOptionPane.showMessageDialog(null, "Registro eliminado correctamente.");
                    mostrarDatos(); 
                    limpiarEntradas();
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo eliminar el registro.");
                }
            } catch (SQLException ex) {
                System.out.println("Error al eliminar el registro: " + ex.getMessage());
            }
        } else {
            // Si el usuario cancela la eliminación, no hacer nada
            System.out.println("Eliminación cancelada.");
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        try {
            
            // Verificar si se ha seleccionado una fila en la tabla
            int filaSeleccionada = tblEstudiantil.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione una fila para actualizar.");
                return;
            }
            
            // Crear un formato de fecha
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");

            // Formatear la fecha en el formato adecuado
            String fechaNac = formatoFecha.format(txtFechNac.getDate());
            String fechaMat = formatoFecha.format(txtFechMatricula.getDate());

            // Construir la consulta SQL utilizando la fecha formateada
            PreparedStatement ps = cn.prepareStatement("UPDATE registro_alumnos SET nombres=?, apellido_paterno=?, apellido_materno=?, dni=?, fech_nac=?, genero=?, fech_matri=?, grado=?, seccion=?, direccion=?, telefono=? WHERE id_estudiantil=?");
            ps.setString(1, txtNombres.getText());
            ps.setString(2, txtApellidoPaterno.getText());
            ps.setString(3, txtApellidoMaterno.getText());
            ps.setString(4, txtDNI.getText());
            ps.setString(5, fechaNac); // Usar la fecha formateada aquí
            ps.setString(6, txtGenero.getSelectedItem().toString());
            ps.setString(7, fechaMat); // Usar la fecha formateada aquí
            ps.setString(8, txtGrado.getSelectedItem().toString());
            ps.setString(9, txtSeccion.getSelectedItem().toString());
            ps.setString(10, txtDireccion.getText());
            ps.setString(11, txtTelefono.getText());
            ps.setString(12, txtID.getText());


            // Ejecutar la consulta
            int indice = ps.executeUpdate();

            if (indice > 0) {
                JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORRECTAMENTE");
                mostrarDatos();
                limpiarEntradas();
            } else {
                JOptionPane.showMessageDialog(null, "No seleccione Fila");
            }

        } catch (SQLException e) {
            System.out.println("ERROR AL ACTUALIZAR " + e);;
        }
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void txtDireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDireccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDireccionActionPerformed

    private void txtTelefonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefonoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefonoActionPerformed

    private void tblEstudiantilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEstudiantilMouseClicked
        int fila = this.tblEstudiantil.getSelectedRow();
        this.txtID.setText(this.tblEstudiantil.getValueAt(fila, 0).toString());
        this.txtNombres.setText(this.tblEstudiantil.getValueAt(fila, 1).toString());
        this.txtApellidoPaterno.setText(this.tblEstudiantil.getValueAt(fila, 2).toString());
        this.txtApellidoMaterno.setText(this.tblEstudiantil.getValueAt(fila, 3).toString());
        this.txtDNI.setText(this.tblEstudiantil.getValueAt(fila, 4).toString());
        
        // Convertir el String a Date para el campo de fecha
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            Date nac = formatoFecha.parse(this.tblEstudiantil.getValueAt(fila, 5).toString());
            this.txtFechNac.setDate(nac);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }   

        this.txtGenero.setSelectedItem(this.tblEstudiantil.getValueAt(fila, 6).toString());
        
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            Date mat = formatoFecha.parse(this.tblEstudiantil.getValueAt(fila, 7).toString());
            this.txtFechMatricula.setDate(mat);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        this.txtGrado.setSelectedItem(this.tblEstudiantil.getValueAt(fila, 8).toString());
        this.txtSeccion.setSelectedItem(this.tblEstudiantil.getValueAt(fila, 9).toString());
        
        this.txtDireccion.setText(this.tblEstudiantil.getValueAt(fila, 10).toString());
        this.txtTelefono.setText(this.tblEstudiantil.getValueAt(fila, 11).toString());
    }//GEN-LAST:event_tblEstudiantilMouseClicked

    private void jLabel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseClicked
        academico p = new academico();
        p.setSize(1000, 670);
        p.setLocation(0,0);

        content.removeAll();
        content.add(p, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }//GEN-LAST:event_jLabel15MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnAñadir;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JPanel content;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblEstudiantil;
    private javax.swing.JTextField txtApellidoMaterno;
    private javax.swing.JTextField txtApellidoPaterno;
    private javax.swing.JTextField txtDNI;
    private javax.swing.JTextField txtDireccion;
    private com.toedter.calendar.JDateChooser txtFechMatricula;
    private com.toedter.calendar.JDateChooser txtFechNac;
    private javax.swing.JComboBox<String> txtGenero;
    private javax.swing.JComboBox<String> txtGrado;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JComboBox<String> txtSeccion;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables

    private void mostrarDatos() {
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
        tblEstudiantil.setModel(modelo);
        String consultasql="select*from registro_alumnos";
        String data[]=new String[12]; // Cambio de longitud a 8

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

                modelo.addRow(data);
            }
        } catch(SQLException e) {
            System.out.println("Error al mostrar Datos "+ e);
        }
    }

    private void limpiarEntradas() {
        txtID.setText("");
        txtNombres.setText("");
        txtApellidoPaterno.setText("");
        txtApellidoMaterno.setText("");
        txtDNI.setText("");
        txtFechNac.setDate(null);
        txtGenero.setSelectedIndex(0);
        txtFechMatricula.setDate(null);
        txtGrado.setSelectedIndex(0);
        txtSeccion.setSelectedIndex(0);
        txtDireccion.setText("");
        txtTelefono.setText("");
    }

}
