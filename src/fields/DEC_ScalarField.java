/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fields;

import complex.DEC_Complex;
import complex.DEC_Iterator;
import complex.DEC_Object;
import containers.DEC_GeometricContainer;
import exceptions.DEC_Exception;
import java.util.ArrayList;
import java.util.HashMap;
import processing.core.PVector;

/**
 *
 * @author laptop
 */
public class DEC_ScalarField {
 public DEC_ScalarField(){
  
 }
 public double fieldFunction(float x, float y, float z){
  return Math.sin(Math.PI*x)*Math.cos(2*Math.PI*y)*Math.sin(Math.PI*z);
 }
 public double fieldFunction(DEC_Object object, DEC_GeometricContainer container)throws DEC_Exception{
  ArrayList<PVector> geometry = container.getGeometricContent(object);
  return sampleFunction(geometry);
 }
 public HashMap<PVector,Float> createLookUpTable(DEC_Complex complex, DEC_GeometricContainer container, int dimension, char type) throws DEC_Exception{
  HashMap<PVector,Float> lookUpTable = new HashMap<PVector,Float>();
  HashMap<PVector,Double> functionValues = evaluateFunction(complex,container,dimension,type);
  double maxValue = -10000000;
  double minValue = 10000000;
  for(PVector v: functionValues.keySet()){
   Double value = functionValues.get(v);
   minValue = Math.min(minValue, value);
   maxValue = Math.max(maxValue, value);
  }
  if(maxValue != minValue){
   for(PVector v: functionValues.keySet()){
    Double value = functionValues.get(v);
    Double normalizedValue = (value.doubleValue()-minValue)/(maxValue-minValue); 
    lookUpTable.put(v,normalizedValue.floatValue());
   }
  }
   else{
    for(PVector v: functionValues.keySet()){
    Double value = functionValues.get(v);
    Double normalizedValue = (value.doubleValue()-minValue)/(maxValue-minValue); 
    lookUpTable.put(v,new Float(1));
   }       
  }
  return lookUpTable;
 }
 public HashMap<PVector,Double> evaluateFunction(DEC_Complex complex, DEC_GeometricContainer container, int dimension, char type) throws DEC_Exception{
  HashMap<PVector,Double> fieldImage = new HashMap<PVector,Double>();
  DEC_Iterator iterator = complex.createIterator(dimension, type);
  while(iterator.hasNext()){
   DEC_Object object = iterator.next();
   PVector objectCenter = object.getVectorContent(0);
   double fieldValue = fieldFunction(object,container);
   fieldImage.put(objectCenter,new Double(fieldValue));
  }
  return fieldImage;
 }
 public double sampleFunction(ArrayList<PVector> points){
  double result = 0;
  for(int i=0;i<points.size();i++){
   result += fieldFunction(points.get(i).x,points.get(i).y,points.get(i).z);
  }
  result /= (double) points.size();
  return result;
 }
}
