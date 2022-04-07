/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notas.programacionavanzada2022;
import java.util.*;
import javax.swing.JTextField;
/**
 *
 * @author sergi
 */
public class ThreadList {
    private JTextField tf; 
    private ArrayList<Child> list; 
    
    
    public ThreadList (JTextField pText)
    {
        this.tf = pText; 
        list = new ArrayList<Child>(); 
    }
    
    public synchronized void push (Child c)
    {
        list.add(c);
        print();
    }
    
    public synchronized void pop (Child c)
    {
        list.remove(c); 
        print(); 
    }
    
    public void print()
    {
        String content="";
        for(int i=0; i<list.size(); i++)
        {
            content=content+list.get(i).getChildName()+" ";
        }
        tf.setText(content);
    }

}
