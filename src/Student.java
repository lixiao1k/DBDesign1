/**
 * Created by shelton on 2017/10/28.
 */
public class Student {
    private String name = "";
    private String id = "";
    private String gender = "";
    private String department = "";

    public Student(String name, String id, String gender, String department){
        this.name = name;
        this.id = id;
        this.gender = gender;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
