/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fieldsAndForms;

import complex.DEC_Object;
import exceptions.DEC_Exception;
import java.util.ArrayList;
import processing.core.PVector;

/**
 *
 * @author laptop
 */
public class ScalarFieldMatrix {
 protected ScalarField[] components;
 protected int numRows;
 protected int numColumns;
 
 public ScalarFieldMatrix(){
  numRows = 0;
  numColumns = 0;
  components = null;
 }
 public ScalarFieldMatrix(int numRows, int numColumns){
  this.numRows = numRows;
  this.numColumns = numColumns;
  this.components = new ScalarField[numRows*numColumns];
 }

 public ScalarField[] getComponents() {
  return components;
 }

 public void setComponents(ScalarField[] components) {
  this.components = components;
 }
 public int size(){
  return components.length;
 }
 public int getNumRows() {
  return numRows;
 }

 public void setNumRows(int numRows) {
  this.numRows = numRows;
 }

 public int getNumColumns() {
  return numColumns;
 }

 public void setNumColumns(int numColumns) {
  this.numColumns = numColumns;
 }
 
 public int ix(int i, int j){
  return i+j*numRows;
 }
 public Double[] value(float x, float y, float z){
  Double[] result = new Double[components.length];
  for(int i=0;i<components.length;i++){
   result[i] = components[i].function(x, y, z);
  }
  return result;
 }
 public Double[] value(PVector p){
  return value(p.x,p.x,p.z);
 }
 public Double[] value(DEC_Object object) throws DEC_Exception{
  PVector c = object.getVectorContent("CENTER");
  return value(c);
 }
 
}
