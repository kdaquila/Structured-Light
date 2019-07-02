package lookuptable;

import core.TXT;
import curvefitting.inverserodbard.InverseRodbard;
import curvefitting.inverserodbard.InverseRodbard_Values;

public class LookUpTable_InverseRodbard extends LookUpTable {
    
    int[] givenInputs;
    double[] measuredOutputs;
    int[] nominalOutputs;
    int[] computedInputs;

    public LookUpTable_InverseRodbard(int[] givenInputs, double[] measuredOutputs, int[] nominalOutputs) {
        this.givenInputs = givenInputs;
        this.measuredOutputs = measuredOutputs;
        this.nominalOutputs = nominalOutputs;
        this.computedInputs = new int[nominalOutputs.length];
    }
    
    public int[][] computeTable() {
        
        // cast givenInputs from int to double
        double[] givenInputs_Double =  new double[givenInputs.length];       
        for (int i = 0; i < givenInputs.length; i++) {
            givenInputs_Double[i] = givenInputs[i]; 
        } 
        
        // cast nominalInputs from int to double
        double[] nominalOutputs_Double =  new double[nominalOutputs.length];       
        for (int i = 0; i < nominalOutputs.length; i++) {
            nominalOutputs_Double[i] = nominalOutputs[i]; 
        } 

        // Curve Fitting on givenInputs and measuredOutput
        InverseRodbard fit = new InverseRodbard(measuredOutputs, givenInputs_Double);
        fit.fit();
        
        // Compute the computedInputs
        double[] computedInputs_Double = InverseRodbard_Values.computeAll(fit.params, nominalOutputs_Double);
        
        // cast computedInputs from double to int
        for (int i = 0; i < computedInputs_Double.length; i++) {
            computedInputs[i] = (int) Math.round(computedInputs_Double[i]); 
        } 
        
        // Store the results
        int[][] lookUpTable = new int[2][nominalOutputs.length];
        for (int i = 0; i < nominalOutputs.length; i++) {
            lookUpTable[0][i] = nominalOutputs[i];
            lookUpTable[1][i] = computedInputs[i];
        }        
        
                    
        // TODO debug remove
        String debugPath = "C:\\Users\\kfd18\\kfd18_Downloads";        
        TXT.saveVector(givenInputs, "%d", debugPath, "givenInputs.txt");
        TXT.saveVector(measuredOutputs, "%f", debugPath, "measuredOutputs.txt");
        TXT.saveVector(nominalOutputs, "%d", debugPath, "nominalOutputs.txt");
        TXT.saveVector(computedInputs, "%d", debugPath, "computedInputs.txt"); 
        
        return lookUpTable;
    }
    

    
}
