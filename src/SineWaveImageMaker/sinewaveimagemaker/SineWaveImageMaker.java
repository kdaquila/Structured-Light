package sinewaveimagemaker;

import core.BufferedImageFactory;
import core.ImageUtils;
import core.XML;
import java.awt.image.BufferedImage;
import java.util.Map;
import static core.Print.println;
import core.TXT;
import java.util.TreeMap;
import lookuptable.LookUpTable;
import sinewavepattern.SineWavePattern;

public class SineWaveImageMaker {
    
    Map<String,Object> config;  
    
    public SineWaveImageMaker(String configPath) {
        // Load the XML configuration file
        XML xml = new XML(configPath);        
        
        // Prepend the base directory to all relative sub directories
        xml.prependBaseDir();
        
        // Get the configuration data as a map
        config = xml.map;   
    }
    
    public Map<String,BufferedImage> makeImages() {
        // Compute the sine wave arrays
        int nRows = (Integer) config.get("nRows");
        int nCols = (Integer) config.get("nCols");
        int amplitude = (Integer) config.get("amplitude");
        int offset = (Integer) config.get("offset");
        double waveLength = (Double) config.get("waveLength");
        int nPhaseSteps = (Integer) config.get("nPhaseSteps");
        String orientation = (String) config.get("waveDirection");          
        Map<String,int[][]> sineWaveArrayStack = SineWavePattern.makeSineArrayStack(nRows, nCols, amplitude, offset, waveLength, nPhaseSteps, orientation);
        
        // Apply the look-up-table
        boolean doBrightnessCalibration = (Boolean) config.get("doBrightnessCalibration");
        Map<String,int[][]> sineWaveArrayStack_Adj = new TreeMap<>();
        if (doBrightnessCalibration) {
            String brightnessCalibrationTableDir = (String) config.get("brightnessCalibrationTableDir");
            String brightnessCalibrationTableFilename = (String) config.get("brightnessCalibrationTableFilename");
            String brightnessCalibrationFullPath = brightnessCalibrationTableDir + "\\" + brightnessCalibrationTableFilename;
            int[][] lookUpTable = TXT.loadMatrix_Integer(brightnessCalibrationFullPath);
            for (String name: sineWaveArrayStack.keySet()) {
                int[][] oldImg = sineWaveArrayStack.get(name);
                int[][] newImg = LookUpTable.applyTable(oldImg, lookUpTable);
                sineWaveArrayStack_Adj.put(name, newImg);
            }        
        }
         
        // Set as images      
        Map<String,BufferedImage> sineWaveImages = new TreeMap<>();
        for (String name: sineWaveArrayStack_Adj.keySet()) {
            int[][] array = sineWaveArrayStack_Adj.get(name);
            BufferedImage image = BufferedImageFactory.build_16bit_Gray(array);
            sineWaveImages.put(name, image);
        }    
        return sineWaveImages;
    }
    
    public static void main(String[] args) {
               
        println("Running the Sine Wave Image Maker App:");

        // Validate arguments
        if (args.length == 0) {
            throw new IllegalArgumentException("Needs exactly one argument which is the path to the XML configuration file.");
        } 

        // Parse the arguments
        String configAbsPath = args[0];
        
        // Create the app
        SineWaveImageMaker app = new SineWaveImageMaker(configAbsPath);
        
        // Create the images
        println("Creating the Images");
        Map<String,BufferedImage> sineWaveImages = app.makeImages();
        
        // Save the images
        println("Saving the Images");
        String sineWavePatternsDir = (String) app.config.get("sineWavePatternsDir");
        ImageUtils.save_batch(sineWaveImages, sineWavePatternsDir);
    }    
    
}