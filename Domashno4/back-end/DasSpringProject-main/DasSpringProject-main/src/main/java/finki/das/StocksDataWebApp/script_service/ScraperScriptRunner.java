package finki.das.StocksDataWebApp.script_service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
//
//public class ScraperScriptRunner {
//    public static void startScript(String scriptPath) {
//        try{
//            ProcessBuilder processBuilder=new ProcessBuilder("python",scriptPath);
//            processBuilder.redirectErrorStream(true);
//            Process process= processBuilder.start();
//
//            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line= bufferedReader.readLine())!=null){
//                System.out.println("[Python script]:"+line);
//            }
//
//            int exitCode= process.waitFor();
//            System.out.println("Exit code:"+exitCode);
//        }catch (Exception e){
//            System.out.println("Error starting script:"+e.getMessage());
//            e.printStackTrace();
//        }
//
//    }
//}

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ScraperScriptRunner {

    public static boolean startScript(String scriptPath) {
        try {
            // Build the process to execute the Python script
            ProcessBuilder processBuilder = new ProcessBuilder("python3", scriptPath);
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to finish with a timeout of 1 hour
            boolean finished = process.waitFor(1, TimeUnit.HOURS);

            if (finished) {
                System.out.println("Script executed successfully: " + scriptPath);
                return true;
            } else {
                // Destroy the process if it exceeds the timeout
                process.destroy();
                System.err.println("Script execution timed out: " + scriptPath);
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error starting script: " + e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            System.err.println("Script execution interrupted: " + e.getMessage());
            return false;
        }
    }
}
