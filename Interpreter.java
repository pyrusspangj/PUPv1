import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.*;

public class Interpreter {
    static Scanner UI = new Scanner(System.in);

    static ArrayList<String> lower_program, reg_program, computationalL, computationalR;
    private ArrayList<Integer> loop_index = new ArrayList<>();
    private ArrayList<Integer> loop_times = new ArrayList<>();
    private ArrayList<List<Object>>  loop_conditions = new ArrayList<>();
    private ArrayList<String> looptype = new ArrayList<>();
    private ArrayList<Objects> loop_through;
    private String as_var;
    private int i_for_loop = -1;
    private int on_loop_type = -1;
    //static boolean skip = false;
    //static boolean enter_else = false;
    //static boolean i_edited = false;
    static boolean loopingfor = false;
    static boolean loopingwhile = false;
    static boolean loopingthrough = false;
    static int through_ind = 0;
    static boolean singelse = false;
    static int i_at;

    public Interpreter(ArrayList<String> lower_program, ArrayList<String> reg_program){
        //this.strip_lines(lower_program, reg_program);
        this.convert_lines(lower_program, reg_program);
        //this.lower_program = lower_program;
        //this.reg_program = reg_program;
        this.run();
    }


    private void convert_lines(ArrayList<String> lp, ArrayList<String> rp) {
        ArrayList<String> newLowerProgram = new ArrayList<>();
        ArrayList<String> newRegProgram = new ArrayList<>();
        String prevline = "";
        for (String line : rp) {
            StringBuilder modifiedLine = new StringBuilder();
            boolean lastCharWasSpace = false;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);

                if ((c == '+' || c == '-' || c == '*' || c == '/') && Commands.has_numbers(prevline)){
                    if (!lastCharWasSpace) {
                        modifiedLine.append(' ');
                    }
                    modifiedLine.append(c);
                    if (i < line.length() - 1 && line.charAt(i + 1) != ' ') {
                        modifiedLine.append(' ');
                    }
                    lastCharWasSpace = false;
                } else {
                    modifiedLine.append(c);
                    if (c == ' ') {
                        lastCharWasSpace = true;
                    } else {
                        lastCharWasSpace = false;
                    }
                }
            }

