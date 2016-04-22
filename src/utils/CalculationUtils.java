/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import fieldsAndForms.ScalarField;
import processing.core.PVector;

/**
 *
 * @author laptop
 */
public class CalculationUtils {
 public static float[] gaussianPoints_Order2 = new float[]{-new Double(Math.sqrt(1.0d/3.0d)).floatValue(),
                                                           new Double(Math.sqrt(1.0d/3.0d)).floatValue()};
 public static Double[] gaussianWeights_Order2 = new Double[]{new Double(1),
                                                              new Double(1)};
 public static float[] gaussianPoints_Order3 = new float[]{-new Double(Math.sqrt(3.0d/5.0d)).floatValue(),
                                                          0, 
                                                          new Double(Math.sqrt(3.0d/5.0d)).floatValue()};
 public static Double[] gaussianWeights_Order3 = new Double[]{new Double(5.0d/9.0d),
                                                              new Double(8.0d/9.0d),
                                                              new Double(5.0d/9.0d)};
 public static float[] rescaleGaussianPoints(float a, float b, int gaussianOrder){
  if(gaussianOrder == 2){
   float[] rescaled = new float[gaussianPoints_Order2.length];
   for(int i=0;i<rescaled.length;i++){
    rescaled[i] = gaussianPoints_Order2[i]*(b-a)/2.0f+(b+a)/2.0f;
   }
   return rescaled;
  }else if(gaussianOrder == 3){
   float[] rescaled = new float[gaussianPoints_Order3.length];
   for(int i=0;i<rescaled.length;i++){
    rescaled[i] = gaussianPoints_Order3[i]*(b-a)/2.0f+(b+a)/2.0f;
   }
   return rescaled;
  }else{
   return null;
  }
 }
 public static float lineIntegral(ScalarField f,PVector v1, PVector v2){
  float result = 0;
  float dist = v1.dist(v2);
  
  return result;
 }
 public static Double gaussianSum(Double[] values, int gaussianOrder){
  Double result = new Double(0);
  if(gaussianOrder == 2){
   for(int i=0;i<gaussianWeights_Order2.length;i++){
    result += gaussianWeights_Order2[i]*values[i];
   }
  }else if(gaussianOrder == 3){
   for(int i=0;i<gaussianWeights_Order2.length;i++){
    result += gaussianWeights_Order2[i]*values[i];
   }
  }
  return result;
 }
 public static Double dotProduct(Double[] u, Double[] v){
  Double result = new Double(0);
  for(int i=0;i<u.length;i++){
   result += u[i]*v[i];
  }
  return result;
 }
 public static Double[] matrixVectorProduct(Double[] A, Double[] b, int numRows, int numCols){
  Double[] result = new Double[numRows];
  for(int i=0;i<numRows;i++){
   Double rowResult = new Double(0);
   for(int j=0;j<numCols;j++){
    int index = i+j*numRows;
    rowResult += b[i]*A[index];
   }
   result[i] = rowResult;
  }
  return result;
 }
 public static Double scalarInnerProduct(Double[] A, Double[] b1, Double[] b2, int numRows, int numCols){
  Double[] firstProduct = matrixVectorProduct(A,b2,numRows,numCols);
  return dotProduct(b1,firstProduct);
 }
}
