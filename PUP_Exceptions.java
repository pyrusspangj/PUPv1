public class PUP_Exceptions extends RuntimeException {

    public PUP_Exceptions(){}

    public PUP_Exceptions(float f){
        super();
    }

    public void Imaginary_Object_Exception(String object_name) throws RuntimeException {
        System.out.printf("Object '%s' you are trying to reach does not exist or is improperly used.\n", object_name);
        throw new PUP_Exceptions(0f);
    }

    public void Missing_Statement_Exception(String line) throws RuntimeException {
        System.out.printf("The line '%s' cannot be compiled due to an improper implementation/use of the statement.\n", line);
        if(line.contains("else")){
            System.out.println(this.else_information());
        }
        throw new PUP_Exceptions(0f);
    }

    private String else_information(){
        String info_block = "It seems that your error has been thrown due to an improper use\n" +
                " of an 'else' block. This typically happens when an 'else' block is written without\n" +
                " the addition of an 'if' block before it.\n" +
                " If this is not your case, then another reason for this error may be the use of\n" +
                " multiple 'else' blocks. Remember: an 'if' block can only have one 'else'\n" +
                " block succeeding it.";
        return info_block;
    }
}
