import java.io.IOException;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;

public class Pigzj {

    public static void main(String[] args) {
        // Check if we have at least 3 arguments (a flag and its corresponding value)
        
        //check that file name is present this is the default case
        // if (args.length == 0) System.exit(1);

        
        // int filenamesStartIndex = 0;
        int p = Runtime.getRuntime().availableProcessors(); // Default to available processors

        if (args.length > 1){
            if (args[0].equals("-p")){
                if (args.length < 2) { // Need at least -p, its value, and one filename
                    System.err.println("Usage: java Pigzj [-p number_of_processors] <filename1> <filename2> ...");
                    System.exit(1);
                }
                try {
                    p = Integer.parseInt(args[1]);
                    // filenamesStartIndex = 2; // Filenames start after -p and its value
                    if (p <= 0) {
                        System.err.println("Number of processors must be greater than 0.");
                        System.exit(2);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number of processors specified.");
                    System.exit(2);
                }
            }
        }
            //byte [] inputData = processStdin();
            ConcurrentHashMap<Integer, byte[]> compressedBlocks = new ConcurrentHashMap<>();
            FileBlockReader blockReader = new FileBlockReader();
            TaskQueue taskQueue = new TaskQueue();

        try{
            List<byte[]> blocks = blockReader.readFileInBlocks();

            for (int i = 0; i < blocks.size(); i++) {
                taskQueue.addTask(new CompressionRunnable(blocks.get(i), i, compressedBlocks));
            }

            int numThreads = p; // Assuming 'p' is the number of threads specified by the user
            WorkerThread[] workers = new WorkerThread[numThreads];
            //Thread[] threads = new Thread[blocks.size()]; //now we need to do it for a specified number of threads
            
            for (int i = 0; i < numThreads; i++) {
                workers[i] = new WorkerThread(taskQueue, "Worker-" + i);
                workers[i].start();
            }

            // Wait for all worker threads to complete
            for (int i = 0; i < numThreads; i++) {
                try{
                    workers[i].join();
                } catch (InterruptedException e){
                    System.err.println("not good :(");
                }
            }
            mergeCompressedBlocks(compressedBlocks, blocks.size());
        }catch (IOException e) {
            System.err.println("Failed to read file in blocks: " + e.getMessage());
        }
        

        
    }

    private static void mergeCompressedBlocks(ConcurrentHashMap<Integer, byte[]> compressedBlocks, int totalBlocks) throws IOException{
        try (BufferedOutputStream bos = new BufferedOutputStream(System.out)) { //instead of writing to outputFile, need to write to stdout
            for (int i = 0; i < totalBlocks; i++) {
                byte[] block = compressedBlocks.get(i);
                if (block != null) {
                    bos.write(block);
                }
            }
        }
    }
    private static byte[] processStdin() throws IOException {
        // System.out.println("Reading from stdin");
        BufferedInputStream bis = new BufferedInputStream(System.in);
        byte[] inputBuffer = new byte[1024]; // Adjust buffer size as needed
        int bytesRead;
    
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((bytesRead = bis.read(inputBuffer)) != -1) {
            baos.write(inputBuffer, 0, bytesRead);
        }
        byte[] inputData = baos.toByteArray();
    
        return inputData;
    }
}

