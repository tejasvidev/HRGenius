package com.hrgenius.report.service;

import com.hrgenius.report.dto.ReportDataset;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ReportExportWriter {
    private ReportExportWriter(){}
    public static byte[] csv(ReportDataset data){
        StringBuilder out=new StringBuilder("\uFEFF");appendCsvRow(out,data.columns());
        for(var row:data.rows())appendCsvRow(out,row);
        return out.toString().getBytes(StandardCharsets.UTF_8);
    }
    private static void appendCsvRow(StringBuilder out,java.util.List<?> cells){
        for(int i=0;i<cells.size();i++){if(i>0)out.append(',');Object cell=cells.get(i);String value=cell==null?"":cell.toString();
            if(!(cell instanceof Number)&&!value.isEmpty()&&"=+-@\t\r".indexOf(value.charAt(0))>=0)value="'"+value;
            out.append('"').append(value.replace("\"","\"\"")).append('"');}
        out.append("\r\n");
    }
    public static byte[] xlsx(ReportDataset data){
        try(ByteArrayOutputStream bytes=new ByteArrayOutputStream();ZipOutputStream zip=new ZipOutputStream(bytes,StandardCharsets.UTF_8)){
            entry(zip,"[Content_Types].xml","<?xml version=\"1.0\" encoding=\"UTF-8\"?><Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/></Types>");
            entry(zip,"_rels/.rels","<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>");
            entry(zip,"xl/workbook.xml","<?xml version=\"1.0\" encoding=\"UTF-8\"?><workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets><sheet name=\"Report\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
            entry(zip,"xl/_rels/workbook.xml.rels","<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/></Relationships>");
            StringBuilder sheet=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
            int rowIndex=1;appendSheetRow(sheet,rowIndex++,data.columns());for(var row:data.rows())appendSheetRow(sheet,rowIndex++,row);sheet.append("</sheetData></worksheet>");
            entry(zip,"xl/worksheets/sheet1.xml",sheet.toString());zip.finish();return bytes.toByteArray();
        }catch(IOException ex){throw new IllegalStateException("Could not build spreadsheet export",ex);}
    }
    private static void appendSheetRow(StringBuilder out,int rowIndex,java.util.List<?> cells){
        out.append("<row r=\"").append(rowIndex).append("\">");for(int c=0;c<cells.size();c++){
            Object value=cells.get(c);String ref=column(c+1)+rowIndex;
            if(value instanceof Number n)out.append("<c r=\"").append(ref).append("\"><v>").append(number(n)).append("</v></c>");
            else if(value instanceof Boolean b)out.append("<c r=\"").append(ref).append("\" t=\"b\"><v>").append(b?1:0).append("</v></c>");
            else out.append("<c r=\"").append(ref).append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">").append(xml(value==null?"":value.toString())).append("</t></is></c>");
        }out.append("</row>");
    }
    private static String number(Number n){return n instanceof BigDecimal d?d.toPlainString():n.toString();}
    private static String column(int n){StringBuilder b=new StringBuilder();while(n>0){int r=(n-1)%26;b.insert(0,(char)('A'+r));n=(n-1)/26;}return b.toString();}
    private static String xml(String s){StringBuilder b=new StringBuilder();for(char c:s.toCharArray()){if(c=='&')b.append("&amp;");else if(c=='<')b.append("&lt;");else if(c=='>')b.append("&gt;");else if(c=='\"')b.append("&quot;");else if(c=='\'')b.append("&apos;");else if(c>=0x20||c=='\n'||c=='\r'||c=='\t')b.append(c);}return b.toString();}
    private static void entry(ZipOutputStream zip,String path,String value)throws IOException{zip.putNextEntry(new ZipEntry(path));zip.write(value.getBytes(StandardCharsets.UTF_8));zip.closeEntry();}
}
