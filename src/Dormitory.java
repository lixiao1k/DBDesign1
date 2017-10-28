/**
 * Created by shelton on 2017/10/28.
 */
public class Dormitory {
    private String name = "";
    private String campus = "";
    private int fare = 0;
    private String telephone = "";

    public Dormitory(String name, String campus, int fare, String telephone){
        this.name = name;
        this.campus = campus;
        this.fare = fare;
        this.telephone = telephone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
