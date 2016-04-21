package machinetheory1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;


/**
 *
 * @author arman pasha
 */
public class MyFileReader {
    
    MachineTheory1 mt;
    public MyFileReader(MachineTheory1 mt) {
        this.mt = mt;
    }
    
    public void read(Path path){
        String all_lines = "";
        Vector<String> alphabet = new Vector<>();
        Vector<State> states = new Vector<>();
        try {
            for(String s : Files.readAllLines(path)){
                all_lines += s;
            }
        } catch (IOException ex) {
            System.err.println("File Read Error!");
        }
        
        String[] raw_machines = all_lines.split("-----");
        for(String machine : raw_machines){
            boolean isNDFA = false;
            String[] machine_info = machine.split("#");
            if(machine_info[0].equals("NDFA")){
                isNDFA = true;
            }
            for(String s: machine_info[1].split(",")){
                alphabet.add(s);
            }
            String initial_state = machine_info[3];
            String[] final_states = machine_info[4].split(",");
            
            
            for(String raw_state: machine_info[5].split("/")){
                String[] each_state = raw_state.split(";");
                boolean is_final = false;
                for(String s: final_states){//for checking wheather state is final or not
                    if(s.equals(each_state[0])){
                        is_final = true;
                    }
                }
                 states.add(new State(each_state[0],each_state[1].split(","),each_state[2].split(","),
                 initial_state.contains(each_state[0]),is_final));
            }
            
            mt.machineInfo(states, alphabet, machine_info[6].split("/"), isNDFA);
            states.removeAllElements();
            alphabet.removeAllElements();
        }
        
    }
    
}
