/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fieldsAndForms;

import complex.DEC_Complex;
import complex.DEC_Iterator;
import complex.DEC_Object;
import containers.DEC_GeometricContainer;
import exceptions.DEC_Exception;
import java.util.ArrayList;
import java.util.HashMap;
import processing.core.PVector;
import utils.CalculationUtils;

/**
 *
 * @author laptop
 */
public class ContinuousDifferentialForm extends DifferentialForm{
 ScalarFieldMatrix coefficientFunctions;
 public ContinuousDifferentialForm() {
  super();
  dimension = 0;
  coefficientFunctions = null;
 }

 public ContinuousDifferentialForm(int dimension, char type) {
  super(dimension, type);
  if(dimension == 0){
   coefficientFunctions = new ScalarFieldMatrix(1, 1);
  }else if(dimension == 1){
   coefficientFunctions = new ScalarFieldMatrix(3, 1);
  }else if(dimension == 2){
   coefficientFunctions = new ScalarFieldMatrix(3,3);
  }else{
   coefficientFunctions = null;
  }
 }
 public ContinuousDifferentialForm(ScalarFieldMatrix coefficientFunctions, char type){
  int dim = -1;
  if(coefficientFunctions.size()==1){
   dim = 0;
  }else if(coefficientFunctions.size()==3){
   dim = 1;
  }else if(coefficientFunctions.size()==9){
   dim = 2;
  }
  if(dim!=-1){
   this.dimension = dimension;
   this.type = type;
   values = new HashMap<DEC_Object,Double>();
   valueLookUpTable = new HashMap<PVector,Float>();
   this.coefficientFunctions = coefficientFunctions;
  }
 }
 public Double value(VectorField[] vectorFields,PVector p){
  return value(vectorFields,p.x,p.y,p.z);
 }
 public Double value(VectorField[] vectorFields, float x, float y, float z){
  Double result = new Double(0);
  if(dimension == 0){
   result = coefficientFunctions.value(x,y,z)[0];
  }else if(dimension == 1){
   Double[] fieldValue = vectorFields[0].fieldFunction(x,y,z);
   Double[] coefficients = coefficientFunctions.value(x, y, z);
   result = CalculationUtils.dotProduct(fieldValue, coefficients);
  }else if(dimension == 2){
   Double[] fieldValue1 = vectorFields[0].fieldFunction(x,y,z);
   Double[] fieldValue2 = vectorFields[1].fieldFunction(x,y,z);
   Double[] coefficients = coefficientFunctions.value(x, y, z);
   result = CalculationUtils.scalarInnerProduct(coefficients, fieldValue1, fieldValue2, 3, 3);
  }
  return result;
 }
 public Double integrate(DEC_Object object, DEC_GeometricContainer container, int gaussianOrder) throws DEC_Exception{
  Double result = new Double(0);
  ArrayList<PVector> verts = container.getGeometricContent(object);
  if(dimension == 0){
   result = coefficientFunctions.value(verts.get(0))[0];
  }else if(dimension == 1){
   PVector p = PVector.sub(verts.get(1),verts.get(0));
   float[] transformedTValues = CalculationUtils.rescaleGaussianPoints(0, 1, gaussianOrder);
   Double[] gaussianWeights;
   
   if(gaussianOrder == 2){
    gaussianWeights = CalculationUtils.gaussianWeights_Order2;
   }else{
    gaussianWeights = CalculationUtils.gaussianWeights_Order3;
   }
   for(int i=0;i<transformedTValues.length;i++){
    
   }
  }
  return result;
 }
 public void discretize(DEC_Complex complex, DEC_GeometricContainer container, int gaussianOrder) throws DEC_Exception{
  DEC_Iterator iterator = complex.createIterator(dimension, type);
  while(iterator.hasNext()){
   DEC_Object object = iterator.next();
   Double integralValue = integrate(object,container,gaussianOrder);
   assignScalar(object, integralValue);
  }  
 }
}
