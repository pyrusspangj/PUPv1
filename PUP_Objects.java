import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PUP_Objects {

    public HashMap<String, Float> numericals = new HashMap<>();
    public HashMap<String, String> strings = new HashMap<>();
    public HashMap<String, Object> objects = new HashMap<>();
    public HashMap<String, Boolean> booleans = new HashMap<>();


    public PUP_Objects(){

    }

    public String toString(){
        return "PUP_Object";
    }

    public void print_data(String[] data){
        System.out.println(Arrays.asList(data));
    }

    public void print_data(ArrayList<String> data){
        System.out.println(data);
    }
}
