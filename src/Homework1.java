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

    private void createTables(){
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


    private void insertStudentData(){
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
            }//批量处理，提高效率
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

    private void insertDormitoryData(){
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

    private void insertAccommodationData(){
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
    //读分配信息的excel文件内容
    private void readAllocationFile() {
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
    // 获取excel文件一行的数据
    private String[] getAllocationDataFromRow(HSSFRow row){
        String [] values = new String[7];
        for(int index = 0; index < 7; index++){
            values[index] = getCellValue(row.getCell(index));
        }
        return values;

    }
    //获取excel文件单元格中的数据
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

    //查询"王小星"同学所在宿舍楼的所有院系。
    private void getDepartWithWang(){
        String sql = "select distinct s.department from student s, accommodation a where s.sid = a.sid " +
                "and a.dname = (select a1.dname from student s1, accommodation a1 where s1.sid = a1.sid" +
                " and s1.sname = '王小星')";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                System.out.println(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
    //将陶园1舍的住宿费提高到1200元
    private void updateFare(){
        String sql = "update dormitory set fare = 1200 where dname = '陶园1舍'";
        try {
            Statement statement = conn.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }


    }
    //交换软件学院男女生宿舍
    private void exchange(){
        String manDorSql = "SELECT DISTINCT a.dname FROM student s, dormitory d,accommodation a WHERE s.sid = a.sid AND d.dname = a.dname " +
                "AND s.department = '软件学院' AND s.gender = '男'";
        String womenDorSql = "SELECT DISTINCT a.dname FROM student s, dormitory d,accommodation a WHERE s.sid = a.sid AND d.dname = a.dname\n" +
                "AND s.department = '软件学院' AND s.gender = '女'";
        String updateManDorSql = "UPDATE accommodation set dname = ? " +
                "WHERE sid IN (SELECT s.sid FROM student s WHERE s.department = '软件学院' AND s.gender = '男')";
        String updateWomenSql = "UPDATE accommodation set dname = ? " +
                "WHERE sid IN (SELECT s.sid FROM student s WHERE s.department = '软件学院' AND s.gender = '女')";
        try {
            Statement statement = conn.createStatement();
            ResultSet manDor = statement.executeQuery(manDorSql);
            manDor.next();
            String manDorString = manDor.getString(1);
            System.out.println(manDorString);
            ResultSet womenDor = statement.executeQuery(womenDorSql);
            womenDor.next();
            String womenDorString = womenDor.getString(1);
            System.out.println(womenDorString);
            PreparedStatement preparedStatement = conn.prepareStatement(updateManDorSql);
            preparedStatement.setString(1,womenDorString);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            conn.commit();
            preparedStatement = conn.prepareStatement(updateWomenSql);
            preparedStatement.setString(1,manDorString);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
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



    public static void main(String[] args){
        Homework1 homework1 = new Homework1();
        long start = 0;
        long end = 0;
        System.out.println("开始为表student插入数据...");
        start = System.currentTimeMillis();
        homework1.insertStudentData();
        end = System.currentTimeMillis();
        System.out.println("表student数据插入完成...用时"+(end-start)+"ms");
        System.out.println("开始为表dormitory插入数据...");
        start = System.currentTimeMillis();
        homework1.insertDormitoryData();
        end = System.currentTimeMillis();
        System.out.println("表dormitory数据插入完成...用时"+(end-start)+"ms");
        System.out.println("开始为表accomodation插入数据...");
        start = System.currentTimeMillis();
        homework1.insertAccommodationData();
        end = System.currentTimeMillis();
        System.out.println("表accomodation数据插入完成...用时"+(end-start)+"ms");
        System.out.println("开始为查询王小星所在楼的所有院系...");
        start = System.currentTimeMillis();
        homework1.getDepartWithWang();
        end = System.currentTimeMillis();
        System.out.println("查询完成"+(end-start)+"ms");
        System.out.println("开始更新宿舍费用...");
        start = System.currentTimeMillis();
        homework1.updateFare();
        end = System.currentTimeMillis();
        System.out.println("更新完成"+(end-start)+"ms");
        System.out.println("开始交换宿舍...");
        start = System.currentTimeMillis();
        homework1.exchange();
        end = System.currentTimeMillis();
        System.out.println("交换完成"+(end-start)+"ms");
        try {
            homework1.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
