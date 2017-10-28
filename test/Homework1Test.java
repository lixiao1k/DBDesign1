import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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
        try {
            homework1.readAllocationFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}