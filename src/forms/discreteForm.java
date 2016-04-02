/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import complex.DEC_Object;
import exceptions.DEC_Exception;
import java.util.HashMap;
import utils.SparseVector;

/**
 *
 * @author laptop
 */
public class discreteForm {
 int dimension;
 HashMap<DEC_Object,Float> form;
 
 public discreteForm(){
  form = new HashMap<DEC_Object,Float>();
 }
 public void put(DEC_Object object, float value){
  if(!form.containsKey(object)){
   form.put(object, new Float(value));
  }else{
   form.put(object, new Float(value));
  }
 }
 public SparseVector toVector() throws DEC_Exception{
  SparseVector asVector = new SparseVector(form.keySet().size());
  for(DEC_Object object: form.keySet()){
   asVector.set(object.getIndex(), form.get(object).floatValue());
  }
  return asVector;
 }
 
}
