import java.util.*;

public class Commands {

    static int on_if = 0;
    static int on_else = 0;

    static HashMap<Integer, Boolean> ifs = new HashMap<>();
    static HashMap<Integer, Boolean> elses = new HashMap<>();

    static void write(String line){
        System.out.print("Print: ");
        //System.out.println(line);
        int set = line.substring(0,2).equals("<>") ? 2 : 1;
        String[] data = line.split(" ");
        data = Variables.replace_write_variables_with_values(data);
        for(int i=set; i<data.length; i++){
            System.out.print(data[i] + " ");
        }
        System.out.println();
    }

    static boolean has_numbers(String line){
        if(line.contains("~s") || line.contains("s~")){
            return false;
        }
        for(int i=0; i<line.length(); i++){
            if(Character.isDigit(line.charAt(i))){
                return true;
            }
        }
        return false;
    }

    static boolean is_variable(String word){
        return word.charAt(0) == '_' && word.charAt(word.length()-1) == '_';
    }

    static float get_regardless_value(String cont){
        if(is_variable(cont)){
            return Variables.numericals.get(cont.substring(1,cont.length()-1));
        } else {
            //System.out.println(cont);
            return Float.parseFloat(cont);
        }
    }

    static Object get_var_value(String cont){
        String var_name = cont.substring(1,cont.length()-1);
        if(Variables.numericals.containsKey(var_name)){
            return Variables.numericals.get(var_name);
        } else if(Variables.strings.containsKey(var_name)){
            return Variables.strings.get(var_name);
        } else if(Variables.booleans.containsKey(var_name)){
            return Variables.booleans.get(var_name);
        } else if(Variables.objects.containsKey(var_name)){
            return Variables.objects.get(var_name);
        }
        return null;
    }

    static ArrayList<Object> get_regardless_arraylist(ArrayList<Object> process){
        for(int i=0; i<process.size(); i++){
            if(is_variable((String)process.get(i))){
                process.set(i, get_var_value((String)process.get(i)));
            } else {
                if(has_numbers((String)process.get(i))){
                    process.set(i, Float.parseFloat((String)process.get(i)));
                }
            }
        }
        return process;
    }

    static Float process_computation(String[] data){
        ArrayList<Object> process = new ArrayList<>();
        for(int i=3; i<data.length; i++){
            if(data[i] == null || data[i].equals(" ")){
                continue;
            } else if(data[i].equals("+") || data[i].equals("-") || data[i].equals("*") || data[i].equals("/")){
                process.add(data[i]);
                continue;
            } else process.add(get_regardless_value(data[i]));
        }

        int i = 0;
        while(process.size() != 1){
            process = new ArrayList<>(clear(process));
            if(process.get(i).equals("*") || process.get(i).equals("/")){
                process.set(i, Variables.pref_math( (Float)process.get(i-1), (String)process.get(i), (Float)process.get(i+1) ));
                process.set(i-1, " ");
                process.set(i+1, " ");
                i=0;
            } else if(NMD(process) && process.get(i).equals("+") || process.get(i).equals("-")){
                process.set(i, Variables.pref_math( (Float)process.get(i-1), (String)process.get(i), (Float)process.get(i+1) ));
                process.set(i-1, " ");
                process.set(i+1, " ");
                i=0;
            } else {
                i++;
                continue;
            }
            process = new ArrayList<>(clear(process));
        }

        return (Float)process.get(0);
    }

    static ArrayList<Object> clear(ArrayList<Object> process){
        Iterator<Object> iterator = process.iterator();
        while (iterator.hasNext()) {
            Object s = iterator.next();
            if (s == null || s.equals(" ")) {
                iterator.remove();
            }
        }
        return process;
    }

    static boolean NMD(ArrayList<Object> process){
        for(Object o : process){
            if(o != null && o.equals("*") || o.equals("/")){
                return false;
            }
        }
        return true;
    }

