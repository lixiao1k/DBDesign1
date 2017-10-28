import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by shelton on 2017/10/28.
 */
public class Homework1Test {
    Homework1 homework1;
    @Before
    public void init(){
        homework1 = new Homework1();
    }
    @Test
    public void testxls(){
        ArrayList<String[]> datas = homework1.readAllocationFile();
        for(int rownum = 0; rownum<datas.size();rownum++){
            String [] dataOfRow = datas.get(rownum);
            for(int i = 0;i<7;i++){
                System.out.print(dataOfRow[i]+" ");
            }
            System.out.println();
        }
    }

    @Test
    public void testInsert(){
        homework1.insertStudentData();
    }

    @Test
    public void testDormitory(){
        homework1.insertDormitoryData();
    }

    @Test
    public void testAccommodation(){
        homework1.insertAccommodationData();
    }

}