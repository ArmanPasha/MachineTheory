package machinetheory1;

import java.nio.file.Paths;
import java.util.Vector;

/**
 *
 * @author arman pasha
 */
public class MachineTheory1 {

    Vector<State> states;
    Vector<String> current_states; //in dfa it includes only one state
    Vector<String> alphabet;
    Vector<State> convert_states; //states which we face during converting
    Vector<Vector> convert_states2;

    public static void main(String[] args) {
        MachineTheory1 mt = new MachineTheory1();
        mt.starter();
    }

    public MachineTheory1() {
        states = new Vector<>();
        current_states = new Vector<>();
        alphabet = new Vector<>();
    }

    private void starter() {
        MyFileReader reader = new MyFileReader(this);
        reader.read(Paths.get("C:\\Users\\arman pasha\\Desktop\\Machines.txt"));
    }

public void machineInfo(Vector<State> states, Vector<String> alphabets, String[] input_str, boolean is_NDFA) {
        String initial_state = "";

        this.alphabet = alphabets;
        this.states = states;
        
        current_states.removeAllElements();

        for (State st : this.states) {
            if (st.is_initial) {
                initial_state = st.name;
                current_states.add(st.name);
                break;
            }
        }
        
        System.out.println("****** NEW MACHINE ******");
        if(is_NDFA)
            System.out.println("NDFA Machine:");
        else
            System.out.println("DFA Machine");
        
        for(String s: input_str){
            delta_star(s);
            System.out.print("String "+s+" :");
            accept_status();
            current_states.removeAllElements();
            current_states.add(initial_state);
        }

        if (is_NDFA) {
            current_states.removeAllElements();
            current_states.add(initial_state);
            convert_states = new Vector<>();
            convert_states2 = new Vector<>();
            converter(current_states);
            after_convert(initial_state);
            
            System.out.println("DFA conversion:");
            for (State st : convert_states) {
                System.out.print("State name: ");
                System.out.println(st.name + "  initial:" + st.is_initial + "   final:" + st.is_final);
                System.out.print("State inputs: ");
                System.out.println(st.inputs);
                System.out.print("State outputs: ");
                System.out.println(st.next_states);
                System.out.println("\n-----------------------------");
            }
        }

        System.out.println("***********************");
    }

    private void delta_star(String input) {
        if (input.length() == 0) {
            return;
        }

        delta(input.charAt(0));

        if (input.length() > 1) {
            delta_star(input.substring(1));
        }
    }

    private void delta(char c) {
        String cs = "" + c; //cs is string form of c
        if(cs.equals(" ")){//*** This is when we have no input
            return;
        }
        int loop_time = current_states.size();
        for (int i = 0; i < loop_time; i++) {
            for (State st : states) {
                if (st.name.equals(current_states.elementAt(0))) {
                    for (int j = 0; j < st.inputs.size(); j++) {
                        if (st.inputs.elementAt(j).equals(cs)) {
                            current_states.add(st.next_states.elementAt(j));
                        }
                    }

                    current_states.remove(0);
                    if (current_states.isEmpty()) //machine could not get to the end of string
                    {
                        return;
                    }
                    break;
                }
            }
        }
    }

    private void accept_status() {
        for (String s : current_states) {
            for (State st : states) {
                if (st.name.equals(s) && st.is_final) {
                    System.out.println("is Accepted!");
                    return;
                }

            }
        }
        System.out.println("is NOT Accepted!");
    }

    private void converter(Vector<String> state) {
        if (state.isEmpty()) {
            return;
        }
        String state_name = state_name_creator(state);

        if (convert_states2.contains(state) || state_Equivalancy(state)) {
            return;
        } else {
            convert_states2.add(state);
        }

        Vector<String> inputs = new Vector<>();
        Vector<String> next_states = new Vector<>();

        for (String alpha : alphabet) {
            current_states = (Vector<String>) state.clone();
            delta(alpha.charAt(0));
            current_states = removeReduntant(current_states);
            if (!current_states.isEmpty() && !inputs.contains(""+alpha.charAt(0))) {
                inputs.add("" + alpha.charAt(0));
                next_states.add(state_name_creator(current_states));
            }
            converter(current_states);
        }

convert_states.add(new State(state_name, vector2array(inputs), vector2array(next_states), false, false));
    }

    private Vector removeReduntant(Vector<String> vect) {
        Vector<String> unique = new Vector<>();
        for (String str : vect) {
            if (!unique.contains(str)) {
                unique.add(str);
            }
        }

        return unique;
    }

    private void after_convert(String initial_name) {//for finding initial state and final states
        Vector<String> final_names = new Vector<>();
        for (State st : states) {
            if (st.is_final) {
                final_names.add(st.name);
            }
        }

        for (State st : convert_states) {
            if (st.name.equals("[" + initial_name + "]")) {
                st.setIs_initial(true);
            }
            for (String str : final_names) {
                if (st.name.contains(str)) {
                    st.setIs_final(true);
                }
            }
        }

    }
  
    private String state_name_creator(Vector<String> state) {
        String state_name = "[";
        for (int i = 0; i < state.size() - 1; i++) {
            state_name += state.elementAt(i) + ",";
        }
        state_name += state.lastElement();
        state_name += "]";

        return state_name;
    }

    private String[] vector2array(Vector<String> vect) {
        String[] str = new String[vect.size()];
        for (int i = 0; i < vect.size(); i++) {
            str[i] = vect.elementAt(i);
        }

        return str;
    }
    
     private boolean state_Equivalancy(Vector<String> state){
         int counter = 0;
        for(Vector<String> vec : convert_states2){
            if(state.size() == vec.size()){
                for(String str : vec){
                    if(!state.contains(str)){
                        break;
                    }
                    counter ++;
                }
                if(counter == state.size())
                    return true;
            }
        }
        
        return false;
     }
}

class State {

    String name;
    Vector<String> inputs;
    Vector<String> next_states;
    boolean is_initial;
    boolean is_final;

    public State(String name, String[] input, String[] next_state, boolean is_initial, boolean is_final) {
        this.name = name;
        inputs = new Vector<>();
        next_states = new Vector<>();
        for (int i = 0; i < input.length; i++) {
            inputs.add(input[i]);
            next_states.add(next_state[i]);
        }
        this.is_initial = is_initial;
        this.is_final = is_final;
    }

    public void setIs_initial(boolean is_initial) {
        this.is_initial = is_initial;
    }

    public void setIs_final(boolean is_final) {
        this.is_final = is_final;
    }

}
