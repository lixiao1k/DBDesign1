import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by shelton on 2017/10/28.
 */
public class Homework1 {

    private Connection conn = null;
    private ArrayList<String[]> allocationData = null;

    public Homework1(){
        try {
            conn = this.getConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        createTables();
        readAllocationFile();
    }

    public void createTables(){
        String student_dropifexists = "drop table if exists student;";
        String student_createSql = "create table student" +
                "(" +
                "sid char(9) not null," +
                "sname char(30) not null," +
                "gender char(8) not null," +
                "department char(50) not null," +
                "primary key(sid)" +
                ")default charset = utf8;";
        String dormitory_dropifexists = "drop table if exists dormitory";
        String dormitory_createSql = "create table dormitory" +
                "(" +
                "dname char(30) not null," +
                "campus char(30) default '仙林'," +
                "telephone char(11) default null," +
                "fare int(11) default 0," +
                "primary key(dname)" +
                ")default charset = utf8";
        String accommodation_dropifexists = "drop table if exists accommodation";
        String accommodation_createSql = "create table accommodation" +
                "(" +
                "  sid char(9) not null," +
                "  dname char(30) not null," +
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


    public void insertStudentData(){
        ArrayList<String[]> datalist = allocationData;
        Iterator<String[]> iterator = datalist.iterator();
        String insertSql = "insert into student(sid,sname,gender,department) values (?,?,?,?)";
        try {
            PreparedStatement preStatement = conn.prepareStatement(insertSql);
            String[] rowdata1 = iterator.next();
            while(iterator.hasNext()){
                String[] rowdata = iterator.next();
                preStatement.setString(1,rowdata[1]);
                preStatement.setString(2,rowdata[2]);
                preStatement.setString(3,rowdata[3]);
                preStatement.setString(4,rowdata[0]);
                preStatement.addBatch();
            }
            preStatement.executeBatch();
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

    public void insertDormitoryData(){
        String insertSql = "insert into dormitory(dname,telephone) values (?,?)";
        ArrayList<String[]> phonelist = getPhoneData();
        try {
            PreparedStatement preStatement = conn.prepareStatement(insertSql);
            String[] rowdata = null;
            for(int numPhone=0;numPhone<phonelist.size();numPhone++){
                rowdata = phonelist.get(numPhone);
                preStatement.setString(1,rowdata[0]);
                preStatement.setString(2,rowdata[1]);
                preStatement.addBatch();
            }
            preStatement.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        ArrayList<String[]> allocatinDateList = allocationData;
        String updateSql = "update dormitory set campus = ?,fare = ? where dname = ?";
        try {
            PreparedStatement preStatement1 = conn.prepareStatement(updateSql);
            String[] allocationRowDate = null;
            for(int numRow = 1;numRow<allocatinDateList.size();numRow++){
                allocationRowDate = allocatinDateList.get(numRow);
                String dname = allocationRowDate[5];
                String campus = allocationRowDate[4];
                int fare = Double.valueOf(allocationRowDate[6]).intValue();
                preStatement1.setString(1,campus);
                preStatement1.setInt(2,fare);
                preStatement1.setString(3,dname);
                preStatement1.addBatch();
            }
            preStatement1.executeBatch();
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

    public void insertAccommodationData(){
        ArrayList<String[]> accomData = allocationData;
        String insertSql = "insert into accommodation(sid,dname) values (?,?)";
        try {
            PreparedStatement preStatement = conn.prepareStatement(insertSql);
            String[] accomRowData = null;
            for(int numRow = 1;numRow<accomData.size();numRow++){
                accomRowData = accomData.get(numRow);
                String sid = accomRowData[1];
                String dname = accomRowData[5];
                preStatement.setString(1,sid);
                preStatement.setString(2,dname);
                preStatement.addBatch();
            }
            preStatement.executeBatch();
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

    private ArrayList<String[]> getPhoneData() {
        ArrayList<String[]> list = new ArrayList<>();
        File file = new File("resource/电话.txt");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            try {
                line = bufferedReader.readLine();
                while((line = bufferedReader.readLine())!=null){
                    String[] phonedata = line.split(";");
                    list.add(phonedata);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void readAllocationFile() {
        String path = "resource/分配方案.xls";
        File file = new File(path);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        HSSFWorkbook hssfWorkbook = null;
        try {
            hssfWorkbook = new HSSFWorkbook(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String[]> filedata = new ArrayList<>();
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        for(int numRow = 0; numRow < hssfSheet.getLastRowNum()+1; numRow++){
            HSSFRow hssfRow = hssfSheet.getRow(numRow);
            if(hssfRow != null){
                String [] values = getAllocationDataFromRow(hssfRow);
                filedata.add(values);
            }
        }
        allocationData = filedata;
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
