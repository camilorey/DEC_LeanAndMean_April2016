/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fieldsAndForms;

import complex.DEC_Complex;
import complex.DEC_Iterator;
import complex.DEC_Object;
import exceptions.DEC_Exception;
import java.util.HashMap;
import processing.core.PVector;

/**
 *
 * @author laptop
 */
public class VectorField extends VectorAssignment{
 
 public VectorField(){
  super();
 }
 public VectorField(int dimension, char type){
  super(dimension,type);
 } 
 public Double P(float x, float y, float z){
  return new Double(x-y);
 }
 public Double Q(float x, float y, float z){
  return new Double(x*z);
 }
 public Double R(float x,float y, float z){
  return new Double(y*y-x*z);
 }
 public Double[] fieldFunction(float x, float y, float z){
  return new Double[]{P(x,y,z),Q(x,y,z), R(x,y,z)};
 }
 public Double[] fieldFunction(DEC_Object object) throws DEC_Exception{
  PVector center = object.getVectorContent("CENTER");
  return fieldFunction(center.x,center.y,center.z);
 }
 public void calculateField(DEC_Complex complex) throws DEC_Exception{
  evaluate(complex);
  System.out.println("number of magnitudes: "+magnitudes.size());
  System.out.println("number of directions: "+directions.size());
  createMagnitudeLookUpTable();
 }
 public void evaluate(DEC_Complex complex) throws DEC_Exception{
  DEC_Iterator iterator = complex.createIterator(dimension, type);
  while(iterator.hasNext()){
   DEC_Object object = iterator.next();
   Double[] fieldValue = fieldFunction(object);
   assignVector(object, fieldValue);
  }
 }
}