    static Boolean is_true(ArrayList<Object> proc){
        //System.out.println(proc);
        int i = 0;
        while(proc.size() != 1){
            //System.out.println(proc);
            if(!proc.get(i).getClass().getSimpleName().equalsIgnoreCase("String")){
                i++;
                continue;
            }
            String word = (String)proc.get(i);
            //System.out.println(proc);
            //System.out.println(word);
            if(word.equalsIgnoreCase("equals")){
                //System.out.println(proc.get(i-1).getClass());
                //System.out.println(proc.get(i+1).getClass());
                Object v = proc.get(i-1);
                Object v2 = proc.get(i+1);
                if(v.equals(true) || v.equals(false)){
                    proc.set(i-1, String.valueOf(v));
                }
                if(v2.equals(true) || v2.equals(false)){
                    proc.set(i+1, String.valueOf(v2));
                }
                if(proc.get(i-1).getClass() != proc.get(i+1).getClass()){
                    proc.set(i, false);
                    proc.remove(i+1);
                    proc.remove(i-1);
                    proc = new ArrayList<>(clear(proc));
                    i=0;
                } else {
                    if(proc.get(i-1).equals(proc.get(i+1))){
                        proc.set(i, true);
                    } else {
                        proc.set(i, false);
                    }
                    proc.remove(i+1);
                    proc.remove(i-1);
                    proc = new ArrayList<>(clear(proc));
                    i=0;
                }
            } else if(word.equalsIgnoreCase("greater")){
                if(proc.get(i-1).getClass() != proc.get(i+1).getClass()){
                    proc.set(i, false);
                    proc.remove(i+2);
                    proc.remove(i+1);
                    proc.remove(i-1);
                    proc.remove(i-2);
                    proc = new ArrayList<>(clear(proc));
                    i=0;
                } else {
                    if((Float)proc.get(i-2) > (Float)proc.get(i+2)){
                        proc.set(i, true);
                    } else {
                        proc.set(i, false);
                    }
                    proc.remove(i+2);
                    proc.remove(i+1);
                    proc.remove(i-1);
                    proc.remove(i-2);
                    proc = new ArrayList<>(clear(proc));
                    i=0;
                }
            } else if(word.equalsIgnoreCase("less")){
                if(proc.get(i-1).getClass() != proc.get(i+1).getClass()){
                    proc.set(i, false);
                    proc.remove(i+2);
                    proc.remove(i+1);
                    proc.remove(i-1);
                    proc.remove(i-2);
                    proc = new ArrayList<>(clear(proc));
                    i=0;
                } else {
                    if((Float)proc.get(i-2) < (Float)proc.get(i+2)){
                        proc.set(i, true);
                    } else {
                        proc.set(i, false);
                    }
                    proc.remove(i+2);
                    proc.remove(i+1);
                    proc.remove(i-1);
                    proc.remove(i-2);
                    proc = new ArrayList<>(clear(proc));
                    i=0;
                }
            } else if(word.equalsIgnoreCase("not")){
                if(proc.get(i-1).getClass() != proc.get(i+1).getClass()){
                    proc.set(i, true);
                    proc.remove(i+3);
                    proc.remove(i+2);
                    proc.remove(i+1);
                    proc.remove(i-1);
                    proc.remove(i-2);
                    proc = new ArrayList<>(clear(proc));
                    i=0;
                } else {
                    if(!Objects.equals(proc.get(i - 2), proc.get(i + 3))){
                        proc.set(i, true);
                    } else {
                        proc.set(i, false);
                    }
                    proc.remove(i+3);
                    proc.remove(i+2);
                    proc.remove(i+1);
                    proc.remove(i-1);
                    proc.remove(i-2);
                    proc = new ArrayList<>(clear(proc));
                    i=0;
                }
            } else if(no_more_compares(proc)){
                if(word.equalsIgnoreCase("or")){
                    if(proc.get(i-1).equals(true) || proc.get(i+1).equals(true)){
                        proc.set(i, true);
                    } else {
                        proc.set(i, false);
                    }
                    proc.remove(i+1);
                    proc.remove(i-1);
                    proc = new ArrayList<>(clear(proc));
                    i=0;
                } else if(word.equalsIgnoreCase("and")){
                    if(proc.get(i-1).equals(true) && proc.get(i+1).equals(true)){
                        proc.set(i, true);
                    } else {
                        proc.set(i, false);
                    }
                    proc.remove(i+1);
                    proc.remove(i-1);
                    proc = new ArrayList<>(clear(proc));
                    i=0;
                }
            }
            else {
                i++;
            }
        }
        //System.out.println(proc.get(0));
        //System.out.println(proc);
        return proc.get(0).equals("true") || proc.get(0).equals(true);
    }

    static boolean no_more_compares(ArrayList<Object> proc){
        for(Object o : proc){
            if(o.equals("equals") || o.equals("greater") || o.equals("less") || o.equals("not")){
                return false;
            }
        }
        return true;
    }

    /*static Boolean process_if(String line){
        String[] data = line.split(" ");
        if(data[0].substring(0,2).equalsIgnoreCase("if")){
            on_if++;
        }
        String ifnum = "if"+String.valueOf(on_if);
        if(data[0].equalsIgnoreCase("end") && data[1].equalsIgnoreCase(ifnum)){
            on_if--;
            Interpreter.enter_else = Interpreter.skip;
            return true;
        } else if(Interpreter.skip){
            return false;
        }
        //process condition
        ArrayList<Object> proc = new ArrayList<>(Arrays.asList(data));
        proc.remove(0);
        proc = new ArrayList<>(get_regardless_arraylist(proc));
        proc = new ArrayList<>(clear(proc));
        //System.out.println(proc);
        return is_true(proc);
    }

     */

