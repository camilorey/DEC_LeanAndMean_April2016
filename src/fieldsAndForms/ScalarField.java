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
public class ScalarField extends ScalarAssignment{
 protected HashMap<PVector,Float> vertexLookUpTable;
 
 public ScalarField(){
  super();
  vertexLookUpTable = new HashMap<PVector,Float>();
 }

 public ScalarField(int dimension,char type){
  super(dimension,type);
  vertexLookUpTable = new HashMap<PVector,Float>();
 }
 public HashMap<PVector, Float> getVertexLookUpTable() {
  return vertexLookUpTable;
 }

 public void setVertexLookUpTable(HashMap<PVector, Float> vertexLookUpTable) {
  this.vertexLookUpTable = vertexLookUpTable;
 }
 public double function(float x, float y, float z){
  return Math.sin(Math.PI*x)*Math.cos(2*Math.PI*y)*Math.cos(3*Math.PI*z);
 }
 public double function(PVector v){
  return function(v.x,v.y,v.z);
 }
 public double function(DEC_Object object) throws DEC_Exception{
  PVector center = object.getVectorContent("CENTER");
  return function(center);
 }
 public void calculateField(DEC_Complex complex) throws DEC_Exception{
  System.out.println("creating vertex look up table");
  createVertexLookUpTable(complex);
  System.out.println("evaluating field");
  evaluate(complex);
  System.out.println("assigned values: "+values.size());
  System.out.println("creating look up table using super");
  createLookUpTable();
 }
 public void createVertexLookUpTable(DEC_Complex complex) throws DEC_Exception{
  DEC_Iterator vertIterator = complex.createIterator(0,'p');
  double minValue = 1000000;
  double maxValue = -1000000; 
  while(vertIterator.hasNext()){
   DEC_Object vertex = vertIterator.next();
   double fValue = function(vertex);
   minValue = Math.min(fValue, minValue);
   maxValue = Math.max(fValue,maxValue);
  }
  DEC_Iterator lookUpIterator = complex.createIterator(0,'p');
  while(lookUpIterator.hasNext()){
   DEC_Object vertex = lookUpIterator.next();
   double fValue = function(vertex);
   Double normalizedValue = new Double(1);
   if(maxValue!=minValue){
    normalizedValue = normalizedValue = new Double((fValue-minValue)/(maxValue-minValue));
   }
   vertexLookUpTable.put(vertex.getVectorContent("CENTER"), normalizedValue.floatValue());
  }
 }
 public void evaluate(DEC_Complex complex) throws DEC_Exception{
  DEC_Iterator functionIterator = complex.createIterator(dimension, type);
  double minValue = 10000000;
  double maxValue = -10000000;
  while(functionIterator.hasNext()){
   DEC_Object object = functionIterator.next();
   double fValue = function(object);
   minValue = Math.min(fValue, minValue);
   maxValue = Math.max(fValue,maxValue);
   assignScalar(object, new Double(fValue));
  }
 }
}
