import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variables {

    private String line;
    static HashMap<String, Float> numericals = new HashMap<>();
    static HashMap<String, String> strings = new HashMap<>();
    static HashMap<String, Object> objects = new HashMap<>();
    static HashMap<String, Boolean> booleans = new HashMap<>();

    public Variables(String regular_line){
        this.line = regular_line;
    }

    public void add_random_number(){
        /*boolean for_object = false;
        if(line.substring(0,3).equals("~o~")){
            line = line.substring(3);
            for_object = true;
        }
         */
        if(line.contains("-")){
            String regex = "(-)\\s";
            // Replace each match with just the '-' character
            String replacedLine = line.replaceAll(regex, "$1");
            line = replacedLine;
        }
        String[] data = line.split(" ");
        String name = data[1];
        //set name to a random number between 1 and 10
        // 0   1   2  3   4      5       6    7  8   9
        data = replace_write_variables_with_values(data);
        //new PUP_Objects().print_data(data);
        float min = -1, max = 0;
        if(data[3].equalsIgnoreCase("a")){
            min = Float.parseFloat(data[7]);
            max = Float.parseFloat(data[9]);
        } else {
            min = Float.parseFloat(data[6]);
            max = Float.parseFloat(data[8]);
        }
        float rand = (float)Math.random()*(max-min)+min;
        rand = (float)Math.floor(rand);
        //if(for_object){
        //}
        numericals.put(name, rand);
    }

    public void add_numerical_value(){
        String[] data = line.split(" ");
        if(Commands.contains_variable(line)){
            ArrayList<Object> vals = Variables.replace_variables_with_values(data);
            //System.out.println(vals);
            //System.out.println(vals);
            for(int i=0; i<vals.size(); i++){
                vals.set(i, String.valueOf(vals.get(i)));
            }
            data = vals.toArray(new String[data.length]);
            //new PUP_Objects().print_data(data);
        }
        line = "";
        for(String s : data){
            line = line.concat(s) + " ";
        }
        line = line.strip();
        String name = data[1];
        Float add = 0.0f;
        if(data.length == 4 || data.length == 5){
            if(data[3].equalsIgnoreCase("-")){
                add = Commands.get_regardless_value(data[4]) * -1;
            } else {
                add = Commands.get_regardless_value(data[3]);
            }
            Variables.numericals.put(data[1], add);
            return;
        } else {
            //add = Commands.process_computation(Interpreter.computationalR.toArray(new String[Interpreter.computationalR.size()]));
            add = Commands.process_computation(data);
            Variables.numericals.put(data[1], add);
        }

    }

    public void add_string_variable(){
        String[] data = line.split(" ");
        if(Commands.contains_variable(line)){
            ArrayList<Object> vals = Variables.replace_variables_with_values(data);
            //System.out.println(vals);
            //System.out.println(vals);
            for(int i=0; i<vals.size(); i++){
                vals.set(i, String.valueOf(vals.get(i)));
            }
            data = vals.toArray(new String[data.length]);
            //new PUP_Objects().print_data(data);
        }
        line = "";
        for(String s : data){
            line = line.concat(s) + " ";
        }
        line = line.strip();
        String name = data[1];
        if(data[3].contains("~s") || data[3].contains("s~")){
            data[3] = data[3].substring(2);
        }
        String input = line.substring(line.indexOf(data[3]));
        strings.put(data[1], input);
    }

    public void add_boolean_variable(){
        String[] data = line.split(" ");
        if(Commands.contains_variable(line)){
            ArrayList<Object> vals = Variables.replace_variables_with_values(data);
            //System.out.println(vals);
            //System.out.println(vals);
            for(int i=0; i<vals.size(); i++){
                vals.set(i, String.valueOf(vals.get(i)));
            }
            data = vals.toArray(new String[data.length]);
            //new PUP_Objects().print_data(data);
        }
        line = "";
        for(String s : data){
            line = line.concat(s) + " ";
        }
        line = line.strip();
        String name = data[1];
        boolean add = data[3].equalsIgnoreCase("true");
        if(data.length == 4){
            Variables.booleans.put(data[1], add);
        }
    }



    public static String[] replace_write_variables_with_values(String[] data){
        for(int i=1; i<data.length; i++){
            if(data[i] == null){
                data[i] = "";
                continue;
            }
            if(Commands.is_variable(data[i])){
                String var_name = data[i].substring(1,data[i].length()-1);
                if(numericals.containsKey(var_name)){
                    data[i] = numericals.get(var_name).toString();
                } else if(strings.containsKey(var_name)){
                    data[i] = strings.get(data[i].substring(1,data[i].length()-1));
                } else if(booleans.containsKey(var_name)){
                    data[i] = booleans.get(data[i].substring(1,data[i].length()-1)).toString();
                } else if(objects.containsKey(var_name)){
                    data[i] = objects.get(data[i].substring(1,data[i].length()-1)).toString();
                }
            }
        }
        return data;
    }

    public static ArrayList<Object> replace_variables_with_values(String[] data){
        ArrayList<Object> array = new ArrayList<>();
        //
        for(int i=0; i<data.length; i++){
            if(data[i] == null){
                data[i] = "";
                continue;
            }
            if(Commands.is_variable(data[i])){
                String var_name = data[i].substring(1,data[i].length()-1);
                if(numericals.containsKey(var_name)){
                    array.add(numericals.get(var_name));
                } else if(strings.containsKey(var_name)){
                    array.add(strings.get(data[i].substring(1,data[i].length()-1)));
                } else if(booleans.containsKey(var_name)){
                    array.add(booleans.get(data[i].substring(1,data[i].length()-1)));
                } else if(objects.containsKey(var_name)){
                    array.add(objects.get(data[i].substring(1,data[i].length()-1)).toString());
                }
            } else if(Commands.has_numbers(data[i])){
                //data[i] = data[i].replace(',', ' ');
                //data[i] = data[i].replace(')', ' ');
                //data[i] = data[i].replace('(', ' ');
                try{
                    array.add(Float.valueOf(data[i]));
                } catch(Exception e){
                    //Integer[] id = fnl_io_nums(data[i]);
                    //int lastnum = id[id.length-1];
                    //if(lastnum == id[0]){
                    //    lastnum = data[i].length();
                    //}
                    //System.out.println(data[i]);
                    //System.out.println(id[0] + " " + (lastnum-1));
                    String num = data[i];
                    num = num.replace(",", "");
                    num = num.replace("(", "");
                    num = num.replace(")", "");
                    array.add(Float.valueOf(num));
                }
            } else {
                array.add(data[i]);
            }
        }
        return array;
    }

    public Float replace_variables_with_float(String[] data){
        return 0f;
    }

    public static Float pref_math(Float num1, String op, Float num2){
        switch(op){
            case "+": return num1+num2;
            case "-": return num1-num2;
            case "*": return num1*num2;
            case "/": return num1/num2;
            default: return 0f;
        }
    }

    public void add_object_variable(){
        objects.put(line.split(" ")[1], new PUP_Objects());
    }

    public void add_object_instance_variable(){
        String object_ref = line.split(" ")[1];
        String object_name = object_ref.substring(0, object_ref.indexOf('.'));
        if(!objects.containsKey(object_name)){
            new PUP_Exceptions().Imaginary_Object_Exception(object_name);
        }
        String send_line = line.substring(line.indexOf("to"));
        switch(Interpreter.value_type(send_line.toLowerCase(), send_line)){
            case "String" : new Variables("~o~" + line).add_string_variable();
                break;
            case "Float" : new Variables("~o~" + line).add_numerical_value();
                break;
            case "Random Float": new Variables("~o~" + line).add_random_number();
                break;
            case "Boolean" : new Variables("~o~" + line).add_boolean_variable();
                break;
            case "Object" : new Variables("~o~" + line).add_object_variable();
                break;
            case "Array" : new Variables("~o~" + line).add_array();
                break;
            default : new Variables("~o~" + line).add_object_instance_variable();
        }
    }

    public void add_array(){
        line = addWhitespaceAfterComma(line);
        String[] data = line.split(" ");
        //System.out.println(line);
        ArrayList<Object> vals = replace_variables_with_values(data);
        //System.out.println("vals: "+vals);
        boolean pastof = false;
        for(int i=0; i<vals.size(); i++){
            data[i] = String.valueOf(vals.get(i));
            if(pastof){
                if(vals.get(i-1).equals("of") && (!data[i].startsWith("[")) || data[i].startsWith("(")){
                    data[i] = data[i].replace("(", "");
                    data[i] = "[" + data[i];
                } else if(i == vals.size()-1 && !data[i].endsWith("]")){
                    data[i] = data[i].replace(")", "");
                    data[i] = data[i] + "]";
                }
                if(data[i].charAt(data[i].length()-1) != ','){
                    data[i] = data[i].concat(",");
                }
            }
            if(data[i].equalsIgnoreCase("of")){
                pastof = true;
            }
        }
        line = "";
        for(String s : data){
            line = line.concat(s) + " ";
        }
        line = line.strip();
        String name = data[1];
        //System.out.println("newly made line: "+line);
        //System.out.println(line);
        try{
            line = line.substring(line.indexOf('(')+1, line.indexOf(')')).strip();
        } catch (IndexOutOfBoundsException e){
            //System.out.println(line);
            line = line.substring(line.indexOf('[')+1, line.indexOf(']')).strip();
        }
        data = line.split(",");
        for(int i=0; i<data.length; i++){
            data[i] = data[i].strip();
        }
        //new PUP_Objects().print_data(data);
        data = replace_write_variables_with_values(data);
        ArrayList<Object> array = new ArrayList<>(replace_variables_with_values(data));
        objects.put(name, array);
        //new PUP_Objects().print_data(data);

    }

    static Integer[] fnl_io_nums(String line){
        ArrayList<Integer> indices = new ArrayList<>();
        for(int i=0; i<line.length(); i++){
            if(Character.isDigit(line.charAt(i))){
                indices.add(i);
            }
        }
        return indices.toArray(new Integer[indices.size()]);
    }

    static String addWhitespaceAfterComma(String line) {
        Pattern pattern = Pattern.compile("(?<!\\s),(?!\\s)");
        Matcher matcher = pattern.matcher(line);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(result, ", ");
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
