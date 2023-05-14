/*
   Jiamin Shi CISC3160
   Prof. Neng-Fa Zhou
*/
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Interpreter{
   static ArrayList<String> list = new ArrayList<String>();
   static Hashtable<String, String> VariableTable = new Hashtable<String, String>();
   static ArrayList<String> vars = new ArrayList<String>();
   static Pattern var = Pattern.compile("([a-zA-Z$]|_[a-zA-Z0-9_$])([a-zA-Z0-9_$]*)?");
   static Pattern num = Pattern.compile("(-)?(0|([1-9][0-9]*?))");
   static Pattern openClose = Pattern.compile("(\\(|\\))*?");
   static Pattern open = Pattern.compile("(\\()*?");
   static Pattern close = Pattern.compile("(\\))*?");
   static Pattern exp = Pattern.compile(open + "(-)*?" + open + "(" + var + "|" + num + ")" 
   + close + "((\\+|-|\\*|/)(-)*?" + open + "("  + var + "|" + num + ")" + close + ")*?" + close);

   static Pattern toke = Pattern.compile("\\+|-|\\*|/|\\(|\\)|=");

   public static String run(String c){
      
      if(createList(c)){
         for(int i = 0; i < list.size(); i ++){
            if(Pattern.matches(var + "=" + num, list.get(i))){
               updateTable(list.get(i)); 
            }
            else if(isValidStatement(list.get(i))){   
               vars.add(getVarName(list.get(i)));
               calculate(list.get(i));
            }
            else{
               vars.clear();
               list.clear();
               VariableTable.clear();
               return "error\n";
            }
  
         }
      }
      else
         return "error\n";
      
      if(!VariableTable.isEmpty()){
         System.out.println();
         for(int i = 0; i < vars.size(); i ++){
            System.out.println(vars.get(i) + " = " + VariableTable.get(vars.get(i)));
         }
       
         vars.clear();
         list.clear();
         VariableTable.clear();
         return "";
      }
      
     vars.clear();
     list.clear();
     VariableTable.clear();
     return "error\n";

   }

   public static String getVarName(String s){
      String na = "";
      for(int i = 0; i < s.length(); i ++)
      {
         if(s.charAt(i) == '='){
            return na;
         }
         else{
            na += s.charAt(i);
         }
            
      
      }
      return "";
   }

   public static void calculate(String s){
      ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
      boolean equalFlag = false; 
      String copy = "";
      int minus = 0;
      
      for(int i = 0; i < s.length(); i ++){
         if(s.charAt(i) == '='){
            equalFlag = true;
         }
         else if(equalFlag && !Pattern.matches("" + toke, "" + s.charAt(i))){
            copy = VariableTable.get("" + s.charAt(i));
            s = s.replaceFirst("" + s.charAt(i), copy);
            
      
         }
         if(s.charAt(i) == '-' && s.charAt(i+1) == '-'){
            minus ++;
         }
         else if(s.charAt(i) == '-' && s.charAt(i+1) != '-') {
            if(minus > 0){
               s = replace(s, minus, i);
               minus = 0;
            }
         }
         
      }
      try {
         Object result = engine.eval(s);
         if(Pattern.matches("" + num, String.valueOf(result))){
            VariableTable.put(vars.get(vars.size()-1), String.valueOf(result));
         }  
      }
      catch (ScriptException e) {
         e.printStackTrace();
      }      
   }

   public static String replace (String b, int m, int pos)
   {
      char[] ep = b.toCharArray(); 
      boolean minusFlag = false;
      if(m%2 == 1){
         for(int i = pos; i > pos - m - 1; i--){
            ep[i] = ' ';
         }
      }
      else if(m%2 == 0){
         for(int i = pos - m; i < pos + 1; i++){
            if(!minusFlag){
               b = b.replaceFirst("---", "-1*");
               i +=2;
               minusFlag = true;
               ep = b.toCharArray(); 
            }
            else{
               ep[i] = ' ';
            }
         }
      }
      b = String.valueOf(ep);
      return b;
   }

   public static void updateTable(String statement){
      String Vname = "";
      String n = "";
      boolean equalFlag = false;
     
      for(int i = 0; i < statement.length(); i ++){
        
         if(statement.charAt(i) == '=' ){
            equalFlag = true;
         }
         if(!equalFlag && statement.charAt(i)!= '='){
            Vname += statement.charAt(i);
         }
         else if(equalFlag && statement.charAt(i)!= '='){
            n += statement.charAt(i);
         }               
      }         
      vars.add(Vname);         
      VariableTable.put(Vname, n);
   }
   
   public static boolean isValidStatement(String s){
      return Pattern.matches(var + "=" + exp, s);
   }
   
   
   public static boolean createList(String c){
      String s= "";
      if(c.charAt(c.length()-1) != ';'){
         return false;
      }

      for(int i = 0; i < c.length(); i++){
         if(c.charAt(i) == ';'){
            list.add(s);
            s = "";
         }
         else if(c.charAt(i) != ' '){
            s += c.charAt(i);
         }
      }
      if(list.size() == 0){
         return(false);
      }
      else{
         return(true);
      }
   }
   
   public static void main(String[]agrs){
      
      String code = "x = 001;";
      System.out.println("Input 1: " + code);
      System.out.print("Output 1: ");
      System.out.println(" " + run(code));
      
      code = "x_2 = 0;";
      System.out.println("Input 2: " + code);
      System.out.print("Output 2: ");
      System.out.println(" " + run(code));
      
      code = "x = 0 y = x; z = ---(x + y);";
      System.out.println("Input 3: " + code);
      System.out.print("Output 3: ");
      System.out.println(" " + run(code));
      
      code = "x = 1; y = 2; z = ---(x+y)*(x+-y);";
      System.out.println("Input 4: " + code);
      System.out.print("Output 4: ");
      System.out.println(" " + run(code));
   }
}