import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by shelton on 2017/10/28.
 */
public class Homework1 {

    private Connection conn = null;

    public Homework1(){
        try {
            conn = this.getConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTables(){
        String student_dropifexists = "drop table if exists student;";
        String student_createSql = "create table student" +
                "(" +
                "sid char(9) not null," +
                "sname char(30) not null," +
                "gender char(2) not null," +
                "department char(50) not null," +
                "primary key(sid)" +
                ")default charset = utf8;";
        String dormitory_dropifexists = "drop table if exists dormitory";
        String dormitory_createSql = "create table dormitory" +
                "(" +
                "doid int(11) auto_increment," +
                "dname char(30) not null," +
                "campus char(30) not null," +
                "telephone char(11) not null," +
                "fare int(11) not null," +
                "primary key(doid)" +
                ")default charset = utf8";
        String accommodation_dropifexists = "drop table if exists accommodation";
        String accommodation_createSql = "create table accommodation" +
                "(" +
                "  sid char(9) not null," +
                "  doid int(11) not null," +
                "  primary key(sid)" +
                ")default charset = utf8;";

        try {
            Statement statement = conn.createStatement();
            statement.addBatch(student_dropifexists);
            statement.addBatch(student_createSql);
            statement.addBatch(dormitory_dropifexists);
            statement.addBatch(dormitory_createSql);
            statement.addBatch(accommodation_dropifexists);
            statement.addBatch(accommodation_createSql);
            statement.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

    }

    public void readAllocationFile() throws IOException {
        String path = "resource/分配方案.xls";
        File file = new File(path);
        InputStream is = new FileInputStream(file);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        for(int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++){
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if(hssfSheet == null){
                continue;
            }
            for(int numRow = 0; numRow < hssfSheet.getLastRowNum(); numRow++){
                HSSFRow hssfRow = hssfSheet.getRow(numRow);
                if(hssfRow != null){
                    String [] values = getAllocationDataFromRow(hssfRow);
                    for(int i = 0; i<7;i++){
                        System.out.print(values[i]+" ");
                    }
                    System.out.println();
                }
            }
        }
    }

    private String[] getAllocationDataFromRow(HSSFRow row){
        String [] values = new String[7];
        for(int index = 0; index < 7; index++){
            values[index] = getCellValue(row.getCell(index));
        }
        return values;

    }

    private String getCellValue(HSSFCell cell){
        if(cell.getCellType() == cell.CELL_TYPE_BOOLEAN){
            return String.valueOf(cell.getBooleanCellValue());
        }else if(cell.getCellType() == cell.CELL_TYPE_NUMERIC){
            return String.valueOf(cell.getNumericCellValue());
        }else if(cell.getCellType() == cell.CELL_TYPE_BLANK){
            return "";
        }else{
            return String.valueOf(cell.getStringCellValue());
        }
    }


    private Connection getConn() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        System.out.println("初始化成功!");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/homework4" +
                "?user=root&password=lixiaodong1996" +
                "&useUnicode=true&useSSL=false&characterEncoding=UTF8");
        connection.setAutoCommit(false);
        return connection;
    }

}
