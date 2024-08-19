import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap; //this is for sharing compressed data instead of just writing straight to stdout


public class CompressionRunnable implements Runnable {
    private byte[] dataBlock;
    private int blockIndex;
    private ConcurrentHashMap<Integer, byte[]> compressedBlocks;

    public CompressionRunnable(byte[] dataBlock, int blockIndex, ConcurrentHashMap<Integer, byte[]> compressedBlocks) {
        this.dataBlock = dataBlock;
        this.blockIndex = blockIndex;
        this.compressedBlocks = compressedBlocks;
    }

    @Override 
    public void run() {
        try{
            byte[] cBlock = FileCompressor.compressFile(dataBlock);
            compressedBlocks.put(blockIndex, cBlock);

        } catch (IOException e){
            System.err.println("Failed to compress block" +  ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}