            newLowerProgram.add(modifiedLine.toString().toLowerCase());
            newRegProgram.add(modifiedLine.toString());
            prevline = line;
        }

        //computationalL = new ArrayList<>(newLowerProgram);
        //computationalR = new ArrayList<>(newRegProgram);
        lower_program = new ArrayList<>(newLowerProgram);
        reg_program = new ArrayList<>(newRegProgram);
    }

    public void run(){
        ArrayList<String> lp = new ArrayList<>(lower_program);
        ArrayList<String> rp = new ArrayList<>(reg_program);
        for(int i = 0; i<lp.size(); i++){
            i_at = i;
            String l = lp.get(i).strip();
            String r = rp.get(i).strip();
            //System.out.println("Line at start: "+r);
            if(l.equals("#")){
                continue;
            }
            if(l.split(" ").length > 3 && l.split(" ")[3].equalsIgnoreCase("input()")){
                System.out.println("User Input:");
                String replace = UI.nextLine();
                rp.set(i, "set " + r.split(" ")[1] + " to " + replace);
                lp.set(i, "set " + l.split(" ")[1] + " to " + replace.toLowerCase());
                r = rp.get(i);
                l = lp.get(i);
            }
            if(l.matches(".* to length of .*")){
                String replace = length(r);
                StringBuilder rl = new StringBuilder(r);
                String val = r.substring(l.indexOf("of")+3).strip();
                //System.out.println(r);
                //System.out.printf("r index of to %d val index of space %d", r.indexOf("to"), val.indexOf(" "));
                int ind = !val.contains(" ") ? r.length() : val.indexOf("~") + l.indexOf("of")+4;
                //System.out.println(val);
                if(val.endsWith("~")){
                    replace = String.valueOf(val.substring(0, val.length()-1).length());
                }
                rl.replace(r.indexOf("length"), ind, replace);
                r = rl.toString();
                l = r.toLowerCase();
                rp.set(i, r);
                lp.set(i, l);
                //System.out.println(r);
            }
            //if(l.startsWith("<>") && singelse){
                //r = r.substring(2).strip();
                //l = r.toLowerCase();
                //continue;
            //}
            String type = this.statement_type(l, r);
            if(l.startsWith("<>") && !type.equals("false singelse")){
                r = r.substring(2).strip();
                l = r.toLowerCase();
            } else if(type != null && type.equals("false singelse")){
                continue;
            }
            //System.out.println(loopingwhile);
            //System.out.println(lower_program);
            //System.out.println(computationalL);
            //System.out.println(r);
            //System.out.println("Type: "+type);
            //System.out.println(skip);
            if (type == null || r.equals("")){
                continue;
            }
            /*
            if(skip){
                //System.out.println("h");
                skip = !Commands.process_if(r);
                //System.out.println(i);
                i = Commands.skip_if(lp, rp)-1;
                i_edited = true;
                //System.out.println(i);
                skip = false;
                continue;
            } else {
                if(lp.get(i).length() >= 4 && lp.get(i).substring(0,4).equals("else") && !i_edited){
                    Commands.on_else++;
                    i = Commands.skip_else(lp, rp)-1;
                    continue;
                }
            }
            */

            if (loopingfor && on_loop_type > -1 && this.looptype.get(on_loop_type).equals("loopfor")) {
                //System.out.println(type);
                if(type.equals("end loop") || type.equals("continue")){
                    this.loop_times.set(i_for_loop, this.loop_times.get(i_for_loop)-1);
                    if(this.loop_times.get(i_for_loop) == 0){
                        this.loop_times.remove(i_for_loop);
                        this.looptype.remove(i_for_loop);
                        i_for_loop--;
                        this.looptype.remove(on_loop_type);
                        on_loop_type--;
                        if(i_for_loop == -1){
                            loopingfor = false;
                        }
                    } else {
                        i = this.loop_index.get(i_for_loop);
                    }

                }
            } if (loopingwhile && on_loop_type > -1 && this.looptype.get(on_loop_type).equals("loopwhile")){
                if(type.equals("end loop") || type.equals("continue")){
                    ArrayList<Object> proc = new ArrayList<>(this.loop_conditions.get(i_for_loop));
                    proc = new ArrayList<>(Commands.get_regardless_arraylist(proc));
                    proc = new ArrayList<>(Commands.clear(proc));
                    boolean val = Commands.is_true(proc);
                    this.loop_times.set(i_for_loop, val ? 9999 : 0);
                    if(this.loop_times.get(i_for_loop) == 0){
                        this.loop_times.remove(i_for_loop);
                        i_for_loop--;
                        this.looptype.remove(on_loop_type);
                        on_loop_type--;
                        if(i_for_loop == -1){
                            loopingwhile = false;
                        }
                    } else {
                        i = this.loop_index.get(i_for_loop);
                    }

                }
            } if(loopingthrough && on_loop_type > -1 && this.looptype.get(on_loop_type).equals("loopthrough")){
                //System.out.println(l);
                //System.out.println("start of loop throughind: " +through_ind);
                Variables.objects.replace(this.as_var, this.loop_through.get(through_ind));
                if(type.equals("end loop") || type.equals("continue")){
                    through_ind++;
                    this.loop_times.set(i_for_loop, this.loop_times.get(i_for_loop)-1);
                    if(this.loop_times.get(i_for_loop) == 0){
                        this.loop_times.remove(i_for_loop);
                        i_for_loop--;
                        this.looptype.remove(on_loop_type);
                        on_loop_type--;
                        if(i_for_loop == -1){
                            loopingthrough = false;
                        }
                    } else {
                        i = this.loop_index.get(i_for_loop);
                    }

                }
                //System.out.println("end of loop throughind: "+through_ind);
            }
            //System.out.println(loopingthrough);
            if(type == null){
                //this.throw_error(i, rp.get(i));
                continue;
            } else if(type.equals("print")){
                Commands.write(r);
            } else if(type.equals("variable setting")){
                lp = new ArrayList<>(lower_program);
                rp = new ArrayList<>(reg_program);
                //l = lp.get(i);
                //r = rp.get(i);
                if(l.contains("to value at")) {
                    String arl = r.substring(r.lastIndexOf(' ')).strip();
                    String[] data = r.split(" ");
                    //System.out.println(arl);
                    ArrayList<Object> v = new ArrayList<>((ArrayList) Variables.objects.get(arl));
                    if(Commands.is_variable(data[5])){
                        data[5] = String.valueOf((int)Commands.get_regardless_value(data[5]));
                    }
                    int ind = Integer.parseInt(data[5]);
                    String val = String.valueOf(v.get(ind)).replace("(", "");
                    val = val.replace(")", "");
                    r = r.substring(0, r.indexOf("value")) + val;
                    l = r.toLowerCase();
                    reg_program.set(i, r);
                    lower_program.set(i, l);
                }
                //System.out.println(this.value_type(l, r));
                switch(this.value_type(l, r)){
                    case "String" : new Variables(r).add_string_variable();
                        break;
                    case "Float" : new Variables(r).add_numerical_value();
                        break;
                    case "Random Float": new Variables(r).add_random_number();
                        break;
                    case "Boolean" : new Variables(r).add_boolean_variable();
                        break;
                    case "Object" : new Variables(r).add_object_variable();
                        break;
                    case "Array" : new Variables(r).add_array();
                        break;
                    default : new Variables(r).add_object_instance_variable();  //Assess object's instance var
                }
            } /*else if(type.equals("if statement")){
                //System.out.println("makes");
                skip = !Commands.process_if(r);
                if(skip){
                    enter_else = true;
                } else{
                    enter_else = false;
                }
                */
            else if(type.equals("if statement")){
                i = Commands.process_if(l, reg_program, i);
                lp = new ArrayList<>(lower_program);
                rp = new ArrayList<>(reg_program);
            } else if(type.equals("else")){
                i = Commands.process_else(l, reg_program, i);
            }
            else if(type.equals("singif")){
                int incr = Commands.singif(r);
                if(incr == -1){
                    rp.set(i, r.substring(0, r.indexOf(">")));
                    lp.set(i, r.substring(0, r.indexOf(">")).toLowerCase());
                } else {
                    singelse = true;
                }
                i += incr;
            } else if(type.equals("change var")){
                Commands.change_variable_value(l, r);
            } else if(type.equals("loopfor")){
                this.loop_index.add(i);
                this.loop_times.add(Integer.parseInt(l.split(" ")[1]));
                this.i_for_loop++;
                loopingfor = true;
                this.looptype.add("loopfor");
                this.on_loop_type++;
            } else if(type.equals("loopwhile")){
                this.loop_conditions.add(Arrays.asList(r.substring(l.indexOf("while")+5).strip().split(" ")));
                this.loop_index.add(i);
                String[] data = l.split(" ");
                ArrayList<Object> proc = new ArrayList<>(Arrays.asList(data));
                proc.remove(0);
                proc.remove(0);
                proc = new ArrayList<>(Commands.get_regardless_arraylist(proc));
                proc = new ArrayList<>(Commands.clear(proc));
                boolean val = Commands.is_true(proc);
                this.loop_times.add(val ? 9999 : 0);
                this.i_for_loop++;
                loopingwhile = val;
                this.looptype.add("loopwhile");
                this.on_loop_type++;
            } else if(type.equals("loopthrough")){
                String[] lt_data = r.split(" ");
                String arl = lt_data[2];
                this.loop_through = new ArrayList<>((ArrayList)Variables.objects.get(arl));
                this.as_var = lt_data[lt_data.length-1];
                Variables.objects.put(as_var, this.loop_through.get(0));
                this.loop_index.add(i);
                this.loop_times.add(this.loop_through.size());
                //System.out.println(this.loop_times);
                this.i_for_loop++;
                through_ind = 0;
                loopingthrough = true;
                this.looptype.add("loopthrough");
                this.on_loop_type++;
            }

            //System.out.println("Line at end: "+r);

        }
    }

    private void throw_error(int index, String line){
        System.out.printf("Error on line %d!\n", index+1);
        System.out.println(line);
    }

    private String statement_type(String line, String r_line){
        String[] data = r_line.split(" ");
        String[] l_data = line.split(" ");
        if(line.equalsIgnoreCase("")){
            return null;
        }
        //System.out.println(line);
        if(data[0].startsWith("<>")){
            if(singelse){
                r_line = r_line.substring(2).strip();
                //System.out.println(r_line);
                //r_line = r_line.substring(0, r_line.indexOf(' '));
                line = r_line.toLowerCase().strip();
                data = r_line.split(" ");
                l_data = line.split(" ");
                //lower_program.set(i_at, line);
                //reg_program.set(i_at, r_line);
                singelse = false;
                //System.out.println(data[0]);
                //System.out.println(line);
            } else {
                singelse = false;
                return "false singelse";
            }
        }
        if(Arrays.asList(data).contains(">")){
            return "singif";
        } else if(data[0].equals("write")){
            return "print";
        } else if(data[0].equals("set")){
            return "variable setting";
        } else if(line.equals("show variables")){
            System.out.println("Strings: "+Variables.strings);
            System.out.println("Floats: "+Variables.numericals);
            System.out.println("Booleans: "+Variables.booleans);
            System.out.println("Objects: "+Variables.objects);
        } else if(data[0].substring(0,2).equals("if")){
            return "if statement";
        } else if(data[0].length() > 3 && data[0].substring(0,4).equals("else")){
            return "else";
        } else if(data[0].equalsIgnoreCase("loop") && data[1].equalsIgnoreCase("while")){
            return "loopwhile";
        } else if(data[0].equalsIgnoreCase("loop") && data[1].equalsIgnoreCase("through")){
            return "loopthrough";
        }
        else if(data[0].length() > 3 && data[0].equals("loop")){
            return "loopfor";
        } else if(data[0].equals("end") && data[1].equals("loop")){
            return "end loop";
        } else if(data[0].startsWith("end")){
            return "ending statement. overlook.";
        } else if(data[0].equals("change")){
            return "change var";
        } else if(line.equalsIgnoreCase("continue")){
            return "continue";
        }
        return null;
    }

    public static String value_type(String line, String r_line) {
        //System.out.println(line);
        String[] data = line.split(" ");
        if(Commands.contains_variable(r_line)){
            ArrayList<Object> vals = Variables.replace_variables_with_values(data);
            //System.out.println(vals);
            //System.out.println(vals);
            for(int i=0; i<vals.size(); i++){
                vals.set(i, String.valueOf(vals.get(i)));
            }
            data = vals.toArray(new String[data.length]);
            //new PUP_Objects().print_data(data);
        }
        if(line.contains("to value at")){
            String arl = data[data.length-1];
            ArrayList<Object> v = new ArrayList<>((ArrayList)Variables.objects.get(arl));
            int ind = Integer.parseInt(data[5]);
            r_line = r_line.substring(0, r_line.indexOf("value")) + String.valueOf(v.get(ind));
            line = r_line.toLowerCase();
            data = line.split(" ");

        }
        //System.out.println(line);
        if(r_line.contains(".") && Commands.object_present(r_line)){
            return "Object " + value_type(line.split(" ")[3], r_line.split(" ")[3]);
        } else if(data[1].contains(".")){
            new PUP_Exceptions().Imaginary_Object_Exception(data[1].substring(0, data[1].indexOf(".")));
        }
        if(line.contains("a list") || (data[data.length-1].charAt(0)=='[' &&
                data[data.length-1].charAt(data[data.length-1].length()-1)==']')){
            return "Array";
        } else if(Commands.has_numbers(r_line)){
            //System.out.println("Float");
            if(line.contains("random number")){
                return "Random Float";
            }
            return "Float";
        } else if(line.contains("true") || line.contains("false")){
            return "Boolean";
        } else if(line.contains("new object")){
            return "Object";
        } else{
            return "String";
        }
    }

    public String length(String line){
        String[] data = line.split(" ");
        //set len to length of _n_
        int index = 5;
        for(int i=0; i<data.length-1; i++){
            if(data[i].equalsIgnoreCase("length") && data[i+1].equalsIgnoreCase("of")){
                index = i+2;
                break;
            }
        }
        int len = 0;
        if(Commands.is_variable(data[index])){
            Object o = Commands.get_var_value(data[index]);
            //System.out.println(o);
            len = o.getClass().getSimpleName().equals("String") ?
                    String.valueOf(o).length() : new ArrayList<Object>((ArrayList)o).size();
        } else {
            return String.valueOf(line.substring(line.indexOf("of")+2).strip().length());
        }
        return String.valueOf(len);
    }

}