    /*static int skip_if(ArrayList<String> lp, ArrayList<String> rp){
        String ifnum = "if"+String.valueOf(on_if);
        int inc = 0;
        for(String s : lp){
            if(s.equals("end "+ifnum)){
                return inc;
            }
            inc++;
        }
        return 0;
    }

     */

    /*static Boolean process_else(String line){
        if(!Interpreter.skip){
            return false;
        }
        String[] data = line.split(" ");
        if(data[0].length() > 3 && data[0].substring(0,4).equalsIgnoreCase("else")){
            on_else++;
        }
        String elsenum = "else"+String.valueOf(on_else);
        if(data[0].equalsIgnoreCase("end") && data[1].equalsIgnoreCase(elsenum)){
            on_else--;
            Interpreter.enter_else = false;
            return false;
        } else if(Interpreter.enter_else){
            return true;
        }
        return true;
    }

     */

    // write hello world! > if [condition]
    // <> [else statement]

    public static int singif(String line){
        String process = line.substring(line.indexOf(">")+1).strip();
        String[] singif = process.split(" ");
        ArrayList<Object> sif = new ArrayList<>(Arrays.asList(singif));
        sif = new ArrayList<>(get_regardless_arraylist(sif));
        sif.remove(0);
        //System.out.println(sif);
        boolean assess = is_true(sif);
        //System.out.println(assess);
        return assess ? -1 : 0;
    }

    /*static int skip_else(ArrayList<String> lp, ArrayList<String> rp){
        String elnum = "else"+String.valueOf(on_else);
        int inc = 0;
        for(String s : lp){
            if(s.equals("end "+elnum)){
                return inc;
            }
            inc++;
        }
        return 0;
    }

     */
    static int process_else(String line, ArrayList<String> rp, int index){
        String[] data = line.split(" ");
        int num = 1;
        try{
            num = Integer.parseInt(line.substring(4, line.length()-1));
        }catch(Exception e){
            new PUP_Exceptions().Missing_Statement_Exception(line);
        }
        int moveto = index;
        if(line.endsWith("T")){
            return index;
        } else {
            for(int i=index; i<rp.size(); i++){
                if(rp.get(i).equalsIgnoreCase("end else"+num)){
                    return i;
                }
            }
        }
        return index;
    }

    static int process_if(String line, ArrayList<String> rp, int index){
        String[] data = line.split(" ");
        int num = Integer.parseInt(data[0].substring(2));
        int moveto = index;
        StringBuilder change = new StringBuilder("else"+num+"O");
        ArrayList<Object> proc = new ArrayList<>(Arrays.asList(data));
        proc.remove(0);
        proc = new ArrayList<>(get_regardless_arraylist(proc));
        proc = new ArrayList<>(clear(proc));
        boolean val = is_true(proc);
        //System.out.println(val);
        for(int i=index; i<rp.size(); i++){
            //System.out.println(rp.get(i));
            if(rp.get(i).equalsIgnoreCase("end if"+num) && !val){
                moveto = i;
            }
            if(rp.get(i).substring(0, rp.get(i).length()-1).equalsIgnoreCase("else"+num)
            || rp.get(i).equalsIgnoreCase("else"+num)){
                change = val ? change.replace(change.length()-1, change.length(), "F") : change.replace(change.length()-1, change.length(), "T");
                //System.out.println(change);
                String chang = String.valueOf(change);
                Interpreter.reg_program.set(i, chang);
                Interpreter.lower_program.set(i, chang);
                break;
            }
        }
        return moveto;
    }

    static int process_if(String line, String[] program, int index){
        String[] data = line.split(" ");
        String[] copy_prog = program.clone();
        int num = Integer.parseInt(data[0].substring(2));
        ArrayList<Object> proc = new ArrayList<>(Arrays.asList(data));
        proc.remove(0);
        proc = new ArrayList<>(get_regardless_arraylist(proc));
        proc = new ArrayList<>(clear(proc));
        boolean val = is_true(proc);
        //System.out.println(num);
        //System.out.println(val);
        ifs.put(num, val);
        elses.put(num, !val);
        if(!val){
            int endifnum = -1;
            //check program to find an else[num], if none found, skip program to end if[num]
            for(int i=index+1; i<program.length; i++){
                if(program[i].equalsIgnoreCase("else"+num)){
                    return i;
                } else if(program[i].equalsIgnoreCase("end if"+num)){
                    endifnum = i;
                }
            }
            return endifnum;
        } else {
            boolean clear = false;
            //we could pass the program through as a parameter and nullify from else[num] to end else[num]??
            Clearing:
            for(int i=index+1; i<copy_prog.length; i++){
                if(clear){
                    if(copy_prog[i].equalsIgnoreCase("end else"+num)){
                        copy_prog[i] = "";
                        clear = false;
                        break Clearing;
                    }
                    copy_prog[i] = "";
                }
                if(copy_prog[i].equalsIgnoreCase("else"+num)){
                    clear = true;
                    copy_prog[i] = "";
                }
            }
            List<String> new_prog = Arrays.asList(copy_prog);
            List<String> new_low_prog = new ArrayList<>();
            for(String s : new_prog){
                new_low_prog.add(s.toLowerCase());
            }
            //System.out.println(new_prog);
            Interpreter.lower_program = new ArrayList<>(new_low_prog);
            Interpreter.reg_program = new ArrayList<>(new_prog);
            return index;
        }
    }

