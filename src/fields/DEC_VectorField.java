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
public class DEC_VectorField {
 public DEC_VectorField(){
  
 }
 public PVector fieldFunction(float x, float y, float z){
  return new PVector(x-y,z+x,-y); 
 }
 public PVector fieldFunction(DEC_Object object, DEC_GeometricContainer container)throws DEC_Exception{
  ArrayList<PVector> geometry = container.getGeometricContent(object);
  return sampleFunction(geometry);
 }
 public HashMap<PVector,PVector> evaluateFunction(DEC_Complex complex, DEC_GeometricContainer container, int dimension, char type) throws DEC_Exception{
  HashMap<PVector,PVector> fieldImage = new HashMap<PVector,PVector>();
  DEC_Iterator iterator = complex.createIterator(dimension, type);
  while(iterator.hasNext()){
   DEC_Object object = iterator.next();
   PVector objectCenter = object.getVectorContent(0);
   PVector fieldValue = fieldFunction(object,container);
   fieldImage.put(objectCenter,fieldValue);
  }
  return fieldImage;
 }
 public PVector sampleFunction(ArrayList<PVector> points){
  PVector result = new PVector();
  for(int i=0;i<points.size();i++){
   result.add(fieldFunction(points.get(i).x,points.get(i).y,points.get(i).z));
  }
  result.div((float) points.size());
  return result;
 }
 
}
