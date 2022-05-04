/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notas.programacionavanzada2022;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sergi
 */
public class Instructor extends Thread{
    
    private String instructorName;
    private Camp instructorCamp;

    public Instructor(String instructorName, Camp instructorCamp) {
        this.instructorName = instructorName;
        this.instructorCamp = instructorCamp;
    }

    Instructor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public Camp getInstructorCamp() {
        return instructorCamp;
    }

    public void setInstructorCamp(Camp instructorCamp) {
        this.instructorCamp = instructorCamp;
    }
    
    public void run(){
        
    }
}