    public static void change_variable_value(String line, String r_line){
        String[] data = line.split(" ");
        String name = data[1];
        ArrayList<Object> n = new ArrayList<>();
        for(String s : data){
            n.add(s);
        }
        //change [var] to [value]
        //change value at [index] to [value] in [array]
        List<Object> vals = new ArrayList<>(get_regardless_arraylist(n));
        //System.out.println(vals);
        boolean arr = line.substring(0, line.indexOf("to")).contains("value at");
        if(!arr){
            //System.out.println(line);
            String type = var_type(line);
            //System.out.println(type);
            if(line.contains("to value at")){
                String[] d = r_line.split(" ");
                String arraylist = r_line.split(" ")[r_line.split(" ").length-1];
                List<Object> vs = new ArrayList<>((ArrayList)Variables.objects.get(arraylist));
                String replace;
                String comp = "set ? to "+r_line.substring(line.indexOf("of")+3, line.indexOf("in")).strip();
                if(d.length > 8){
                    replace = String.valueOf(process_computation(comp.split(" "))).strip();
                } else {
                    if(is_variable(d[5])){
                        replace = String.valueOf(get_var_value(d[5])).strip();
                    } else {
                        replace = d[5].strip();
                    }
                }
                int ind = Integer.parseInt(replace.substring(0, replace.indexOf(".")));
                replace = String.valueOf(vs.get(ind));
                r_line = r_line.substring(0, line.indexOf("value")).strip() + " " + replace;
                line = r_line.toLowerCase();
                //System.out.println(r_line);
                type = var_type(line);
                change_variable_value(line, r_line);
                return;
            }
            switch(type){
                case "String": Variables.strings.replace(name, (String)vals.get(3));
                break;
                case "Float": if(vals.size() > 4){
                    if(Commands.contains_variable(line)){
                        ArrayList<Object> val = Variables.replace_variables_with_values(data);
                        //System.out.println(vals);
                        //System.out.println(vals);
                        for(int i=0; i<vals.size(); i++){
                            val.set(i, String.valueOf(vals.get(i)));
                        }
                        data = val.toArray(new String[data.length]);
                        //new PUP_Objects().print_data(data);
                    }
                    line = "";
                    for(String s : data){
                        line = line.concat(s) + " ";
                    }
                    line = line.strip();
                    Variables.numericals.replace(name, process_computation(data));
                } else {
                    Variables.numericals.replace(name, (Float)vals.get(3));
                }
                break;
                case "Object": Variables.objects.replace(name, vals.get(3));
                break;
                case "Boolean": Variables.booleans.replace(name, Boolean.valueOf(String.valueOf(vals.get(3))));
            }
            //System.out.println(vals.get(3));
        } else {
            String v = r_line.substring(r_line.indexOf("to")+2, r_line.indexOf("in")).strip();
            //System.out.println(v + "MEMEMEMEME");
            Object val = is_variable(v) ? get_var_value(v) : get_regardless_obj_val(v);
            name = data[data.length-1];
            List<Object> newray = new ArrayList<>((List)Variables.objects.get(name));
            newray.set((int)((float)vals.get(3)), val);
            Variables.objects.replace(name, newray);
        }
    }

    public static String var_type(String line){
        String name = line.split(" ")[1];
        if(Variables.strings.containsKey(name)){
            return "String";
        } else if(Variables.numericals.containsKey(name)){
            return "Float";
        } else if(Variables.objects.containsKey(name)){
            return "Object";
        } else if(Variables.booleans.containsKey(name)){
            return "Boolean";
        } else return "null";
    }

    public static Object get_regardless_obj_val(String cont){
        if(has_numbers(cont)){
            return Float.valueOf(cont);
        } else {
            return cont;
        }
    }

    public static boolean object_present(String line){
        String name = line.substring(0, line.indexOf("."));
        name = name.substring(name.lastIndexOf(" "));
        return Variables.objects.containsKey(name);
    }

    public static boolean contains_variable(String line){
        String[] data = line.split(" ");
        for(String s : data){
            if(s.charAt(0) == '_' && s.charAt(s.length()-1) == '_'){
                return true;
            }
        }
        return false;
    }

}
