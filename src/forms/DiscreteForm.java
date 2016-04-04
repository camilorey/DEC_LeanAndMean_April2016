/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import complex.DEC_Complex;
import complex.DEC_Object;
import complex.DEC_PrimalObject;
import containers.DEC_GeometricContainer;
import exceptions.DEC_Exception;
import java.util.HashMap;
import processing.core.PVector;
import utils.SparseVector;

/**
 *
 * @author laptop
 */
public class DiscreteForm {
 int dimension;
 double[] values;
 
 public DiscreteForm(int formSize){
  values = new double[formSize];
 }
 public void put(DEC_Object object, double value){
  values[object.getIndex()] = value;
 }
 public HashMap<PVector,Double> createLookUpTable(DEC_Complex complex) throws DEC_Exception{
  HashMap<PVector,Double> lookUpTable = new HashMap<PVector,Double>();
  double maxValue = -10000000;
  double minValue = 10000000;
  for(int i=0;i<values.length;i++){
   minValue = Math.min(minValue, values[i]);
   maxValue = Math.max(maxValue,values[i]);
  }
  for(int i=0;i<values.length;i++){
   DEC_PrimalObject object = complex.getPrimalObject(dimension, i);
   PVector c = object.getVectorContent(0);
   double normalizedValue = 1.0d;
   if(maxValue!= minValue){
    normalizedValue = (values[i]-minValue)/(maxValue-minValue);
   }
   lookUpTable.put(c, new Double(normalizedValue));
  }
  return lookUpTable;
 }
 public SparseVector toVector() throws DEC_Exception{
  SparseVector asVector = new SparseVector(values.length);
  return asVector;
 }
 
}
