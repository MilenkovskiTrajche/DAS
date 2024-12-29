package finki.das.StocksDataWebApp.script_service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ScraperScriptRunner {
    public static void startScript(String scriptPath) {
        try{
            ProcessBuilder processBuilder=new ProcessBuilder("python",scriptPath);
            processBuilder.redirectErrorStream(true);
            Process process= processBuilder.start();

            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line= bufferedReader.readLine())!=null){
                System.out.println("[Python script]:"+line);
            }

            int exitCode= process.waitFor();
            System.out.println("Exit code:"+exitCode);
        }catch (Exception e){
            System.out.println("Error starting script:"+e.getMessage());
            e.printStackTrace();
        }

    }
}
