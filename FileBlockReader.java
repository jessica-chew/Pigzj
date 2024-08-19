import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBlockReader {
    private static final int BLOCK_SIZE = 128 * 1024; // 128KB
    private BufferedInputStream bis;

    // Constructor for reading from stdin
    public FileBlockReader() {
        this.bis = new BufferedInputStream(System.in);
    }

    public List<byte[]> readFileInBlocks() throws IOException {
        List<byte[]> blocks = new ArrayList<>();
        byte[] buffer = new byte[BLOCK_SIZE];
        int bytesRead;

        // Read directly from stdin into blocks
        while ((bytesRead = bis.read(buffer)) != -1) {
            // If bytesRead is less than BLOCK_SIZE, it's the last block
            byte[] block = bytesRead == BLOCK_SIZE ? buffer.clone() : java.util.Arrays.copyOf(buffer, bytesRead);
            blocks.add(block);
            if (bytesRead < BLOCK_SIZE) {
                break; // If it's the last block
            }
        }
        return blocks;
    }
}
